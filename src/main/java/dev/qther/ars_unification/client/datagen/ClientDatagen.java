package dev.qther.ars_unification.client.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientDatagen {
    public static PackOutput output = null;

    @SubscribeEvent
    public static void datagen(GatherDataEvent event) {
        output = event.getGenerator().getPackOutput();

        event.getGenerator().addProvider(event.includeClient(), new AULangDatagen(output));
    }
}
