package me.dueris.genesismc.core.factory.powers.effects;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.Powers.tailwind;

public class TailWind extends BukkitRunnable {

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (tailwind.contains(p)) {
                p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.17F);
            }
        }
    }
}
