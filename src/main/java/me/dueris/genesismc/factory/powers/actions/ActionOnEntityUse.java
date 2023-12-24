package me.dueris.genesismc.factory.powers.actions;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class ActionOnEntityUse extends CraftPower implements Listener {

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void entityRightClickEntity(PlayerInteractEntityEvent e) {
        Player actor = e.getPlayer();
        Entity target = e.getRightClicked();

        if (!(target instanceof Player player)) return;
        if (!getPowerArray().contains(target)) return;

        for (OriginContainer origin : OriginPlayerUtils.getOrigin(player).values()) {
            for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                if(GenesisMC.getConditionExecutor().check("condition", "conditions", player, power, getPowerFile(), actor, target, actor.getLocation().getBlock(), null, actor.getActiveItem(), null)){
                    if(GenesisMC.getConditionExecutor().check("item_condition", "item_conditions", player, power, getPowerFile(), actor, target, actor.getLocation().getBlock(), null, actor.getActiveItem(), null)){
                        if(GenesisMC.getConditionExecutor().check("bientity_condition", "bientity_conditions", player, power, getPowerFile(), actor, target, actor.getLocation().getBlock(), null, actor.getActiveItem(), null)){
                            if (power == null) continue;
                            setActive(e.getPlayer(), power.getTag(), true);
                            Actions.biEntityActionType(actor, target, power.getBiEntityAction());
                            Actions.ItemActionType(actor.getActiveItem(), power.getAction("held_item_action"));
                            Actions.ItemActionType(actor.getActiveItem(), power.getAction("result_item_action"));
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
        return "origins:action_on_entity_use";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_on_entity_use;
    }

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if(powers_active.containsKey(p)){
            if(powers_active.get(p).containsKey(tag)){
                powers_active.get(p).replace(tag, bool);
            }else{
                powers_active.get(p).put(tag, bool);
            }
        }else{
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }
}