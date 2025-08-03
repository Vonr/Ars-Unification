package dev.qther.ars_unification.processors.cut;

import dev.qther.ars_unification.ArsUnification;
import dev.qther.ars_unification.processors.Processor;
import dev.qther.ars_unification.recipe.RecipeWrapper;
import mekanism.api.recipes.MekanismRecipeTypes;
import mekanism.api.recipes.SawmillRecipe;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class MekanismSawmillProcessor extends Processor<SingleRecipeInput, SawmillRecipe> {
    public MekanismSawmillProcessor(MinecraftServer server) {
        super(server, MekanismRecipeTypes.TYPE_SAWING.get());
    }

    @Override
    public Set<Item> getExistingInputs() {
        return ArsUnification.cutRecipesIngredientSet(this.recipeManager());
    }

    @Override
    public @Nullable Ingredient getIngredient(SawmillRecipe recipe) {
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
    public @Nullable RecipeHolder<?> processCommon(Set<Item> existing, RecipeHolder<? extends SawmillRecipe> recipeHolder, Ingredient ingredient) {
        var wrapper = new RecipeWrapper.Cut(recipeHolder.id(), ingredient);
        var recipe = recipeHolder.value();
        for (var output : recipe.getMainOutputDefinition()) {
            wrapper = wrapper.withItems(output.copy());
        }
        for (var output : recipe.getSecondaryOutputDefinition()) {
            wrapper = wrapper.withItems(output.copy(), (float) recipe.getSecondaryChance());
        }

        return wrapper.asHolder();
    }
}
