package com.example.examplemod.dream;

import com.example.examplemod.allofmyhate.GriefNetworking;
import com.example.examplemod.entity.ModEntities;
import com.example.examplemod.grief.GriefManager;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


//i made that in forge so i just had to transfer it to neofroge api about the same logic
public class DreamManager {
    private static final ResourceKey<Level> DREAM_DIM = ResourceKey.create(
            net.minecraft.core.registries.Registries.DIMENSION,
            net.minecraft.resources.Identifier.fromNamespaceAndPath("examplemod", "dream")
    );
    private static final int DREAM_DURATION_TICKS = 1200; // name is explanetory ;-;
    private static final int SPAWN_INTERVAL_TICKS = 60;   // one comrade per second might change
    private static final int MAX_ACTIVE_COMRADES = 24;      // safety cap so it doesn't spiral
    private static final int FIZZLE_DURATION_TICKS = 120;



    private static final int FREEZE_DURATION_TICKS = 15; // ~0.75s
    private static final Map<UUID, Integer> freezeTicks = new HashMap<>();




    private static final Map<UUID, Integer> spawnedCount = new HashMap<>();


    private static final Map<UUID, Boolean> pending = new HashMap<>();
    private static final Map<UUID, Integer> activeTimers = new HashMap<>();
    private static final Map<UUID, ResourceKey<Level>> returnDimension = new HashMap<>();
    private static final Map<UUID, Vec3> returnPosition = new HashMap<>();
    //OMG THAT GOT SO CROUDED i should organise better



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

        ServerLevel level = (ServerLevel) player.level();
        var server = level.getServer();
        var dreamLevel = server.getLevel(DREAM_DIM);
        if (dreamLevel == null) return;

        player.teleportTo(dreamLevel, 0.5, -61.0, 0.5, java.util.Set.of(), 0.0F, 0.0F, false);
        activeTimers.put(player.getUUID(), DREAM_DURATION_TICKS);
        spawnedCount.put(player.getUUID(), 0);

        spawnComrade(player, dreamLevel);
    }

    public static void tick(ServerPlayer player) {
        UUID id = player.getUUID();
        if (!activeTimers.containsKey(id)) return;

        int remaining = activeTimers.get(id) - 1;

        if (remaining <= 0) {
            endDream(player);
            return;
        }

        activeTimers.put(id, remaining);

        // fizzle effect during the final few seconds
        if (remaining <= FIZZLE_DURATION_TICKS) {
            float progress = 1f - (remaining / (float) FIZZLE_DURATION_TICKS);
            PacketDistributor.sendToPlayer(player, new GriefNetworking.FizzlePacket(progress));
        }

        // clear spawned entities 1 second before the dream actually ends
        if (remaining == 20) {
            ServerLevel level = (ServerLevel) player.level();
            var toRemove = level.getEntities(
                    net.minecraft.world.level.entity.EntityTypeTest.forClass(net.minecraft.world.entity.Entity.class),
                    new net.minecraft.world.phys.AABB(-1_000_000, -1_000_000, -1_000_000, 1_000_000, 1_000_000, 1_000_000),
                    entity -> !(entity instanceof net.minecraft.world.entity.player.Player)
            );
            for (var entity : toRemove) {
                entity.discard();
            }
        }

        if (freezeTicks.getOrDefault(id, 0) > 0) {
            player.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
            freezeTicks.merge(id, -1, Integer::sum);
        }

        // periodic comrade spawning
        if (remaining % SPAWN_INTERVAL_TICKS == 0 && spawnedCount.getOrDefault(id, 0) < MAX_ACTIVE_COMRADES) {
            ServerLevel level = (ServerLevel) player.level();
            spawnComrade(player, level);
        }

    }
    private static void spawnComrade(ServerPlayer player, ServerLevel dreamLevel) {
        var comrade = ModEntities.DEAD_COMRADE.get().create(dreamLevel, EntitySpawnReason.TRIGGERED);
        if (comrade == null) return;

        comrade.setVariant(player.getRandom().nextInt(3));


        double angle = player.getRandom().nextDouble() * Math.PI * 2;
        double dist = 7 + player.getRandom().nextDouble() * 3; // 6 or 7... 6,7  blocks away, varied
        double x = player.getX() + Math.cos(angle) * dist;
        double z = player.getZ() + Math.sin(angle) * dist;

        comrade.snapTo(x, player.getY(), z, 0.0F, 0.0F);
        comrade.setTarget(player);
        dreamLevel.addFreshEntity(comrade);
        cinematicSnap(player, comrade);

        spawnedCount.merge(player.getUUID(), 1, Integer::sum);
    }

    private static void cinematicSnap(ServerPlayer player, net.minecraft.world.entity.Entity comrade) {
        double dx = comrade.getX() - player.getX();
        double dz = comrade.getZ() - player.getZ();
        double dy = comrade.getEyeY() - player.getEyeY();
        double horizontalDist = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) (net.minecraft.util.Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;
        float pitch = (float) -(net.minecraft.util.Mth.atan2(dy, horizontalDist) * (180.0 / Math.PI));

        player.teleportTo((ServerLevel) player.level(),
                0.5, -62, 0.5,
                java.util.Set.of(),
                yaw, pitch, false);

        freezeTicks.put(player.getUUID(), FREEZE_DURATION_TICKS);
    }
    private static void endDream(ServerPlayer player) {

        UUID id = player.getUUID();
        activeTimers.remove(id);
        spawnedCount.remove(id);

        GriefManager.onDreamEnded(player);

        PacketDistributor.sendToPlayer(player, new GriefNetworking.FizzlePacket(0f)); // <-- this one

        MinecraftServer server = ((ServerLevel) player.level()).getServer();

        ResourceKey<Level> dim = returnDimension.remove(id);
        Vec3 pos = returnPosition.remove(id);
        if (dim == null || pos == null) return;

        var targetLevel = server.getLevel(dim);
        if (targetLevel == null) return;

        player.teleportTo(targetLevel, pos.x, pos.y, pos.z, java.util.Set.of(), 0.0F, 0.0F, false);
        player.setHealth(player.getMaxHealth() / 2.0F);
    }
}// THIS IS A MESS I will nOT AT ANNYTHING IN HERE ANYMORE 179 lines ;-;