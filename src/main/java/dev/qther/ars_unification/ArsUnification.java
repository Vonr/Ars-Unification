package dev.qther.ars_unification;

import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import dev.qther.ars_unification.processors.Processor;
import dev.qther.ars_unification.processors.crush.*;
import dev.qther.ars_unification.processors.cut.FarmersDelightCuttingBoardProcessor;
import dev.qther.ars_unification.processors.cut.MekanismSawmillProcessor;
import dev.qther.ars_unification.processors.cut.ModernIndustrializationCuttingMachineProcessor;
import dev.qther.ars_unification.setup.registry.AURecipeRegistry;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Mod(ArsUnification.MODID)
public class ArsUnification {
    public static final String MODID = "ars_unification";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public ArsUnification(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        NeoForge.EVENT_BUS.register(this);

        AURecipeRegistry.RECIPE_SERIALIZERS.register(modEventBus);
        AURecipeRegistry.RECIPE_TYPES.register(modEventBus);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDatapackSync(OnDatapackSyncEvent event) {
        processRecipes(event.getPlayerList().getServer().getRecipeManager());
    }
    
    record ProcessorInfo(int priority, String modid, Function<RecipeManager, Processor> constructor) {}

    public static void processRecipes(RecipeManager recipeManager) {
        var mods = ModList.get();
        List<ProcessorInfo> processors = new ArrayList<>();
        processors.add(new ProcessorInfo(Config.mekanismCrusher, "mekanism", MekanismCrusherProcessor::new));
        processors.add(new ProcessorInfo(Config.enderioSagMill, "enderio_machines", EnderIOSAGMillProcessor::new));
        processors.add(new ProcessorInfo(Config.integratedDynamicsSqueezer, "integrateddynamics", IntegratedDynamicsSqueezerProcessor::new));
        processors.add(new ProcessorInfo(Config.actuallyAdditionsCrusher, "actuallyadditions", ActuallyAdditionsCrusherProcessor::new));
        processors.add(new ProcessorInfo(Config.modernIndustrializationMacerator, "modern_industrialization", ModernIndustrializationMaceratorProcessor::new));

        processors.add(new ProcessorInfo(Config.mekanismSawMill, "mekanism", MekanismSawmillProcessor::new));
        processors.add(new ProcessorInfo(Config.modernIndustrializationCuttingMachine, "modern_industrialization", ModernIndustrializationCuttingMachineProcessor::new));
        processors.add(new ProcessorInfo(Config.farmersDelightCuttingBoard, "farmersdelight", FarmersDelightCuttingBoardProcessor::new));
        processors.removeIf(p -> p.priority == -1);
        processors.sort(Comparator.comparing(ProcessorInfo::priority));

        for (var processor : processors.reversed()) {
            if (mods.isLoaded(processor.modid)) {
                var p = processor.constructor.apply(recipeManager);
                ArsUnification.LOGGER.info("Running processor {}", p.getClass().getName());
                p.processRecipes();
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

    public static Set<Item> cutRecipesIngredientSet(RecipeManager recipeManager) {
        var recipes = recipeManager.getAllRecipesFor(AURecipeRegistry.CUT_TYPE.get());

        Set<Item> set = new ObjectOpenHashSet<>(recipes.size());
        for (var recipe : recipes) {
            for (var stack : recipe.value().input().getItems()) {
                set.add(stack.getItem());
            }
        }

        return set;
    }
}
