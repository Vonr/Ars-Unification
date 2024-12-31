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
    
    public ModConfigSpec.IntValue MEKANISM_CRUSHER;
    public ModConfigSpec.IntValue ENDERIO_SAG_MILL;
    public ModConfigSpec.IntValue INTEGRATEDDYNAMICS_SQUEEZER;
    public ModConfigSpec.BooleanValue INTEGRATEDDYNAMICS_USE_MECHANICAL;
    public ModConfigSpec.IntValue ACTUALLYADDITIONS_CRUSHER;
    public ModConfigSpec.IntValue MODERN_INDUSTRIALIZATION_MACERATOR;
    
    public ModConfigSpec.IntValue MEKANISM_SAW_MILL;
    public ModConfigSpec.IntValue MODERN_INDUSTRIALIZATION_CUTTING_MACHINE;
    public ModConfigSpec.IntValue FARMERS_DELIGHT_CUTTING_BOARD;

    public ModConfigSpec.IntValue MODERN_INDUSTRIALIZATION_COMPRESSOR;

    private Config(ModConfigSpec.Builder builder) {
         EXCEPTIONS = builder.comment("List of Regular Expressions to deny recipe unification of").defineListAllowEmpty("exceptions", List.of(), Config::validateRegex);

         builder = builder.push("crush");
         MEKANISM_CRUSHER = builder.comment("Priority of Mekanism's Crusher recipes, -1 to disable").defineInRange("mekanism.crusher", 100, -1, 2147483647);
         ENDERIO_SAG_MILL = builder.comment("Priority of Ender IO's SAG Mill recipes, -1 to disable").defineInRange("enderio.sag_mill", 110, -1, 2147483647);
         INTEGRATEDDYNAMICS_SQUEEZER = builder.comment("Priority of Integrated Dynamics's Squeezer recipes, -1 to disable").defineInRange("integrateddynamics.squeezer", 120, -1, 2147483647);
         INTEGRATEDDYNAMICS_USE_MECHANICAL = builder.comment("Use Integrated Dynamics's Mechanical Squeezer recipes instead").define("integrateddynamics.use_mechanical", true);
         ACTUALLYADDITIONS_CRUSHER = builder.comment("Priority of Actually Additions's Crusher recipes, -1 to disable").defineInRange("actuallyadditions.crusher", 80, -1, 2147483647);
         MODERN_INDUSTRIALIZATION_MACERATOR = builder.comment("Priority of Modern Industrialization's Macerator recipes, -1 to disable").defineInRange("modern_industrialization.macerator", 90, -1, 2147483647);

         builder = builder.pop().push("cut");
         MEKANISM_SAW_MILL = builder.comment("Priority of Mekanism's Sawmill recipes, -1 to disable").defineInRange("mekanism.sawmill", 100, -1, 2147483647);
         MODERN_INDUSTRIALIZATION_CUTTING_MACHINE = builder.comment("Priority of Modern Industrialization's Cutting Machine recipes, -1 to disable").defineInRange("modern_industrialization.cutting_machine", -1, -1, 2147483647);
         FARMERS_DELIGHT_CUTTING_BOARD = builder.comment("Priority of Farmer's Delight's Cutting Board recipes, -1 to disable").defineInRange("farmersdelight.cutting_board", 60, -1, 2147483647);

        builder = builder.pop().push("press");
        MODERN_INDUSTRIALIZATION_COMPRESSOR = builder.comment("Priority of Modern Industrialization's Compressor recipes, -1 to disable").defineInRange("modern_industrialization.cutting_machine", -1, 100, 2147483647);
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
    static void onLoad(final ModConfigEvent event) {
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
