package me.dueris.genesismc.core.factory.powers.entity;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.Powers.no_shield;
import static org.bukkit.Material.SHIELD;

public class NoShield extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (no_shield.contains(p)) {
                p.setCooldown(SHIELD, 120);
            }
        }
    }
}
