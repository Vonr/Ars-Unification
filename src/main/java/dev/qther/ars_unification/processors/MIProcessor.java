package dev.qther.ars_unification.processors;

import aztech.modern_industrialization.MIFluids;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

public abstract class MIProcessor extends Processor<RecipeInput, MachineRecipe> {
    public MIProcessor(RecipeManager recipeManager, RecipeType<MachineRecipe> type) {
        super(recipeManager, type);
    }

    @Override
    public @Nullable Ingredient getIngredient(MachineRecipe recipe) {
        if (!recipe.fluidOutputs.isEmpty()) {
            return null;
        }

        for (var fluidIn : recipe.fluidInputs) {
            if (!fluidIn.fluid().isSame(MIFluids.LUBRICANT.asFluid())) {
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
