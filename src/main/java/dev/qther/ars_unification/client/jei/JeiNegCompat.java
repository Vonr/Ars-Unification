package dev.qther.ars_unification.client.jei;

import alexthw.not_enough_glyphs.common.glyphs.effects.EffectFlatten;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import dev.qther.ars_unification.ArsUnification;
import mezz.jei.api.registration.IRecipeCatalystRegistration;

public class JeiNegCompat {
    public static void registerCategories(IRecipeCatalystRegistration registry) {
        registry.addRecipeCatalyst(ArsUnification.withAugmentTooltip(EffectFlatten.INSTANCE, AugmentSensitive.INSTANCE), JeiArsUnificationPlugin.PRESS_RECIPE_TYPE.get());
    }
}
