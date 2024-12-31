package dev.qther.ars_unification.processors.crush;

import dev.qther.ars_unification.ArsUnification;
import dev.qther.ars_unification.processors.Processor;
import dev.qther.ars_unification.recipe.RecipeWrapper;
import mekanism.api.recipes.ItemStackToItemStackRecipe;
import mekanism.api.recipes.MekanismRecipeTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class MekanismCrusherProcessor extends Processor<SingleRecipeInput, ItemStackToItemStackRecipe> {
    public MekanismCrusherProcessor(RecipeManager recipeManager) {
        super(recipeManager, MekanismRecipeTypes.TYPE_CRUSHING.get());
    }

    @Override
    public Set<Item> getExistingInputs() {
        return ArsUnification.crushRecipesIngredientSet(this.recipeManager);
    }

    @Override
    public @Nullable Ingredient getIngredient(ItemStackToItemStackRecipe recipe) {
        var sized = recipe.getInput().ingredient();
        if (sized.count() != 1) {
            return null;
        }

        var ingredient = sized.ingredient();
        if (ingredient.isEmpty()) {
            return null;
        }

        return ingredient;
    }

    @Override
    public @Nullable RecipeHolder<?> processCommon(Set<Item> existing, RecipeHolder<? extends ItemStackToItemStackRecipe> recipeHolder, Ingredient ingredient) {
        var wrapper = new RecipeWrapper.Crush(recipeHolder.id(), ingredient);
        for (var output : recipeHolder.value().getOutputDefinition()) {
            wrapper = wrapper.withItems(output.copy());
        }

        return new RecipeHolder<>(wrapper.path, wrapper.asRecipe());
    }
}
