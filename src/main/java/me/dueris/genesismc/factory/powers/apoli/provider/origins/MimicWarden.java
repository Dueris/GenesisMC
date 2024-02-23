package me.dueris.genesismc.factory.powers.apoli.provider.origins;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.KeybindTriggerEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.apoli.provider.PowerProvider;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.util.CooldownUtils;
import me.dueris.genesismc.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class MimicWarden extends CraftPower implements Listener, PowerProvider {
    public static ArrayList<Player> mimicWardenPlayers = new ArrayList<>();
    public static Map<UUID, Integer> particleTasks = new HashMap<>();
    protected static NamespacedKey powerReference = GenesisMC.originIdentifier("mimic_warden");

    public static Map<UUID, Integer> getParticleTasks() {
        return particleTasks;
    }

    @Override
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return null;
    }

    @EventHandler
    public void key(KeybindTriggerEvent e) {
        Player p = e.getPlayer();
        if (mimicWardenPlayers.contains(p)) {
            if (p.getFoodLevel() < 6) return;
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                if (CooldownUtils.isPlayerInCooldown(p, "key.origins.primary_active")) return;
                if (e.getKey().equals("key.origins.primary_active")) {
                    Location eyeLoc = p.getEyeLocation();

                    CooldownUtils.addCooldown(p, "Sonic Boom", "origins:mimic_warden", 1200, "key.origins.primary_active");

                    Location startLocation = p.getEyeLocation();

                    p.getWorld().playSound(p, Sound.ENTITY_WARDEN_SONIC_BOOM, 8, 1);
                    p.getWorld().spawnParticle(Particle.SONIC_BOOM, p.getEyeLocation(), 1);
                    p.getWorld().spawnParticle(Particle.SWEEP_ATTACK, p.getEyeLocation(), 1);

                    int taskId = new BukkitRunnable() {
                        final Location origin = startLocation.clone();
                        int particleCounter = 1;

                        @Override
                        public void run() {
                            double time = particleCounter / 14.0;

                            Location center = startLocation.clone().add(startLocation.getDirection().multiply(particleCounter * 3));
                            for(Entity entity : center.getNearbyEntities(2, 2, 2)){
                                if(entity == p) continue;
                                if(entity instanceof LivingEntity victim){
                                    victim.knockback(0.7, p.getX(), p.getZ());
                                    attack(victim, 14);
                                    victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 2, false, false, false));
                                    victim.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 50, 2, false, false, false));
                                }
                            }

                            Random random = new Random();
                            double x = random.nextDouble(0.5);
                            double y = random.nextDouble(0.5);
                            double z = random.nextDouble(0.5);

                            Location randomLocation = startLocation.add(new Vector(x, y, z).rotateAroundY(random.nextDouble(180)).rotateAroundX(random.nextDouble(180)).rotateAroundZ(random.nextDouble(180)));
                            Location randomLocation1 = startLocation.add(new Vector(x, y, z).rotateAroundY(random.nextDouble(180)).rotateAroundX(random.nextDouble(180)).rotateAroundZ(random.nextDouble(180)));
                            Location randomLocation2 = startLocation.add(new Vector(x, y, z).rotateAroundY(random.nextDouble(180)).rotateAroundX(random.nextDouble(180)).rotateAroundZ(random.nextDouble(180)));
                            Location randomLocation3 = startLocation.add(new Vector(x, y, z).rotateAroundY(random.nextDouble(180)).rotateAroundX(random.nextDouble(180)).rotateAroundZ(random.nextDouble(180)));

                            createSpiralParticleEffect(p, center, time, randomLocation);
                            createSpiralParticleEffect(p, center, time, randomLocation1);
                            createSpiralParticleEffect(p, center, time, randomLocation2);
                            createSpiralParticleEffect(p, center, time, randomLocation3);

                            center.getWorld().spawnParticle(Particle.SONIC_BOOM, center, 1, null);

                            if (center.distance(origin) >= 15.0) {
                                this.cancel();
                                particleTasks.remove(p.getUniqueId());
                            }

                            particleCounter++;
                            if (particleCounter >= 100) {
                                this.cancel();
                                particleTasks.remove(p.getUniqueId());
                            }
                            if(center.getBlock().getType().isCollidable() || center.getBlock().getType().isCollidable() || center.getBlock().getType().isCollidable()) {
                                particleTasks.remove(p.getUniqueId());
                                cancel();
                            }
                        }
                    }.runTaskTimer(GenesisMC.getPlugin(), 0L, 1L).getTaskId();

                    particleTasks.put(p.getUniqueId(), taskId);
                }

            }
        }
    }

    private void attack(LivingEntity entity, int damage){
        String namespace = "origins";
        String key = "sonic_boom";
        DamageType dmgType = Utils.DAMAGE_REGISTRY.get(new ResourceLocation(namespace, key));
        net.minecraft.world.entity.LivingEntity en = ((CraftLivingEntity) entity).getHandle();
        en.hurt(Utils.getDamageSource(dmgType), damage);
    }

    private void createSpiralParticleEffect(Player player, Location center, double time, Location playerDirection) {
        double maxDistance = 15;
        int numParticles = 30;

        double angleIncrement = 3 * Math.PI / numParticles;

        for (int i = 0; i < numParticles; i++) {
            double angle = i * angleIncrement + time;

            double x = maxDistance * Math.cos(angle);
            double y = i * 0.02;
            double z = maxDistance * Math.sin(angle);

            Vector particleDirection = new Vector(x, y, z).multiply(2).normalize();
            Vector rotatedDirection = rotateVector(particleDirection, playerDirection);

            Particle.DustTransition dustTransition = new Particle.DustTransition(Color.fromRGB(19, 109, 242), Color.fromRGB(225, 234, 252), 0.5f);

            player.spawnParticle(Particle.DUST_COLOR_TRANSITION, center.getX() + rotatedDirection.getX(), center.getY() + rotatedDirection.getY(), center.getZ() + rotatedDirection.getZ(), 1, 0, 0, 0, 0, dustTransition);
        }
    }

    private Vector rotateVector(Vector vector, Location rotation) {
        Random random = new Random();
        double x = vector.getX();
        double y = vector.getY();
        double z = vector.getZ();

        double cosYaw = Math.cos(rotation.getYaw() + random.nextDouble(0.5));
        double sinYaw = Math.sin(rotation.getYaw() + random.nextDouble(0.5));
        double cosPitch = Math.cos(rotation.getPitch() + random.nextDouble(0.5));
        double sinPitch = Math.sin(rotation.getPitch() + random.nextDouble(0.5));

        double newX = x * cosYaw - z * sinYaw;
        double newY = x * sinPitch * sinYaw + y * cosPitch - z * sinPitch * cosYaw;
        double newZ = x * cosPitch * sinYaw + y * sinPitch + z * cosPitch * cosYaw;

        return new Vector(newX, newY, newZ);
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return mimicWardenPlayers;
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
