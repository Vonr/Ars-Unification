package dev.qther.ars_unification.client.jei;

import com.google.common.base.Suppliers;
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
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@JeiPlugin
public class JeiArsUnificationPlugin implements IModPlugin {
    public static final Supplier<RecipeType<RecipeHolder<CutRecipe>>> CUT_RECIPE_TYPE = type(AURecipeRegistry.CUT_TYPE);
    public static final Supplier<RecipeType<RecipeHolder<PressRecipe>>> PRESS_RECIPE_TYPE = type(AURecipeRegistry.PRESS_TYPE);

    private static <R extends Recipe<?>> Supplier<RecipeType<RecipeHolder<R>>> type(Supplier<? extends net.minecraft.world.item.crafting.RecipeType<R>> ty) {
        return Suppliers.memoize(() -> RecipeType.createFromVanilla(ty.get()));
    }

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

    @SuppressWarnings("unchecked")
    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registry) {
        List<RecipeHolder<CutRecipe>> cutRecipes = new ArrayList<>();
        List<RecipeHolder<PressRecipe>> pressRecipes = new ArrayList<>();

        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        for (var recipe : manager.getRecipes()) {
            switch (recipe.value()) {
                case CutRecipe cutRecipe -> cutRecipes.add((RecipeHolder<CutRecipe>) recipe);
                case PressRecipe pressRecipe -> pressRecipes.add((RecipeHolder<PressRecipe>) recipe);
                default -> {}
            }
        }
        registry.addRecipes(CUT_RECIPE_TYPE.get(), cutRecipes);

        if (ModList.get().isLoaded("not_enough_glyphs")) {
            registry.addRecipes(PRESS_RECIPE_TYPE.get(), pressRecipes);
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        registry.addRecipeCatalyst(ArsUnification.withAugmentTooltip(EffectCut.INSTANCE, AugmentSensitive.INSTANCE), CUT_RECIPE_TYPE.get());

        if (ModList.get().isLoaded("not_enough_glyphs")) {
            JeiNegCompat.registerCategories(registry);
        }
    }
}