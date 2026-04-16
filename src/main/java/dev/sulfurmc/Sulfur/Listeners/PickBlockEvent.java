package dev.sulfurmc.Sulfur.Listeners;

import dev.sulfurmc.Sulfur.Utils.Listeners.Event;
import dev.sulfurmc.Sulfur.Utils.Listeners.Listener;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerPickBlockEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class PickBlockEvent extends Listener {

    @Event
    public void onPick(PlayerPickBlockEvent event) {

        Player player = event.getPlayer();
        ItemStack prevItem = player.getItemInMainHand();

        if (!player.getGameMode().equals(GameMode.CREATIVE)) return;

        Material material = Material.fromKey(event.getBlock().name());

        if (player.getInventory().getItemStack(player.getHeldSlot()).material() == material) return;

        ItemStack item = ItemStack.of(material);
        boolean inHotbar = false;
        int slot = 0;

        for (int i = 0; i < player.getInventory().getSize(); i++) {

            ItemStack itemStack = player.getInventory().getItemStack(i);

            if (itemStack.material().equals(material)) {

                if (i <= 8) {

                    inHotbar = true;
                    slot = i;

                } else {

                    item = itemStack;
                    player.getInventory().setItemStack(i, ItemStack.of(Material.AIR));
                    break;

                }

            }

        }

        if (!inHotbar) {

            boolean isHotbarFull = true;
            int lastHotbarSlot = 0;

            for (int i = 0; i < player.getInventory().getSize(); i++) {

                if (i <= 8) {

                    if (player.getInventory().getItemStack(i).material() == Material.AIR) {

                        isHotbarFull = false;
                        lastHotbarSlot = i;

                        break;

                    }

                }

            }

            if (isHotbarFull) {

                player.getInventory().setItemStack(player.getHeldSlot(), item);
                player.getInventory().addItemStack(prevItem);

            } else {

                player.getInventory().addItemStack(item);
                player.setHeldItemSlot((byte) lastHotbarSlot);

            }

        } else {

            player.setHeldItemSlot((byte) slot);

        }

    }

}
