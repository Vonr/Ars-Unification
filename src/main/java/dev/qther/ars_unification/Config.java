package dev.qther.ars_unification;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@EventBusSubscriber(modid = ArsUnification.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    public static final Config CONFIG;
    public static final ModConfigSpec SPEC;

    public Matcher exceptions;

    public ModConfigSpec.ConfigValue<List<? extends String>> EXCEPTIONS;

    public ModConfigSpec.BooleanValue ARS_TECHNICA_TRANSMUTATION_FOCUS_CUT_INPUT_AMOUNT_DOUBLING;
    public ModConfigSpec.BooleanValue ARS_TECHNICA_TRANSMUTATION_FOCUS_CUT_CHANCE_OUTPUT_DOUBLING;
    public ModConfigSpec.BooleanValue ARS_TECHNICA_TRANSMUTATION_FOCUS_PRESS_INPUT_AMOUNT_DOUBLING;
    public ModConfigSpec.BooleanValue ARS_TECHNICA_TRANSMUTATION_FOCUS_PRESS_CHANCE_OUTPUT_DOUBLING;

    public ModConfigSpec.IntValue MEKANISM_CRUSHER;
    public ModConfigSpec.IntValue ENDERIO_SAG_MILL;
    public ModConfigSpec.IntValue INTEGRATEDDYNAMICS_SQUEEZER;
    public ModConfigSpec.BooleanValue INTEGRATEDDYNAMICS_USE_MECHANICAL;
    public ModConfigSpec.IntValue ACTUALLYADDITIONS_CRUSHER;
    public ModConfigSpec.IntValue MODERN_INDUSTRIALIZATION_MACERATOR;
    public ModConfigSpec.IntValue IMMERSIVE_ENGINEERING_CRUSHER;
    public ModConfigSpec.IntValue OCCULTISM_CRUSHER;
    public ModConfigSpec.IntValue OCCULTISM_CRUSHER_TIER;
    public ModConfigSpec.IntValue CRUSH_AE2_INSCRIBER;

    public ModConfigSpec.IntValue MEKANISM_SAWMILL;
    public ModConfigSpec.IntValue MODERN_INDUSTRIALIZATION_CUTTING_MACHINE;
    public ModConfigSpec.IntValue FARMERS_DELIGHT_CUTTING_BOARD;
    public ModConfigSpec.IntValue IMMERSIVE_ENGINEERING_SAWMILL;

    public ModConfigSpec.IntValue MODERN_INDUSTRIALIZATION_COMPRESSOR;
    public ModConfigSpec.IntValue PRESS_AE2_INSCRIBER_CIRCUIT_PRINTING;
    public ModConfigSpec.ConfigValue<List<? extends String>> PRESS_AE2_INSCRIBER_CIRCUIT_PRINTING_PRESSES;

    private Config(ModConfigSpec.Builder builder) {
        EXCEPTIONS = builder.comment("List of Regular Expressions to deny recipe unification of").defineListAllowEmpty("exceptions", List.of(), () -> "", Config::validateRegex);

        builder = builder.push("compat");
        builder = builder.push("ars_technica");
        builder = builder.push("transmutation_focus");
        ARS_TECHNICA_TRANSMUTATION_FOCUS_CUT_INPUT_AMOUNT_DOUBLING = builder.comment("Whether to double amount of items processed by Cut when a Focus of Transmutation from Ars Technica is equipped.").define("cut_double_processing_amount", true);
        ARS_TECHNICA_TRANSMUTATION_FOCUS_CUT_CHANCE_OUTPUT_DOUBLING = builder.comment("Whether to double chance based outputs for Cut-type recipes when a Focus of Transmutation from Ars Technica is equipped.").define("cut_double_chance_outputs", true);
        ARS_TECHNICA_TRANSMUTATION_FOCUS_PRESS_INPUT_AMOUNT_DOUBLING = builder.comment("Whether to double amount of items processed by Flatten when a Focus of Transmutation from Ars Technica is equipped.").define("press_double_processing_amount", true);
        ARS_TECHNICA_TRANSMUTATION_FOCUS_PRESS_CHANCE_OUTPUT_DOUBLING = builder.comment("Whether to double chance based outputs for Press-type recipes when a Focus of Transmutation from Ars Technica is equipped.").define("press_double_chance_outputs", true);
        builder = builder.pop();
        builder = builder.pop();
        builder = builder.pop();

        builder = builder.push("crush");
        MEKANISM_CRUSHER = builder.comment("Priority of Mekanism's Crusher recipes, -1 to disable").defineInRange("mekanism.crusher.priority", 100, -1, Integer.MAX_VALUE);
        ENDERIO_SAG_MILL = builder.comment("Priority of Ender IO's SAG Mill recipes, -1 to disable").defineInRange("enderio.sag_mill.priority", 110, -1, Integer.MAX_VALUE);
        INTEGRATEDDYNAMICS_SQUEEZER = builder.comment("Priority of Integrated Dynamics's Squeezer recipes, -1 to disable").defineInRange("integrateddynamics.squeezer.priority", 120, -1, Integer.MAX_VALUE);
        INTEGRATEDDYNAMICS_USE_MECHANICAL = builder.comment("Use the Mechanical Squeezer's recipes instead").define("integrateddynamics.squeezer.mechanical", true);
        ACTUALLYADDITIONS_CRUSHER = builder.comment("Priority of Actually Additions's Crusher recipes, -1 to disable").defineInRange("actuallyadditions.crusher.priority", 80, -1, Integer.MAX_VALUE);
        MODERN_INDUSTRIALIZATION_MACERATOR = builder.comment("Priority of Modern Industrialization's Macerator recipes, -1 to disable").defineInRange("modern_industrialization.macerator.priority", 90, -1, Integer.MAX_VALUE);
        IMMERSIVE_ENGINEERING_CRUSHER = builder.comment("Priority of Immersive Engineering's Crusher recipes, -1 to disable").defineInRange("immersiveengineering.crusher.priority", 85, -1, Integer.MAX_VALUE);
        OCCULTISM_CRUSHER = builder.comment("Priority of Occultism's Crusher recipes, -1 to disable").defineInRange("occultism.crusher.priority", 115, -1, Integer.MAX_VALUE);
        OCCULTISM_CRUSHER_TIER = builder.comment("Tier to use for Occultism's Crusher").defineInRange("occultism.crusher.tier", 1, 1, 4);
        CRUSH_AE2_INSCRIBER = builder.comment("Priority of AE2's Inscriber recipes, -1 to disable").defineInRange("ae2.inscriber.priority", 20, -1, Integer.MAX_VALUE);
        builder = builder.pop();

        builder = builder.push("cut");
        MEKANISM_SAWMILL = builder.comment("Priority of Mekanism's Sawmill recipes, -1 to disable").defineInRange("mekanism.sawmill.priority", 100, -1, Integer.MAX_VALUE);
        MODERN_INDUSTRIALIZATION_CUTTING_MACHINE = builder.comment("Priority of Modern Industrialization's Cutting Machine recipes, -1 to disable").defineInRange("modern_industrialization.cutting_machine.priority", -1, -1, Integer.MAX_VALUE);
        FARMERS_DELIGHT_CUTTING_BOARD = builder.comment("Priority of Farmer's Delight's Cutting Board recipes, -1 to disable").defineInRange("farmersdelight.cutting_board.priority", 60, -1, Integer.MAX_VALUE);
        IMMERSIVE_ENGINEERING_SAWMILL = builder.comment("Priority of Immersive Engineering's Sawmill recipes, -1 to disable").defineInRange("immersiveengineering.sawmill.priority", 80, -1, Integer.MAX_VALUE);
        builder = builder.pop();

        builder = builder.push("press");
        MODERN_INDUSTRIALIZATION_COMPRESSOR = builder.comment("Priority of Modern Industrialization's Compressor recipes, -1 to disable").defineInRange("modern_industrialization.compressor.priority", -1, 100, Integer.MAX_VALUE);
        PRESS_AE2_INSCRIBER_CIRCUIT_PRINTING = builder.comment("Priority of AE2's Inscriber circuit printing recipes, -1 to disable").defineInRange("ae2.inscriber.circuit_printing.priority", 200, -1, Integer.MAX_VALUE);
        PRESS_AE2_INSCRIBER_CIRCUIT_PRINTING_PRESSES = builder.comment("List of allowed press item IDs").defineListAllowEmpty("ae2.inscriber.circuit_printing.presses", List.of("ae2:calculation_processor_press", "ae2:engineering_processor_press", "ae2:logic_processor_press", "ae2:silicon_press", "megacells:accumulation_processor_press", "extendedae:concurrent_processor_press", "advanced_ae:quantum_processor_press", "appflux:energy_processor_press"), () -> "", (l) -> true);
        builder = builder.pop();
    }

    private static boolean validateRegex(final Object obj) {
        if (!(obj instanceof String regexStr)) {
            return false;
        }

        try {
            var ignored = Pattern.compile(regexStr);
        } catch (PatternSyntaxException e) {
            return false;
        }

        return true;
    }

    @SubscribeEvent
    static void onLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() != SPEC) {
            return;
        }

        var patternBuilder = new StringBuilder("(");
        for (var subpattern : CONFIG.EXCEPTIONS.get()) {
            if (patternBuilder.length() > 1) {
                patternBuilder.append('|');
            }
            patternBuilder.append("(^");
            patternBuilder.append(subpattern);
            patternBuilder.append("$)");
        }
        patternBuilder.append(")");

        var pattern = patternBuilder.toString();
        ArsUnification.LOGGER.info("Trying to compile deny list pattern {}", patternBuilder);
        try {
            CONFIG.exceptions = Pattern.compile(pattern.length() <= 2 ? "" : pattern, Pattern.CASE_INSENSITIVE).matcher("");
        } catch (Exception e) {
            ArsUnification.LOGGER.error("Could not compile deny list pattern", e);
        }
    }

    public static boolean isExcluded(ResourceLocation id) {
        return CONFIG.exceptions.reset(id.toString()).matches();
    }

    static {
        Pair<Config, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(Config::new);

        CONFIG = pair.getLeft();
        SPEC = pair.getRight();
    }
}
