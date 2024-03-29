package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class ActionOverTime extends CraftPower {

    private static HashMap<String /*tag*/, Boolean /*allowed*/> taggedAllowedMap = new HashMap<>();
    private final int ticksE;
    private Long interval;

    public ActionOverTime() {
        this.interval = 1L;
        this.ticksE = 0;
    }

    @Override
    public void run(Player p) {
        if (getPowerArray().contains(p)) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (power == null) continue;

                    interval = power.getLongOrDefault("interval", 20L);
                    if (Bukkit.getServer().getCurrentTick() % interval != 0) {
                        return;
                    } else {
                        taggedAllowedMap.putIfAbsent(power.getTag(), false);
                        if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p)) {
                            if (!taggedAllowedMap.get(power.getTag())) {
                                taggedAllowedMap.put(power.getTag(), true);
                                Actions.EntityActionType(p, power.getAction("rising_action"));
                            }
                            setActive(p, power.getTag(), true);
                            Actions.EntityActionType(p, power.getEntityAction());
                        } else {
                            if (taggedAllowedMap.get(power.getTag())) {
                                taggedAllowedMap.put(power.getTag(), false);
                                Actions.EntityActionType(p, power.getAction("falling_action"));
                            }
                            setActive(p, power.getTag(), false);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:action_over_time";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_ove_time;
    }

}
