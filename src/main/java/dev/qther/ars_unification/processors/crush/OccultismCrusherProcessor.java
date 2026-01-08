package dev.qther.ars_unification.processors.crush;

import com.klikli_dev.occultism.Occultism;
import com.klikli_dev.occultism.crafting.recipe.CrushingRecipe;
import com.klikli_dev.occultism.crafting.recipe.TieredSingleRecipeInput;
import com.klikli_dev.occultism.registry.OccultismRecipes;
import dev.qther.ars_unification.ArsUnification;
import dev.qther.ars_unification.Config;
import dev.qther.ars_unification.processors.Processor;
import dev.qther.ars_unification.recipe.RecipeWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class OccultismCrusherProcessor extends Processor<TieredSingleRecipeInput, CrushingRecipe> {
    public OccultismCrusherProcessor(MinecraftServer server) {
        super(server, OccultismRecipes.CRUSHING_TYPE.get());
    }

    @Override
    public Set<Item> getExistingInputs() {
        return ArsUnification.crushRecipesIngredientSet(this.recipeManager());
    }

    @Override
    public @Nullable Ingredient getIngredient(CrushingRecipe recipe) {
        var ingredients = recipe.getIngredients();
        if (ingredients.size() != 1 || ingredients.getFirst().isEmpty() || !doesTierMatch(recipe)) {
            return null;
        }

        return ingredients.getFirst();
    }

    private static boolean doesTierMatch(CrushingRecipe recipe) {
        var tier = Config.CONFIG.OCCULTISM_CRUSHER_TIER.getAsInt();
        boolean tierMatches = true;
        var minTier = recipe.getMinTier();
        var maxTier = recipe.getMaxTier();

        //tiers can be -1 in which case they are ignored, only if >= 0 we check
        if (minTier >= 0 && maxTier >= 0) {
            tierMatches = tier >= minTier && tier <= maxTier;
        } else if (minTier >= 0) {
            tierMatches = tier >= minTier;
        } else if (maxTier >= 0) {
            tierMatches = tier <= maxTier;
        }
        return tierMatches;
    }

    public static float getMultiplier() {
        var tier = Config.CONFIG.OCCULTISM_CRUSHER_TIER.getAsInt();
        var config = switch (tier) {
            case 1 -> Occultism.SERVER_CONFIG.spiritJobs.crusherFoliot.outputMultiplier;
            case 2 -> Occultism.SERVER_CONFIG.spiritJobs.crusherDjinni.outputMultiplier;
            case 3 -> Occultism.SERVER_CONFIG.spiritJobs.crusherAfrit.outputMultiplier;
            case 4 -> Occultism.SERVER_CONFIG.spiritJobs.crusherMarid.outputMultiplier;
            default -> {
                ArsUnification.LOGGER.warn("Unknown Occultism crusher tier {}", tier);
                yield Occultism.SERVER_CONFIG.spiritJobs.crusherFoliot.outputMultiplier;
            }
        };
        
        return config.get().floatValue();
    }

    @Override
    public @Nullable RecipeHolder<?> processCommon(Set<Item> existing, RecipeHolder<? extends CrushingRecipe> recipeHolder, Ingredient ingredient) {
        var recipe = recipeHolder.value();
        var wrapper = new RecipeWrapper.Crush(recipeHolder.id(), ingredient);

        ItemStack result;
        try {
            result = recipe.getResultItem(this.registryAccess()).copy();
        } catch (Exception e) {
            ArsUnification.LOGGER.warn("Failed to get result item", e);
            return null;
        }

        var outputMultiplier = recipe.getIgnoreCrushingMultiplier() ? 1 : getMultiplier();
        result.setCount((int) (result.getCount() * outputMultiplier));

        wrapper = wrapper.withItems(result);
        return wrapper.asHolder();
    }
}
