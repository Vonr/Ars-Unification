package dev.qther.ars_unification.processors.cut;

import aztech.modern_industrialization.MIFluids;
import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import dev.qther.ars_unification.ArsUnification;
import dev.qther.ars_unification.processors.MIProcessor;
import dev.qther.ars_unification.recipe.RecipeWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ModernIndustrializationCuttingMachineProcessor extends MIProcessor {
    public ModernIndustrializationCuttingMachineProcessor(MinecraftServer server) {
        super(server, MIMachineRecipeTypes.CUTTING_MACHINE, new Fluid[]{ MIFluids.LUBRICANT.asFluid() });
    }

    @Override
    public Set<Item> getExistingInputs() {
        return ArsUnification.cutRecipesIngredientSet(this.recipeManager());
    }

    @Override
    public @Nullable RecipeHolder<?> processCommon(Set<Item> existing, RecipeHolder<? extends MachineRecipe> recipeHolder, Ingredient ingredient) {
        var wrapper = new RecipeWrapper.Cut(recipeHolder.id(), ingredient);
        for (var output : recipeHolder.value().itemOutputs) {
            wrapper = wrapper.withItems(output.getStack(), output.probability());
        }

        return wrapper.asHolder();
    }
}
