package hu.jgj52.Sulfur.Listeners;

import hu.jgj52.Sulfur.Sulfur;
import hu.jgj52.Sulfur.Utils.Listeners.Event;
import hu.jgj52.Sulfur.Utils.Listeners.Listener;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

public class PlayerJoinListener extends Listener {
    @Event
    public void onJoin(AsyncPlayerConfigurationEvent event) {
        event.setSpawningInstance(Sulfur.ic);

        event.getPlayer().setRespawnPoint(getHighestPoint(new Pos(0.5, 0, 0.5), event.getSpawningInstance()));
    }

    public static Pos getHighestPoint(Pos initialPosition, Instance instance) {

        int highest = 319;

        for (int y = 319; y > -64; y--) {

            BlockVec block = new BlockVec(
                    initialPosition.blockX(),
                    y,
                    initialPosition.blockZ()
            );

            if (instance.getBlock(block) != Block.AIR) {

                highest = y;
                break;

            }

        }

        return new Pos(
                initialPosition.blockX(),
                highest,
                initialPosition.blockZ()
        );

    }

}
