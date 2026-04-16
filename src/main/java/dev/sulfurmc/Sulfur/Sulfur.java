package dev.sulfurmc.Sulfur;

import com.google.gson.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.sulfurmc.Sulfur.Commands.*;
import dev.sulfurmc.Sulfur.Listeners.*;
import dev.sulfurmc.Sulfur.Permissions.User;
import dev.sulfurmc.Sulfur.Utils.*;
import dev.sulfurmc.Sulfur.Utils.Listeners.Listener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.Auth;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.UnitModifier;
import net.minestom.server.utils.identity.PermissionThing;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.Statement;
import java.util.*;
import java.util.jar.*;

public class Sulfur {

    public static InstanceContainer ic;
    public static JsonObject conf;
    public static Map<Plugin, LoadedPlugin> loadedPlugins = new HashMap<>();
    public static Map<String, LoadedPlugin> loadedPluginsByName = new HashMap<>();
    public static Auth auth;
    public static boolean local;
    public static HikariDataSource ds;

    public static List<SulfurCommand> registeredCommands = new ArrayList<>();

    static void main() {
        conf = new Server().getConfig();
        JsonObject serverConf = conf.get("server").getAsJsonObject();
        local = !serverConf.get("storeInPostgres").getAsBoolean();
        auth = serverConf.get("velocity").getAsBoolean() ? new Auth.Velocity(serverConf.get("velocitySecret").getAsString()) :
                serverConf.get("bungeecord").getAsBoolean() ? new Auth.Bungee() :
                serverConf.get("onlineMode").getAsBoolean() ? new Auth.Online() : new Auth.Offline();
        MinecraftServer server = MinecraftServer.init(auth);

        InstanceManager im = MinecraftServer.getInstanceManager();
        ic = im.createInstanceContainer(new AnvilLoader("worlds/world"));

        ic.setGenerator(unit -> {
            UnitModifier modifier = unit.modifier();
            modifier.setBlock(new Pos(0, -1, 0), Block.BEDROCK);
        });

        ic.setChunkSupplier(LightingChunk::new);

        PermissionThing.setAdd((uuid, perm) -> new User(uuid).add(perm));
        PermissionThing.setRemove((uuid, perm) -> new User(uuid).add(perm));
        PermissionThing.setHas((uuid, perm) -> new User(uuid).has(perm));

        MinecraftServer.getCommandManager().setUnknownCommandCallback((sender, command) -> {
            sender.sendMessage(Component.translatable("command.unknown.command").color(NamedTextColor.RED));
            sender.sendMessage(Component.text(command).decorate(TextDecoration.UNDERLINED).color(NamedTextColor.RED).append(Component.translatable("command.context.here").color(NamedTextColor.RED).decoration(TextDecoration.UNDERLINED, false)));
        });

        if (!local) {
            JsonObject pgConf = serverConf.get("postgres").getAsJsonObject();
            HikariConfig config = new HikariConfig();

            config.setJdbcUrl("jdbc:postgresql://" + pgConf.get("host").getAsString() + ":" + pgConf.get("port").getAsInt() + "/" + pgConf.get("database").getAsString());
            config.setUsername(pgConf.get("user").getAsString());
            config.setPassword(pgConf.get("password").getAsString());

            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);

            ds = new HikariDataSource(config);

            try (Connection conn = ds.getConnection(); Statement st = conn.createStatement()) {
                st.execute("""
                        CREATE TABLE IF NOT EXISTS sulfur_data (
                            name VARCHAR NOT NULL,
                            location VARCHAR NOT NULL,
                            data JSONB NOT NULL DEFAULT '{}',
                            PRIMARY KEY (name, location)
                        )
                        """);
                st.execute("""
                DO $$
                BEGIN
                    IF NOT EXISTS (
                        SELECT 1 FROM pg_constraint
                        WHERE conname = 'sulfur_data_name_location_unique'
                    ) THEN
                        ALTER TABLE sulfur_data
                        ADD CONSTRAINT sulfur_data_name_location_unique UNIQUE (name, location);
                    END IF;
                END;
                $$;
                """);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        registerPlugins();

        MinecraftServer.setBrandName(
                serverConf.get("brand").getAsString()
                        .replace("&", "§")
        );
        server.start(serverConf.get("host").getAsString(), serverConf.get("port").getAsInt());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (LoadedPlugin plugin : loadedPlugins.values()) {
                plugin.getPlugin().onDisable();
            }
            for (Player player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                player.kick(MiniMessage.miniMessage().deserialize(serverConf.get("shutdownKick").getAsString()));
            }
            try {
                ic.saveInstance().get();
                ic.saveChunksToStorage().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ds != null && !ds.isClosed()) {
                ds.close();
            }
        }));
    }

    private static void register() {
        new PlayerJoinListener();
        new ServerPingListener();
        new PickupItemListener();
        new ItemDropListener();
        new UserListener();
        new PickBlockEvent();

        new PluginsCommand();
        new VersionCommand();
        new ReloadCommand();
    }

    public static void registerPlugins() {
        registeredCommands.forEach(c -> MinecraftServer.getCommandManager().unregister(c));
        registeredCommands.clear();

        Listener.unregisterAll();

        loadedPlugins.keySet().forEach(Plugin::onDisable);
        loadedPlugins.clear();

        register();

        File folder = new File("plugins");
        try {
            if (!folder.exists()) Files.createDirectory(folder.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File[] jars = folder.listFiles((_, name) -> name.endsWith(".jar"));
        if (jars == null) return;

        URLClassLoader cl = new URLClassLoader(
                Arrays.stream(jars)
                        .map(jar -> {
                            try {
                                return jar.toURI().toURL();
                            } catch (MalformedURLException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .toList()
                        .toArray(new URL[0]),
                Sulfur.class.getClassLoader()
        );

        for (File jar : jars) {
            try (JarFile jarFile = new JarFile(jar)) {
                JarEntry entry = jarFile.getJarEntry("plugin.yml");

                if (entry == null) continue;

                try (InputStream is = jarFile.getInputStream(entry)) {
                    LoadedPlugin data = new Yaml().loadAs(is, LoadedPlugin.class);

                    Class<?> clazz = cl.loadClass(data.getMain());
                    Plugin plugin = (Plugin) clazz.getDeclaredConstructors()[0].newInstance();
                    data.setPlugin(plugin);
                    data.setJarFile(jarFile);
                    loadedPlugins.put(plugin, data);
                    loadedPluginsByName.put(data.getName(), data);
                    plugin.onEnable();
                } catch (ClassNotFoundException | InstantiationException |
                         IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static File getDataFolder(Plugin plugin) {
        for (LoadedPlugin loaded : loadedPlugins.values()) {
            if (loaded.getPlugin() == plugin) {
                return new File("plugins", loaded.getName());
            }
        } return new File("plugins", "null");
    }

    public static Logger getLogger(Plugin plugin) {

        return new Logger(plugin);

    }

}