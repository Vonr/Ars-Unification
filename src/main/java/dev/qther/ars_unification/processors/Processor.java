package dev.qther.ars_unification.processors;

import dev.qther.ars_unification.Config;
import dev.qther.ars_unification.mixin.RecipeManagerAccessor;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class Processor<I extends RecipeInput, T extends Recipe<I>> {
    public final MinecraftServer server;
    public final RecipeType<? extends T> recipeType;

    public Processor(MinecraftServer server, RecipeType<? extends T> recipeType) {
        this.server = server;
        this.recipeType = recipeType;
    }

    public RecipeManager recipeManager() {
        return server.getRecipeManager();
    }

    public RegistryAccess registryAccess() {
        return server.registryAccess();
    }

    public abstract Set<Item> getExistingInputs();

    public abstract @Nullable Ingredient getIngredient(T recipe);

    public void processRecipes() {
        var existing = this.getExistingInputs();
        var recipes = this.getSortedRecipes();

        Map<ResourceLocation, RecipeHolder<?>> toReplace = new Object2ObjectOpenHashMap<>(((RecipeManagerAccessor) this.recipeManager()).getByName());

        for (var holder : recipes) {
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

                    var flattened = Ingredient.of(tag.getItems().stream().filter(stack -> !stack.isEmpty() && stack.getCount() == 1 && !existing.contains(stack.getItem())));
                    if (flattened.getItems().length != tag.getItems().size()) {
                        if (flattened.getItems().length > 0) {
                            var toAdd = this.makeRecipe(existing, holder, flattened);
                            if (toAdd != null) {
                                toReplace.put(toAdd.id(), toAdd);
                            }
                        }
                        continue;
                    }

                    var toAdd = this.makeRecipe(existing, holder, ingredient);
                    if (toAdd != null) {
                        toReplace.put(toAdd.id(), toAdd);
                    }

                    continue;
                } else if (value instanceof Ingredient.ItemValue item) {
                    if (item.item().isEmpty() || item.item().getCount() != 1 || existing.contains(item.item().getItem())) {
                        continue;
                    }

                    var toAdd = this.makeRecipe(existing, holder, ingredient);
                    if (toAdd != null) {
                        toReplace.put(toAdd.id(), toAdd);
                    }

                    continue;
                }
            }

            Ingredient usable = Ingredient.of(Arrays.stream(ingredient.getItems()).filter(stack -> !stack.isEmpty() && stack.getCount() == 1 && !existing.contains(stack.getItem())));
            if (usable.getItems().length > 0) {
                var toAdd = this.makeRecipe(existing, holder, usable);
                if (toAdd != null) {
                    toReplace.put(toAdd.id(), toAdd);
                }
            }
        }

        this.recipeManager().replaceRecipes(toReplace.values());
    }

    public abstract @Nullable RecipeHolder<?> processCommon(Set<Item> existing, RecipeHolder<? extends T> recipeHolder, Ingredient ingredient);

    public @Nullable RecipeHolder<?> makeRecipe(Set<Item> existing, RecipeHolder<? extends T> recipeHolder, Ingredient ingredient) {
        var out = this.processCommon(existing, recipeHolder, ingredient);
        for (var input : ingredient.getItems()) {
            existing.add(input.getItem());
        }
        return out;
    }

    public List<? extends RecipeHolder<? extends T>> getSortedRecipes() {
        var recipes = new ArrayList<>(this.recipeManager().getAllRecipesFor(this.recipeType));
        recipes.sort(Comparator.comparing(r -> r.id().toString()));
        return recipes;
    }
}
