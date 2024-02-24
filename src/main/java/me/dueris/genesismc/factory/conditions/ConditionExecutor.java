package me.dueris.genesismc.factory.conditions;

import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBiome;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.damage.DamageSource;
import org.bukkit.event.entity.EntityDamageEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import it.unimi.dsi.fastutil.Pair;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.conditions.types.BiEntityConditions;
import me.dueris.genesismc.factory.conditions.types.BiomeConditions;
import me.dueris.genesismc.factory.conditions.types.BlockConditions;
import me.dueris.genesismc.factory.conditions.types.DamageConditions;
import me.dueris.genesismc.factory.conditions.types.EntityConditions;
import me.dueris.genesismc.factory.conditions.types.FluidConditions;
import me.dueris.genesismc.factory.conditions.types.ItemConditions;
import me.dueris.genesismc.registry.Registrar;
import me.dueris.genesismc.registry.Registries;

public class ConditionExecutor {
    public static BiEntityConditions biEntityCondition = new BiEntityConditions();
    public static BiomeConditions biomeCondition = new BiomeConditions();
    public static BlockConditions blockCondition = new BlockConditions();
    public static DamageConditions damageCondition = new DamageConditions();
    public static EntityConditions entityCondition = new EntityConditions();
    public static FluidConditions fluidCondition = new FluidConditions();
    public static ItemConditions itemCondition = new ItemConditions();

    public static void registerAll(){
        biEntityCondition.prep();
        biomeCondition.prep();
        blockCondition.prep();
        damageCondition.prep();
        entityCondition.prep();
        fluidCondition.prep();
        itemCondition.prep();
    }

    private static boolean isMetaCondition(JSONObject condition){
        return condition.containsKey("type") ?
            condition.get("type").toString().equals("apoli:and") ||
            condition.get("type").toString().equals("apoli:chance") ||
            condition.get("type").toString().equals("apoli:constant") ||
            condition.get("type").toString().equals("apoli:not") ||
            condition.get("type").toString().equals("apoli:or")
            : false;
    }

    private static boolean chance(JSONObject condition){
        float chance = (float) condition.get("chance");
        if(chance > 1f){
            chance = 1f;
        }
        return new Random().nextFloat(1.0f) < chance;
    }

