package dev.qther.ars_unification;

import com.hollingsworth.arsnouveau.common.crafting.recipes.CrushRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class RecipeWrappers {
    public static class Crush {
        public ResourceLocation path;
        public Ingredient ing;

        public Crush(String string, Ingredient ingredient){
            this.path = ArsUnification.prefix(string);
            this.ing = ingredient;
        }

        public Crush(ResourceLocation location, Ingredient ingredient){
            this.path = ArsUnification.prefix(location.getNamespace() + '/' + location.getPath());
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
            return new CrushRecipe(this.ing, outputs);
        }
    }
}
