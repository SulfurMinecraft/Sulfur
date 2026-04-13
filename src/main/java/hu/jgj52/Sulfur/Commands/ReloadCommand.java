package hu.jgj52.Sulfur.Commands;

import hu.jgj52.Sulfur.Sulfur;
import net.minestom.server.command.builder.Command;

public class ReloadCommand extends Command {

    public ReloadCommand() {

        super("reload", "rl");

        setDefaultExecutor((sender, context) -> {

            Sulfur.registerPlugins();

            sender.sendMessage("Reloaded plugins successfully!");

        });

    }

}