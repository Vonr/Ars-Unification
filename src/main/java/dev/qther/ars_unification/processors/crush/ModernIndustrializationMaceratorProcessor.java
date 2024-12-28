package dev.qther.ars_unification.processors.crush;

import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import dev.qther.ars_unification.ArsUnification;
import dev.qther.ars_unification.processors.Processor;
import dev.qther.ars_unification.recipe.RecipeWrappers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ModernIndustrializationMaceratorProcessor extends Processor<RecipeInput, MachineRecipe> {
    public ModernIndustrializationMaceratorProcessor(RecipeManager recipeManager) {
        super(recipeManager, MIMachineRecipeTypes.MACERATOR);
    }

    @Override
    public Set<Item> getExistingInputs() {
        return ArsUnification.crushRecipesIngredientSet(this.recipeManager);
    }

    @Override
    public @Nullable Ingredient getIngredient(MachineRecipe recipe) {
        if (!recipe.fluidInputs.isEmpty() || !recipe.fluidOutputs.isEmpty()) {
            return null;
        }

        var ingredientList = recipe.itemInputs;
        if (ingredientList.size() != 1) {
            return null;
        }

        var first = ingredientList.getFirst();
        var ingredient = first.ingredient();
        if (ingredient.isEmpty() || first.amount() != 1) {
            return null;
        }

        return ingredient;
    }

    @Override
    public @Nullable RecipeHolder<?> processCommon(Set<Item> existing, RecipeHolder<? extends MachineRecipe> recipeHolder, Ingredient ingredient) {
        var wrapper = new RecipeWrappers.Crush(recipeHolder.id(), ingredient);

        var recipe = recipeHolder.value();
        for (var output : recipe.itemOutputs) {
            wrapper = wrapper.withItems(output.getStack(), output.probability());
        }

        return new RecipeHolder<>(wrapper.path, wrapper.asRecipe());
    }
}
