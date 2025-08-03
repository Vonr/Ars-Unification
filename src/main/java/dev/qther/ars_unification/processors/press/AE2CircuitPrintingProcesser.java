package dev.qther.ars_unification.processors.press;

import appeng.recipes.AERecipeTypes;
import appeng.recipes.handlers.InscriberProcessType;
import appeng.recipes.handlers.InscriberRecipe;
import dev.qther.ars_unification.ArsUnification;
import dev.qther.ars_unification.Config;
import dev.qther.ars_unification.processors.Processor;
import dev.qther.ars_unification.recipe.RecipeWrapper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class AE2CircuitPrintingProcesser extends Processor<RecipeInput, InscriberRecipe> {
    public AE2CircuitPrintingProcesser(MinecraftServer server) {
        super(server, AERecipeTypes.INSCRIBER);
    }

    @Override
    public Set<Item> getExistingInputs() {
        return ArsUnification.pressRecipesIngredientSet(this.recipeManager());
    }

    @Override
    public @Nullable Ingredient getIngredient(InscriberRecipe recipe) {
        if (recipe.getProcessType() != InscriberProcessType.INSCRIBE) {
            return null;
        }

        var maybePress = recipe.getTopOptional();
        if (maybePress.isEmpty()) {
            return null;
        }

        if (!recipe.getBottomOptional().isEmpty()) {
            return null;
        }

        var presses = maybePress.getItems();
        if (presses.length != 1) {
            return null;
        }

        var press = presses[0];
        var id = BuiltInRegistries.ITEM.getKey(press.getItem()).toString();
        if (!Config.CONFIG.PRESS_AE2_INSCRIBER_CIRCUIT_PRINTING_PRESSES.get().contains(id)) {
            return null;
        }

        var resource = recipe.getMiddleInput();
        if (resource.isEmpty()) {
            return null;
        }

        // Ignore press duplication recipes
        if (recipe.getResultItem().is(press.getItem())) {
            return null;
        }

        return resource;
    }

    @Override
    public @Nullable RecipeHolder<?> processCommon(Set<Item> existing, RecipeHolder<? extends InscriberRecipe> recipeHolder, Ingredient ingredient) {
        return new RecipeWrapper.Press(recipeHolder.id(), ingredient)
                .withItems(recipeHolder.value().getResultItem().copy())
                .asHolder();
    }
}
