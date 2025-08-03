package dev.qther.ars_unification.processors;

import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import dev.qther.ars_unification.ArsUnification;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class MIProcessor extends Processor<RecipeInput, MachineRecipe> {
    public Fluid[] ignoredFluids;

    public MIProcessor(MinecraftServer server, RecipeType<MachineRecipe> type, Fluid[] ignoredFluids) {
        super(server, type);
        this.ignoredFluids = ignoredFluids;
    }

    public MIProcessor(MinecraftServer server, RecipeType<MachineRecipe> type) {
        this(server, type, new Fluid[0]);
    }

    @Override
    public @Nullable Ingredient getIngredient(MachineRecipe recipe) {
        if (!recipe.fluidOutputs.isEmpty()) {
            return null;
        }

        if (this.ignoredFluids.length == 0 && !recipe.fluidInputs.isEmpty()) {
            return null;
        }

        for (var fluidIn : recipe.fluidInputs) {
            var fluidStacks = fluidIn.fluid().getStacks();
            var isIgnored = fluidStacks.length == 0;
            fluidIngredients: for (var fluid : fluidStacks) {
                for (var ignored : ignoredFluids) {
                    if (fluid.is(ignored)) {
                        isIgnored = true;
                        break fluidIngredients;
                    }
                }
            }

            if (!isIgnored) {
                return null;
            }
        }

        var ingredientList = recipe.itemInputs;
        if (ingredientList.size() != 1) {
            return Ingredient.EMPTY;
        }

        var first = ingredientList.getFirst();
        var ingredient = first.ingredient();
        if (ingredient.isEmpty() || first.amount() != 1) {
            return Ingredient.EMPTY;
        }

        return ingredient;
    }
}
