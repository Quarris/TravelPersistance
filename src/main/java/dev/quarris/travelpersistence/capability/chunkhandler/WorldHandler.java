package dev.quarris.travelpersistence.capability.chunkhandler;

import dev.quarris.travelpersistence.ModRef;
import dev.quarris.travelpersistence.capability.chunkloader.ChunkLoadingHandler;
import dev.quarris.travelpersistence.capability.chunkloader.IChunkLoadingHandler;
import dev.quarris.travelpersistence.capability.traveller.ITraveller;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import java.util.*;

public class WorldHandler implements IWorldHandler {

    private Map<ChunkPos, Boolean> chunksToForceLoad = new HashMap<>();
    private final Map<ChunkPos, IChunkLoadingHandler> loadedChunks = new HashMap<>();

    @Override
    public void onTravellerEnteredChunk(ServerWorld world, ITraveller traveller, ChunkPos entered, ChunkPos exited) {
        IChunkLoadingHandler chunk = this.loadedChunks.get(entered);
        if (chunk == null) {
            chunk = new ChunkLoadingHandler();
        }

        if (chunk.onTravellerEnteredChunk(world, traveller)) {
            ModRef.logger().debug("Traveller entered chunk, forcing loading at " + entered);
            this.chunksToForceLoad.put(entered, true);
            this.loadedChunks.put(entered, chunk);
        }

        chunk = this.loadedChunks.get(exited);
        if (chunk == null) {
            chunk = new ChunkLoadingHandler();
        }

        if (chunk.onTravellerExitedChunk(world, traveller)) {
            ModRef.logger().debug("Traveller exited chunk, removing chunk from being forced loaded at " + exited);
            this.chunksToForceLoad.put(exited, false);
            this.loadedChunks.remove(exited);
        }
    }

    @Override
    public void updateChunkLoadingStatus(ServerWorld world) {
        Iterator<Map.Entry<ChunkPos, Boolean>> forceLoaderIte = this.chunksToForceLoad.entrySet().iterator();
        while (forceLoaderIte.hasNext()) {
            Map.Entry<ChunkPos, Boolean> entry = forceLoaderIte.next();
            world.forceChunk(entry.getKey().x, entry.getKey().z, entry.getValue());
            forceLoaderIte.remove();
        }

        Iterator<Map.Entry<ChunkPos, IChunkLoadingHandler>> ite = this.loadedChunks.entrySet().iterator();
        while (ite.hasNext()) {
            Map.Entry<ChunkPos, IChunkLoadingHandler> entry = ite.next();
            ChunkPos chunkPos = entry.getKey();
            IChunkLoadingHandler chunk = entry.getValue();
            if (chunk.tickAndShouldUnload(world)) {
                ModRef.logger().debug("Removing chunk from forced loading at " + chunkPos);
                world.forceChunk(chunkPos.x, chunkPos.z, false);
                ite.remove();
            }
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        ListNBT chunksToForceLoadNBT = new ListNBT();
        for (Map.Entry<ChunkPos, Boolean> entry : this.chunksToForceLoad.entrySet()) {
            CompoundNBT chunkNBT = new CompoundNBT();
            chunkNBT.putInt("PosX", entry.getKey().x);
            chunkNBT.putInt("PosZ", entry.getKey().z);
            chunkNBT.putBoolean("Load", entry.getValue());
            chunksToForceLoadNBT.add(chunkNBT);
        }
        nbt.put("ChunksToForceLoad", chunksToForceLoadNBT);
        ListNBT loadedChunksNBT = new ListNBT();
        for (Map.Entry<ChunkPos, IChunkLoadingHandler> entry : this.loadedChunks.entrySet()) {
            CompoundNBT chunkNBT = new CompoundNBT();
            chunkNBT.putInt("PosX", entry.getKey().x);
            chunkNBT.putInt("PosZ", entry.getKey().z);
            chunkNBT.put("Chunk", entry.getValue().serializeNBT());
            loadedChunksNBT.add(chunkNBT);
        }
        nbt.put("LoadedChunks", loadedChunksNBT);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.chunksToForceLoad.clear();
        this.loadedChunks.clear();
        ListNBT chunksToForceLoadNBT = nbt.getList("ChunksToForceLoad", Constants.NBT.TAG_COMPOUND);
        for (INBT inbt : chunksToForceLoadNBT) {
            CompoundNBT chunkNBT = (CompoundNBT) inbt;
            ChunkPos pos = new ChunkPos(chunkNBT.getInt("PosX"), chunkNBT.getInt("PosZ"));
            this.chunksToForceLoad.put(pos, nbt.getBoolean("Load"));
        }
        ListNBT loadedChunksNBT = nbt.getList("LoadedChunks", Constants.NBT.TAG_COMPOUND);
        for (INBT inbt : loadedChunksNBT) {
            CompoundNBT chunkNBT = (CompoundNBT) inbt;
            ChunkPos pos = new ChunkPos(chunkNBT.getInt("PosX"), chunkNBT.getInt("PosZ"));
            IChunkLoadingHandler handler = new ChunkLoadingHandler();
            handler.deserializeNBT(nbt.getCompound("Chunk"));
            this.loadedChunks.put(pos, handler);
        }
    }
}
