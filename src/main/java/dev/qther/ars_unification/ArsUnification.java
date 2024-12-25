package dev.qther.ars_unification;

import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import dev.qther.ars_unification.processors.crush.*;
import dev.qther.ars_unification.processors.Processor;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.function.Function;

@Mod(ArsUnification.MODID)
public class ArsUnification {
    public static final String MODID = "ars_unification";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public ArsUnification(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        var mgr = event.getServer().getRecipeManager();
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
                    value.second().apply(mgr).processRecipes();
                }
            }
        }
    }

    public static ResourceLocation prefix(String str) {
        return ResourceLocation.fromNamespaceAndPath(MODID, str);
    }

    public static Set<Item> crushRecipesIngredientSet(RecipeManager recipeManager) {
        var recipes = recipeManager.getAllRecipesFor(RecipeRegistry.CRUSH_TYPE.get());

        Set<Item> set = new ObjectOpenHashSet<>(recipes.size());
        for (var recipe : recipes) {
            for (var stack : recipe.value().input().getItems()) {
                set.add(stack.getItem());
            }
        }

        return set;
    }
}
