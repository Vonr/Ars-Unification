package dev.qther.ars_unification.processors.crush;

import dev.qther.ars_unification.ArsUnification;
import dev.qther.ars_unification.processors.OritechProcessor;
import dev.qther.ars_unification.recipe.RecipeWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;
import rearth.oritech.init.recipes.OritechRecipe;
import rearth.oritech.init.recipes.RecipeContent;

import java.util.Set;

public class OritechFragmentForgeProcessor extends OritechProcessor {
    public OritechFragmentForgeProcessor(MinecraftServer server) {
        super(server, RecipeContent.GRINDER);
    }

    @Override
    public Set<Item> getExistingInputs() {
        return ArsUnification.crushRecipesIngredientSet(this.recipeManager());
    }

    @Override
    public @Nullable RecipeHolder<?> processCommon(Set<Item> existing, RecipeHolder<? extends OritechRecipe> recipeHolder, Ingredient ingredient) {
        var recipe = recipeHolder.value();
        var wrapper = new RecipeWrapper.Crush(recipeHolder.id(), ingredient);
        for (var output : recipe.getResults()) {
            wrapper = wrapper.withItems(output);
        }

        return wrapper.asHolder();
    }
}
