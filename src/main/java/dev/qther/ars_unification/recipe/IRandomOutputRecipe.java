package dev.qther.ars_unification.recipe;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public interface IRandomOutputRecipe {
    void rollOutputs(Consumer<ItemStack> consumer, SpellResolver resolver, SpellStats spellStats, RandomSource random);

    boolean isOutputDeterministic();
}
