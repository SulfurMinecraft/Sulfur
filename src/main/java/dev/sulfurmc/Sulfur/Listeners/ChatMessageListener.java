package dev.sulfurmc.Sulfur.Listeners;

import dev.sulfurmc.Sulfur.Utils.Listeners.Event;
import dev.sulfurmc.Sulfur.Utils.Listeners.Listener;
import dev.sulfurmc.Sulfur.Utils.Logger;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerChatEvent;

public class ChatMessageListener extends Listener {

    @Event
    public void onChatMessage(PlayerChatEvent event) {

        Logger logger = new Logger().setPrefix("CHAT");
        Player player = event.getPlayer();
        String message = event.getRawMessage();

        logger.info("<" + player.getUsername() + "> " + message);

    }

}
