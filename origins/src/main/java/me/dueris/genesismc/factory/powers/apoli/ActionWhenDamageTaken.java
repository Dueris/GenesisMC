package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ActionWhenDamageTaken extends CraftPower implements Listener {

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void d(EntityDamageEvent e) {
        if (e.getDamage() == 0 || e.isCancelled()) return;
        Entity actor = e.getEntity();
        if (!(actor instanceof Player player)) return;
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(player, getPowerFile(), layer)) {
                if (power == null) continue;
                if (!ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) actor)) return;
                if (!ConditionExecutor.testDamage(power.get("damage_condition"), e)) return;
                Actions.EntityActionType(actor, power.getEntityAction());
                Actions.EntityActionType(actor, power.getAction("action"));

                setActive(player, power.getTag(), true);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        setActive(player, power.getTag(), false);
                    }
                }.runTaskLater(GenesisMC.getPlugin(), 2L);
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:action_when_damage_taken";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_when_damage_taken;
    }

}
