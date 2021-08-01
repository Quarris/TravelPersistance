package dev.quarris.travelpersistence.capability.chunkloader;


import dev.quarris.travelpersistence.capability.traveller.ITraveller;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Set;

public interface IChunkLoadingHandler extends INBTSerializable<CompoundNBT> {

    /**
     * Checks to see if the chunk should unload due to the being loaded for too long.
     * @param world The world
     * @return {@code true} if should unload this chunk
     */
    boolean tickAndShouldUnload(ServerWorld world);

    /**
     * When the traveller entered a chunk.
     * @param traveller The traveller
     * @return {@code true} if the chunk should start to be force loaded
     */
    boolean onTravellerEnteredChunk(ServerWorld world, ITraveller traveller);

    /**
     * When the traveller exits a chunk.
     * @param traveller The traveller
     * @return {@code true}, if should immediately unload the chunk
     */
    boolean onTravellerExitedChunk(ServerWorld world, ITraveller traveller);

}
