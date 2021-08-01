package dev.quarris.travelpersistence.capability.chunkhandler;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WorldHandlerCapability {

    @CapabilityInject(IWorldHandler.class)
    public static Capability<IWorldHandler> WORLD_HANDLER_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IWorldHandler.class, new Capability.IStorage<IWorldHandler>() {
            @Nullable
            @Override
            public INBT writeNBT(Capability<IWorldHandler> capability, IWorldHandler instance, Direction side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<IWorldHandler> capability, IWorldHandler instance, Direction side, INBT nbt) {
                instance.deserializeNBT((CompoundNBT) nbt);
            }
        }, WorldHandler::new);
    }

    public static class Provider implements ICapabilitySerializable<CompoundNBT> {

        public final IWorldHandler worldHandler;
        public final LazyOptional<IWorldHandler> lazyWorldHandler;

        public Provider() {
            this.worldHandler = new WorldHandler();
            this.lazyWorldHandler = LazyOptional.of(() -> this.worldHandler);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == WORLD_HANDLER_CAPABILITY)
                return this.lazyWorldHandler.cast();

            return LazyOptional.empty();
        }

        @Override
        public CompoundNBT serializeNBT() {
            return this.worldHandler.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            this.worldHandler.deserializeNBT(nbt);
        }
    }

}
