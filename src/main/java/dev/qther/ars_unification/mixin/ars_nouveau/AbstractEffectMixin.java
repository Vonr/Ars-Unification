package dev.qther.ars_unification.mixin.ars_nouveau;

import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = AbstractEffect.class, remap = false)
public abstract class AbstractEffectMixin extends AbstractSpellPartMixin {
    @WrapMethod(method = "onResolve")
    public void wrapResolve(HitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver, Operation<Void> original) {
        original.call(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
    }
}
