package dev.qther.ars_unification.client.emi;

import alexthw.not_enough_glyphs.common.glyphs.effects.EffectFlatten;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import dev.qther.ars_unification.ArsUnification;

public class EmiNegCompat {
    public static void registerCategories(EmiRegistry registry) {
        EmiArsUnificationPlugin.FLATTEN_GLYPH = EmiStack.of(ArsUnification.withAugmentTooltip(EffectFlatten.INSTANCE, AugmentSensitive.INSTANCE));
        EmiArsUnificationPlugin.PRESS_CATEGORY = new EmiRecipeCategory(ArsUnification.prefix("press"), EmiArsUnificationPlugin.FLATTEN_GLYPH);
        registry.addCategory(EmiArsUnificationPlugin.PRESS_CATEGORY);
        registry.addWorkstation(EmiArsUnificationPlugin.PRESS_CATEGORY, EmiArsUnificationPlugin.FLATTEN_GLYPH);
    }
}
