package dev.sulfurmc.Sulfur.Commands;

import dev.sulfurmc.Sulfur.Sulfur;
import dev.sulfurmc.Sulfur.Utils.SulfurCommand;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class ReloadCommand extends SulfurCommand {

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