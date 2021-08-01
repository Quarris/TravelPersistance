package dev.quarris.travelpersistence;

import dev.quarris.travelpersistence.capability.chunkhandler.WorldHandlerCapability;
import dev.quarris.travelpersistence.capability.traveller.TravellerCapability;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = ModRef.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Setup {

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        TravellerCapability.register();
        WorldHandlerCapability.register();
    }

    @SubscribeEvent
    public static void onConfigReload(ModConfig.ModConfigEvent reloading) {
        Configs.onReload();
    }

}
