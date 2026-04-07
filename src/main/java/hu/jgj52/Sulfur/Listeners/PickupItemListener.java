package hu.jgj52.Sulfur.Listeners;

import hu.jgj52.Sulfur.Utils.Listeners.Event;
import hu.jgj52.Sulfur.Utils.Listeners.Listener;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.item.PickupItemEvent;

public class PickupItemListener extends Listener {
    @Event
    public void onPickup(PickupItemEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;

        player.getInventory().addItemStack(event.getItemStack());
    }
}
