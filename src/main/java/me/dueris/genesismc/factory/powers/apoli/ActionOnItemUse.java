package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.LayerContainer;
import me.dueris.genesismc.registry.PowerContainer;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class ActionOnItemUse extends CraftPower implements Listener {

    public ActionOnItemUse() {

    }

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void entityRightClick(PlayerInteractEvent e) {
        Player player = e.getPlayer(); // aka "actor"
        if (!getPowerArray().contains(player)) return;
        if (!e.getAction().isRightClick()) return;

        for (LayerContainer layer : CraftApoli.getLayers()) {
            for (PowerContainer power : OriginPlayerAccessor.getMultiPowerFileFromType(player, getPowerFile(), layer)) {
                if (power == null) continue;
                if (GenesisMC.getConditionExecutor().check("condition", "conditions", player, power, getPowerFile(), player, null, player.getLocation().getBlock(), null, e.getItem(), null)) {
                    if (GenesisMC.getConditionExecutor().check("item_condition", "item_conditions", player, power, getPowerFile(), player, null, player.getLocation().getBlock(), null, e.getItem(), null)) {
                        if (GenesisMC.getConditionExecutor().check("entity_condition", "entity_conditions", player, power, getPowerFile(), player, null, player.getLocation().getBlock(), null, e.getItem(), null)) {
                            setActive(e.getPlayer(), power.getTag(), true);
                            Actions.ItemActionType(e.getItem(), power.getAction("item_action"));
                            Actions.EntityActionType(player, power.getAction("entity_action"));
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    setActive(e.getPlayer(), power.getTag(), false);
                                }
                            }.runTaskLater(GenesisMC.getPlugin(), 2L);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:action_on_item_use";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_on_item_use;
    }

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if (powers_active.containsKey(p)) {
            if (powers_active.get(p).containsKey(tag)) {
                powers_active.get(p).replace(tag, bool);
            } else {
                powers_active.get(p).put(tag, bool);
            }
        } else {
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }
}