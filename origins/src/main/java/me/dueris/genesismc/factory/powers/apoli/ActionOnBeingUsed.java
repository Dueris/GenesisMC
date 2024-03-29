package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class ActionOnBeingUsed extends CraftPower implements Listener {

    @EventHandler
    public void entityRightClickEntity(PlayerInteractEntityEvent e) {
        Player actor = e.getPlayer();
        Entity target = e.getRightClicked();
        if (!(target instanceof Player player)) return;
        if (!getPowerArray().contains(player)) return;

        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(player, getPowerFile(), layer)) {
                if (!(ConditionExecutor.testEntity((JSONObject) power.get("condition"), (CraftEntity) e.getPlayer()) && ConditionExecutor.testBiEntity(power.get("bientity_condition"), (CraftEntity) actor, (CraftEntity) target) && ConditionExecutor.testItem(power.get("item_condition"), actor.getInventory().getItem(e.getHand()))))
                    return;

                setActive(player, power.getTag(), true);
                Actions.ItemActionType(actor.getInventory().getItem(e.getHand()), power.getAction("held_item_action"));
                if (power.get("result_stack") != null) {
                    JSONObject jsonObject = power.get("result_stack");
                    int amt;
                    if (jsonObject.get("amount").toString() != null) {
                        amt = Integer.parseInt(jsonObject.get("amount").toString());
                    } else {
                        amt = 1;
                    }
                    ItemStack itemStack = new ItemStack(Material.valueOf(jsonObject.get("item").toString().toUpperCase().split(":")[jsonObject.get("item").toString().split(":").length]), amt);
                    actor.getInventory().addItem(itemStack);
                    Actions.ItemActionType(itemStack, power.getAction("result_item_action"));
                }
                Actions.BiEntityActionType(actor, target, power.getBiEntityAction());
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
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "apoli:action_on_being_used";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_on_being_used;
    }
}
