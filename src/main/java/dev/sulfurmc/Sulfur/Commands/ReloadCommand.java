package dev.sulfurmc.Sulfur.Commands;

import dev.sulfurmc.Sulfur.Sulfur;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.command.builder.Command;

public class ReloadCommand extends Command {

    public ReloadCommand() {

        super("reload", "rl");

        setDefaultExecutor((sender, _) -> {

            Sulfur.registerPlugins();

            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    Sulfur.conf.get("messages").getAsJsonObject().get("reloaded").getAsString()
            ));

        });

    }

}