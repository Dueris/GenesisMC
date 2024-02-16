package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.LayerContainer;
import me.dueris.genesismc.registry.PowerContainer;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.GenericGameEvent;

import java.util.ArrayList;
import java.util.HashMap;

import static me.dueris.genesismc.factory.powers.apoli.superclass.PreventSuperClass.prevent_game_event;

public class PreventGameEvent extends CraftPower implements Listener {
    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void event(GenericGameEvent e) {
        if (e.getEntity() == null) return;
        if (e.getEntity() instanceof Player p) {
            if (!this.getPowerArray().contains(p)) return;
            for (LayerContainer layer : CraftApoli.getLayers()) {
                for (PowerContainer power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (GenesisMC.getConditionExecutor().check("condition", "conditions", p, power, getPowerFile(), p, null, p.getLocation().getBlock(), null, p.getActiveItem(), null)) {
                        String event = power.getString("event");
                        if (event.contains(":")) {
                            event = event.split(":")[1];
                        }
                        if (e.getEvent().key().asString().equals(event)) {
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:prevent_game_event";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return prevent_game_event;
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