    public static boolean testBiEntity(JSONObject condition, CraftEntity actor, CraftEntity target){
        if(condition.isEmpty()) return true; // Empty condition, do nothing
        Pair entityPair = new Pair<CraftEntity,CraftEntity>() {

            @Override
            public CraftEntity left() {
                return actor;
            }

            @Override
            public CraftEntity right() {
                return target;
            }
        };
        if(isMetaCondition(condition)){
            String type = condition.get("type").toString();
            switch(type) {
                case "apoli:and" -> {
                    JSONArray array = (JSONArray) condition.get("conditions");
                    List<Boolean> cons = new ArrayList<>();
                    array.forEach(object -> {
                        if(object instanceof JSONObject obj){
                            Registrar<BiEntityConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.BIENTITY_CONDITION);
                            BiEntityConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.get("type").toString()));
                            boolean invert = (boolean) obj.getOrDefault("inverted", false);
                            if(con != null){
                                cons.add(getPossibleInvert(invert, con.test(condition, entityPair)));
                            }else{
                                cons.add(getPossibleInvert(invert, true)); // Condition null or not found.
                            }
                        }
                    });
                    for(boolean b : cons){
                        if(!b) return false;
                    }
                    return true;
                }
                case "apoli:or" -> {
                    JSONArray array = (JSONArray) condition.get("conditions");
                    List<Boolean> cons = new ArrayList<>();
                    array.forEach(object -> {
                        if(object instanceof JSONObject obj){
                            Registrar<BiEntityConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.BIENTITY_CONDITION);
                            BiEntityConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.get("type").toString()));
                            boolean invert = (boolean) obj.getOrDefault("inverted", false);
                            if(con != null){
                                cons.add(getPossibleInvert(invert, con.test(condition, entityPair)));
                            }else{
                                cons.add(getPossibleInvert(invert, true)); // Condition null or not found.
                            }
                        }
                    });
                    for(boolean b : cons){
                        if(b) return true;
                    }
                    return false;
                }
                case "apoli:constant" -> {
                    return getPossibleInvert((boolean)condition.getOrDefault("inverted", false), (boolean)condition.get("value"));
                }
                case "apoli:chance" -> {
                    return getPossibleInvert((boolean)condition.getOrDefault("inverted", false), chance(condition));
                }
            }
        }else{
            // return the condition
            Registrar<BiEntityConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.BIENTITY_CONDITION);
            BiEntityConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(condition.get("type").toString()));
            boolean invert = (boolean) condition.getOrDefault("inverted", false);
            if(con != null){
                return getPossibleInvert(invert, con.test(condition, entityPair));
            }else{
                return getPossibleInvert(invert, true); // Condition null or not found.
            }
        }
        return false;
    }

    public static boolean testBiome(JSONObject condition, org.bukkit.block.Biome biome){
        if(condition.isEmpty()) return true; // Empty condition, do nothing
        if(isMetaCondition(condition)){
            String type = condition.get("type").toString();
            switch(type) {
                case "apoli:and" -> {
                    JSONArray array = (JSONArray) condition.get("conditions");
                    List<Boolean> cons = new ArrayList<>();
                    array.forEach(object -> {
                        if(object instanceof JSONObject obj){
                            Registrar<BiomeConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.BIOME_CONDITION);
                            BiomeConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.get("type").toString()));
                            boolean invert = (boolean) obj.getOrDefault("inverted", false);
                            if(con != null){
                                cons.add(getPossibleInvert(invert, con.test(condition, CraftBiome.bukkitToMinecraft(biome))));
                            }else{
                                cons.add(getPossibleInvert(invert, true)); // Condition null or not found.
                            }
                        }
                    });
                    for(boolean b : cons){
                        if(!b) return false;
                    }
                    return true;
                }
                case "apoli:or" -> {
                    JSONArray array = (JSONArray) condition.get("conditions");
                    List<Boolean> cons = new ArrayList<>();
                    array.forEach(object -> {
                        if(object instanceof JSONObject obj){
                            Registrar<BiomeConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.BIOME_CONDITION);
                            BiomeConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.get("type").toString()));
                            boolean invert = (boolean) obj.getOrDefault("inverted", false);
                            if(con != null){
                                cons.add(getPossibleInvert(invert, con.test(condition, CraftBiome.bukkitToMinecraft(biome))));
                            }else{
                                cons.add(getPossibleInvert(invert, true)); // Condition null or not found.
                            }
                        }
                    });
                    for(boolean b : cons){
                        if(b) return true;
                    }
                    return false;
                }
                case "apoli:constant" -> {
                    return getPossibleInvert((boolean)condition.getOrDefault("inverted", false), (boolean)condition.get("value"));
                }
                case "apoli:chance" -> {
                    return getPossibleInvert((boolean)condition.getOrDefault("inverted", false), chance(condition));
                }
            }
        }else{
            // return the condition
            Registrar<BiomeConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.BIOME_CONDITION);
            BiomeConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(condition.get("type").toString()));
            boolean invert = (boolean) condition.getOrDefault("inverted", false);
            if(con != null){
                return getPossibleInvert(invert, con.test(condition, CraftBiome.bukkitToMinecraft(biome)));
            }else{
                return getPossibleInvert(invert, true); // Condition null or not found.
            }
        }
        return false;
    }

    @SuppressWarnings("index out of bounds")
    public static boolean testBlock(JSONObject condition, CraftBlock block){
        if(condition.isEmpty()) return true; // Empty condition, do nothing
        if(isMetaCondition(condition)){
            String type = condition.get("type").toString();
            switch(type) {
                case "apoli:and" -> {
                    JSONArray array = (JSONArray) condition.get("conditions");
                    List<Boolean> cons = new ArrayList<>();
                    array.forEach(object -> {
                        if(object instanceof JSONObject obj){
                            Registrar<BlockConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.BLOCK_CONDITION);
                            BlockConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.get("type").toString()));
                            boolean invert = (boolean) obj.getOrDefault("inverted", false);
                            if(con != null){
                                cons.add(getPossibleInvert(invert, con.test(condition, block)));
                            }else{
                                cons.add(getPossibleInvert(invert, true)); // Condition null or not found.
                            }
                        }
                    });
                    for(boolean b : cons){
                        if(!b) return false;
                    }
                    return true;
                }
                case "apoli:or" -> {
                    JSONArray array = (JSONArray) condition.get("conditions");
                    List<Boolean> cons = new ArrayList<>();
                    array.forEach(object -> {
                        if(object instanceof JSONObject obj){
                            Registrar<BlockConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.BLOCK_CONDITION);
                            BlockConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.get("type").toString()));
                            boolean invert = (boolean) obj.getOrDefault("inverted", false);
                            if(con != null){
                                cons.add(getPossibleInvert(invert, con.test(condition, block)));
                            }else{
                                cons.add(getPossibleInvert(invert, true)); // Condition null or not found.
                            }
                        }
                    });
                    for(boolean b : cons){
                        if(b) return true;
                    }
                    return false;
                }
                case "apoli:constant" -> {
                    return getPossibleInvert((boolean)condition.getOrDefault("inverted", false), (boolean)condition.get("value"));
                }
                case "apoli:chance" -> {
                    return getPossibleInvert((boolean)condition.getOrDefault("inverted", false), chance(condition));
                }
            }
        }else{
            // return the condition
            Registrar<BlockConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.BLOCK_CONDITION);
            BlockConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(condition.get("type").toString()));
            boolean invert = (boolean) condition.getOrDefault("inverted", false);
            if(con != null){
                return getPossibleInvert(invert, con.test(condition, block));
            }else{
                return getPossibleInvert(invert, true); // Condition null or not found.
            }
        }
        return false;
    }

    public static boolean testDamage(JSONObject condition, EntityDamageEvent event){
        if(condition.isEmpty()) return true; // Empty condition, do nothing
        if(isMetaCondition(condition)){
            String type = condition.get("type").toString();
            switch(type) {
                case "apoli:and" -> {
                    JSONArray array = (JSONArray) condition.get("conditions");
                    List<Boolean> cons = new ArrayList<>();
                    array.forEach(object -> {
                        if(object instanceof JSONObject obj){
                            Registrar<DamageConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.DAMAGE_CONDITION);
                            DamageConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.get("type").toString()));
                            boolean invert = (boolean) obj.getOrDefault("inverted", false);
                            if(con != null){
                                cons.add(getPossibleInvert(invert, con.test(condition, event)));
                            }else{
                                cons.add(getPossibleInvert(invert, true)); // Condition null or not found.
                            }
                        }
                    });
                    for(boolean b : cons){
                        if(!b) return false;
                    }
                    return true;
                }
                case "apoli:or" -> {
                    JSONArray array = (JSONArray) condition.get("conditions");
                    List<Boolean> cons = new ArrayList<>();
                    array.forEach(object -> {
                        if(object instanceof JSONObject obj){
                            Registrar<DamageConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.DAMAGE_CONDITION);
                            DamageConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.get("type").toString()));
                            boolean invert = (boolean) obj.getOrDefault("inverted", false);
                            if(con != null){
                                cons.add(getPossibleInvert(invert, con.test(condition, event)));
                            }else{
                                cons.add(getPossibleInvert(invert, true)); // Condition null or not found.
                            }
                        }
                    });
                    for(boolean b : cons){
                        if(b) return true;
                    }
                    return false;
                }
                case "apoli:constant" -> {
                    return getPossibleInvert((boolean)condition.getOrDefault("inverted", false), (boolean)condition.get("value"));
                }
                case "apoli:chance" -> {
                    return getPossibleInvert((boolean)condition.getOrDefault("inverted", false), chance(condition));
                }
            }
        }else{
            // return the condition
            Registrar<DamageConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.DAMAGE_CONDITION);
            DamageConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(condition.get("type").toString()));
            boolean invert = (boolean) condition.getOrDefault("inverted", false);
            if(con != null){
                return getPossibleInvert(invert, con.test(condition, event));
            }else{
                return getPossibleInvert(invert, true); // Condition null or not found.
            }
        }
        return false;
    }

    public static boolean testEntity(JSONObject condition, CraftEntity entity){
        if(condition.isEmpty()) return true; // Empty condition, do nothing
        if(isMetaCondition(condition)){
            String type = condition.get("type").toString();
            switch(type) {
                case "apoli:and" -> {
                    JSONArray array = (JSONArray) condition.get("conditions");
                    List<Boolean> cons = new ArrayList<>();
                    array.forEach(object -> {
                        if(object instanceof JSONObject obj){
                            Registrar<EntityConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.ENTITY_CONDITION);
                            EntityConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.get("type").toString()));
                            boolean invert = (boolean) obj.getOrDefault("inverted", false);
                            if(con != null){
                                cons.add(getPossibleInvert(invert, con.test(condition, entity)));
                            }else{
                                cons.add(getPossibleInvert(invert, true)); // Condition null or not found.
                            }
                        }
                    });
                    for(boolean b : cons){
                        if(!b) return false;
                    }
                    return true;
                }
                case "apoli:or" -> {
                    JSONArray array = (JSONArray) condition.get("conditions");
                    List<Boolean> cons = new ArrayList<>();
                    array.forEach(object -> {
                        if(object instanceof JSONObject obj){
                            Registrar<EntityConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.ENTITY_CONDITION);
                            EntityConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.get("type").toString()));
                            boolean invert = (boolean) obj.getOrDefault("inverted", false);
                            if(con != null){
                                cons.add(getPossibleInvert(invert, con.test(condition, entity)));
                            }else{
                                cons.add(getPossibleInvert(invert, true)); // Condition null or not found.
                            }
                        }
                    });
                    for(boolean b : cons){
                        if(b) return true;
                    }
                    return false;
                }
                case "apoli:constant" -> {
                    return getPossibleInvert((boolean)condition.getOrDefault("inverted", false), (boolean)condition.get("value"));
                }
                case "apoli:chance" -> {
                    return getPossibleInvert((boolean)condition.getOrDefault("inverted", false), chance(condition));
                }
            }
        }else{
            // return the condition
            Registrar<EntityConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.ENTITY_CONDITION);
            EntityConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(condition.get("type").toString()));
            boolean invert = (boolean) condition.getOrDefault("inverted", false);
            if(con != null){
                return getPossibleInvert(invert, con.test(condition, entity));
            }else{
                return getPossibleInvert(invert, true); // Condition null or not found.
            }
        }
        return false;
    }

    public static boolean testItem(JSONObject condition, CraftItemStack itemStack){
        if(condition.isEmpty()) return true; // Empty condition, do nothing
        if(isMetaCondition(condition)){
            String type = condition.get("type").toString();
            switch(type) {
                case "apoli:and" -> {
                    JSONArray array = (JSONArray) condition.get("conditions");
                    List<Boolean> cons = new ArrayList<>();
                    array.forEach(object -> {
                        if(object instanceof JSONObject obj){
                            Registrar<ItemConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.ITEM_CONDITION);
                            ItemConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.get("type").toString()));
                            boolean invert = (boolean) obj.getOrDefault("inverted", false);
                            if(con != null){
                                cons.add(getPossibleInvert(invert, con.test(condition, itemStack)));
                            }else{
                                cons.add(getPossibleInvert(invert, true)); // Condition null or not found.
                            }
                        }
                    });
                    for(boolean b : cons){
                        if(!b) return false;
                    }
                    return true;
                }
                case "apoli:or" -> {
                    JSONArray array = (JSONArray) condition.get("conditions");
                    List<Boolean> cons = new ArrayList<>();
                    array.forEach(object -> {
                        if(object instanceof JSONObject obj){
                            Registrar<ItemConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.ITEM_CONDITION);
                            ItemConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.get("type").toString()));
                            boolean invert = (boolean) obj.getOrDefault("inverted", false);
                            if(con != null){
                                cons.add(getPossibleInvert(invert, con.test(condition, itemStack)));
                            }else{
                                cons.add(getPossibleInvert(invert, true)); // Condition null or not found.
                            }
                        }
                    });
                    for(boolean b : cons){
                        if(b) return true;
                    }
                    return false;
                }
                case "apoli:constant" -> {
                    return getPossibleInvert((boolean)condition.getOrDefault("inverted", false), (boolean)condition.get("value"));
                }
                case "apoli:chance" -> {
                    return getPossibleInvert((boolean)condition.getOrDefault("inverted", false), chance(condition));
                }
            }
        }else{
            // return the condition
            Registrar<ItemConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.ITEM_CONDITION);
            ItemConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(condition.get("type").toString()));
            boolean invert = (boolean) condition.getOrDefault("inverted", false);
            if(con != null){
                return getPossibleInvert(invert, con.test(condition, itemStack));
            }else{
                return getPossibleInvert(invert, true); // Condition null or not found.
            }
        }
        return false;
    }

    public static boolean testFluid(JSONObject condition, net.minecraft.world.level.material.Fluid fluid){
        if(condition.isEmpty()) return true; // Empty condition, do nothing
        if(isMetaCondition(condition)){
            String type = condition.get("type").toString();
            switch(type) {
                case "apoli:and" -> {
                    JSONArray array = (JSONArray) condition.get("conditions");
                    List<Boolean> cons = new ArrayList<>();
                    array.forEach(object -> {
                        if(object instanceof JSONObject obj){
                            Registrar<FluidConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.FLUID_CONDITION);
                            FluidConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.get("type").toString()));
                            boolean invert = (boolean) obj.getOrDefault("inverted", false);
                            if(con != null){
                                cons.add(getPossibleInvert(invert, con.test(condition, fluid)));
                            }else{
                                cons.add(getPossibleInvert(invert, true)); // Condition null or not found.
                            }
                        }
                    });
                    for(boolean b : cons){
                        if(!b) return false;
                    }
                    return true;
                }
                case "apoli:or" -> {
                    JSONArray array = (JSONArray) condition.get("conditions");
                    List<Boolean> cons = new ArrayList<>();
                    array.forEach(object -> {
                        if(object instanceof JSONObject obj){
                            Registrar<FluidConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.FLUID_CONDITION);
                            FluidConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.get("type").toString()));
                            boolean invert = (boolean) obj.getOrDefault("inverted", false);
                            if(con != null){
                                cons.add(getPossibleInvert(invert, con.test(condition, fluid)));
                            }else{
                                cons.add(getPossibleInvert(invert, true)); // Condition null or not found.
                            }
                        }
                    });
                    for(boolean b : cons){
                        if(b) return true;
                    }
                    return false;
                }
                case "apoli:constant" -> {
                    return getPossibleInvert((boolean)condition.getOrDefault("inverted", false), (boolean)condition.get("value"));
                }
                case "apoli:chance" -> {
                    return getPossibleInvert((boolean)condition.getOrDefault("inverted", false), chance(condition));
                }
            }
        }else{
            // return the condition
            Registrar<FluidConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.FLUID_CONDITION);
            FluidConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(condition.get("type").toString()));
            boolean invert = (boolean) condition.getOrDefault("inverted", false);
            if(con != null){
                return getPossibleInvert(invert, con.test(condition, fluid));
            }else{
                return getPossibleInvert(invert, true); // Condition null or not found.
            }
        }
        return false;
    }

    protected static boolean getPossibleInvert(boolean inverted, boolean original){
        return inverted ? !original : original;
    }
    /*
    private static boolean checkSubCondition(JSONObject subCondition, Player p, Power power, String powerfile, Entity actor, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent dmgevent, String powerFile) {
        if ("apoli:and".equals(subCondition.get("type"))) {
            JSONArray conditionsArray = (JSONArray) subCondition.get("conditions");
            boolean allTrue = true;

            for (Object subConditionObj : conditionsArray) {
                if (subConditionObj instanceof JSONObject subSubCondition) {
                    boolean subSubConditionResult = checkSubCondition(subSubCondition, p, power, powerfile, actor, target, block, fluid, itemStack, dmgevent, powerFile);
                    if (!subSubConditionResult) {
                        allTrue = false;
                        break;
                    }
                }
            }

            return allTrue;
        } else if (subCondition.get("type").equals("apoli:power_active")) {
            if (!powers_active.containsKey(p)) return false;
            if (subCondition.get("power").toString().contains("*")) {
                String[] powerK = subCondition.get("power").toString().split("\\*");
                for (String string : powers_active.get(p).keySet()) {
                    if (string.startsWith(powerK[0]) && string.endsWith(powerK[1])) {
                        return powers_active.get(p).get(string);
                    }
                }
            } else {
                String powerF = subCondition.get("power").toString();
                boolean invert = (boolean) subCondition.getOrDefault("inverted", false);
                return getResult(invert, Optional.of(powers_active.get(p).getOrDefault(powerF, false))).get();
            }
        } else {
            AtomicBoolean[] booleanOptional = {null};

            if (booleanOptional[0] == null && dmgevent != null) {
                var check = damageCondition.check(subCondition, actor, target, block, fluid, itemStack, dmgevent);
                check.ifPresent(val -> booleanOptional[0] = new AtomicBoolean(val));
            }

            if (booleanOptional[0] == null && actor != null) {
                var check = entityCondition.check(subCondition, actor, target, block, fluid, itemStack, dmgevent);
                check.ifPresent(val -> booleanOptional[0] = new AtomicBoolean(val));
            }

            if (booleanOptional[0] == null && actor != null && target != null) {
                var check = biEntityCondition.check(subCondition, actor, target, block, fluid, itemStack, dmgevent);
                check.ifPresent(val -> booleanOptional[0] = new AtomicBoolean(val));
            }

            if (booleanOptional[0] == null && block != null) {
                var check = blockCondition.check(subCondition, actor, target, block, fluid, itemStack, dmgevent);
                check.ifPresent(val -> booleanOptional[0] = new AtomicBoolean(val));
            }

            if (booleanOptional[0] == null && block != null) {
                var check = biomeCondition.check(subCondition, actor, target, block, fluid, itemStack, dmgevent);
                check.ifPresent(val -> booleanOptional[0] = new AtomicBoolean(val));
            }

            if (booleanOptional[0] == null && fluid != null) {
//                var check = fluidCondition.check(subCondition, actor, target, block, fluid, itemStack, dmgevent);
//                check.ifPresent(val -> booleanOptional[0] = new AtomicBoolean(val));
            }

            if (booleanOptional[0] == null && itemStack != null) {
                var check = itemCondition.check(subCondition, actor, target, block, fluid, itemStack, dmgevent);
                check.ifPresent(val -> booleanOptional[0] = new AtomicBoolean(val));
            }

            if (booleanOptional[0] == null) {
                return true;
            } else {
                return booleanOptional[0].get();
            }
        }
        return false;
    }

    public static boolean checkConditions(JSONArray conditionsArray, Player p, Power power, String powerfile, Entity actor, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent dmgevent, String powerFile) {
        boolean allTrue = true;

        for (Object subConditionObj : conditionsArray) {
            if (subConditionObj instanceof JSONObject subCondition) {
                boolean subConditionResult = checkSubCondition(subCondition, p, power, powerfile, actor, target, block, fluid, itemStack, dmgevent, powerFile);
                if (!subConditionResult) {
                    allTrue = false;
                    break;
                }
            }
        }

        return allTrue;
    }

    public static Optional<Boolean> getResult(boolean inverted, Optional<Boolean> condition) {
        if (condition.isPresent()) {
            if (inverted) {
                return Optional.of(!condition.get());
            } else {
                return Optional.of(condition.get());
            }
        } else {
            return Optional.empty();
        }
    }

    public boolean check(String singular, String plural, Player p, Power powerContainer, String powerfile, Entity actor, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent dmgevent) {
        if (powerContainer == null) return true;
        if (powerContainer.getJsonListSingularPlural(singular, plural) == null) return true;
        if (powerContainer.getJsonListSingularPlural(singular, plural).isEmpty()) return true;
        for (JSONObject condition : powerContainer.getJsonListSingularPlural(singular, plural)) {
            if (condition.get("type").equals("apoli:and")) {
                JSONArray conditionsArray = (JSONArray) condition.get("conditions");

                return checkConditions(conditionsArray, p, powerContainer, powerfile, actor, target, block, fluid, itemStack, dmgevent, powerfile);
            } else if (condition.get("type").equals("apoli:or")) {
                JSONArray conditionsArray = (JSONArray) condition.get("conditions");

                for (Object subConditionObj : conditionsArray) {
                    if (subConditionObj instanceof JSONObject subCondition) {
                        boolean subConditionResult = checkSubCondition(subCondition, p, powerContainer, powerfile, actor, target, block, fluid, itemStack, dmgevent, powerfile);
                        if (subConditionResult) {
                            return true;
                        }
                    }
                }
            } else if (condition.get("type").equals("apoli:constant")) {
                return (boolean) condition.get("value");
            } else if (condition.get("type").toString().equalsIgnoreCase("apoli:meat")) {
                boolean inverted = Boolean.valueOf(condition.getOrDefault("inverted", false).toString());
                if (itemStack.getType().isEdible()) {
                    if (inverted) {
                        if (getNonMeatMaterials().contains(itemStack.getType())) {
                            return true;
                        }
                    } else {
                        if (getMeatMaterials().contains(itemStack.getType())) {
                            return true;
                        }
                    }
                } else {
                    return false;
                }
            } else {
                AtomicBoolean[] booleanOptional = {null};

                if (booleanOptional[0] == null && (singular.contains("entity_") || plural.contains("entity_") || plural.equals("conditions") || singular.equals("condition"))) {
                    Optional<Boolean> bool = entity.check(condition, actor, target, block, fluid, itemStack, dmgevent);
                    bool.ifPresent(val -> booleanOptional[0] = new AtomicBoolean(val));
                }
                if (booleanOptional[0] == null && (singular.contains("bientity_") || plural.contains("bientity_") || plural.equals("conditions") || singular.equals("condition"))) {
                    Optional<Boolean> bool = bientity.check(condition, actor, target, block, fluid, itemStack, dmgevent);
                    bool.ifPresent(val -> booleanOptional[0] = new AtomicBoolean(val));
                }
                if (booleanOptional[0] == null && (singular.contains("block_") || plural.contains("block_") || plural.equals("conditions") || singular.equals("condition"))) {
                    Optional<Boolean> bool = blockCon.check(condition, actor, target, block, fluid, itemStack, dmgevent);
                    bool.ifPresent(val -> booleanOptional[0] = new AtomicBoolean(val));
                }
                if (booleanOptional[0] == null && (singular.contains("biome_") || plural.contains("biome_") || plural.equals("conditions") || singular.equals("condition"))) {
                    Optional<Boolean> bool = biome.check(condition, actor, target, block, fluid, itemStack, dmgevent);
                    bool.ifPresent(val -> booleanOptional[0] = new AtomicBoolean(val));
                }
                if (booleanOptional[0] == null && (singular.contains("damage_") || plural.contains("damage_") || plural.equals("conditions") || singular.equals("condition"))) {
                    Optional<Boolean> bool = damage.check(condition, actor, target, block, fluid, itemStack, dmgevent);
                    bool.ifPresent(val -> booleanOptional[0] = new AtomicBoolean(val));
                }
                if (booleanOptional[0] == null && (singular.contains("fluid_") || plural.contains("fluid_") || plural.equals("conditions") || singular.equals("condition"))) {
//                    Optional<Boolean> bool = fluidCon.check(condition, actor, target, block, fluid, itemStack, dmgevent);
//                    bool.ifPresent(val -> booleanOptional[0] = new AtomicBoolean(val));
                }
                if (booleanOptional[0] == null && (singular.contains("item_") || plural.contains("item_") || plural.equals("conditions") || singular.equals("condition"))) {
                    Optional<Boolean> bool = item.check(condition, actor, target, block, fluid, itemStack, dmgevent);
                    bool.ifPresent(val -> booleanOptional[0] = new AtomicBoolean(val));
                }
                // Custom conditions
                if (booleanOptional[0] == null) {
                    try {
                        for (Class<? extends Condition> conditionClass : customConditions) {
                            Optional<Boolean> bool = conditionClass.newInstance().check(condition, actor, target, block, fluid, itemStack, dmgevent);
                            bool.ifPresent(val -> booleanOptional[0] = new AtomicBoolean(val));
                        }
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (booleanOptional[0] == null) {
                    booleanOptional[0] = new AtomicBoolean(true);
                }
                return booleanOptional[0].get();
            }
        }
        return false;
    }
    */
}
