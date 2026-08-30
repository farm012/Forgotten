package com.example.echoesofthepast.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GrievingSoul extends PathfinderMob {
    private static final double TALK_RANGE = 4.0D;
    private static final int TALK_COOLDOWN_TICKS = 40; //between lines while nearby

    private static final String[] DIALOGUE = {
            "...hey. You there...",
            "I used to live here, you know.",
            "It's quiet now. Too quiet.",
            "I keep waiting for someone to come home.",
            "Do you think anyone remembers us?",
            "Stay a while. It's been so long since anyone stayed.",
            "I don't feel so well, my life is about to end before i see her."
    };

    private static final Map<UUID, Integer> dialogueIndex = new HashMap<>();
    private static final Map<UUID, Integer> talkCooldown = new HashMap<>();

    public GrievingSoul(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D); // never moves
    }

    @Override
    public boolean isInvulnerableTo(net.minecraft.server.level.ServerLevel level, DamageSource source) {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) return;

        var nearby = this.level().getEntitiesOfClass(
                Player.class, this.getBoundingBox().inflate(TALK_RANGE));

        for (Player p : nearby) {
            if (!(p instanceof ServerPlayer player)) continue;

            player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 120, 1, false, true));

            UUID id = player.getUUID();
            int cooldown = talkCooldown.getOrDefault(id, 0) - 1;
            if (cooldown <= 0) {
                int index = dialogueIndex.getOrDefault(id, 0);
                if (index < DIALOGUE.length) {
                    player.sendSystemMessage(
                            Component.literal("Grieving Soul: " + DIALOGUE[index])
                                    .withStyle(net.minecraft.ChatFormatting.DARK_GRAY, net.minecraft.ChatFormatting.ITALIC)
                    );
                    dialogueIndex.put(id, index + 1);
                }
                cooldown = TALK_COOLDOWN_TICKS;
            }
            talkCooldown.put(id, cooldown);
        }
    }
}