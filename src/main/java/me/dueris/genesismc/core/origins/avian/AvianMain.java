package me.dueris.genesismc.core.origins.avian;

import me.dueris.api.entity.OriginPlayer;
import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

import static org.bukkit.Material.*;

public class AvianMain implements Listener {

    public static EnumSet<Material> inedibleFoodAvian;
    public static EnumSet<Material> beds;
    static {
        inedibleFoodAvian = EnumSet.of(COOKED_BEEF, COOKED_CHICKEN, COOKED_MUTTON, COOKED_COD, COOKED_PORKCHOP, COOKED_RABBIT, COOKED_SALMON,
                BEEF, CHICKEN, MUTTON, COD, PORKCHOP, RABBIT, SALMON, TROPICAL_FISH, PUFFERFISH, RABBIT_STEW);
        beds = EnumSet.of(WHITE_BED, LIGHT_GRAY_BED, GRAY_BED, BLACK_BED, BROWN_BED, RED_BED, ORANGE_BED, YELLOW_BED, LIME_BED, GREEN_BED,
                CYAN_BED,LIGHT_BLUE_BED, BLUE_BED, PURPLE_BED, MAGENTA_BED, PINK_BED);
    }

    @EventHandler
    public void onBedLeave(PlayerBedLeaveEvent e) {
        PersistentDataContainer data = e.getPlayer().getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (origintag.equalsIgnoreCase("genesis:origin-avian")) {
            Player p = e.getPlayer();
            long time = Bukkit.getServer().getWorld(p.getWorld().getName()).getTime();

            if (time == 0) {
                p.getWorld().dropItemNaturally(p.getLocation(), new ItemStack(EGG));
                OriginPlayer pls = (OriginPlayer) e.getPlayer();

            }
        }
    }

    @EventHandler
    public void onItemConsume(PlayerInteractEvent e) {
        PersistentDataContainer data = e.getPlayer().getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (origintag.equalsIgnoreCase("genesis:origin-avian")) {
            ItemStack item = e.getItem();
            if (item == null) return;
            for (Material food : inedibleFoodAvian) {
                if (item.getType() == food) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent e) {
        PersistentDataContainer data = e.getPlayer().getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (origintag.equalsIgnoreCase("genesis:origin-avian")) {
            if (e.getPlayer().getWorld().getEnvironment() == World.Environment.NORMAL) {
                if (e.getBed().getY() <= 99) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        PersistentDataContainer data = e.getPlayer().getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (origintag.equalsIgnoreCase("genesis:origin-avian")) {
            Player p = e.getPlayer();
            if (p.getWorld().getEnvironment() == World.Environment.NORMAL) {
                Block block = e.getClickedBlock();
                for (Material bed : beds) {
                    if (e.getClickedBlock() != null) {  //added null check in dev
                        if (block.getType() == bed) {
                            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                if (block.getY() <= 99) {
                                    e.setCancelled(true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}


