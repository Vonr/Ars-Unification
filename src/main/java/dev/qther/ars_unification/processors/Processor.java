package dev.qther.ars_unification.processors;

import net.minecraft.world.item.crafting.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Processor {
    public final RecipeManager recipeManager;

    public Processor(RecipeManager recipeManager) {
        this.recipeManager = recipeManager;
    }

    public void processRecipes() {
    }

    public <I extends RecipeInput, T extends Recipe<I>> List<RecipeHolder<T>> getSortedRecipes(RecipeType<T> recipeType) {
        var recipes = new ArrayList<>(this.recipeManager.getAllRecipesFor(recipeType));
        recipes.sort(Comparator.comparing(r -> r.id().toString()));
        return recipes;
    }
}
