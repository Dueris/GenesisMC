package me.dueris.genesismc.factory.powers.block;

import me.dueris.genesismc.factory.powers.Power;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class AirFromPotions implements Listener {
    @EventHandler
    public void OnDrink(PlayerItemConsumeEvent e) {
        if (Power.water_breathing.contains(e.getPlayer())) {
            if (e.getItem().getType().equals(Material.POTION)) {
                if (e.getPlayer().getRemainingAir() > 250) {
                    e.getPlayer().setRemainingAir(300);
                } else {
                    e.getPlayer().setRemainingAir(e.getPlayer().getRemainingAir() + 60);
                }
            }
        }
    }
}
