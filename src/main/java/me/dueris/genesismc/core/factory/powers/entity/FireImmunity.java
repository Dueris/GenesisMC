package me.dueris.genesismc.core.factory.powers.entity;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import static me.dueris.genesismc.core.factory.powers.Powers.fire_immunity;

public class FireImmunity implements Listener {

    @EventHandler
    public void OnDamageFire(EntityDamageEvent e) {
        if (e.getEntity().isDead()) return;
        if (e.getEntity() == null) return;
        if (e.getEntity() instanceof Player p) {
            if (fire_immunity.contains(p)) {
                if (e.getCause().equals(EntityDamageEvent.DamageCause.FIRE) || e.getCause().equals(EntityDamageEvent.DamageCause.HOT_FLOOR) || e.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK) || e.getCause().equals(EntityDamageEvent.DamageCause.LAVA)) {
                    e.setCancelled(true);
                    e.setDamage(0);
                }
            }
        }
    }

}
