package me.dueris.genesismc.factory.powers.OriginsMod.value_modifying;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExhaustionEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.OriginsMod.player.attributes.AttributeHandler.getOperationMappingsFloat;
import static me.dueris.genesismc.factory.powers.OriginsMod.value_modifying.ValueModifyingSuperClass.modify_exhaustion;

public class ModifyExhaustionPower extends CraftPower implements Listener {

    @Override
    public void setActive(Boolean bool){
        if(powers_active.containsKey(getPowerFile())){
            powers_active.replace(getPowerFile(), bool);
        }else{
            powers_active.put(getPowerFile(), bool);
        }
    }

    @Override
    public Boolean getActive(){
        return powers_active.get(getPowerFile());
    }

    @EventHandler
    public void run(EntityExhaustionEvent e){
        Player p = (Player) e.getEntity();
        if(modify_exhaustion.contains(p)){
            for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                if(conditionExecutor.check("condition", "conditions", p, origin, "origins:modify_exhaustion", null, p)){
                    for(HashMap<String, Object> modifier : origin.getPowerFileFromType("origins:modify_exhaustion").getConditionFromString("modifier", "modifiers")){
                        Float value = Float.valueOf(modifier.get("value").toString());
                        String operation = modifier.get("operation").toString();
                        BinaryOperator mathOperator = getOperationMappingsFloat().get(operation);
                        if (mathOperator != null) {
                            float result = (float) mathOperator.apply(e.getExhaustion(), value);
                            e.setExhaustion(result);
                            setActive(true);
                        }
                    }

                }else{
                    setActive(false);
                }
            }
        }
    }

    @Override
    public void run() {

    }

    @Override
    public String getPowerFile() {
        return "origins:modify_exhaustion";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_exhaustion;
    }
}
