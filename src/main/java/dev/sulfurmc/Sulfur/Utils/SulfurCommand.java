package dev.sulfurmc.Sulfur.Utils;

import dev.sulfurmc.Sulfur.Sulfur;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;

public abstract class SulfurCommand extends Command {
    public SulfurCommand(String name) {
        super(name);
        MinecraftServer.getCommandManager().register(this);
        Sulfur.registeredCommands.add(this);
    }

    public SulfurCommand(String name, String... aliases) {
        super(name, aliases);
        MinecraftServer.getCommandManager().register(this);
        Sulfur.registeredCommands.add(this);
    }
}
