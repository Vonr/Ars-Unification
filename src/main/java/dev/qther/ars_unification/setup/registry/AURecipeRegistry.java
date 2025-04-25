package dev.qther.ars_unification.setup.registry;

import dev.qther.ars_unification.ArsUnification;
import dev.qther.ars_unification.recipe.CutRecipe;
import dev.qther.ars_unification.recipe.PressRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AURecipeRegistry {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, ArsUnification.MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, ArsUnification.MODID);

    public static final String CUT_RECIPE_ID = "cut";
    public static final String PRESS_RECIPE_ID = "press";

    public static final DeferredHolder<RecipeType<?>, ModRecipeType<CutRecipe>> CUT_TYPE = RECIPE_TYPES.register(CUT_RECIPE_ID, ModRecipeType::new);
    public static final DeferredHolder<RecipeSerializer<?>, CutRecipe.Serializer> CUT_SERIALIZER = RECIPE_SERIALIZERS.register(CUT_RECIPE_ID, CutRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, ModRecipeType<PressRecipe>> PRESS_TYPE = RECIPE_TYPES.register(PRESS_RECIPE_ID, ModRecipeType::new);
    public static final DeferredHolder<RecipeSerializer<?>, PressRecipe.Serializer> PRESS_SERIALIZER = RECIPE_SERIALIZERS.register(PRESS_RECIPE_ID, PressRecipe.Serializer::new);

    public static class ModRecipeType<T extends Recipe<?>> implements RecipeType<T> {
        @Override
        public String toString() {
            return BuiltInRegistries.RECIPE_TYPE.getKey(this).toString();
        }
    }
}
