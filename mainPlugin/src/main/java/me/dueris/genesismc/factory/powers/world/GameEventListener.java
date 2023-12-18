package me.dueris.genesismc.factory.powers.world;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.GenericGameEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class GameEventListener extends CraftPower implements Listener {
    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void event(GenericGameEvent e){
        if(e.getEntity() == null) return;
        if(e.getEntity() instanceof Player p){
            if(!this.getPowerArray().contains(p)) return;
            for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                for(PowerContainer power : origin.getPowerContainers()){
                    if(GenesisMC.getConditionExecutor().check("condition", "conditions", p, power, getPowerFile(), p, null, p.getLocation().getBlock(), null, p.getActiveItem(), null)){
                        String event = power.get("event");
                        if(event.contains(":")){
                            event = event.split(":")[1];
                        }
                        if(e.getEvent().toString().equals(event)){
                            Actions.EntityActionType(e.getEntity(), power.getAction("entity_action"));
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:game_event_listener";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return game_event_listener;
    }

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }
}