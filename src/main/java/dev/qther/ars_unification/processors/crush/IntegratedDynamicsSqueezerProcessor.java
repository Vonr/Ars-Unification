package dev.qther.ars_unification.processors.crush;

import dev.qther.ars_unification.ArsUnification;
import dev.qther.ars_unification.Config;
import dev.qther.ars_unification.processors.Processor;
import dev.qther.ars_unification.recipe.RecipeWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.cyclops.integrateddynamics.RegistryEntries;
import org.cyclops.integrateddynamics.core.recipe.type.RecipeSqueezer;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class IntegratedDynamicsSqueezerProcessor extends Processor<CraftingInput, RecipeSqueezer> {
    public IntegratedDynamicsSqueezerProcessor(MinecraftServer server) {
        super(
                server,
                Config.CONFIG.INTEGRATEDDYNAMICS_USE_MECHANICAL.get()
                        ? RegistryEntries.RECIPETYPE_MECHANICAL_SQUEEZER.get()
                        : RegistryEntries.RECIPETYPE_SQUEEZER.get()
        );
    }

    @Override
    public Set<Item> getExistingInputs() {
        return ArsUnification.crushRecipesIngredientSet(this.recipeManager());
    }

    @Override
    public @Nullable Ingredient getIngredient(RecipeSqueezer recipe) {
        if (recipe.getOutputFluid().isPresent()) {
            return null;
        }

        var ingredient = recipe.getInputIngredient();
        if (ingredient.isEmpty()) {
            return null;
        }

        return ingredient;
    }

    @Override
    public @Nullable RecipeHolder<?> processCommon(Set<Item> existing, RecipeHolder<? extends RecipeSqueezer> recipeHolder, Ingredient ingredient) {
        var wrapper = new RecipeWrapper.Crush(recipeHolder.id(), ingredient);
        for (var output : recipeHolder.value().getOutputItems()) {
            wrapper = wrapper.withItems(output.getIngredientFirst(), output.getChance());
        }

        return wrapper.asHolder();
    }
}
