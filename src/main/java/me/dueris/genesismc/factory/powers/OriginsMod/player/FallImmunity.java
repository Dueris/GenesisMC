package me.dueris.genesismc.factory.powers.OriginsMod.player;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;

public class FallImmunity extends CraftPower implements Listener {

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
    public void acrobatics(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        if (fall_immunity.contains(p)) {
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                if (conditionExecutor.check("condition", "conditions", p, origin, "origins:fall_immunity", e, p)) {
                    setActive(true);
                    if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                        e.setCancelled(true);
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
        return "origins:fall_immunity";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return fall_immunity;
    }
}
