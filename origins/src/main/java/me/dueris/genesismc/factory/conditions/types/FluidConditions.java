package me.dueris.genesismc.factory.conditions.types;

import me.dueris.calio.registry.Registerable;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.TagRegistryParser;
import me.dueris.genesismc.registry.Registries;
import net.minecraft.world.level.material.Fluid;
import org.bukkit.NamespacedKey;
import org.json.simple.JSONObject;

import java.util.function.BiPredicate;

public class FluidConditions {
    public void prep() {
        // Meta conditions, shouldnt execute
        // Meta conditions are added in each file to ensure they dont error and skip them when running
        // a meta condition inside another meta condition
        register(new ConditionFactory(GenesisMC.apoliIdentifier("and"), (condition, obj) -> {
            throw new IllegalStateException("Executor should not be here right now! Report to Dueris!");
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("or"), (condition, obj) -> {
            throw new IllegalStateException("Executor should not be here right now! Report to Dueris!");
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("chance"), (condition, obj) -> {
            throw new IllegalStateException("Executor should not be here right now! Report to Dueris!");
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("constant"), (condition, obj) -> {
            throw new IllegalStateException("Executor should not be here right now! Report to Dueris!");
        }));
        // Meta conditions end
        register(new ConditionFactory(GenesisMC.apoliIdentifier("empty"), (condition, fluid) -> {
            return fluid.defaultFluidState().isEmpty();
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("in_tag"), (condition, fluid) -> {
            for (String flu : TagRegistryParser.getRegisteredTagFromFileKey(condition.get("tag").toString())) {
                if (flu == null) continue;
                if (fluid == null) continue;
                return flu.equalsIgnoreCase(fluid.toString());
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("still"), (condition, fluid) -> {
            return fluid.defaultFluidState().isSource();
        }));
    }

    private void register(ConditionFactory factory) {
        GenesisMC.getPlugin().registry.retrieve(Registries.FLUID_CONDITION).register(factory);
    }

    public class ConditionFactory implements Registerable {
        NamespacedKey key;
        BiPredicate<JSONObject, Fluid> test;

        public ConditionFactory(NamespacedKey key, BiPredicate<JSONObject, Fluid> test) {
            this.key = key;
            this.test = test;
        }

        public boolean test(JSONObject condition, net.minecraft.world.level.material.Fluid tester) {
            return test.test(condition, tester);
        }

        @Override
        public NamespacedKey getKey() {
            return key;
        }
    }
}
