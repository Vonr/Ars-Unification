package dev.qther.ars_unification.client.datagen;

import alexthw.not_enough_glyphs.common.glyphs.effects.EffectFlatten;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectCut;
import dev.qther.ars_unification.ArsUnification;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.TreeMap;

public class AULangDatagen extends LanguageProvider {

    private final Map<String, String> data = new TreeMap<>();

    public AULangDatagen(PackOutput output) {
        super(output, ArsUnification.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("ars_unification.cut_recipe", "Cut Glyph");
        add("emi.category.ars_unification.cut", data.get("ars_unification.cut_recipe"));
        add("ars_unification.press_recipe", "Flatten Glyph");
        add("emi.category.ars_unification.press", data.get("ars_unification.press_recipe"));

        add("ars_unification.augment_with", "Augment this glyph with %s to use these recipes.");

        addAugmentDescription(EffectCut.INSTANCE, AugmentAOE.INSTANCE, "Increases the radius in which to look for items to process.");
        addAugmentDescription(EffectCut.INSTANCE, AugmentPierce.INSTANCE, "Increases the number of items Cut will process.");
        addAugmentDescription(EffectCut.INSTANCE, AugmentSensitive.INSTANCE, "Cut will try to process items nearby.");

        addAugmentDescription(EffectFlatten.INSTANCE, AugmentAOE.INSTANCE, "Increases the radius in which to look for items to process.");
        addAugmentDescription(EffectFlatten.INSTANCE, AugmentPierce.INSTANCE, "Increases the number of items Flatten will process.");
        addAugmentDescription(EffectFlatten.INSTANCE, AugmentSensitive.INSTANCE, "Flatten will try to process items nearby.");
    }

    @Override
    public void add(@NotNull Item key, @NotNull String name) {
        super.add(key, name);
    }

    @Override
    public void add(@NotNull String key, @NotNull String value) {
        super.add(key, value);
        data.put(key, value);
    }

    public void addAugmentDescription(AbstractSpellPart part, AbstractAugment augment, String description) {
        add("ars_nouveau.augment_desc." + part.getRegistryName().getPath() + "_" + augment.getRegistryName().getPath(), description);
    }
}
