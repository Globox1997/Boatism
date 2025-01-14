package net.shirojr.boatism.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.shirojr.boatism.entity.BoatismEntities;
import net.shirojr.boatism.entity.custom.BoatEngineEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EntityHandler {
    private EntityHandler() {
    }

    public static Optional<BoatEngineEntity> getBoatEngineEntityFromUuid(@Nullable UUID uuid, World world, Vec3d pos, int searchSize) {
        if (uuid == null) return Optional.empty();
        List<BoatEngineEntity> possibleEntities = world.getEntitiesByType(BoatismEntities.BOAT_ENGINE,
                Box.of(pos, searchSize, searchSize, searchSize),
                boatEngine -> boatEngine.getUuid().equals(uuid));
        if (possibleEntities.size() < 1) return Optional.empty();
        return Optional.ofNullable(possibleEntities.get(0));
    }

    public static void removePossibleBoatEngineEntry(Entity entity) {
        if (!(entity instanceof BoatEntity boatEntity)) return;
        ((BoatEngineCoupler) boatEntity).boatism$getBoatEngineEntityUuid()
                .flatMap(uuid -> EntityHandler.getBoatEngineEntityFromUuid(uuid, boatEntity.getWorld(),
                        boatEntity.getPos(), 10))
                .ifPresent(boatEngineEntity -> {
                    ItemStack boatEngineItemStack = BoatEngineNbtHelper.getItemStackFromBoatEngineEntity(boatEngineEntity);
                    boatEngineEntity.removeBoatEngine(boatEntity);
                    if (!(boatEngineEntity.getWorld() instanceof ServerWorld serverWorld)) return;
                    Vec3d pos = boatEngineEntity.getBlockPos().toCenterPos();
                    ItemScatterer.spawn(serverWorld, pos.getX(), pos.getY(), pos.getZ(), boatEngineItemStack);
                });
    }
}
