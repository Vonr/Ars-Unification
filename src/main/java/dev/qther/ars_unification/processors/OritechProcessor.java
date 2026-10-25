package dev.qther.ars_unification.processors;

import dev.qther.ars_unification.ArsUnification;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;
import rearth.oritech.init.recipes.OritechRecipe;

public abstract class OritechProcessor extends Processor<RecipeInput, OritechRecipe> {
    public Fluid[] ignoredFluids;

    public OritechProcessor(MinecraftServer server, RecipeType<OritechRecipe> type, Fluid[] ignoredFluids) {
        super(server, type);
        this.ignoredFluids = ignoredFluids;
    }

    public OritechProcessor(MinecraftServer server, RecipeType<OritechRecipe> type) {
        this(server, type, new Fluid[0]);
    }

    @Override
    public @Nullable Ingredient getIngredient(OritechRecipe recipe) {
        if (!recipe.getFluidOutputs().isEmpty()) {
            ArsUnification.LOGGER.info("skipping due to fluid output");
            return null;
        }

        var fluidIn = recipe.getFluidInput();
        if (fluidIn != null && !fluidIn.isEmpty()) {
            var fluidStacks = fluidIn.getFluidStacks();
            if (!fluidStacks.isEmpty()) {
                var isIgnored = false;
                fluidIngredients:
                for (var fluid : fluidStacks) {
                    if (fluid.isEmpty()) {
                        isIgnored = true;
                        break;
                    }

                    for (var ignored : ignoredFluids) {
                        if (fluid.getFluid().isSame(ignored)) {
                            isIgnored = true;
                            break fluidIngredients;
                        }
                    }
                }

                if (!isIgnored) {
                    return null;
                }
            }
        }

        var ingredientList = recipe.getInputs();
        if (ingredientList.size() != 1) {
            return Ingredient.EMPTY;
        }

        var ingredient = ingredientList.getFirst();
        if (ingredient.isEmpty()) {
            return Ingredient.EMPTY;
        }

        return ingredient;
    }
}
