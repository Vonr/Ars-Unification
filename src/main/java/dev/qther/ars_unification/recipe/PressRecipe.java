package dev.qther.ars_unification.recipe;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.crafting.recipes.SpecialSingleInputRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.qther.ars_unification.ArsUnification;
import dev.qther.ars_unification.Config;
import dev.qther.ars_unification.compat.AUArsTechnicaCompat;
import dev.qther.ars_unification.setup.registry.AURecipeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public record PressRecipe(
        Ingredient input,
        List<PressOutput> outputs,
        boolean skipBlockPlace
) implements SpecialSingleInputRecipe, IRandomOutputRecipe {

    public PressRecipe(Ingredient input, List<PressOutput> outputs) {
        this(input, outputs, false);
    }

    @Override
    public void rollOutputs(Consumer<ItemStack> consumer, SpellResolver resolver, SpellStats spellStats, RandomSource random) {
        for (var roll : outputs) {
            var doubleFromTechnica = roll.chance < 1.0 && ModList.get().isLoaded("ars_technica") && Config.CONFIG.ARS_TECHNICA_TRANSMUTATION_FOCUS_PRESS_CHANCE_OUTPUT_DOUBLING.get() && AUArsTechnicaCompat.shouldDoubleOutputs(resolver);

            if (random.nextDouble() <= roll.chance) {
                int num = roll.maxRange > 1 ? random.nextInt(roll.maxRange) + 1 : 1;
                for (int i = 0; i < num; i++) {
                    if (doubleFromTechnica) {
                        consumer.accept(roll.stack.copyWithCount(Math.min(roll.stack.getMaxStackSize(), roll.stack.getCount() * 2)));
                    } else {
                        consumer.accept(roll.stack.copy());
                    }
                }
            }
        }
    }

    @Override
    public boolean isOutputDeterministic() {
        for (var output : outputs) {
            if (!output.isDeterministic()) {
                return false;
            }
        }

        return true;
    }

    public boolean shouldSkipBlockPlace() {
        return this.skipBlockPlace;
    }

    @Override
    public boolean matches(SingleRecipeInput input, @NotNull Level level) {
        return this.input.test(input.getItem(0));
    }

    @Override
    @NotNull
    public RecipeSerializer<?> getSerializer() {
        return AURecipeRegistry.PRESS_SERIALIZER.get();
    }

    @Override
    @NotNull
    public RecipeType<?> getType() {
        return AURecipeRegistry.PRESS_TYPE.get();
    }

    public record PressOutput(ItemStack stack, float chance, int maxRange) {

        public PressOutput(ItemStack stack, float chance) {
            this(stack, chance, 1);
        }

        public static final Codec<PressOutput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.fieldOf("stack").forGetter(PressOutput::stack),
                Codec.FLOAT.fieldOf("chance").forGetter(PressOutput::chance),
                Codec.INT.fieldOf("maxRange").forGetter(PressOutput::maxRange)
        ).apply(instance, PressOutput::new));

        public boolean isDeterministic() {
            return this.chance >= 1.0 && this.maxRange <= 1;
        }
    }

    public static class Serializer implements RecipeSerializer<PressRecipe> {

        public static final MapCodec<PressRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("input").forGetter(PressRecipe::input),
                PressOutput.CODEC.listOf().fieldOf("output").forGetter(PressRecipe::outputs),
                Codec.BOOL.optionalFieldOf("skip_block_place", false).forGetter(PressRecipe::shouldSkipBlockPlace)
        ).apply(instance, PressRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, PressRecipe> STREAM_CODEC = StreamCodec.of(
                PressRecipe.Serializer::toNetwork, PressRecipe.Serializer::fromNetwork
        );

        public static void toNetwork(RegistryFriendlyByteBuf buf, PressRecipe recipe) {
            buf.writeInt(recipe.outputs.size());
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.input);
            for (PressOutput i : recipe.outputs) {
                buf.writeFloat(i.chance);
                ItemStack.STREAM_CODEC.encode(buf, i.stack);
                buf.writeInt(i.maxRange);
            }
            buf.writeBoolean(recipe.skipBlockPlace);
        }

        public static PressRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            int length = buffer.readInt();
            Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            List<PressOutput> stacks = new ArrayList<>();

            for (int i = 0; i < length; i++) {
                try {
                    float chance = buffer.readFloat();
                    ItemStack outStack = ItemStack.STREAM_CODEC.decode(buffer);
                    int maxRange = buffer.readInt();
                    stacks.add(new PressOutput(outStack, chance, maxRange));
                } catch (Exception e) {
                    ArsUnification.LOGGER.error("could not deserialize recipe from network", e);
                    break;
                }
            }
            boolean skipBlockPlace = buffer.readBoolean();
            return new PressRecipe(input, stacks, skipBlockPlace);
        }

        @Override
        @NotNull
        public MapCodec<PressRecipe> codec() {
            return CODEC;
        }

        @Override
        @NotNull
        public StreamCodec<RegistryFriendlyByteBuf, PressRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
