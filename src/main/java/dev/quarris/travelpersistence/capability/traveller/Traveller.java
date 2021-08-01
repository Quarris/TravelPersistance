package dev.quarris.travelpersistence.capability.traveller;


import dev.quarris.travelpersistence.Configs;
import dev.quarris.travelpersistence.ModRef;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;

import java.util.UUID;

public class Traveller implements ITraveller {

    private UUID entityUUID;
    private boolean canChunkLoad = true;
    private boolean expired;
    private long endTimer;

    public Traveller() {

    }

    public Traveller(UUID uuid) {
        this.entityUUID = uuid;
    }

    public void setUUID(UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
        this.entityUUID = uuid;
    }

    @Override
    public void onEnteredChunk() {
        this.expired = false;
    }

    @Override
    public void tick(Entity entity) {
        if (entity == null || !entity.getUniqueID().equals(this.entityUUID)) {
            this.canChunkLoad = false;
            return;
        }

        if (!this.canChunkLoad && !this.expired) {
            ModRef.logger().debug("Traveller reset timer at " + entity.world.getGameTime());
            this.endTimer = entity.world.getGameTime() + Configs.getTravelTimerTicks();
            this.canChunkLoad = true;
            return;
        }

        if (Math.abs(entity.getMotion().length()) < 0.001) {
            ModRef.logger().debug("Traveller " + entity.getDisplayName().getString() + " has halted");
            this.canChunkLoad = false;
        } else if (entity.world.getGameTime() > this.endTimer) {
            ModRef.logger().debug("Traveller " + entity.getDisplayName().getString() + " has been travelling for too long");
            this.canChunkLoad = false;
            this.expired = true;
        }

        if (entity.world.getGameTime() % 20 == 0) {
           //ModRef.logger().debug(entity.getDisplayName().getString() + " has been travelling for " + (Configs.getTravelTimerTicks() - (this.endTimer - entity.world.getGameTime())) / 20 + "seconds");
        }
    }

    @Override
    public boolean hasExpired() {
        return this.expired;
    }

    @Override
    public boolean enablesChunkLoading() {
        return this.canChunkLoad;
    }

    @Override
    public UUID getTravellerUUID() {
        return this.entityUUID;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putLong("EndTimer", this.endTimer);
        nbt.putBoolean("CanChunkLoad", this.canChunkLoad);
        nbt.putUniqueId("UUID", this.entityUUID);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.endTimer = nbt.getLong("EndTimer");
        this.canChunkLoad = nbt.getBoolean("CanChunkLoad");
        this.entityUUID = nbt.getUniqueId("UUID");
    }
}
