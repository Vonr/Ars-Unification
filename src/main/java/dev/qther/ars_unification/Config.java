package dev.qther.ars_unification;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = ArsUnification.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.ConfigValue<List<? extends String>> EXCEPTIONS = BUILDER.comment("Recipe IDs to not take (deny-list)").defineListAllowEmpty("exceptions", List.of(), Config::validateRecipeID);

    public static int mekanismCrusher;
    public static int enderioSagMill;
    public static int integratedDynamicsSqueezer;
    public static boolean integratedDynamicsUseMechanical;
    public static int actuallyAdditionsCrusher;
    public static int modernIndustrializationMacerator;

    private static final ModConfigSpec.IntValue MEKANISM_CRUSHER = BUILDER.comment("Priority of Mekanism's Crusher recipes, -1 to disable").defineInRange("crush.mekanism.crusher", 100, -1, 2147483647);

    private static final ModConfigSpec.IntValue ENDERIO_SAG_MILL = BUILDER.comment("Priority of Ender IO's SAG Mill recipes, -1 to disable").defineInRange("crush.enderio.sag_mill", 110, -1, 2147483647);

    private static final ModConfigSpec.IntValue INTEGRATEDDYNAMICS_SQUEEZER = BUILDER.comment("Priority of Integrated Dynamics's Squeezer recipes, -1 to disable").defineInRange("crush.integrateddynamics.squeezer", 120, -1, 2147483647);
    private static final ModConfigSpec.BooleanValue INTEGRATEDDYNAMICS_USE_MECHANICAL = BUILDER.comment("Use Integrated Dynamics's Mechanical Squeezer recipes instead").define("crush.integrateddynamics.use_mechanical", true);

    private static final ModConfigSpec.IntValue ACTUALLYADDITIONS_CRUSHER = BUILDER.comment("Priority of Actually Additions's Crusher recipes, -1 to disable").defineInRange("crush.actuallyadditions.crusher", 80, -1, 2147483647);

    private static final ModConfigSpec.IntValue MODERN_INDUSTRIALIZATION_MACERATOR = BUILDER.comment("Priority of Modern Industrialization's Macerator recipes, -1 to disable").defineInRange("crush.modern_industrialization.macerator", 90, -1, 2147483647);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static Set<ResourceLocation> exceptions;

    private static boolean validateRecipeID(final Object obj) {
        return obj instanceof String itemName && ResourceLocation.tryParse(itemName) != null;
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        exceptions = EXCEPTIONS.get().stream().map(ResourceLocation::parse).collect(Collectors.toSet());

        mekanismCrusher = MEKANISM_CRUSHER.get();
        enderioSagMill = ENDERIO_SAG_MILL.get();
        integratedDynamicsSqueezer = INTEGRATEDDYNAMICS_SQUEEZER.get();
        integratedDynamicsUseMechanical = INTEGRATEDDYNAMICS_USE_MECHANICAL.get();
        actuallyAdditionsCrusher = ACTUALLYADDITIONS_CRUSHER.get();
        modernIndustrializationMacerator = MODERN_INDUSTRIALIZATION_MACERATOR.get();
    }
}
