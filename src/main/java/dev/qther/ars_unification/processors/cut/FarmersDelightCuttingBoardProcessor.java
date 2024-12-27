package dev.qther.ars_unification.processors.cut;

import dev.qther.ars_unification.ArsUnification;
import dev.qther.ars_unification.Config;
import dev.qther.ars_unification.mixin.RecipeManagerAccessor;
import dev.qther.ars_unification.processors.Processor;
import dev.qther.ars_unification.recipe.RecipeWrappers;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;

import java.util.Map;

public class FarmersDelightCuttingBoardProcessor extends Processor {
    public FarmersDelightCuttingBoardProcessor(RecipeManager recipeManager) {
        super(recipeManager);
    }

    @Override
    public void processRecipes() {
        super.processRecipes();

        var existing = ArsUnification.cutRecipesIngredientSet(recipeManager);
        var recipes = this.getSortedRecipes(ModRecipeTypes.CUTTING.get());

        Map<ResourceLocation, RecipeHolder<?>> toReplace = new Object2ObjectOpenHashMap<>(((RecipeManagerAccessor) this.recipeManager).getByName());

        for (var recipe : recipes) {
            if (Config.isExcluded(recipe.id())) {
                continue;
            }

            var board = recipe.value();

            var ingredientList = board.getIngredients();
            if (ingredientList.size() != 1) {
                continue;
            }

            var ingredients = ingredientList.getFirst();

            if (!ingredients.isCustom()) {
                var values = ingredients.getValues();
                if (values.length != 1) {
                    continue;
                }

                var value = values[0];

                if (value instanceof Ingredient.TagValue tag) {
                    if (tag.getItems().isEmpty() || tag.getItems().stream().anyMatch(i -> existing.contains(i.getItem()))) {
                        continue;
                    }

                    var wrapper = new RecipeWrappers.Cut(recipe.id(), ingredients);
                    for (var output : board.getResults()) {
                        wrapper = wrapper.withItems(output);
                    }

                    var holder = new RecipeHolder<>(wrapper.path, wrapper.asRecipe());
                    toReplace.put(holder.id(), holder);
                    for (var input : tag.getItems()) {
                        existing.add(input.getItem());
                    }

                    continue;
                } else if (value instanceof Ingredient.ItemValue item) {
                    if (item.item().isEmpty() || existing.contains(item.item().getItem())) {
                        continue;
                    }

                    var wrapper = new RecipeWrappers.Cut(recipe.id(), ingredients);
                    for (var output : board.getResults()) {
                        wrapper = wrapper.withItems(output);
                    }

                    var holder = new RecipeHolder<>(wrapper.path, wrapper.asRecipe());
                    toReplace.put(holder.id(), holder);
                    existing.add(item.item().getItem());

                    continue;
                }
            }

            for (var ing : ingredients.getItems()) {
                if (ing.isEmpty() || ing.getCount() != 1 || existing.contains(ing.getItem())) {
                    continue;
                }

                var wrapper = new RecipeWrappers.Cut(recipe.id(), Ingredient.of(ing));
                for (var output : board.getResults()) {
                    wrapper = wrapper.withItems(output);
                }

                var holder = new RecipeHolder<>(wrapper.path, wrapper.asRecipe());
                toReplace.put(holder.id(), holder);
                existing.add(ing.getItem());
            }
        }

        this.recipeManager.replaceRecipes(toReplace.values());
    }
}
