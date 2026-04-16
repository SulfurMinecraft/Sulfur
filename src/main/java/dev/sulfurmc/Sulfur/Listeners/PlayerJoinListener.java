package dev.sulfurmc.Sulfur.Listeners;

import dev.sulfurmc.Sulfur.Sulfur;
import dev.sulfurmc.Sulfur.Utils.Listeners.Event;
import dev.sulfurmc.Sulfur.Utils.Listeners.Listener;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

public class PlayerJoinListener extends Listener {
    @Event
    public void onJoin(AsyncPlayerConfigurationEvent event) {
        event.setSpawningInstance(Sulfur.ic);

        event.getPlayer().setRespawnPoint(getHighestPoint(new Pos(0, 0, 0), event.getSpawningInstance()).add(0.5d, 0.0d, 0.5d));
    }

    private static Pos getHighestPoint(Pos initialPosition, Instance instance) {

        int highest = 319;

        try {

            instance.loadChunk(initialPosition).get();

        } catch (Exception e) {

            throw new RuntimeException(e);

        }

        for (int y = 319; y > -64; y--) {

            BlockVec block = new BlockVec(
                    initialPosition.blockX(),
                    Math.min(319, y),
                    initialPosition.blockZ()
            );

            if (instance.getBlock(block) != Block.AIR) {

                highest = y;
                break;

            }

        }

        return new Pos(
                initialPosition.blockX(),
                Math.min(319, Math.max(-64, highest + 1)),
                initialPosition.blockZ()
        );

    }

}
