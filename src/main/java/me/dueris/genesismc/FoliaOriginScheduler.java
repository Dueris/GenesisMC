package me.dueris.genesismc;

import com.github.Anon8281.universalScheduler.bukkitScheduler.BukkitScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import com.github.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.FlightHandler;
import me.dueris.genesismc.factory.powers.Overlay;
import me.dueris.genesismc.factory.powers.actions.ActionOnItemUse;
import me.dueris.genesismc.factory.powers.actions.ActionOverTime;
import me.dueris.genesismc.factory.powers.player.Gravity;
import me.dueris.genesismc.factory.powers.player.damage.Burn;
import me.dueris.genesismc.files.GenesisDataFiles;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftAbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;

public class FoliaOriginScheduler {

    final Plugin plugin;

    public FoliaOriginScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    public static TaskScheduler getGlobalScheduler() {return GenesisMC.getGlobalScheduler();}

    public BukkitTask runTask(BukkitRunnable runnable) {
        runnables.add(runnable);
        return runnable.runTask(plugin);
    }

    private long getOneIfNotPositive(long x) {
        return x <= 0 ? 1L : x;
    }

    public BukkitTask runTaskLater(BukkitRunnable runnable, long delay) {
        runnables.add(runnable);
        delay = getOneIfNotPositive(delay);
        if (delay <= 0) {
            return runTask(runnable);
        }
        return runnable.runTaskLater(plugin, delay);
    }

    ArrayList<BukkitRunnable> runnables = new ArrayList<>();

    public ArrayList<BukkitRunnable> getRunnables() {
        return runnables;
    }

    public BukkitTask runTaskTimer(BukkitRunnable runnable, long delay, long period) {
        runnables.add(runnable);
        delay = getOneIfNotPositive(delay);
        return runnable.runTaskTimer(GenesisMC.getPlugin(), delay, period);
    }

    public static class OriginSchedulerTree extends BukkitRunnable {

        private HashMap<Player, Integer> ticksEMap = new HashMap<>();

        @Override
        public void run() {
            for(Player p : OriginPlayer.hasPowers){
                if(!OriginPlayer.getPowersApplied(p).contains(Gravity.class)){
                    Gravity gravity = new Gravity();
                    gravity.run(p);
                } else if(!OriginPlayer.getPowersApplied(p).contains(FlightHandler.class)){
                    FlightHandler flightHandler = new FlightHandler();
                    flightHandler.run(p);
                } else if(!OriginPlayer.getPowersApplied(p).contains(Overlay.class)){
                    Overlay overlay = new Overlay();
                    overlay.run(p);
                }
                if(OriginPlayer.getPowersApplied(p).isEmpty()) {
                    p.sendMessage("BSDFPR");
                }
                for(Class<? extends CraftPower> c : OriginPlayer.getPowersApplied(p)){
                    try {
                        if (c.newInstance() instanceof Burn) {
                            ((Burn) c.newInstance()).run(p, ticksEMap);
                        } else if (c.newInstance() instanceof ActionOverTime){
                            ((ActionOverTime) c.newInstance()).run(p, ticksEMap);
                        } else {
                             c.newInstance().run(p);
                        }
                    } catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

}