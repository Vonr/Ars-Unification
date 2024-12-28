package dev.qther.ars_unification.processors.cut;

import aztech.modern_industrialization.MIFluids;
import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import dev.qther.ars_unification.ArsUnification;
import dev.qther.ars_unification.processors.Processor;
import dev.qther.ars_unification.recipe.RecipeWrappers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ModernIndustrializationCuttingMachineProcessor extends Processor<RecipeInput, MachineRecipe> {
    public ModernIndustrializationCuttingMachineProcessor(RecipeManager recipeManager) {
        super(recipeManager, MIMachineRecipeTypes.CUTTING_MACHINE);
    }

    @Override
    public Set<Item> getExistingInputs() {
        return ArsUnification.cutRecipesIngredientSet(this.recipeManager);
    }

    @Override
    public @Nullable Ingredient getIngredient(MachineRecipe recipe) {
        if (!recipe.fluidOutputs.isEmpty()) {
            return null;
        }

        for (var fluidIn : recipe.fluidInputs) {
            if (!fluidIn.fluid().isSame(MIFluids.LUBRICANT.asFluid())) {
                return null;
            }
        }

        var ingredientList = recipe.itemInputs;
        if (ingredientList.size() != 1) {
            return Ingredient.EMPTY;
        }

        var first = ingredientList.getFirst();
        var ingredient = first.ingredient();
        if (ingredient.isEmpty() || first.amount() != 1) {
            return Ingredient.EMPTY;
        }

        return ingredient;
    }

    @Override
    public @Nullable RecipeHolder<?> processCommon(Set<Item> existing, RecipeHolder<? extends MachineRecipe> recipeHolder, Ingredient ingredient) {
        var wrapper = new RecipeWrappers.Cut(recipeHolder.id(), ingredient);
        for (var output : recipeHolder.value().itemOutputs) {
            wrapper = wrapper.withItems(output.getStack(), output.probability());
        }

        return new RecipeHolder<>(wrapper.path, wrapper.asRecipe());
    }
}
