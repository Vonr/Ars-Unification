package dev.qther.ars_unification;

import com.google.common.base.Stopwatch;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import dev.qther.ars_unification.processors.Processor;
import dev.qther.ars_unification.processors.crush.*;
import dev.qther.ars_unification.processors.cut.FarmersDelightCuttingBoardProcessor;
import dev.qther.ars_unification.processors.cut.ImmersiveEngineeringSawmillProcessor;
import dev.qther.ars_unification.processors.cut.MekanismSawmillProcessor;
import dev.qther.ars_unification.processors.cut.ModernIndustrializationCuttingMachineProcessor;
import dev.qther.ars_unification.processors.press.AE2CircuitPrintingProcesser;
import dev.qther.ars_unification.processors.press.ModernIndustrializationCompressorProcessor;
import dev.qther.ars_unification.setup.registry.AURecipeRegistry;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
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
                         Function<RecipeManager, Processor<?, ?>> constructor) {
    }

    private static final List<ProcessorInfo> PROCESSORS = new ArrayList<>();
    private static boolean processorsRegistered = false;

    public static void processRecipes(RecipeManager recipeManager) {
        var mods = ModList.get();

        if (!processorsRegistered) {
            processorsRegistered = true;

            // Crush
            PROCESSORS.add(new ProcessorInfo(Config.CONFIG.MEKANISM_CRUSHER, "mekanism", MekanismCrusherProcessor::new));
            PROCESSORS.add(new ProcessorInfo(Config.CONFIG.ENDERIO_SAG_MILL, "enderio_machines", EnderIOSAGMillProcessor::new));
            PROCESSORS.add(new ProcessorInfo(Config.CONFIG.INTEGRATEDDYNAMICS_SQUEEZER, "integrateddynamics", IntegratedDynamicsSqueezerProcessor::new));
            PROCESSORS.add(new ProcessorInfo(Config.CONFIG.ACTUALLYADDITIONS_CRUSHER, "actuallyadditions", ActuallyAdditionsCrusherProcessor::new));
            PROCESSORS.add(new ProcessorInfo(Config.CONFIG.MODERN_INDUSTRIALIZATION_MACERATOR, "modern_industrialization", ModernIndustrializationMaceratorProcessor::new));
            PROCESSORS.add(new ProcessorInfo(Config.CONFIG.IMMERSIVE_ENGINEERING_CRUSHER, "immersiveengineering", ImmersiveEngineeringCrusherProcessor::new));
            PROCESSORS.add(new ProcessorInfo(Config.CONFIG.OCCULTISM_CRUSHER, "occultism", OccultismCrusherProcessor::new));
            PROCESSORS.add(new ProcessorInfo(Config.CONFIG.CRUSH_AE2_INSCRIBER, "ae2", AE2InscriberProcesser::new));

            // Cut
            PROCESSORS.add(new ProcessorInfo(Config.CONFIG.MEKANISM_SAWMILL, "mekanism", MekanismSawmillProcessor::new));
            PROCESSORS.add(new ProcessorInfo(Config.CONFIG.MODERN_INDUSTRIALIZATION_CUTTING_MACHINE, "modern_industrialization", ModernIndustrializationCuttingMachineProcessor::new));
            PROCESSORS.add(new ProcessorInfo(Config.CONFIG.FARMERS_DELIGHT_CUTTING_BOARD, "farmersdelight", FarmersDelightCuttingBoardProcessor::new));
            PROCESSORS.add(new ProcessorInfo(Config.CONFIG.IMMERSIVE_ENGINEERING_SAWMILL, "immersiveengineering", ImmersiveEngineeringSawmillProcessor::new));

            if (mods.isLoaded("not_enough_glyphs")) {
                // Press
                PROCESSORS.add(new ProcessorInfo(Config.CONFIG.MODERN_INDUSTRIALIZATION_COMPRESSOR, "modern_industrialization", ModernIndustrializationCompressorProcessor::new));
                PROCESSORS.add(new ProcessorInfo(Config.CONFIG.PRESS_AE2_INSCRIBER_CIRCUIT_PRINTING, "ae2", AE2CircuitPrintingProcesser::new));
            }
        }

        if (!Config.SPEC.isLoaded()) {
            return;
        }

        PROCESSORS.sort(Comparator.comparing(p -> p.priority.get()));

        var totalSw = Stopwatch.createStarted();
        for (var processor : PROCESSORS.reversed()) {
            if (processor.priority.get() != -1 && mods.isLoaded(processor.modid)) {
                var p = processor.constructor.apply(recipeManager);
                var sw = Stopwatch.createStarted();
                p.processRecipes();
                sw.stop();
                ArsUnification.LOGGER.info("{} processed recipes in {}", p.getClass().getSimpleName(), sw);
            }
        }
        totalSw.stop();
        ArsUnification.LOGGER.info("Finished processing recipes in {}", totalSw);
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

    public static ItemStack withAugmentTooltip(AbstractSpellPart item, AbstractAugment augment) {
        var stack = item.getGlyph().getDefaultInstance();
        var component = Component.translatable("ars_unification.augment_with", Component.translatable(augment.getLocalizationKey())).withStyle(ChatFormatting.RESET).withStyle(ChatFormatting.GOLD);
        stack.set(DataComponents.LORE, new ItemLore(ObjectLists.singleton(component), ObjectLists.singleton(component)));
        return stack;
    }
}
