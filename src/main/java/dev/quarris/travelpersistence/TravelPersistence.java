package dev.quarris.travelpersistence;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(ModRef.ID)
public class TravelPersistence {

    public TravelPersistence() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configs.init(new ForgeConfigSpec.Builder()).build());
    }
}
