package dev.qther.ars_unification.processors.cut;

import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import dev.qther.ars_unification.ArsUnification;
import dev.qther.ars_unification.processors.MIProcessor;
import dev.qther.ars_unification.recipe.RecipeWrapper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ModernIndustrializationCuttingMachineProcessor extends MIProcessor {
    public ModernIndustrializationCuttingMachineProcessor(RecipeManager recipeManager) {
        super(recipeManager, MIMachineRecipeTypes.CUTTING_MACHINE);
    }

    @Override
    public Set<Item> getExistingInputs() {
        return ArsUnification.cutRecipesIngredientSet(this.recipeManager);
    }

    @Override
    public @Nullable RecipeHolder<?> processCommon(Set<Item> existing, RecipeHolder<? extends MachineRecipe> recipeHolder, Ingredient ingredient) {
        var wrapper = new RecipeWrapper.Cut(recipeHolder.id(), ingredient);
        for (var output : recipeHolder.value().itemOutputs) {
            wrapper = wrapper.withItems(output.getStack(), output.probability());
        }

        return new RecipeHolder<>(wrapper.path, wrapper.asRecipe());
    }
}
