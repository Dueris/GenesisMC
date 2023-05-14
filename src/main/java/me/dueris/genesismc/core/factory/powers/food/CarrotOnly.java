package me.dueris.genesismc.core.factory.powers.food;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static me.dueris.genesismc.core.factory.powers.Powers.carrot_only;

public class CarrotOnly implements Listener {
    @EventHandler
    public void onItemConsume(PlayerInteractEvent e) {
        PersistentDataContainer data = e.getPlayer().getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (carrot_only.contains(origintag)) {
            @NotNull ItemStack item = e.getItem();

            if (item == null) return;;
            if (!item.getType().isEdible()) return;

            if (!(item.getType() == Material.CARROT || item.getType() == Material.GOLDEN_CARROT)) {
                e.setCancelled(true);
            }

        }
    }
}