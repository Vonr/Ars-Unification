package dev.qther.ars_unification.processors.crush;

import appeng.recipes.AERecipeTypes;
import appeng.recipes.handlers.InscriberRecipe;
import dev.qther.ars_unification.ArsUnification;
import dev.qther.ars_unification.processors.Processor;
import dev.qther.ars_unification.recipe.RecipeWrapper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class AE2InscriberProcesser extends Processor<RecipeInput, InscriberRecipe> {
    public AE2InscriberProcesser(RecipeManager recipeManager) {
        super(recipeManager, AERecipeTypes.INSCRIBER);
    }

    @Override
    public Set<Item> getExistingInputs() {
        return ArsUnification.crushRecipesIngredientSet(this.recipeManager);
    }

    @Override
    public @Nullable Ingredient getIngredient(InscriberRecipe recipe) {
        if (!recipe.getTopOptional().isEmpty() || !recipe.getBottomOptional().isEmpty()) {
            return null;
        }

        Ingredient ingredient = recipe.getMiddleInput();
        if (ingredient.isEmpty()) {
            return null;
        }

        return ingredient;
    }

    @Override
    public @Nullable RecipeHolder<?> processCommon(Set<Item> existing, RecipeHolder<? extends InscriberRecipe> recipeHolder, Ingredient ingredient) {
        return new RecipeWrapper.Crush(recipeHolder.id(), ingredient)
                .withItems(recipeHolder.value().getResultItem())
                .asHolder();
    }
}
