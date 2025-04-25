package dev.qther.ars_unification.mixin.neg;

import alexthw.not_enough_glyphs.common.glyphs.effects.EffectFlatten;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.qther.ars_unification.mixin.ars_nouveau.AbstractEffectMixin;
import dev.qther.ars_unification.setup.registry.AURecipeRegistry;
import dev.qther.ars_unification.util.ItemUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(value = EffectFlatten.class, remap = false)
public class EffectFlattenMixin extends AbstractEffectMixin {
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
        if (world instanceof ServerLevel level && spellStats.isSensitive()) {
            double aoeBuff = spellStats.getAoeMultiplier();
            int pierceBuff = spellStats.getBuffCount(AugmentPierce.INSTANCE);
            int limit = (int) (4 + (4 * aoeBuff) + (4 * pierceBuff));
            List<ItemEntity> itemEntities = level.getEntitiesOfClass(ItemEntity.class, new AABB(BlockPos.containing(rayTraceResult.getLocation())).inflate(aoeBuff + 1.0));
            ItemUtil.processItems(level, itemEntities, limit, AURecipeRegistry.PRESS_TYPE.get(), r -> r.getRolledOutputs(level.random));
        } else {
            original.call(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
        }
    }
}
