package dev.qther.ars_unification.processors.crush;

import de.ellpeck.actuallyadditions.mod.crafting.ActuallyRecipes;
import dev.qther.ars_unification.ArsUnification;
import dev.qther.ars_unification.Config;
import dev.qther.ars_unification.RecipeWrappers;
import dev.qther.ars_unification.mixin.RecipeManagerAccessor;
import dev.qther.ars_unification.processors.Processor;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.Map;

public class ActuallyAdditionsCrusherProcessor extends Processor {
    public ActuallyAdditionsCrusherProcessor(RecipeManager recipeManager) {
        super(recipeManager);
    }

    @Override
    public void processRecipes() {
        super.processRecipes();

        var existing = ArsUnification.crushRecipesIngredientSet(recipeManager);
        var recipes = this.recipeManager.getAllRecipesFor(ActuallyRecipes.Types.CRUSHING.get());

        Map<ResourceLocation, RecipeHolder<?>> toReplace = new Object2ObjectOpenHashMap<>(((RecipeManagerAccessor) this.recipeManager).getByName());

        for (var recipe : recipes) {
            if (Config.isExcluded(recipe.id())) {
                continue;
            }

            var crush = recipe.value();

            var ingredients = crush.getInput();
            if (ingredients.isEmpty()) {
                continue;
            }

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

                    var wrapper = new RecipeWrappers.Crush(recipe.id(), ingredients)
                        .withItems(crush.getOutputOne(), crush.getFirstChance())
                        .withItems(crush.getOutputTwo(), crush.getSecondChance());

                    var holder = new RecipeHolder<>(wrapper.path, wrapper.asRecipe());
                    toReplace.put(holder.id(), holder);

                    continue;
                } else if (value instanceof Ingredient.ItemValue item) {
                    if (item.item().isEmpty() || existing.contains(item.item().getItem())) {
                        continue;
                    }

                    var wrapper = new RecipeWrappers.Crush(recipe.id(), ingredients)
                            .withItems(crush.getOutputOne(), crush.getFirstChance())
                            .withItems(crush.getOutputTwo(), crush.getSecondChance());

                    var holder = new RecipeHolder<>(wrapper.path, wrapper.asRecipe());
                    toReplace.put(holder.id(), holder);

                    continue;
                }
            }

            for (var ing : ingredients.getItems()) {
                if (ing.isEmpty() || ing.getCount() != 1 || existing.contains(ing.getItem())) {
                    continue;
                }

                var wrapper = new RecipeWrappers.Crush(recipe.id(), ingredients)
                        .withItems(crush.getOutputOne(), crush.getFirstChance())
                        .withItems(crush.getOutputTwo(), crush.getSecondChance());

                var holder = new RecipeHolder<>(wrapper.path, wrapper.asRecipe());
                toReplace.put(holder.id(), holder);
            }
        }

        this.recipeManager.replaceRecipes(toReplace.values());
    }
}
