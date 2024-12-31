package dev.qther.ars_unification.processors.cut;

import dev.qther.ars_unification.ArsUnification;
import dev.qther.ars_unification.processors.Processor;
import dev.qther.ars_unification.recipe.RecipeWrapper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipeInput;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;

import java.util.Set;

public class FarmersDelightCuttingBoardProcessor extends Processor<CuttingBoardRecipeInput, CuttingBoardRecipe> {
    public FarmersDelightCuttingBoardProcessor(RecipeManager recipeManager) {
        super(recipeManager, ModRecipeTypes.CUTTING.get());
    }

    @Override
    public Set<Item> getExistingInputs() {
        return ArsUnification.cutRecipesIngredientSet(this.recipeManager);
    }

    @Override
    public @Nullable Ingredient getIngredient(CuttingBoardRecipe recipe) {
        var ingredientList = recipe.getIngredients();
        if (ingredientList.size() != 1) {
            return null;
        }

        return ingredientList.getFirst();
    }

    @Override
    public @Nullable RecipeHolder<?> processCommon(Set<Item> existing, RecipeHolder<? extends CuttingBoardRecipe> recipeHolder, Ingredient ingredient) {
        var wrapper = new RecipeWrapper.Cut(recipeHolder.id(), ingredient);
        for (var output : recipeHolder.value().getResults()) {
            wrapper = wrapper.withItems(output);
        }

        return new RecipeHolder<>(wrapper.path, wrapper.asRecipe());
    }
}
