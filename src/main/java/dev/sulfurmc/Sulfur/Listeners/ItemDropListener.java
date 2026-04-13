package dev.sulfurmc.Sulfur.Listeners;

import dev.sulfurmc.Sulfur.Utils.Listeners.Event;
import dev.sulfurmc.Sulfur.Utils.Listeners.Listener;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.event.item.ItemDropEvent;

import java.time.Duration;
import java.util.Random;

public class ItemDropListener extends Listener {
    @Event
    public void onItemDrop(ItemDropEvent event) {
        Random r = new Random();

        ItemEntity itemEntity = new ItemEntity(event.getItemStack());
        itemEntity.setInstance(event.getInstance(), event.getPlayer().getPosition().add(0, 1.25, 0));
        itemEntity.setVelocity(event.getPlayer().getPosition().direction().add(r.nextFloat() / 5 - 0.1f, 0.25, r.nextFloat() / 5 - 0.1f).mul(5));
        itemEntity.setPickupDelay(Duration.ofMillis(1000));
    }
}
