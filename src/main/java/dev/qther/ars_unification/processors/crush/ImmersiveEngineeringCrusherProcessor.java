package dev.qther.ars_unification.processors.crush;

import blusunrize.immersiveengineering.api.crafting.CrusherRecipe;
import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import dev.qther.ars_unification.ArsUnification;
import dev.qther.ars_unification.processors.Processor;
import dev.qther.ars_unification.recipe.RecipeWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ImmersiveEngineeringCrusherProcessor extends Processor<RecipeInput, CrusherRecipe> {
    public ImmersiveEngineeringCrusherProcessor(MinecraftServer server) {
        super(server, IERecipeTypes.CRUSHER.type().get());
    }

    @Override
    public Set<Item> getExistingInputs() {
        return ArsUnification.crushRecipesIngredientSet(this.recipeManager());
    }

    @Override
    public @Nullable Ingredient getIngredient(CrusherRecipe recipe) {
        return recipe.input;
    }

    @Override
    public @Nullable RecipeHolder<?> processCommon(Set<Item> existing, RecipeHolder<? extends CrusherRecipe> recipeHolder, Ingredient ingredient) {
        var recipe = recipeHolder.value();
        var wrapper = new RecipeWrapper.Crush(recipeHolder.id(), ingredient)
                .withItems(recipe.output.get());

        for (var output : recipe.secondaryOutputs) {
            wrapper = wrapper.withItems(output.stack().get(), output.chance());
        }

        return wrapper.asHolder();
    }
}
