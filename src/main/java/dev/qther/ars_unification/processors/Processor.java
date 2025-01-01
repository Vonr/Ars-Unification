package dev.qther.ars_unification.processors;

import dev.qther.ars_unification.Config;
import dev.qther.ars_unification.mixin.RecipeManagerAccessor;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class Processor<I extends RecipeInput, T extends Recipe<I>> {
    public final RecipeManager recipeManager;
    public final RecipeType<? extends T> recipeType;

    public Processor(RecipeManager recipeManager, RecipeType<? extends T> recipeType) {
        this.recipeManager = recipeManager;
        this.recipeType = recipeType;
    }

    public abstract Set<Item> getExistingInputs();

    public abstract @Nullable Ingredient getIngredient(T recipe);

    public void processRecipes() {
        var existing = this.getExistingInputs();
        var recipes = this.getSortedRecipes();

        Map<ResourceLocation, RecipeHolder<?>> toReplace = new Object2ObjectOpenHashMap<>(((RecipeManagerAccessor) this.recipeManager).getByName());

        nextRecipe: for (var holder : recipes) {
            if (Config.isExcluded(holder.id())) {
                continue;
            }

            var recipe = holder.value();

            var ingredient = this.getIngredient(recipe);
            if (ingredient == null || ingredient.isEmpty()) {
                continue;
            }

            if (!ingredient.isCustom()) {
                var values = ingredient.getValues();
                if (values.length != 1) {
                    continue;
                }

                var value = values[0];

                if (value instanceof Ingredient.TagValue tag) {
                    if (tag.getItems().isEmpty()) {
                        continue;
                    }

                    for (ItemStack i : tag.getItems()) {
                        if (existing.contains(i.getItem())) {
                            continue nextRecipe;
                        }
                    }

                    var toAdd = this.processTag(existing, holder, ingredient);
                    if (toAdd != null) {
                        toReplace.put(toAdd.id(), toAdd);
                    }

                    continue;
                } else if (value instanceof Ingredient.ItemValue item) {
                    if (item.item().isEmpty() || existing.contains(item.item().getItem())) {
                        continue;
                    }

                    var toAdd = this.processItem(existing, holder, ingredient);
                    if (toAdd != null) {
                        toReplace.put(toAdd.id(), toAdd);
                    }

                    continue;
                }
            }

            for (var stack : ingredient.getItems()) {
                if (stack.isEmpty() || stack.getCount() != 1 || existing.contains(stack.getItem())) {
                    continue;
                }

                var toAdd = this.processStack(existing, holder, stack);
                if (toAdd != null) {
                    toReplace.put(toAdd.id(), toAdd);
                }
            }
        }

        this.recipeManager.replaceRecipes(toReplace.values());
    }

    public abstract @Nullable RecipeHolder<?> processCommon(Set<Item> existing, RecipeHolder<? extends T> recipeHolder, Ingredient ingredient);

    public @Nullable RecipeHolder<?> processStack(Set<Item> existing, RecipeHolder<? extends T> recipeHolder, ItemStack stack) {
        var out = this.processCommon(existing, recipeHolder, Ingredient.of(stack));
        existing.add(stack.getItem());
        return out;
    }

    public @Nullable RecipeHolder<?> processTag(Set<Item> existing, RecipeHolder<? extends T> recipeHolder, Ingredient ingredient) {
        var out = this.processCommon(existing, recipeHolder, ingredient);
        for (var input : ingredient.getValues()[0].getItems()) {
            existing.add(input.getItem());
        }
        return out;
    }

    public @Nullable RecipeHolder<?> processItem(Set<Item> existing, RecipeHolder<? extends T> recipeHolder, Ingredient ingredient) {
        var out = this.processCommon(existing, recipeHolder, ingredient);
        existing.add(((Ingredient.ItemValue) ingredient.getValues()[0]).item().getItem());
        return out;
    }

    public List<? extends RecipeHolder<? extends T>> getSortedRecipes() {
        var recipes = new ArrayList<>(this.recipeManager.getAllRecipesFor(this.recipeType));
        recipes.sort(Comparator.comparing(r -> r.id().toString()));
        return recipes;
    }
}
