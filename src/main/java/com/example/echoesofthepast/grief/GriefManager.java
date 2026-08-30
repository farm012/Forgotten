package com.example.echoesofthepast.grief;

import com.example.echoesofthepast.allofmyhate.GriefNetworking;
import com.example.echoesofthepast.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
//i kinda forgot to comment on this one but it seems straight forward, just grief system i didn't finish yet


public class GriefManager {
    private static final int MAX_GRIEF = 100;
    private static final int GRIEF_PER_DREAM = 10;
    private static final int GRIEF_PER_DAY = 7;
    private static final int DRAIN_PER_SECOND = 1;

    private static final int DEATH_DELAY_TICKS = 200;
    private static final Map<UUID, Integer> pendingDeath = new HashMap<>();







    private static final Map<UUID, Integer> grief = new HashMap<>();
    private static final Map<UUID, Long> lastCountedDay = new HashMap<>();

    public static int getGrief(ServerPlayer player) {
        return grief.getOrDefault(player.getUUID(), 0);
    }

    public static void addGrief(ServerPlayer player, int amount) {
        int current = getGrief(player);
        boolean wasZero = current <= 0;

        int updated = Math.min(current + amount, MAX_GRIEF);
        grief.put(player.getUUID(), updated);
        GriefNetworking.sendToPlayer(player, updated);

        if (wasZero && updated > 0) {
            awardAdvancement(player, "something_isnt_right");
        }
        if (current < 50 && updated >= 50) {
            awardAdvancement(player, "grief_grows");
        }

        if (updated >= MAX_GRIEF && !pendingDeath.containsKey(player.getUUID())) {
            startDeathCountdown(player);
        }
    }
    public static void tickDeathCountdown(ServerPlayer player) {
        UUID id = player.getUUID();
        Integer remaining = pendingDeath.get(id);
        if (remaining == null) return;

        remaining--;
        if (remaining <= 0) {
            killFromGrief(player);
            pendingDeath.remove(id);
        } else {
            pendingDeath.put(id, remaining);
        }
    }


    private static void startDeathCountdown(ServerPlayer player) {
        pendingDeath.put(player.getUUID(), DEATH_DELAY_TICKS);
        player.sendSystemMessage(
                net.minecraft.network.chat.Component.literal("You feel your grief finally consuming you...")
                        .withStyle(net.minecraft.ChatFormatting.DARK_RED, net.minecraft.ChatFormatting.BOLD)
        );
    }


    private static void awardAdvancement(ServerPlayer player, String name) {
        var server = ((net.minecraft.server.level.ServerLevel) player.level()).getServer();
        var advancement = server.getAdvancements().get(
                net.minecraft.resources.Identifier.fromNamespaceAndPath("echoesofthepast", name));
        if (advancement != null) {
            player.getAdvancements().award(advancement, "impossible");
        }
    }
    public static void onDreamEnded(ServerPlayer player) {
        addGrief(player, GRIEF_PER_DREAM);
    }

    public static void checkDailyGrief(ServerPlayer player) {

        ServerLevel level = (ServerLevel) player.level();
        long currentDay = level.getGameTime() / 24000L;

        long lastDay = lastCountedDay.getOrDefault(player.getUUID(), currentDay);
        if (currentDay > lastDay) {
            addGrief(player, GRIEF_PER_DAY);
        }
        lastCountedDay.put(player.getUUID(), currentDay);
    }

    private static void killFromGrief(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("You couldn't live with that."));
        player.kill((ServerLevel) player.level());
        grief.remove(player.getUUID());
    }

    public static void checkEmblemDrain(ServerPlayer player) {
        boolean holdingEmblem = player.getMainHandItem().is(ModItems.GRIEF_EMBLEM.get())
                || player.getOffhandItem().is(ModItems.GRIEF_EMBLEM.get());

        if (!holdingEmblem) return;

        ServerLevel level = (ServerLevel) player.level();
        if (level.getGameTime() % 20 != 0) return;

        // I had to settle for just adding potion effects because well.. it is annoying to add it fundementally needs uuids
        player.addEffect(new MobEffectInstance(MobEffects.SPEED, 40, 0, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 40, 0, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 40, 0, false, false));

        int current = getGrief(player);
        if (current <= 0) return;

        int updated = Math.max(current - DRAIN_PER_SECOND, 0);
        grief.put(player.getUUID(), updated);
        GriefNetworking.sendToPlayer(player, updated);
    }

    public static int getMaxGrief() {
        return MAX_GRIEF;
    }

}