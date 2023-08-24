package me.dueris.genesismc.factory.powers.OriginsMod.player;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.events.OriginChangeEvent;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.HashMap;

public class StartingEquipmentPower extends CraftPower implements Listener {

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

    @Override
    public void run() {

    }

    @EventHandler
    public void runGive(OriginChangeEvent e){
        if(starting_equip.contains(e.getPlayer())){
            for(OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()){
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                if(conditionExecutor.check("condition", "conditions", e.getPlayer(), origin, getPowerFile(), null, e.getPlayer())) {
                    setActive(true);
                    runGiveItems(e.getPlayer(), origin);
                }else{
                    setActive(false);
                }
            }
        }
    }

    public void runGiveItems(Player p, OriginContainer origin){
        for(HashMap<String, Object> stack : origin.getPowerFileFromType(getPowerFile()).getSingularAndPlural("stack", "stacks")){
            p.getInventory().addItem(new ItemStack(Material.valueOf(stack.get("item").toString().toUpperCase().split(":")[1]), Integer.valueOf(origin.getPowerFileFromType(getPowerFile()).get("amount", "1"))));
        }
    }

    @EventHandler
    public void runRespawn(PlayerRespawnEvent e){
        if(starting_equip.contains(e.getPlayer())){
            for(OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()){
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                if(conditionExecutor.check("condition", "conditions", e.getPlayer(), origin, getPowerFile(), null, e.getPlayer())) {
                    setActive(true);
                    if(origin.getPowerFileFromType(getPowerFile()).get("recurrent", "false") != null){
                        if(origin.getPowerFileFromType(getPowerFile()).get("recurrent") == "true"){
                            runGiveItems(e.getPlayer(), origin);
                        }
                    }
                }else{
                    setActive(false);
                }

            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:starting_equipment";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return starting_equip;
    }
}
