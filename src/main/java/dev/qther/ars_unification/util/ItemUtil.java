package dev.qther.ars_unification.util;

import com.hollingsworth.arsnouveau.common.crafting.recipes.SpecialSingleInputRecipe;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Function;

public class ItemUtil {
    public static final Hash.Strategy<ItemStack> COUNT_INSENSITIVE_HASH_STRATEGY = new Hash.Strategy<ItemStack>() {
        @Override
        public int hashCode(ItemStack o) {
            var itemHash = o.getItem().hashCode();
            itemHash ^= o.getComponents().hashCode() + 0x9e3779b9 + (itemHash << 6) + (itemHash >> 2);
            return itemHash;
        }

        @Override
        public boolean equals(ItemStack a, ItemStack b) {
            return (a == null && b == null) || (a != null && b != null && ItemStack.isSameItemSameComponents(a, b));
        }
    };

    public static void drop(ServerLevel level, ItemStack stack, Vec3 pos) {
        if (stack.isEmpty()) {
            return;
        }

        var maxSize = stack.getMaxStackSize();
        while (!stack.isEmpty()) {
            level.addFreshEntity(new ItemEntity(level, pos.x, pos.y, pos.z, stack.copyWithCount(Math.min(maxSize, stack.getCount()))));
            stack.shrink(maxSize);
        }
    }

    public static <R extends SpecialSingleInputRecipe> void processItems(ServerLevel level, List<ItemEntity> itemEntities, int limit, RecipeType<R> type, Function<R, List<ItemStack>> outputExtractor) {
        if (itemEntities.isEmpty()) {
            return;
        }

        RecipeHolder<R> recipe = null;
        int itemsProcessed = 0;

        var results = new ObjectOpenCustomHashSet<>(8, COUNT_INSENSITIVE_HASH_STRATEGY);

        for (ItemEntity entity : itemEntities) {
            if (itemsProcessed >= limit) {
                break;
            }

            ItemStack stack = entity.getItem();
            recipe = level.getRecipeManager().getRecipeFor(type, new SingleRecipeInput(stack), level, recipe).orElse(null);
            if (recipe == null) {
                continue;
            }

            while (!stack.isEmpty() && itemsProcessed < limit) {
                List<ItemStack> outputs = outputExtractor.apply(recipe.value());
                stack.shrink(1);
                itemsProcessed++;
                for (ItemStack result : outputs) {
                    var existing = results.get(result);
                    if (existing != null) {
                        existing.setCount(result.getCount() + existing.getCount());
                    } else {
                        results.add(result);
                    }
                }
            }

            for (var result : results) {
                ItemUtil.drop(level, result, entity.position());
            }

            results.clear();
        }
    }
}
