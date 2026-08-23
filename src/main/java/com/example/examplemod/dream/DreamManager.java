package com.example.examplemod.dream;

import com.example.examplemod.entity.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DreamManager {
    private static final ResourceKey<Level> DREAM_DIM = ResourceKey.create(
            net.minecraft.core.registries.Registries.DIMENSION,
            net.minecraft.resources.Identifier.fromNamespaceAndPath("examplemod", "dream")
    );
    private static final int DREAM_DURATION_TICKS = 200;

    private static final Map<UUID, Boolean> pending = new HashMap<>();
    private static final Map<UUID, Integer> activeTimers = new HashMap<>();
    private static final Map<UUID, ResourceKey<Level>> returnDimension = new HashMap<>();
    private static final Map<UUID, Vec3> returnPosition = new HashMap<>();

    public static void markPending(ServerPlayer player) {
        pending.put(player.getUUID(), true);
    }

    public static boolean isPending(ServerPlayer player) {
        return pending.getOrDefault(player.getUUID(), false);
    }

    public static void enterDream(ServerPlayer player) {
        pending.remove(player.getUUID());

        returnDimension.put(player.getUUID(), player.level().dimension());
        returnPosition.put(player.getUUID(), player.position());

        MinecraftServer server = ((ServerLevel) player.level()).getServer();
        var dreamLevel = server.getLevel(DREAM_DIM);
        if (dreamLevel == null) return; // dimension not loaded/registered - check datapack files

        player.teleportTo(dreamLevel, 0.5, 65.0, 0.5, java.util.Set.of(), 0.0F, 0.0F, false);
        activeTimers.put(player.getUUID(), DREAM_DURATION_TICKS);

        // spawn the dead comrade near the player in the dream
        var comrade = ModEntities.DEAD_COMRADE.get().create(dreamLevel, EntitySpawnReason.TRIGGERED);
        if (comrade != null) {
            comrade.snapTo(player.getX() + 5, player.getY(), player.getZ(), 0.0F, 0.0F);
            comrade.setTarget(player);
            dreamLevel.addFreshEntity(comrade);
        }
    }

    public static void tick(ServerPlayer player) {
        UUID id = player.getUUID();
        if (!activeTimers.containsKey(id)) return;

        int remaining = activeTimers.get(id) - 1;
        if (remaining <= 0) {
            endDream(player);
        } else {
            activeTimers.put(id, remaining);
        }
    }

    private static void endDream(ServerPlayer player) {
        UUID id = player.getUUID();
        activeTimers.remove(id);

        MinecraftServer server = ((ServerLevel) player.level()).getServer();

        ResourceKey<Level> dim = returnDimension.remove(id);
        Vec3 pos = returnPosition.remove(id);
        if (dim == null || pos == null) return;

        var targetLevel = server.getLevel(dim);
        if (targetLevel == null) return;

        player.teleportTo(targetLevel, pos.x, pos.y, pos.z, java.util.Set.of(), 0.0F, 0.0F, false);
        player.setHealth(player.getMaxHealth() / 2.0F);
    }
}