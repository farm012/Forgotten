package com.example.examplemod.ambient;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

public class AmbientSoundManager {

    public static void playDreamEntryAmbience(ServerPlayer player, ServerLevel dreamLevel) {
        dreamLevel.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.AMBIENT_CAVE,
                SoundSource.AMBIENT,
                1.0F,
                0.6F
        );

        dreamLevel.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.ZOMBIE_AMBIENT,
                SoundSource.HOSTILE,
                0.4F,
                0.3F
        );
    }

    public static void playGuardianRoar(Entity guardian) {
        guardian.level().playSound(
                null,
                guardian.getX(),
                guardian.getY(),
                guardian.getZ(),
                SoundEvents.ZOMBIE_AMBIENT,
                SoundSource.HOSTILE,
                1.0F,
                0.5F
        );

        guardian.level().playSound(
                null,
                guardian.getX(),
                guardian.getY(),
                guardian.getZ(),
                SoundEvents.WITHER_AMBIENT,
                SoundSource.HOSTILE,
                0.6F,
                0.8F
        );
    }
}