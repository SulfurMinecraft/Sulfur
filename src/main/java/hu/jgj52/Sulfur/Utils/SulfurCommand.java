package hu.jgj52.Sulfur.Utils;

import hu.jgj52.Sulfur.Sulfur;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static hu.jgj52.Sulfur.Sulfur.loadedPlugins;

public class SulfurCommand {

    private Command command;
    private Plugin plugin;

    public SulfurCommand(Command command, Plugin plugin) {

        this.command = command;
        this.plugin = plugin;

    }

    public void register() {

        String prefix = "null";
        ArrayList<String> aliases = new ArrayList();

        for (LoadedPlugin loaded : loadedPlugins.values()) {

            if (loaded.getPlugin() == plugin) {

                prefix = loaded.getName().toLowerCase();

            }

        }

        for (String alias : command.getAliases()) aliases.add(prefix + " " + alias); // Will be used later

        AtomicBoolean exists = new AtomicBoolean(false);

        Sulfur.registeredCommands.forEach((plugin1, commands) -> {

            for (SulfurCommand command1 : commands) {

                if (
                        command1.getCommand().getName().equals(command.getName())
                                || Arrays.stream(command1.getCommand().getAliases())
                                .anyMatch(a -> a.equals(command.getName()))
                                || Arrays.stream(command.getAliases())
                                .anyMatch(a -> a.equals(command1.getCommand().getName()))
                ) {

                    System.out.println("Command " + command.getName() + " already exists!");

                    exists.set(true);

                }

            }

        });

        if (exists.get()) return;

        Sulfur.registeredCommands.putIfAbsent(plugin, new ArrayList<>());
        Sulfur.registeredCommands.get(plugin).add(this);

        MinecraftServer.getCommandManager().register(command);

    }

    public void unregister() {

        MinecraftServer.getCommandManager().unregister(command);

        Sulfur.registeredCommands.get(plugin).remove(this);

    }

    public Command getCommand() {

        return command;

    }

    public Plugin getPlugin() {

        return plugin;

    }

}
