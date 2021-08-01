package dev.quarris.travelpersistence;

import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.minecraftforge.common.ForgeConfigSpec.*;
import static net.minecraftforge.common.ForgeConfigSpec.Builder;
import static net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class Configs {

    public static ConfigValue<List<String>> travellers;
    public static Set<String> travellerSet;

    private static IntValue forceLoadTimer;
    private static IntValue travelTimer;

    public static Builder init(Builder builder) {
        travellers = builder.comment(
                "Defines the traveller entities which are capable to load chunks as they travel through them.",
                "You should probably add entities such as trains if the modpack contains them.",
                "By default this adds all the minecarts."
        ).define("travellers", Configs::buildTravellerList, o -> {
            if (!(o instanceof List))
                return false;

            List list = (List) o;
            if (list.size() == 0)
                return true;

            for (Object value : list) {
                if (!(value instanceof String) || !ForgeRegistries.ENTITIES.containsKey(new ResourceLocation((String) value))) {
                    ModRef.logger().warn("Could not find entity");
                }
            }
            return true;
        });

        forceLoadTimer = builder.comment(
                "Defines the amount of time that the chunk will stay loaded once a traveller enters the chunk.",
                "This timer resets for every traveller than enters a chunk, so the chunk won't randomly unload if multiple travellers enter chunks.",
                "If set to 0, then the chunk will not unload unless all travellers exit the chunk.",
                "Measured in seconds."
        ).defineInRange("forceLoadTimer", 30, 0, 600);

        travelTimer = builder.comment(
                "Defines the amount of time that a traveller can be moving for before it loses its ability to force chunks to load",
                "Set to 0 to make travellers ignore this restrictions",
                "Measured in seconds."
        ).defineInRange("travelTimer", 20*60, 0, Integer.MAX_VALUE);

        return builder;
    }

    public static int getForceLoadTimerTicks() {
        return forceLoadTimer.get() * 20;
    }

    public static int getTravelTimerTicks() {
        return travelTimer.get() * 20;
    }

    public static void onReload() {
        travellerSet = new HashSet<>(travellers.get());
    }

    public static List<String> buildTravellerList() {
        return Arrays.asList(
                EntityType.MINECART.getRegistryName().toString(),
                EntityType.CHEST_MINECART.getRegistryName().toString(),
                EntityType.FURNACE_MINECART.getRegistryName().toString(),
                EntityType.HOPPER_MINECART.getRegistryName().toString(),
                EntityType.TNT_MINECART.getRegistryName().toString(),
                EntityType.COMMAND_BLOCK_MINECART.getRegistryName().toString(),
                EntityType.SPAWNER_MINECART.getRegistryName().toString()
        );
    }
}
