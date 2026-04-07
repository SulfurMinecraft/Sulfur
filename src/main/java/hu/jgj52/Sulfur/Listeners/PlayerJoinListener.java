package hu.jgj52.Sulfur.Listeners;

import hu.jgj52.Sulfur.Sulfur;
import hu.jgj52.Sulfur.Utils.Listeners.Event;
import hu.jgj52.Sulfur.Utils.Listeners.Listener;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;

public class PlayerJoinListener extends Listener {
    @Event
    public void onJoin(AsyncPlayerConfigurationEvent event) {
        event.setSpawningInstance(Sulfur.ic);

        event.getSpawningInstance().loadChunk(new Pos(0.5, 0, 0.5));

        event.getPlayer().setRespawnPoint(Sulfur.getHighestPoint(new Pos(0.5, 0, 0.5), event.getSpawningInstance()));
    }
}
