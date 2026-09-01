package com.example.echoesofthepast.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class LabyrinthFangItem extends Item {
    public LabyrinthFangItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level instanceof ServerLevel serverLevel) {
            int durationTicks = 2000;


            player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, durationTicks, 2, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, durationTicks / 2, 1, false, true));

            serverLevel.playSound(null, player.blockPosition(),
                    SoundEvents.ENDER_DRAGON_GROWL, SoundSource.PLAYERS, 1.4F, 1.0F);

            for (int i = 0; i < 60; i++) {
                double angle = level.getRandom().nextDouble() * Math.PI * 2;
                double radius = 0.2 + level.getRandom().nextDouble() * 0.9;
                double px = player.getX() + Math.cos(angle) * radius;
                double pz = player.getZ() + Math.sin(angle) * radius;
                serverLevel.sendParticles(ParticleTypes.FLAME,
                        px, player.getY(), pz, 1, 0, 0.4 + level.getRandom().nextDouble() * 0.6, 0, 0.05);
            }

            player.getCooldowns().addCooldown(stack, durationTicks);
        }

        return InteractionResult.SUCCESS;
    }
}