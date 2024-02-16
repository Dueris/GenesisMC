package me.dueris.genesismc.util.entity;

import javassist.NotFoundException;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.PowerUpdateEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.apoli.GravityPower;
import me.dueris.genesismc.factory.powers.apoli.provider.OriginSimpleContainer;
import me.dueris.genesismc.registry.LayerContainer;
import me.dueris.genesismc.registry.OriginContainer;
import me.dueris.genesismc.registry.PowerContainer;
import me.dueris.genesismc.storage.GenesisConfigs;
import me.dueris.genesismc.storage.OriginDataContainer;
import me.dueris.genesismc.util.SendCharts;
import me.dueris.genesismc.util.enums.OriginDataType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class OriginPlayerAccessor {

    // Power maps of every power based on each layer applied to the player
    public static HashMap<Player, HashMap<LayerContainer, ArrayList<PowerContainer>>> playerPowerMapping = new HashMap<>();
    // A list of CraftPowers to be ran on the player
    public static HashMap<Player, ArrayList<Class<? extends CraftPower>>> powersAppliedList = new HashMap<>();
    // A list of Players that have powers that should be run
    public static ArrayList<Player> hasPowers = new ArrayList<>();

    public static void moveEquipmentInventory(Player player, EquipmentSlot equipmentSlot) {
        ItemStack item = player.getInventory().getItem(equipmentSlot);

        if (item != null && item.getType() != Material.AIR) {
            // Find an empty slot in the player's inventory
            int emptySlot = player.getInventory().firstEmpty();

            if (emptySlot != -1) {
                // Set the equipment slot to empty
                player.getInventory().setItem(equipmentSlot, null);

                // Move the item to the empty slot
                player.getInventory().setItem(emptySlot, item);
            }
        }
    }

    public static boolean hasOrigin(Player player, String originTag) {
        if (OriginDataContainer.getDataMap().containsKey(player)) {
            HashMap<LayerContainer, OriginContainer> origins = CraftApoli.toOrigin(OriginDataContainer.getLayer(player));
            for (OriginContainer origin : origins.values()) if (origin.getTag().equals(originTag)) return true;
        }
        return false;
    }

    public static OriginContainer getOrigin(Player player, LayerContainer layer) {
        if (!OriginDataContainer.getDataMap().containsKey(player)) {
            if (OriginDataContainer.getLayer(player) == null) {
                setOrigin(player, layer, CraftApoli.nullOrigin());
                return CraftApoli.nullOrigin();
            }
        }
        return CraftApoli.toOrigin(OriginDataContainer.getLayer(player), layer);
    }

    public static HashMap<LayerContainer, OriginContainer> getOrigin(Player player) {
        return CraftApoli.toOrigin(OriginDataContainer.getLayer(player));
    }

    public static void setupPowers(Player p) {
        OriginDataContainer.loadData(p);
        String[] layers = OriginDataContainer.getLayer(p).split("\n");
        HashMap<LayerContainer, ArrayList<PowerContainer>> map = new HashMap<>();
        for (String layer : layers) {
            String[] layerData = layer.split("\\|");
            LayerContainer layerContainer = CraftApoli.getLayerFromTag(layerData[0]);
            ArrayList<PowerContainer> powers = new ArrayList<>();
            // setup powers
            for (String dataPiece : layerData) {
                if (layerData.length == 1) continue;
                PowerContainer powerCon = CraftApoli.keyedPowerContainers.get(dataPiece);
                if (powerCon != null) {
                    if (powers.contains(powerCon)) continue;
                    powers.add(powerCon);
                    if (powerCon.isOriginMultipleParent()) {
                        ArrayList<PowerContainer> nestedPowers = CraftApoli.getNestedPowers(powerCon);
                        for (PowerContainer nested : nestedPowers) {
                            if (nested != null) powers.add(nested);
                        }
                    }
                }
            }
            map.put(layerContainer, powers);
        }
        playerPowerMapping.put(p, map);
    }

    public static ArrayList<PowerContainer> getMultiPowerFileFromType(Player p, String powerType) {
        ArrayList<PowerContainer> powers = new ArrayList<>();
        if (playerPowerMapping.get(p) == null) return powers;
        for (LayerContainer layer : CraftApoli.getLayers()) {
            if (layer == null) continue;
            for (PowerContainer power : playerPowerMapping.get(p).get(layer)) {
                if (power == null) continue;
                if (power.getType().equals(powerType)) powers.add(power);
            }
        }
        return powers;
    }

    public static ArrayList<PowerContainer> getMultiPowerFileFromType(Player p, String powerType, LayerContainer layer) {
        ArrayList<PowerContainer> powers = new ArrayList<>();
        if (playerPowerMapping.get(p) == null) return powers;
        for (PowerContainer power : playerPowerMapping.get(p).get(layer)) {
            if (power == null) continue;
            if (power.getType().equals(powerType)) powers.add(power);
        }
        return powers;
    }

    public static PowerContainer getSinglePowerFileFromType(Player p, String powerType, LayerContainer layer) {
        if (playerPowerMapping.get(p) == null) return null;
        for (PowerContainer power : playerPowerMapping.get(p).get(layer)) {
            if (power.getType().equals(powerType)) return power;
        }
        return null;
    }

    public static boolean hasCoreOrigin(Player player, LayerContainer layer) {
        String originTag = OriginPlayerAccessor.getOrigin(player, layer).getTag();
        if (originTag.contains("origins:human")) {
            return true;
        } else if (originTag.contains("origins:enderian")) {
            return true;
        } else if (originTag.contains("origins:merling")) {
            return true;
        } else if (originTag.contains("origins:phantom")) {
            return true;
        } else if (originTag.contains("origins:elytrian")) {
            return true;
        } else if (originTag.contains("origins:blazeborn")) {
            return true;
        } else if (originTag.contains("origins:avian")) {
            return true;
        } else if (originTag.contains("origins:arachnid")) {
            return true;
        } else if (originTag.contains("origins:shulk")) {
            return true;
        } else if (originTag.contains("origins:feline")) {
            return true;
        } else if (originTag.contains("origins:starborne")) {
            return true;
        } else if (originTag.contains("origins:allay")) {
            return true;
        } else if (originTag.contains("origins:rabbit")) {
            return true;
        } else if (originTag.contains("origins:bee")) {
            return true;
        } else if (originTag.contains("origins:sculkling")) {
            return true;
        } else if (originTag.contains("origins:creep")) {
            return true;
        } else if (originTag.contains("origins:slimeling")) {
            return true;
        } else return originTag.contains("origins:piglin");
    }

    public static boolean hasPower(Player p, String powerKey) {
        if (playerPowerMapping.containsKey(p)) {
            for (LayerContainer layerContainer : playerPowerMapping.get(p).keySet()) {
                for (PowerContainer power : playerPowerMapping.get(p).get(layerContainer)) {
                    if (power.getTag().equalsIgnoreCase(powerKey)) return true;
                }
            }
        }
        return false;
    }

    public static void setOrigin(Player player, LayerContainer layer, OriginContainer origin) {
        NamespacedKey key = new NamespacedKey(GenesisMC.getPlugin(), "originLayer");
        HashMap<LayerContainer, OriginContainer> origins = CraftApoli.toOrigin(player.getPersistentDataContainer().get(key, PersistentDataType.STRING));
        if (!CraftApoli.getLayers().contains(layer)) {
            return;
        }

        if (!origin.getTag().equals(CraftApoli.nullOrigin().getTag())) {
            try {
                unassignPowers(player, layer);
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        }
        for (LayerContainer layers : origins.keySet()) {
            if (layer.getTag().equals(layers.getTag())) origins.replace(layers, origin);
        }
        player.getPersistentDataContainer().set(key, PersistentDataType.STRING, CraftApoli.toOriginSetSaveFormat(origins));
        OriginDataContainer.loadData(player);
        setupPowers(player);

        String originTag = origin.getTag();
        if (!originTag.equals(CraftApoli.nullOrigin().getTag())) SendCharts.originPopularity(player);
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    assignPowers(player, layer);
                    // Extra precaution due to gravity messing up on origin switch
                    GravityPower g = new GravityPower();
                    g.run(player);
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                } catch (SecurityException e) {
                    throw new RuntimeException(e);
                } catch (NotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }.runTaskLater(GenesisMC.getPlugin(), 3L);
    }

    public static void removeOrigin(Player player, LayerContainer layer) {
        HashMap<LayerContainer, OriginContainer> origins = getOrigin(player);
        ArrayList<LayerContainer> layers = new ArrayList<>(origins.keySet());
        for (LayerContainer playerLayer : layers) {
            if (playerLayer.getTag().equals(layer.getTag())) origins.remove(playerLayer);
        }
        player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originLayer"), PersistentDataType.STRING, CraftApoli.toOriginSetSaveFormat(origins));
        OriginDataContainer.loadData(player);
    }

    public static LayerContainer getLayer(Player p, OriginContainer origin) {
        HashMap<LayerContainer, OriginContainer> origins = getOrigin(p);
        for (LayerContainer layer : origins.keySet()) {
            if (origins.get(layer).getTag().equals(origin.getTag())) return layer;
        }
        return null;
    }

    public static void resetOriginData(Player player, OriginDataType type) {
        if (type.equals(OriginDataType.CAN_EXPLODE)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, 1);
        } else if (type.equals(OriginDataType.SHULKER_BOX_DATA)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "shulker-box"), PersistentDataType.STRING, "");
        } else if (type.equals(OriginDataType.TOGGLE)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "toggle"), PersistentDataType.INTEGER, 1);
        } else if (type.equals(OriginDataType.IN_PHASING_FORM)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.BOOLEAN, false);
        }

    }

    public static boolean isInPhantomForm(Player player) {
        return player.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.BOOLEAN);
    }

    public static void assignPowers(@NotNull Player player) {
        HashMap<LayerContainer, OriginContainer> origins = getOrigin(player);
        for (LayerContainer layer : origins.keySet()) {
            try {
                assignPowers(player, layer);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static List<Class<? extends CraftPower>> getPowersApplied(Player p) {
        List<Class<? extends CraftPower>> array = new ArrayList<>();
        for (Player plc : powersAppliedList.keySet()) {
            if (plc.equals(p)) {
                for (Class<? extends CraftPower> c : powersAppliedList.get(plc)) {
                    array.add(c);
                }

            }
        }
        return array;
    }

    public static void assignPowers(@NotNull Player player, @NotNull LayerContainer layer) throws InstantiationException, IllegalAccessException, NotFoundException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        try {
            List<PowerContainer> powersToExecute = new ArrayList<>();
            CompletableFuture.runAsync(() -> {
                for (PowerContainer power : playerPowerMapping.get(player).get(layer)) {
                    if (power == null) continue;
                    if (power.getType().equalsIgnoreCase("apoli:simple")) {
                        try {
                            Class<? extends CraftPower> c = OriginSimpleContainer.getFromRegistry(power.getTag());
                            if (c == null) continue;
                            CraftPower craftPower = c.newInstance();

                            Field field = c.getDeclaredField("powerReference");
                            field.setAccessible(true);
                            if (power.getTag().equalsIgnoreCase(((NamespacedKey) field.get(craftPower)).asString())) {
                                craftPower.getPowerArray().add(player);
                                if (!powersAppliedList.containsKey(player)) {
                                    ArrayList lst = new ArrayList<>();
                                    lst.add(c);
                                    powersAppliedList.put(player, lst);
                                } else {
                                    powersAppliedList.get(player).add(c);
                                }
                                if (GenesisConfigs.getMainConfig().getString("console-startup-debug").equalsIgnoreCase("true")) {
                                    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Assigned builtinImpl power[" + power.getTag() + "] to player " + player.getName());
                                }
                                powersToExecute.add(power);
                            }
                        } catch (InstantiationException | IllegalAccessException
                                 | NoSuchFieldException | SecurityException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            Class<? extends CraftPower> c = CraftPower.getCraftPowerFromKeyOrThrow(power.getType());
                            if (c == null) continue;
                            CraftPower craftPower = c.newInstance();

                            if (craftPower != null) {
                                craftPower.getPowerArray().add(player);
                                if (!powersAppliedList.containsKey(player)) {
                                    ArrayList lst = new ArrayList<>();
                                    lst.add(c);
                                    powersAppliedList.put(player, lst);
                                } else {
                                    powersAppliedList.get(player).add(c);
                                }
                                if (GenesisConfigs.getMainConfig().getString("console-startup-debug").equalsIgnoreCase("true")) {
                                    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Assigned power[" + power.getTag() + "] to player " + player.getName());
                                }
                                powersToExecute.add(power);
                            }
                        } catch (NotFoundException | InstantiationException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).thenRun(() -> {
                OriginDataContainer.loadData(player);
                setupPowers(player);
                hasPowers.add(player);
            }).get();

            powersToExecute.forEach((power) -> {
                new PowerUpdateEvent(player, power, false).callEvent();
            });
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void unassignPowers(@NotNull Player player) {
        HashMap<LayerContainer, OriginContainer> origins = getOrigin(player);
        for (LayerContainer layer : origins.keySet()) {
            try {
                unassignPowers(player, layer);
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void unassignPowers(@NotNull Player player, @NotNull LayerContainer layer) throws NotFoundException {
        try {
            List<PowerContainer> powersToExecute = new ArrayList<>();
            CompletableFuture.runAsync(() -> {
                for (PowerContainer power : playerPowerMapping.get(player).get(layer)) {
                    if (power == null) continue;
                    if (power.getType().equalsIgnoreCase("apoli:simple")) {
                        try {
                            Class<? extends CraftPower> c = OriginSimpleContainer.getFromRegistry(power.getTag());
                            if (c == null) continue;
                            CraftPower craftPower = c.newInstance();

                            Field field = c.getDeclaredField("powerReference");
                            field.setAccessible(true);
                            if (power.getTag().equalsIgnoreCase(((NamespacedKey) field.get(craftPower)).asString())) {
                                craftPower.getPowerArray().remove(player);
                                if (GenesisConfigs.getMainConfig().getString("console-startup-debug").equalsIgnoreCase("true")) {
                                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Removed builtinImpl power[" + power.getTag() + "] from player " + player.getName());
                                }
                                powersToExecute.add(power);
                            }

                        } catch (NoSuchFieldException | SecurityException
                                 | InstantiationException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            Class<? extends CraftPower> c = CraftPower.getCraftPowerFromKeyOrThrow(power.getType());
                            if (c == null) continue;

                            CraftPower craftPower = c.newInstance();
                            if (craftPower != null) {
                                craftPower.getPowerArray().remove(player);
                                if (GenesisConfigs.getMainConfig().getString("console-startup-debug").equalsIgnoreCase("true")) {
                                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Removed power[" + power.getTag() + "] from player " + player.getName());
                                }
                                powersToExecute.add(power);
                            }
                        } catch (InstantiationException | IllegalAccessException | NotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).thenRun(() -> {
                for (Class<? extends CraftPower> classes : getPowersApplied(player)) {
                    powersAppliedList.get(player).remove(classes);
                }
                OriginDataContainer.unloadData(player);
                hasPowers.remove(player);
            }).get();

            powersToExecute.forEach((power) -> {
                new PowerUpdateEvent(player, power, true).callEvent();
            });
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}