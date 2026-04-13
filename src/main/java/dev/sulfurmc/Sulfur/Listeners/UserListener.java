package dev.sulfurmc.Sulfur.Listeners;

import dev.sulfurmc.Sulfur.Permissions.User;
import dev.sulfurmc.Sulfur.Utils.Listeners.Event;
import dev.sulfurmc.Sulfur.Utils.Listeners.Listener;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;

public class UserListener extends Listener {
    @Event
    public void onJoin(PlayerSpawnEvent event) {
        if (!event.isFirstSpawn()) return;

        User.load(event.getPlayer().getUuid());
    }

    @Event
    public void onLeave(PlayerDisconnectEvent event) {
        User.unload(event.getPlayer().getUuid());
    }
}
