package dev.qther.ars_unification.recipe;

import com.hollingsworth.arsnouveau.common.crafting.recipes.CrushRecipe;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import dev.qther.ars_unification.ArsUnification;
import dev.qther.ars_unification.setup.registry.AURecipeRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class RecipeWrapper<R extends Recipe<?>, O> {
    public ResourceLocation path;
    public Ingredient ing;
    List<O> outputs = new ArrayList<>();

    public RecipeWrapper(String string, Ingredient ingredient){
        this.path = ArsUnification.prefix(string);
        this.ing = ingredient;
    }

    RecipeWrapper(String prefix, ResourceLocation location, Ingredient ingredient){
        this.path = ArsUnification.prefix(prefix + '/' + location.getNamespace() + '/' + location.getPath());
        this.ing = ingredient;
    }

    public abstract RecipeWrapper<R, O> withItems(ItemStack output, float chance);

    public abstract RecipeWrapper<R, O> withItems(ItemStack output);

    public abstract R asRecipe();

    public static class Crush extends RecipeWrapper<CrushRecipe, CrushRecipe.CrushOutput> {
        public Crush(ResourceLocation location, Ingredient ingredient){
            super(RecipeRegistry.CRUSH_RECIPE_ID, location, ingredient);
        }

        @Override
        public Crush withItems(ItemStack output, float chance) {
            if (output.isEmpty()) {
                return this;
            }
            this.outputs.add(new CrushRecipe.CrushOutput(output, chance));
            return this;
        }

        @Override
        public Crush withItems(ItemStack output) {
            if (output.isEmpty()) {
                return this;
            }
            this.outputs.add(new CrushRecipe.CrushOutput(output, 1.0f));
            return this;
        }

        @Override
        public CrushRecipe asRecipe() {
            return new CrushRecipe(this.ing, this.outputs);
        }
    }

    public static class Cut extends RecipeWrapper<CutRecipe, CutRecipe.CutOutput> {
        public Cut(ResourceLocation location, Ingredient ingredient){
            super(AURecipeRegistry.CUT_RECIPE_ID, location, ingredient);
        }

        @Override
        public Cut withItems(ItemStack output, float chance) {
            if (output.isEmpty()) {
                return this;
            }
            this.outputs.add(new CutRecipe.CutOutput(output, chance));
            return this;
        }

        @Override
        public Cut withItems(ItemStack output) {
            if (output.isEmpty()) {
                return this;
            }
            this.outputs.add(new CutRecipe.CutOutput(output, 1.0f));
            return this;
        }

        @Override
        public CutRecipe asRecipe() {
            return new CutRecipe(this.ing, this.outputs);
        }
    }

    public static class Press extends RecipeWrapper<PressRecipe, PressRecipe.PressOutput> {
        public Press(ResourceLocation location, Ingredient ingredient){
            super(AURecipeRegistry.PRESS_RECIPE_ID, location, ingredient);
        }

        @Override
        public Press withItems(ItemStack output, float chance) {
            if (output.isEmpty()) {
                return this;
            }
            this.outputs.add(new PressRecipe.PressOutput(output, chance));
            return this;
        }

        @Override
        public Press withItems(ItemStack output) {
            if (output.isEmpty()) {
                return this;
            }
            this.outputs.add(new PressRecipe.PressOutput(output, 1.0f));
            return this;
        }

        @Override
        public PressRecipe asRecipe() {
            return new PressRecipe(this.ing, this.outputs);
        }
    }
}
