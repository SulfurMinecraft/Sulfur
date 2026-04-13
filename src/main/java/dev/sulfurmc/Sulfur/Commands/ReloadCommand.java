package dev.sulfurmc.Sulfur.Commands;

import dev.sulfurmc.Sulfur.Sulfur;
import net.minestom.server.command.builder.Command;

public class ReloadCommand extends Command {

    public ReloadCommand() {

        super("reload", "rl");

        setDefaultExecutor((sender, context) -> {

            Sulfur.registerPlugins(false);

            sender.sendMessage("Reloaded plugins successfully!");

        });

    }

}