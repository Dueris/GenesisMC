package me.dueris.genesismc.core.choosing;

import me.dueris.genesismc.api.events.choose.contents.EXPMenuContents;
import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ChoosingMenuEXP implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void EXPCHOOSE_MENU(InventoryClickEvent e){
        if(e.getCurrentItem() != null){
            if(e.getView().getTitle().equalsIgnoreCase("Choosing Menu")) {
                PersistentDataContainer data = e.getWhoClicked().getPersistentDataContainer();
                int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
                Random random = new Random();
                @NotNull Inventory expmenu = Bukkit.createInventory(e.getWhoClicked(), 54, "Expanded Origins");
                if (e.getCurrentItem().getType().equals(Material.MUSIC_DISC_OTHERSIDE)) {
                    expmenu.setContents(EXPMenuContents.EXPContents());
                    e.getWhoClicked().openInventory(expmenu);

                }
            }
        }
    }

}
