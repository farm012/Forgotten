package com.example.echoesofthepast.hallucination;

import com.example.echoesofthepast.entity.ModEntities;
import com.example.echoesofthepast.grief.GriefManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HallucinationManager {
    private static final float TRIGGER_THRESHOLD = 0.5F;

    private static final Map<UUID, Integer> hallucinationCooldown = new HashMap<>();

    private static final String[] FLICKER_LINES = {
            "Did you just see that?",
            "There's nothing there. There's nothing there.",
            "It was just a shadow.",
            "Wait i remember that"
    };

    public static void tick(ServerPlayer player) {
        float griefFraction = GriefManager.getGrief(player) / (float) GriefManager.getMaxGrief();
        if (griefFraction < TRIGGER_THRESHOLD) return;

        UUID id = player.getUUID();
        int cooldown = hallucinationCooldown.getOrDefault(id, 0) - 1;
        if (cooldown > 0) {
            hallucinationCooldown.put(id, cooldown);
            return;
        }

        // requency scales from every ~45-90s at 50% grief,
        // every ~15-30s as grief approaches max
        float intensityAboveThreshold = (griefFraction - TRIGGER_THRESHOLD) / (1.0F - TRIGGER_THRESHOLD);
        int baseWait = (int) (1800 - (intensityAboveThreshold * 1200));
        hallucinationCooldown.put(id, baseWait + player.getRandom().nextInt(baseWait / 2 + 1));

        triggerGlimpse(player);
    }

    private static void triggerGlimpse(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) return;

        double angle = player.getRandom().nextDouble() * Math.PI * 2;
        double dist = 10 + player.getRandom().nextDouble() * 4;
        double x = player.getX() + Math.cos(angle) * dist;
        double z = player.getZ() + Math.sin(angle) * dist;

        var comrade = ModEntities.DEAD_COMRADE.get().create(level, EntitySpawnReason.TRIGGERED);
        if (comrade == null) return;

        comrade.setVariant(player.getRandom().nextInt(3));
        comrade.snapTo(x, player.getY(), z, 0.0F, 0.0F);

        level.addFreshEntity(comrade);

        //a quiet unease line, separate from the ambient dread whispers (hows my language skills? wait no one is looking at that anyways *sighs*)
        if (player.getRandom().nextFloat() < 0.5F) {
            player.sendSystemMessage(
                    Component.literal(FLICKER_LINES[player.getRandom().nextInt(FLICKER_LINES.length)])
                            .withStyle(net.minecraft.ChatFormatting.GRAY, net.minecraft.ChatFormatting.ITALIC)
            );
        }

        comrade.tickCount = 0;
        comrade.setHallucination(true);
    }


}
