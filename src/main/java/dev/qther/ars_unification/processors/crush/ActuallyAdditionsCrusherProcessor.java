package dev.qther.ars_unification.processors.crush;

import de.ellpeck.actuallyadditions.mod.crafting.ActuallyRecipes;
import de.ellpeck.actuallyadditions.mod.crafting.CrushingRecipe;
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

public class ActuallyAdditionsCrusherProcessor extends Processor<RecipeInput, CrushingRecipe> {
    public ActuallyAdditionsCrusherProcessor(RecipeManager recipeManager) {
        super(recipeManager, ActuallyRecipes.Types.CRUSHING.get());
    }

    @Override
    public Set<Item> getExistingInputs() {
        return ArsUnification.crushRecipesIngredientSet(this.recipeManager);
    }

    @Override
    public @Nullable Ingredient getIngredient(CrushingRecipe recipe) {
        return recipe.getInput();
    }

    @Override
    public @Nullable RecipeHolder<?> processCommon(Set<Item> existing, RecipeHolder<? extends CrushingRecipe> recipeHolder, Ingredient ingredient) {
        var recipe = recipeHolder.value();
        var wrapper = new RecipeWrapper.Crush(recipeHolder.id(), ingredient)
                .withItems(recipe.getOutputOne(), recipe.getFirstChance())
                .withItems(recipe.getOutputTwo(), recipe.getSecondChance());

        return wrapper.asHolder();
    }
}
