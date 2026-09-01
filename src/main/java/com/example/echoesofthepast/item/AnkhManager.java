package com.example.echoesofthepast.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = "echoesofthepast")
public class AnkhManager {
    private static final int REVIVE_COOLDOWN_TICKS = 1800; // 90 seconds
    private static final Map<UUID, Integer> reviveCooldown = new HashMap<>();

    public static boolean isHolding(ServerPlayer player) {
        return player.getMainHandItem().is(com.example.echoesofthepast.item.ModItems.ANKH.get())
                || player.getOffhandItem().is(com.example.echoesofthepast.item.ModItems.ANKH.get());
    }

    public static void tickPassive(ServerPlayer player) {
        if (!isHolding(player)) return;

        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 40, 0, false, false));
        if (player.hasEffect(MobEffects.POISON)) player.removeEffect(MobEffects.POISON);
        if (player.hasEffect(MobEffects.SLOWNESS)) player.removeEffect(MobEffects.SLOWNESS);

        UUID id = player.getUUID();
        int cd = reviveCooldown.getOrDefault(id, 0);
        if (cd > 0) reviveCooldown.put(id, cd - 1);
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!isHolding(player)) return;

        int cd = reviveCooldown.getOrDefault(player.getUUID(), 0);
        if (cd > 0) return;

        event.setCanceled(true);
        player.setHealth(player.getMaxHealth());
        player.clearFire();
        reviveCooldown.put(player.getUUID(), REVIVE_COOLDOWN_TICKS);

        if (player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {

            serverLevel.playSound(null, player.blockPosition(),
                    net.minecraft.sounds.SoundEvents.TOTEM_USE,
                    net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
            serverLevel.playSound(null, player.blockPosition(),
                    net.minecraft.sounds.SoundEvents.PHANTOM_FLAP,
                    net.minecraft.sounds.SoundSource.PLAYERS, 0.8F, 0.6F);

            //dense rising fire burst
            for (int i = 0; i < 150; i++) {
                double angle = player.getRandom().nextDouble() * Math.PI * 2;
                double radius = player.getRandom().nextDouble() * 1.2;
                double px = player.getX() + Math.cos(angle) * radius;
                double pz = player.getZ() + Math.sin(angle) * radius;
                serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.FLAME,
                        px, player.getY(), pz, 1, 0, 0.5 + player.getRandom().nextDouble() * 1.0, 0, 0.08);
            }
            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.LAVA,
                    player.getX(), player.getY() + 0.5, player.getZ(), 20, 0.5, 0.3, 0.5, 0.1);

            player.setDeltaMovement(player.getDeltaMovement().x, 0.8D, player.getDeltaMovement().z);
            player.hurtMarked = true;
        }
    }
}