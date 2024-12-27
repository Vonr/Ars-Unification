package dev.qther.ars_unification.recipe;

import com.hollingsworth.arsnouveau.common.crafting.recipes.SpecialSingleInputRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.qther.ars_unification.ArsUnification;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record CutRecipe(
        Ingredient input,
        List<CutOutput> outputs,
        boolean skipBlockPlace
) implements SpecialSingleInputRecipe {

    public CutRecipe(Ingredient input, List<CutOutput> outputs) {
        this(input, outputs, false);
    }

    public List<ItemStack> getRolledOutputs(RandomSource random) {
        List<ItemStack> finalOutputs = new ArrayList<>();
        for (CutOutput cutRoll : outputs) {
            if (random.nextDouble() <= cutRoll.chance) {
                if (cutRoll.maxRange > 1) {
                    int num = random.nextInt(cutRoll.maxRange) + 1;
                    for (int i = 0; i < num; i++) {
                        finalOutputs.add(cutRoll.stack.copy());
                    }
                } else {
                    finalOutputs.add(cutRoll.stack.copy());
                }
            }
        }

        return finalOutputs;
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
        return AURecipeRegistry.CUT_SERIALIZER.get();
    }

    @Override
    @NotNull
    public RecipeType<?> getType() {
        return AURecipeRegistry.CUT_TYPE.get();
    }

    public record CutOutput(ItemStack stack, float chance, int maxRange) {

        public CutOutput(ItemStack stack, float chance) {
            this(stack, chance, 1);
        }

        public static final Codec<CutOutput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.fieldOf("stack").forGetter(CutOutput::stack),
                Codec.FLOAT.fieldOf("chance").forGetter(CutOutput::chance),
                Codec.INT.fieldOf("maxRange").forGetter(CutOutput::maxRange)
        ).apply(instance, CutOutput::new));
    }

    public static class Serializer implements RecipeSerializer<CutRecipe> {

        public static final MapCodec<CutRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("input").forGetter(CutRecipe::input),
                CutOutput.CODEC.listOf().fieldOf("output").forGetter(CutRecipe::outputs),
                Codec.BOOL.optionalFieldOf("skip_block_place", false).forGetter(CutRecipe::shouldSkipBlockPlace)
        ).apply(instance, CutRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CutRecipe> STREAM_CODEC = StreamCodec.of(
                CutRecipe.Serializer::toNetwork, CutRecipe.Serializer::fromNetwork
        );

        public static void toNetwork(RegistryFriendlyByteBuf buf, CutRecipe recipe) {
            buf.writeInt(recipe.outputs.size());
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.input);
            for (CutOutput i : recipe.outputs) {
                buf.writeFloat(i.chance);
                ItemStack.STREAM_CODEC.encode(buf, i.stack);
                buf.writeInt(i.maxRange);
            }
            buf.writeBoolean(recipe.skipBlockPlace);
        }

        public static CutRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            int length = buffer.readInt();
            Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            List<CutOutput> stacks = new ArrayList<>();

            for (int i = 0; i < length; i++) {
                try {
                    float chance = buffer.readFloat();
                    ItemStack outStack = ItemStack.STREAM_CODEC.decode(buffer);
                    int maxRange = buffer.readInt();
                    stacks.add(new CutOutput(outStack, chance, maxRange));
                } catch (Exception e) {
                    ArsUnification.LOGGER.error("could not deserialize recipe from network", e);
                    break;
                }
            }
            boolean skipBlockPlace = buffer.readBoolean();
            return new CutRecipe(input, stacks, skipBlockPlace);
        }

        @Override
        @NotNull
        public MapCodec<CutRecipe> codec() {
            return CODEC;
        }

        @Override
        @NotNull
        public StreamCodec<RegistryFriendlyByteBuf, CutRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
