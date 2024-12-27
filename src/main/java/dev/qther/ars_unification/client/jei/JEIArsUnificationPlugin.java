package dev.qther.ars_unification.client.jei;

import com.hollingsworth.arsnouveau.client.container.IAutoFillTerminal;
import com.hollingsworth.arsnouveau.client.jei.CraftingTerminalTransferHandler;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectCut;
import dev.qther.ars_unification.ArsUnification;
import dev.qther.ars_unification.recipe.CutRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JEIArsUnificationPlugin implements IModPlugin {
    public static final RecipeType<CutRecipe> CUT_RECIPE_TYPE = RecipeType.create(ArsUnification.MODID, "cut", CutRecipe.class);

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ArsUnification.prefix("main");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(
                new CutRecipeCategory(registry.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registry) {
        List<CutRecipe> cutRecipes = new ArrayList<>();

        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        for (Recipe<?> i : manager.getRecipes().stream().map(RecipeHolder::value).toList()) {
            if (i instanceof CutRecipe cutRecipe) {
                cutRecipes.add(cutRecipe);
            }
        }
        registry.addRecipes(CUT_RECIPE_TYPE, cutRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        registry.addRecipeCatalyst(new ItemStack(EffectCut.INSTANCE.glyphItem), CUT_RECIPE_TYPE);
    }

    @Override
    public void registerRecipeTransferHandlers(@NotNull IRecipeTransferRegistration registration) {
        CraftingTerminalTransferHandler.registerTransferHandlers(registration);
    }

    private static IJeiRuntime jeiRuntime;

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
        JEIArsUnificationPlugin.jeiRuntime = jeiRuntime;
    }

    static {
        IAutoFillTerminal.updateSearch.add(new IAutoFillTerminal.ISearchHandler() {

            @Override
            public void setSearch(String text) {
                if (jeiRuntime != null) {
                    if (jeiRuntime.getIngredientFilter() != null) {
                        jeiRuntime.getIngredientFilter().setFilterText(text);
                    }
                }
            }

            @Override
            public String getSearch() {
                if (jeiRuntime != null) {
                    if (jeiRuntime.getIngredientFilter() != null) {
                        return jeiRuntime.getIngredientFilter().getFilterText();
                    }
                }
                return "";
            }

            @Override
            public String getName() {
                return "JEI";
            }
        });
    }
}