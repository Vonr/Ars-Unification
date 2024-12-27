package dev.qther.ars_unification.recipe;

import com.hollingsworth.arsnouveau.common.crafting.recipes.CrushRecipe;
import dev.qther.ars_unification.ArsUnification;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeWrappers {
    public static class Crush {
        public ResourceLocation path;
        public Ingredient ing;

        public Crush(String string, Ingredient ingredient){
            this.path = ArsUnification.prefix(string);
            this.ing = ingredient;
        }

        public Crush(ResourceLocation location, Ingredient ingredient){
            this.path = ArsUnification.prefix("crush/" + location.getNamespace() + '/' + location.getPath());
            this.ing = ingredient;
        }

        List<CrushRecipe.CrushOutput> outputs = new ArrayList<>();

        public Crush withItems(ItemStack output, float chance) {
            if (output.isEmpty()) {
                return this;
            }
            this.outputs.add(new CrushRecipe.CrushOutput(output, chance));
            return this;
        }

        public Crush withItems(ItemStack output) {
            if (output.isEmpty()) {
                return this;
            }
            this.outputs.add(new CrushRecipe.CrushOutput(output, 1.0f));
            return this;
        }

        public CrushRecipe asRecipe() {
            ArsUnification.LOGGER.info("Creating Crush Recipe for {}: {} -> {}",
                    path,
                    Arrays.stream(this.ing.getItems()).map(i -> BuiltInRegistries.ITEM.getKey(i.getItem()).toString()).collect(Collectors.joining(", ")),
                    this.outputs.stream().map(i -> BuiltInRegistries.ITEM.getKey(i.stack().getItem()).toString()).collect(Collectors.joining(", "))
            );
            return new CrushRecipe(this.ing, this.outputs);
        }
    }

    public static class Cut {
        public ResourceLocation path;
        public Ingredient ing;

        public Cut(String string, Ingredient ingredient){
            this.path = ArsUnification.prefix(string);
            this.ing = ingredient;
        }

        public Cut(ResourceLocation location, Ingredient ingredient){
            this.path = ArsUnification.prefix("cut/" + location.getNamespace() + '/' + location.getPath());
            this.ing = ingredient;
        }

        List<CutRecipe.CutOutput> outputs = new ArrayList<>();

        public Cut withItems(ItemStack output, float chance) {
            if (output.isEmpty()) {
                return this;
            }
            this.outputs.add(new CutRecipe.CutOutput(output, chance));
            return this;
        }

        public Cut withItems(ItemStack output) {
            if (output.isEmpty()) {
                return this;
            }
            this.outputs.add(new CutRecipe.CutOutput(output, 1.0f));
            return this;
        }

        public CutRecipe asRecipe() {
            ArsUnification.LOGGER.info("Creating Cut Recipe for {}: {} -> {}",
                    path,
                    Arrays.stream(this.ing.getItems()).map(i -> BuiltInRegistries.ITEM.getKey(i.getItem()).toString()).collect(Collectors.joining(", ")),
                    this.outputs.stream().map(i -> BuiltInRegistries.ITEM.getKey(i.stack().getItem()).toString()).collect(Collectors.joining(", "))
            );
            return new CutRecipe(this.ing, this.outputs);
        }
    }
}
