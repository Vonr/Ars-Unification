package dev.qther.ars_unification.client.jei;

import alexthw.not_enough_glyphs.common.glyphs.effects.EffectFlatten;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectCut;
import dev.qther.ars_unification.ArsUnification;
import dev.qther.ars_unification.recipe.CutRecipe;
import dev.qther.ars_unification.recipe.PressRecipe;
import dev.qther.ars_unification.setup.registry.AURecipeRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JEIArsUnificationPlugin implements IModPlugin {
    public static final RecipeType<CutRecipe> CUT_RECIPE_TYPE = RecipeType.create(ArsUnification.MODID, AURecipeRegistry.CUT_RECIPE_ID, CutRecipe.class);
    public static final RecipeType<PressRecipe> PRESS_RECIPE_TYPE = RecipeType.create(ArsUnification.MODID, AURecipeRegistry.PRESS_RECIPE_ID, PressRecipe.class);

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ArsUnification.prefix("main");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        var gui = registry.getJeiHelpers().getGuiHelper();

        registry.addRecipeCategories(
                new CutRecipeCategory(gui),
                new PressRecipeCategory(gui)
        );
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registry) {
        List<CutRecipe> cutRecipes = new ArrayList<>();
        List<PressRecipe> pressRecipes = new ArrayList<>();

        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        for (var recipe : manager.getRecipes()) {
            switch (recipe.value()) {
                case CutRecipe cutRecipe -> cutRecipes.add(cutRecipe);
                case PressRecipe pressRecipe -> pressRecipes.add(pressRecipe);
                default -> {}
            }
        }
        registry.addRecipes(CUT_RECIPE_TYPE, cutRecipes);

        if (ModList.get().isLoaded("not_enough_glyphs")) {
            registry.addRecipes(PRESS_RECIPE_TYPE, pressRecipes);
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        registry.addRecipeCatalyst(ArsUnification.withAugmentTooltip(EffectCut.INSTANCE, AugmentSensitive.INSTANCE), CUT_RECIPE_TYPE);

        if (ModList.get().isLoaded("not_enough_glyphs")) {
            registry.addRecipeCatalyst(ArsUnification.withAugmentTooltip(EffectFlatten.INSTANCE, AugmentSensitive.INSTANCE), PRESS_RECIPE_TYPE);
        }
    }
}