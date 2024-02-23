package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.dueris.genesismc.factory.powers.apoli.StackingStatusEffect.getPotionEffectType;

public class EffectImmunity extends CraftPower {

    public EffectImmunity() {

    }

    @Override
    public void run(Player p) {
        if (effect_immunity.contains(p)) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (conditionExecutor.check("condition", "conditions", p, power, getPowerFile(), p, null, null, null, p.getItemInHand(), null)) {
                        setActive(p, power.getTag(), true);
                        List<String> effects = new ArrayList<>();
                        if(power.getStringOrDefault("effect", null) != null){
                            effects.add(power.getString("effect"));
                        }
                        if(!power.getStringList("effects").isEmpty()){
                            effects.addAll(power.getStringList("effects"));
                        }
                        if(!effects.isEmpty()){
                            for (String effectString : effects) {
                                PotionEffectType effectType = getPotionEffectType(effectString);
                                if (effectType != null) {
                                    if (p.hasPotionEffect(effectType)) {
                                        p.removePotionEffect(effectType);
                                    }
                                }
                            }
                        }
                    } else {
                        setActive(p, power.getTag(), false);
                    }
                }

            }
        }
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


    @Override
    public String getPowerFile() {
        return "apoli:effect_immunity";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return effect_immunity;
    }
}
