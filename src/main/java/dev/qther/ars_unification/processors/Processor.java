package dev.qther.ars_unification.processors;

import net.minecraft.world.item.crafting.RecipeManager;

public class Processor {
    public final RecipeManager recipeManager;

    public Processor(RecipeManager recipeManager) {
        this.recipeManager = recipeManager;
    }

    public void processRecipes() {}
}
