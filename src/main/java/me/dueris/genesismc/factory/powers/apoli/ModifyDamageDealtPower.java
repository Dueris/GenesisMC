package me.dueris.genesismc.factory.powers.apoli;

import com.google.gson.JsonSyntaxException;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.apoli.AttributeHandler.*;
import static me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass.modify_damage_dealt;

public class ModifyDamageDealtPower extends CraftPower implements Listener {

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

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void damageEVENT(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player p && modify_damage_dealt.contains(p)) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                try {
                    ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                    for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                        if (conditionExecutor.check("bientity_condition", "bientity_condition", p, power, "apoli:modify_damage_dealt", p, e.getEntity(), p.getLocation().getBlock(), null, p.getItemInHand(), e)) {
                            if (conditionExecutor.check("condition", "condition", p, power, "apoli:modify_damage_dealt", p, e.getEntity(), p.getLocation().getBlock(), null, p.getItemInHand(), e)) {
                                if (conditionExecutor.check("item_condition", "item_condition", p, power, "apoli:modify_damage_dealt", p, e.getEntity(), p.getLocation().getBlock(), null, p.getItemInHand(), e)) {
                                    for (HashMap<String, Object> modifier : power.getJsonListSingularPlural("modifier", "modifiers")) {
                                        Object value = modifier.get("value");
                                        String operation = modifier.get("operation").toString();
                                        runSetDMG(e, operation, value);
                                        setActive(p, power.getTag(), true);
                                    }
                                }
                            }
                        } else {
                            setActive(p, power.getTag(), false);
                        }
                    }
                } catch (Exception ev) {
                    // throw new RuntimeException(); // urm why?
                }
            }
        }
    }

    public void runSetDMG(EntityDamageByEntityEvent e, String operation, Object value) {
        double damage = e.getDamage();

        if (value instanceof Double) {
            BinaryOperator<Double> doubleOperator = getOperationMappingsDouble().get(operation);
            if (doubleOperator != null) {
                double newDamage = doubleOperator.apply(damage, (Double) value);
                e.setDamage(newDamage);
            }
        } else if (value instanceof Long) {
            BinaryOperator<Long> longOperator = getOperationMappingsLong().get(operation);
            if (longOperator != null) {
                long newDamage = longOperator.apply((long) damage, (Long) value);
                e.setDamage(newDamage);
            }
        } else if (value instanceof Integer) {
            BinaryOperator<Integer> intOperator = getOperationMappingsInteger().get(operation);
            if (intOperator != null) {
                int newDamage = intOperator.apply((int) damage, (Integer) value);
                e.setDamage(newDamage);
            }
        } else if (value instanceof Float) {
            BinaryOperator<Float> floatOperator = getOperationMappingsFloat().get(operation);
            if (floatOperator != null) {
                float newDamage = floatOperator.apply((float) damage, (Float) value);
                e.setDamage(newDamage);
            }
        } else {
            throw new JsonSyntaxException("Unsupported number type: " + value.getClass());
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:modify_damage_dealt";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_damage_dealt;
    }
}
