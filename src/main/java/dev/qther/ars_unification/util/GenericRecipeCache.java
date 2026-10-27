package dev.qther.ars_unification.util;

import dev.qther.ars_unification.ArsUnification;
import dev.qther.ars_unification.recipe.CutRecipe;
import dev.qther.ars_unification.recipe.PressRecipe;
import dev.qther.ars_unification.setup.registry.AURecipeRegistry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayDeque;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class GenericRecipeCache<R extends Recipe<I>, I extends RecipeInput> {
    public static final GenericRecipeCache<CutRecipe, SingleRecipeInput> CUT_CACHE = new GenericRecipeCache<>(AURecipeRegistry.CUT_TYPE.get(), 8);
    public static final GenericRecipeCache<PressRecipe, SingleRecipeInput> PRESS_CACHE = new GenericRecipeCache<>(AURecipeRegistry.PRESS_TYPE.get(), 8);

    public final RecipeType<R> recipeType;
    protected ArrayDeque<RecipeHolder<? extends Recipe<I>>> cache;
    protected int size;

    protected int ticksAboveSize = 0b0000_0000_0000_0000_0000_0000_0000_0000;
    protected boolean addedThisTick = false;

    public GenericRecipeCache(RecipeType<R> recipeType, int size) {
        this.recipeType = recipeType;
        this.size = size;
        this.cache = new ArrayDeque<>(this.size);
        NeoForge.EVENT_BUS.addListener(this::onDatapackReload);
        NeoForge.EVENT_BUS.addListener(this::onServerTick);
    }

    protected void onDatapackReload(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) {
            this.clear();
        }
    }

    protected void onServerTick(ServerTickEvent.Post event) {
        if (this.addedThisTick) {
            this.ticksAboveSize = (this.ticksAboveSize << 1) + 1;
            if (Integer.bitCount(this.ticksAboveSize) >= 16) {
                var highestBit = Integer.highestOneBit(this.size);
                this.size = highestBit << 1;

                ArrayDeque<RecipeHolder<? extends Recipe<I>>> newCache = new ArrayDeque<>(this.size);
                newCache.addAll(this.cache);
                this.cache = newCache;
            }
        } else {
            this.ticksAboveSize <<= 1;
        }

        this.addedThisTick = false;
    }

    @SuppressWarnings("unchecked")
    public @Nullable RecipeHolder<R> get(Level level, I input) {
        if (input.isEmpty()) {
            return null;
        }

        for (var cached : this.cache) {
            if (cached.value() instanceof EmptyResultRecipe && cached.value().matches(input, level)) {
                return null;
            }

            var casted = (R) cached.value();
            if (casted.matches(input, level)) {
                return (RecipeHolder<R>) cached;
            }
        }

        var holder = level.getRecipeManager().getRecipeFor(this.recipeType, input, level);
        if (holder.isEmpty()) {
            this.add(new RecipeHolder<>(EmptyResultRecipe.ID, new EmptyResultRecipe<>(input)));
            return null;
        }

        var recipe = holder.get();
        this.add(recipe);
        return recipe;
    }

    private void add(RecipeHolder<? extends Recipe<I>> holder) {
        if (this.cache.size() == size) {
            this.cache.removeFirst();
        }
        this.cache.add(holder);
        this.addedThisTick = true;
    }


    public void clear() {
        this.cache.clear();
    }

    record EmptyResultRecipe<I extends RecipeInput>(RecipeInput input) implements Recipe<I> {
        public static final ResourceLocation ID = ArsUnification.prefix("empty");

        @Override
        public boolean matches(I input, Level level) {
            if (this.input instanceof SingleRecipeInput single && input instanceof SingleRecipeInput other) {
                return ItemStack.isSameItemSameComponents(single.item(), other.item());
            }
            return this.input.equals(input);
        }

        @Override
        public ItemStack assemble(I input, HolderLookup.Provider registries) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean canCraftInDimensions(int width, int height) {
            return true;
        }

        @Override
        public ItemStack getResultItem(HolderLookup.Provider registries) {
            return ItemStack.EMPTY;
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return RecipeSerializer.SHAPELESS_RECIPE;
        }

        @Override
        public RecipeType<?> getType() {
            return RecipeType.CRAFTING;
        }
    }
}
