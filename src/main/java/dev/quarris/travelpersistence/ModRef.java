package dev.quarris.travelpersistence;

import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModRef {

    public static final String ID = "minecartpersistence";

    private static final Logger LOGGER = LogManager.getLogger(TravelPersistence.class);
    public static Logger logger() {
        return LOGGER;
    }

    public static ResourceLocation res(String res) {
        return new ResourceLocation(ID, res);
    }
}
