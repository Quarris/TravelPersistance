package dev.quarris.travelpersistence.capability.traveller;

import dev.quarris.travelpersistence.capability.chunkhandler.WorldHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class TravellerCapability {

    @CapabilityInject(ITraveller.class)
    public static Capability<ITraveller> TRAVELLER_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(ITraveller.class, new Capability.IStorage<ITraveller>() {
            @Nullable
            @Override
            public INBT writeNBT(Capability<ITraveller> capability, ITraveller instance, Direction side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<ITraveller> capability, ITraveller instance, Direction side, INBT nbt) {
                instance.deserializeNBT((CompoundNBT) nbt);
            }
        }, Traveller::new);
    }

    public static class Provider implements ICapabilitySerializable<CompoundNBT> {

        public final ITraveller traveller;
        public final LazyOptional<ITraveller> lazyTraveller;

        public Provider(UUID uuid) {
            this.traveller = new Traveller(uuid);
            this.lazyTraveller = LazyOptional.of(() -> this.traveller);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == TRAVELLER_CAPABILITY)
                return this.lazyTraveller.cast();

            return LazyOptional.empty();
        }

        @Override
        public CompoundNBT serializeNBT() {
            return this.traveller.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            this.traveller.deserializeNBT(nbt);
        }
    }
}
