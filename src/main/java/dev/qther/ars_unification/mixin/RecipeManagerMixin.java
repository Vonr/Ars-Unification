package dev.qther.ars_unification.mixin;

import com.google.gson.JsonElement;
import dev.qther.ars_unification.Config;
import dev.qther.ars_unification.processors.Processor;
import dev.qther.ars_unification.processors.crush.*;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.function.Function;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At(value = "TAIL"), order = 2147483647)
    private void onApply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        var mods = ModList.get();
        Int2ObjectAVLTreeMap<Pair<String, Function<RecipeManager, Processor>>> processors = new Int2ObjectAVLTreeMap<>();
        processors.put(Config.mekanismCrusher, Pair.of("mekanism", MekanismCrusherProcessor::new));
        processors.put(Config.enderioSagMill, Pair.of("enderio_machines", EnderIOSAGMillProcessor::new));
        processors.put(Config.integratedDynamicsSqueezer, Pair.of("integrateddynamics", IntegratedDynamicsSqueezerProcessor::new));
        processors.put(Config.actuallyAdditionsCrusher, Pair.of("actuallyadditions", ActuallyAdditionsCrusherProcessor::new));
        processors.put(Config.modernIndustrializationMacerator, Pair.of("modern_industrialization", ModernIndustrializationMaceratorProcessor::new));

        for (var processor : processors.int2ObjectEntrySet().reversed()) {
            if (processor.getIntKey() != -1) {
                var value = processor.getValue();
                if (mods.isLoaded(value.first())) {
                    value.second().apply((RecipeManager) (Object) this).processRecipes();
                }
            }
        }
    }
}
