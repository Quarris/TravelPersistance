package dev.quarris.travelpersistence.capability.chunkloader;

import dev.quarris.travelpersistence.Configs;
import dev.quarris.travelpersistence.ModRef;
import dev.quarris.travelpersistence.capability.traveller.ITraveller;
import dev.quarris.travelpersistence.capability.traveller.TravellerCapability;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class ChunkLoadingHandler implements IChunkLoadingHandler {

    private long lastForceLoadTime;
    private final Set<UUID> travellers = new HashSet<>();

    @Override
    public boolean tickAndShouldUnload(ServerWorld world) {
        Iterator<UUID> ite = this.travellers.iterator();
        while (ite.hasNext()) {
            UUID uuid = ite.next();
            Entity entity = world.getEntityByUuid(uuid);
            if (entity == null) {
                ModRef.logger().debug("Traveller no longer exists in this chunk");
                ite.remove();
                continue;
            }
            entity.getCapability(TravellerCapability.TRAVELLER_CAPABILITY).ifPresent(traveller -> {
                traveller.tick(world.getEntityByUuid(uuid));
                if (!traveller.enablesChunkLoading()) {
                    ite.remove();
                }
            });
        }

        int unloadTimer = Configs.getForceLoadTimerTicks();
        if (unloadTimer == 0) {
            return this.travellers.isEmpty();
        }

        if (world.getGameTime() >= lastForceLoadTime + unloadTimer) {
            ModRef.logger().debug("Chunk timed out");
        } else if (world.getGameTime() % 20 == 0) {
            //ModRef.logger().debug("Chunk timing out in: " + (lastForceLoadTime + unloadTimer - world.getGameTime()) / 20);
        }

        return this.travellers.isEmpty() || world.getGameTime() >= lastForceLoadTime + unloadTimer;
    }

    @Override
    public boolean onTravellerEnteredChunk(ServerWorld world, ITraveller traveller) {
        traveller.onEnteredChunk();
        boolean wasEmpty = this.travellers.isEmpty();
        if (this.travellers.add(traveller.getTravellerUUID())) {
            this.lastForceLoadTime = world.getGameTime();
        }
        return wasEmpty;
    }

    @Override
    public boolean onTravellerExitedChunk(ServerWorld world, ITraveller traveller) {
        this.travellers.remove(traveller.getTravellerUUID());
        return this.travellers.isEmpty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        ListNBT travellersNBT = new ListNBT();
        for (UUID uuid : this.travellers) {
            CompoundNBT uuidNBT = new CompoundNBT();
            uuidNBT.putUniqueId("UUID", uuid);
            travellersNBT.add(uuidNBT);
        }
        nbt.put("Travellers", travellersNBT);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.travellers.clear();
        ListNBT travellersNBT = nbt.getList("Travellers", Constants.NBT.TAG_COMPOUND);
        for (INBT inbt : travellersNBT) {
            CompoundNBT uuidNBT = (CompoundNBT) inbt;
            this.travellers.add(uuidNBT.getUniqueId("UUID"));
        }
    }
}
