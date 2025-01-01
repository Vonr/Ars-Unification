package dev.qther.ars_unification.mixin.neg;

import alexthw.not_enough_glyphs.common.glyphs.EffectFlatten;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.qther.ars_unification.mixin.ars_nouveau.AbstractEffectMixin;
import dev.qther.ars_unification.recipe.CutRecipe;
import dev.qther.ars_unification.recipe.PressRecipe;
import dev.qther.ars_unification.setup.registry.AURecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(value = EffectFlatten.class, remap = false)
public class EffectFlattenMixin extends AbstractEffectMixin  {
    @Override
    public Set<AbstractAugment> editAugmentSet(Set<AbstractAugment> original) {
        var set = new HashSet<>(original);
        set.add(AugmentSensitive.INSTANCE);
        set.add(AugmentAOE.INSTANCE);
        set.add(AugmentPierce.INSTANCE);
        return set;
    }


    @Override
    public void editAugmentDescriptions(Map<AbstractAugment, String> map, CallbackInfo ci) {
        map.put(AugmentAOE.INSTANCE, "Increases the radius in which to look for items to process.");
        map.put(AugmentPierce.INSTANCE, "Increases the number of items Cut will process.");
        map.put(AugmentSensitive.INSTANCE, "Cut will try to process items nearby.");
    }

    @Override
    public void wrapResolve(HitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver, Operation<Void> original) {
        if (spellStats.isSensitive()) {
            double aoeBuff = spellStats.getAoeMultiplier();
            int pierceBuff = spellStats.getBuffCount(AugmentPierce.INSTANCE);
            int limit = (int) (4 + (4 * aoeBuff) + (4 * pierceBuff));
            List<ItemEntity> itemEntities = world.getEntitiesOfClass(ItemEntity.class, new AABB(BlockPos.containing(rayTraceResult.getLocation())).inflate(aoeBuff + 1.0));
            if (!itemEntities.isEmpty()) {
                ars_unification$pressItems(world, itemEntities, limit);
            }
        } else {
            original.call(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
        }
    }


    @Unique
    private static void ars_unification$pressItems(Level world, List<ItemEntity> itemEntities, int limit) {
        List<RecipeHolder<PressRecipe>> recipes = world.getRecipeManager().getAllRecipesFor(AURecipeRegistry.PRESS_TYPE.get());
        PressRecipe lastHit = null; // Cache this for AOE hits
        int itemsPressed = 0;
        for (ItemEntity IE : itemEntities) {
            if (itemsPressed >= limit) {
                break;
            }

            ItemStack stack = IE.getItem();
            Item item = stack.getItem();

            if (lastHit == null || !lastHit.matches(item.getDefaultInstance(), world)) {
                RecipeHolder<PressRecipe> holder = null;
                for (var recipe : recipes) {
                    if (recipe.value().matches(item.getDefaultInstance(), world)) {
                        holder = recipe;
                        break;
                    }
                }
                lastHit = holder == null ? null : holder.value();
            }

            if (lastHit == null) {
                continue;
            }

            while (!stack.isEmpty() && itemsPressed < limit) {
                List<ItemStack> outputs = lastHit.getRolledOutputs(world.random);
                stack.shrink(1);
                itemsPressed++;
                for (ItemStack result : outputs) {
                    world.addFreshEntity(new ItemEntity(world, IE.getX(), IE.getY(), IE.getZ(), result.copy()));
                }
            }

        }
    }
}
