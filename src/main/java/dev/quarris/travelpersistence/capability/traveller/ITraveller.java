package dev.quarris.travelpersistence.capability.traveller;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;

public interface ITraveller extends INBTSerializable<CompoundNBT> {

    /**
     * Checks if this traveller can chunk load as its travelling.
     * This is turned off when the traveller has stopped moving.
     * @return {@code true}, if this traveller can force load chunks.
     */
    boolean enablesChunkLoading();

    boolean hasExpired();

    void onEnteredChunk();

    /**
     * Ticks the traveller
     * This checks if the traveller has been in motion for too long
     * and disable its capability is required.
     * Also re-enables it once the entity has been reloaded.
     * @param entity The entity for this traveller instance.
     */
    void tick(Entity entity);

    /**
     * Gets the entity UUID attached to this capability
     * @return The UUID of the traveller entity.
     */
    UUID getTravellerUUID();

}
