package me.dueris.genesismc.core.factory.powers.food;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Random;

import static me.dueris.genesismc.core.factory.powers.Powers.more_exhaustion;

public class MoreExhaustion implements Listener {

    @EventHandler
    public void onSprint(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (more_exhaustion.contains(p)) {
            if (p.isSprinting() && !p.getGameMode().equals(GameMode.CREATIVE) && !p.getGameMode().equals(GameMode.SPECTATOR)) {
                Random random = new Random();
                int r = random.nextInt(650);
                if (!p.isSwimming()) {
                    if (r < 10) {
                        int foodamt = p.getFoodLevel();
                        p.setFoodLevel(foodamt - 1);
                    }
                }
            }
        }
    }

}
