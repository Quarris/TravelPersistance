package dev.quarris.travelpersistence;

import dev.quarris.travelpersistence.capability.chunkhandler.WorldHandlerCapability;
import dev.quarris.travelpersistence.capability.traveller.TravellerCapability;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModRef.ID)
public class EventHandler {

    @SubscribeEvent
    public static void attachChunkCapabilities(AttachCapabilitiesEvent<Chunk> event) {
        //event.addCapability(ModRef.res("chunk_loading_handler"), new ChunkLoadingHandlerCapability.Provider());
    }

    @SubscribeEvent
    public static void attachWorldCapabilities(AttachCapabilitiesEvent<World> event) {
        event.addCapability(ModRef.res("world_handler"), new WorldHandlerCapability.Provider());
    }

    @SubscribeEvent
    public static void attachTravellerCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (!Configs.travellerSet.contains(event.getObject().getType().getRegistryName().toString()))
            return;

        if (event.getObject().world.isRemote)
            return;

        event.addCapability(ModRef.res("traveller"), new TravellerCapability.Provider(event.getObject().getUniqueID()));
    }

    @SubscribeEvent
    public static void enterChunk(EntityEvent.EnteringChunk event) {
        if (event.getEntity().world.isRemote())
            return;

        event.getEntity().getCapability(TravellerCapability.TRAVELLER_CAPABILITY).ifPresent(traveller -> {
            event.getEntity().world.getCapability(WorldHandlerCapability.WORLD_HANDLER_CAPABILITY).ifPresent(world -> {
                ModRef.logger().debug(event.getEntity().getDisplayName().getString() + " has entered a chunk at " + new ChunkPos(event.getNewChunkX(), event.getNewChunkZ()));
                world.onTravellerEnteredChunk((ServerWorld) event.getEntity().world, traveller, new ChunkPos(event.getNewChunkX(), event.getNewChunkZ()), new ChunkPos(event.getOldChunkX(), event.getOldChunkZ()));
            });
        });
    }

    @SubscribeEvent
    public static void tickWorld(TickEvent.WorldTickEvent event) {
        if (event.side == LogicalSide.CLIENT || event.phase == TickEvent.Phase.START)
            return;

        event.world.getCapability(WorldHandlerCapability.WORLD_HANDLER_CAPABILITY).ifPresent(world -> {
            world.updateChunkLoadingStatus((ServerWorld) event.world);
        });
    }
}
