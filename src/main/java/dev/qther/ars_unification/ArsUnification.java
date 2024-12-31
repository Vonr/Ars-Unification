package dev.qther.ars_unification;

import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import dev.qther.ars_unification.processors.Processor;
import dev.qther.ars_unification.processors.crush.*;
import dev.qther.ars_unification.processors.cut.FarmersDelightCuttingBoardProcessor;
import dev.qther.ars_unification.processors.cut.MekanismSawmillProcessor;
import dev.qther.ars_unification.processors.cut.ModernIndustrializationCuttingMachineProcessor;
import dev.qther.ars_unification.processors.press.ModernIndustrializationCompressorProcessor;
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
import net.neoforged.neoforge.common.ModConfigSpec;
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
        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SPEC);
        NeoForge.EVENT_BUS.register(this);

        AURecipeRegistry.RECIPE_SERIALIZERS.register(modEventBus);
        AURecipeRegistry.RECIPE_TYPES.register(modEventBus);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDatapackSync(OnDatapackSyncEvent event) {
        processRecipes(event.getPlayerList().getServer().getRecipeManager());
    }

    record ProcessorInfo(ModConfigSpec.IntValue priority, String modid,
                         Function<RecipeManager, Processor> constructor) {
    }

    private static final List<ProcessorInfo> PROCESSORS = new ArrayList<>();
    private static boolean conditionalProcessorsRegistered = false;

    static {
        // Crush
        PROCESSORS.add(new ProcessorInfo(Config.CONFIG.MEKANISM_CRUSHER, "mekanism", MekanismCrusherProcessor::new));
        PROCESSORS.add(new ProcessorInfo(Config.CONFIG.ENDERIO_SAG_MILL, "enderio_machines", EnderIOSAGMillProcessor::new));
        PROCESSORS.add(new ProcessorInfo(Config.CONFIG.INTEGRATEDDYNAMICS_SQUEEZER, "integrateddynamics", IntegratedDynamicsSqueezerProcessor::new));
        PROCESSORS.add(new ProcessorInfo(Config.CONFIG.ACTUALLYADDITIONS_CRUSHER, "actuallyadditions", ActuallyAdditionsCrusherProcessor::new));
        PROCESSORS.add(new ProcessorInfo(Config.CONFIG.MODERN_INDUSTRIALIZATION_MACERATOR, "modern_industrialization", ModernIndustrializationMaceratorProcessor::new));

        // Cut
        PROCESSORS.add(new ProcessorInfo(Config.CONFIG.MEKANISM_SAW_MILL, "mekanism", MekanismSawmillProcessor::new));
        PROCESSORS.add(new ProcessorInfo(Config.CONFIG.MODERN_INDUSTRIALIZATION_CUTTING_MACHINE, "modern_industrialization", ModernIndustrializationCuttingMachineProcessor::new));
        PROCESSORS.add(new ProcessorInfo(Config.CONFIG.FARMERS_DELIGHT_CUTTING_BOARD, "farmersdelight", FarmersDelightCuttingBoardProcessor::new));
    }

    public static void processRecipes(RecipeManager recipeManager) {
        var mods = ModList.get();

        if (!conditionalProcessorsRegistered) {
            conditionalProcessorsRegistered = true;
            if (mods.isLoaded("not_enough_glyphs")) {
                PROCESSORS.add(new ProcessorInfo(Config.CONFIG.MODERN_INDUSTRIALIZATION_COMPRESSOR, "modern_industrialization", ModernIndustrializationCompressorProcessor::new));
            }
        }

        PROCESSORS.sort(Comparator.comparing(p -> p.priority.get()));

        for (var processor : PROCESSORS.reversed()) {
            if (processor.priority.get() != -1 && mods.isLoaded(processor.modid)) {
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

    public static Set<Item> pressRecipesIngredientSet(RecipeManager recipeManager) {
        var recipes = recipeManager.getAllRecipesFor(AURecipeRegistry.PRESS_TYPE.get());

        Set<Item> set = new ObjectOpenHashSet<>(recipes.size());
        for (var recipe : recipes) {
            for (var stack : recipe.value().input().getItems()) {
                set.add(stack.getItem());
            }
        }

        return set;
    }
}
