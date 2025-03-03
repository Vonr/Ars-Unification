package dev.qther.ars_unification.client.emi;

import com.hollingsworth.arsnouveau.common.spell.effect.EffectCut;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import dev.qther.ars_unification.ArsUnification;
import dev.qther.ars_unification.setup.registry.AURecipeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.NotNull;

@EmiEntrypoint
public class EmiArsUnificationPlugin implements EmiPlugin {
    public static final EmiStack CUT_GLYPH = EmiStack.of(EffectCut.INSTANCE.glyphItem);
    public static final EmiRecipeCategory CUT_CATEGORY = new EmiRecipeCategory(ArsUnification.prefix("cut"), CUT_GLYPH);

    public static EmiStack FLATTEN_GLYPH;
    public static EmiRecipeCategory PRESS_CATEGORY;

    @Override
    public void register(EmiRegistry registry) {
        this.registerCategories(registry);
        this.registerRecipes(registry);
    }

    public void registerCategories(EmiRegistry registry) {
        registry.addCategory(CUT_CATEGORY);
        registry.addWorkstation(CUT_CATEGORY, CUT_GLYPH);

        if (ModList.get().isLoaded("not_enough_glyphs")) {
            EmiNegCompat.registerCategories(registry);
        }
    }

    public void registerRecipes(@NotNull EmiRegistry registry) {
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        manager.getAllRecipesFor(AURecipeRegistry.CUT_TYPE.get()).stream().map(EmiCutRecipe::new).forEach(registry::addRecipe);
        manager.getAllRecipesFor(AURecipeRegistry.PRESS_TYPE.get()).stream().map(EmiPressRecipe::new).forEach(registry::addRecipe);
    }
}
