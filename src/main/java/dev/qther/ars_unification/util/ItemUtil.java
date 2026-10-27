package dev.qther.ars_unification.util;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.crafting.recipes.SpecialSingleInputRecipe;
import dev.qther.ars_unification.recipe.IRandomOutputRecipe;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ItemUtil {
    public static void drop(ServerLevel level, ItemStack stack, Vec3 pos) {
        if (stack.isEmpty()) {
            return;
        }

        var maxSize = stack.getMaxStackSize();
        while (!stack.isEmpty()) {
            var split = stack.split(maxSize);
            level.addFreshEntity(new ItemEntity(level, pos.x, pos.y, pos.z, split));
        }
    }

    public static void replaceItemEntity(ItemEntity entity, ItemStack stack) {
        var level = entity.level();
        entity.setItem(stack);
        entity.setDeltaMovement(level.random.nextDouble() * 0.2 - 0.1, 0.2, level.random.nextDouble() * 0.2 - 0.1);
        entity.lifespan = entity.getAge() + 6000;
        entity.setPickUpDelay(0);
    }

    public static <R extends SpecialSingleInputRecipe & IRandomOutputRecipe> void processItems(ServerLevel level, List<ItemEntity> itemEntities, int limit, GenericRecipeCache<R, SingleRecipeInput> recipeCache, SpellResolver resolver, SpellStats spellStats) {
        if (itemEntities.isEmpty()) {
            return;
        }

        RecipeHolder<R> recipe = null;
        int itemsProcessed = 0;

        var output = new ItemCollection();

        for (var entity : itemEntities) {
            if (itemsProcessed >= limit) {
                break;
            }

            var stack = entity.getItem();
            if (stack.isEmpty()) {
                continue;
            }

            if (recipe == null || !recipe.value().matches(stack, level)) {
                recipe = recipeCache.get(level, new SingleRecipeInput(stack));
                if (recipe == null) {
                    continue;
                }
            }

            var rolls = Math.min(stack.getCount(), limit - itemsProcessed);
            if (recipe.value().isOutputDeterministic()) {
                recipe.value().rollOutputs(s -> output.add(s, s.getCount() * rolls), resolver, spellStats, level.random);
            } else {
                for (int roll = 0; roll < rolls; roll++) {
                    recipe.value().rollOutputs(output::add, resolver, spellStats, level.random);
                }
            }

            stack.shrink(rolls);
            itemsProcessed += rolls;

            var outputIter = output.iterator();
            if (stack.isEmpty() && outputIter.hasNext())  {
                stack = outputIter.next();
                replaceItemEntity(entity, stack);
            }

            while (outputIter.hasNext()) {
                level.addFreshEntity(new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), outputIter.next()));
            }
            output.clear();

            // Remove item entity to prevent further actions (e.g. Pickup) in the same tick from doing unnecessary work
            if (stack.isEmpty()) {
                entity.discard();
            }
        }
    }
}
