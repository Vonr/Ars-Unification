package dev.qther.ars_unification.processors.cut;

import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import blusunrize.immersiveengineering.api.crafting.SawmillRecipe;
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

public class ImmersiveEngineeringSawmillProcessor extends Processor<RecipeInput, SawmillRecipe> {
    public ImmersiveEngineeringSawmillProcessor(RecipeManager recipeManager) {
        super(recipeManager, IERecipeTypes.SAWMILL.type().get());
    }

    @Override
    public Set<Item> getExistingInputs() {
        return ArsUnification.cutRecipesIngredientSet(this.recipeManager);
    }

    @Override
    public @Nullable Ingredient getIngredient(SawmillRecipe recipe) {
        return recipe.input;
    }

    @Override
    public @Nullable RecipeHolder<?> processCommon(Set<Item> existing, RecipeHolder<? extends SawmillRecipe> recipeHolder, Ingredient ingredient) {
        var recipe = recipeHolder.value();
        var wrapper = new RecipeWrapper.Cut(recipeHolder.id(), ingredient)
                .withItems(recipe.output.get());

        for (var output : recipe.secondaryOutputs.get()) {
            wrapper = wrapper.withItems(output.copy());
        }

        for (var output : recipe.secondaryStripping.get()) {
            wrapper = wrapper.withItems(output.copy());
        }

        return wrapper.asHolder();
    }
}
