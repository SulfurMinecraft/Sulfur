package hu.jgj52.Sulfur.Listeners;

import hu.jgj52.Sulfur.Permissions.User;
import hu.jgj52.Sulfur.Utils.Listeners.Event;
import hu.jgj52.Sulfur.Utils.Listeners.Listener;
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
