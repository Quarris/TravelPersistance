package dev.quarris.travelpersistence.capability.chunkhandler;

import dev.quarris.travelpersistence.capability.traveller.ITraveller;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;

public interface IWorldHandler extends INBTSerializable<CompoundNBT> {
    /**
     * Called when the traveller changes chunks.
     * Used to force load chunks and reset their internal timers.
     * @param world The world
     * @param traveller The traveller that entered the chunk
     * @param entered The chunk position that the traveller entered to
     * @param exited The chunk position that the traveller exited from
     */
    void onTravellerEnteredChunk(ServerWorld world, ITraveller traveller, ChunkPos entered, ChunkPos exited);

    /**
     *
     * @param world The world
     */
    void updateChunkLoadingStatus(ServerWorld world);
}
