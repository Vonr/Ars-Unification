package dev.qther.ars_unification.compat;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.zeroregard.ars_technica.helpers.SpellResolverHelpers;
import org.jetbrains.annotations.Nullable;

public class AUArsTechnicaCompat {
    public static boolean shouldDoubleOutputs(@Nullable SpellResolver resolver) {
        return SpellResolverHelpers.shouldDoubleOutputs(resolver);
    }
}
