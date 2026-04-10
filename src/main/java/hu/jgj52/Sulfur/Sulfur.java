package hu.jgj52.Sulfur;

import com.google.gson.*;
import hu.jgj52.Sulfur.Commands.*;
import hu.jgj52.Sulfur.Listeners.*;
import hu.jgj52.Sulfur.Utils.LoadedPlugin;
import hu.jgj52.Sulfur.Utils.Plugin;
import hu.jgj52.Sulfur.Utils.Server;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Sulfur {

    public static InstanceContainer ic;
    public static JsonObject conf;
    public static Map<String, LoadedPlugin> loadedPlugins = new HashMap<>();
    public static Auth auth;

    static void main() {
        conf = new Server().getConfig();
        JsonObject serverConf = conf.get("server").getAsJsonObject();
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

        new PlayerJoinListener();
        new ServerPingListener();
        new PickupItemListener();
        new ItemDropListener();

        MinecraftServer.getCommandManager().register(new PluginsCommand());
        MinecraftServer.getCommandManager().register(new VersionCommand());

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
        }));
    }

    private static void registerPlugins() {
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
                    loadedPlugins.put(data.getName(), data);
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
}