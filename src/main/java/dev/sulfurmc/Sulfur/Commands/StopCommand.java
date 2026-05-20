package dev.sulfurmc.Sulfur.Commands;

import dev.sulfurmc.Sulfur.Sulfur;
import dev.sulfurmc.Sulfur.Utils.SulfurCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;

public class StopCommand extends SulfurCommand {
    public StopCommand() {
        super("stop");

        setDefaultExecutor((sender, _) -> {

            for (Player players : MinecraftServer.getConnectionManager().getOnlinePlayers()) {

                String message = Sulfur.conf.get("server").getAsJsonObject().get("shutdownKick").getAsString();

                Component text = MiniMessage.miniMessage().deserialize(message);

                players.kick(text);

            }

            MinecraftServer.stopCleanly();

        });
    }
}