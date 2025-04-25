package dev.qther.ars_unification.mixin.ars_nouveau;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Set;

@Mixin(value = AbstractSpellPart.class, remap = false)
public abstract class AbstractSpellPartMixin {
    @ModifyReturnValue(method = "augmentSetOf", at = @At("RETURN"))
    public Set<AbstractAugment> editAugmentSet(Set<AbstractAugment> original) {
        return original;
    }

    @Inject(method = "addAugmentDescriptions", at = @At("RETURN"))
    public void editAugmentDescriptions(Map<AbstractAugment, String> map, CallbackInfo ci) {}
}
