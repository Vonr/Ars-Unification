package dev.qther.ars_unification;

import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@Mod(ArsUnification.MODID)
public class ArsUnification {
    public static final String MODID = "ars_unification";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public ArsUnification(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
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
