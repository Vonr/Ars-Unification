package dev.qther.ars_unification.processors.crush;

import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.recipe.SagMillingRecipe;
import dev.qther.ars_unification.ArsUnification;
import dev.qther.ars_unification.processors.Processor;
import dev.qther.ars_unification.recipe.RecipeWrapper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class EnderIOSAGMillProcessor extends Processor<SagMillingRecipe.Input, SagMillingRecipe> {
    public EnderIOSAGMillProcessor(RecipeManager recipeManager) {
        super(recipeManager, MachineRecipes.SAG_MILLING.type().get());
    }

    @Override
    public Set<Item> getExistingInputs() {
        return ArsUnification.crushRecipesIngredientSet(this.recipeManager);
    }

    @Override
    public @Nullable Ingredient getIngredient(SagMillingRecipe recipe) {
        return recipe.input();
    }

    @Override
    public @Nullable RecipeHolder<?> processCommon(Set<Item> existing, RecipeHolder<? extends SagMillingRecipe> recipeHolder, Ingredient ingredient) {
        var wrapper = new RecipeWrapper.Crush(recipeHolder.id(), ingredient);
        for (var output : recipeHolder.value().outputs()) {
            wrapper = wrapper.withItems(output.getItemStack(), output.chance());
        }

        return wrapper.asHolder();
    }
}
