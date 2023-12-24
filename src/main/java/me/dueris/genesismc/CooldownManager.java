package me.dueris.genesismc;

import me.dueris.genesismc.events.OriginChangeEvent;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static me.dueris.genesismc.factory.powers.player.FireProjectile.in_cooldown_patch;

public class CooldownManager implements @NotNull Listener {

    @EventHandler
    public void runs(OriginChangeEvent e) {
        cooldowns.remove(e.getPlayer());
        cooldownBars.remove(e.getPlayer());
    }

    public static HashMap<Player, ArrayList<String>> cooldowns = new HashMap<>();
    public static HashMap<Player, BossBar> cooldownBars = new HashMap<>();

    public static void addCooldown(Player player, OriginContainer origin, String title, String dont_use, int cooldownTicks, String cooldownKeybindType) {
        if (!in_cooldown_patch.contains(player) && dont_use.equals("origins:fire_projectile")) return; // this fixes a weird bug with the fire projectile power
        if (isPlayerInCooldown(player, cooldownKeybindType)) {
            resetCooldown(player, cooldownKeybindType);
        }
        if(cooldownTicks == 0) return;
        BossBar bar = createCooldownBar(player, BarColor.WHITE, getCooldownPegAMT(cooldownTicks), title);
        bar.addPlayer(player);
        startTickingCooldown(bar, player, cooldownTicks, cooldownKeybindType);
        cooldownBars.put(player, bar);
        if(!cooldowns.containsKey(player) || cooldowns.get(player).isEmpty()){
            ArrayList<String> list = new ArrayList<>();
            list.add(cooldownKeybindType);
            cooldowns.put(player, list);
        }else{
            cooldowns.get(player).add(cooldownKeybindType);
        }
    }

    public static BarStyle getCooldownPegAMT(int ticks) {
       return BarStyle.SOLID;
    }

    public static boolean isPlayerInCooldown(Player player, String cooldownKeybindType) {
        return cooldowns.containsKey(player) && cooldowns.get(player).contains(cooldownKeybindType)
                && cooldownBars.containsKey(player);
    }

    public static void resetCooldown(Player player, String cooldownKeybindType) {
        if (isPlayerInCooldown(player, cooldownKeybindType)) {
            BossBar bar = cooldownBars.get(player);
            bar.removePlayer(player);
            cooldownBars.remove(player);
            cooldowns.get(player).remove(cooldownKeybindType);
        }
    }

    public static boolean isPlayerInCooldownFromTag(Player player, String tag) {
        for (@NotNull Iterator<KeyedBossBar> it = Bukkit.getBossBars(); it.hasNext(); ) {
            KeyedBossBar active = it.next();
            if (active.getTitle().equalsIgnoreCase(tag)) {
                if (active.getPlayers().contains(player)) return true;
            }
        }
        return false;
    }

    public static BossBar createCooldownBar(Player player, BarColor color, BarStyle style, String title) {
        BossBar bossBar = Bukkit.createBossBar(title, color, style);
        bossBar.setProgress(1.0);
        return bossBar;
    }

    public static void startTickingCooldown(BossBar bar, Player player, int cooldownTicks, String cooldownKeybindType) {
        final double decreasePerTick = 1.0 / cooldownTicks;

        new BukkitRunnable() {
            int ticksElapsed = -1;

            @Override
            public void run() {
                try {
                    ticksElapsed++;
                    double progress = 1.0 - (ticksElapsed * decreasePerTick);
                    bar.setProgress(progress);

                    int remainingTicks = cooldownTicks - ticksElapsed;

                    if (ticksElapsed >= cooldownTicks || cooldownTicks == 0) {
                        resetCooldown(player, cooldownKeybindType);
                        bar.setProgress(0);
                        bar.setVisible(false);
                        cancel();
                    }
                } catch (Exception e) {
                    //fail silent
                }

            }
        }.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
    }

}