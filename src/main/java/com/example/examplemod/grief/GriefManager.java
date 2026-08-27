package com.example.examplemod.grief;

import com.example.examplemod.allofmyhate.GriefNetworking;
import com.example.examplemod.item.ModItems;
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
    private static final int GRIEF_PER_DREAM = 15;
    private static final int GRIEF_PER_DAY = 10;
    private static final int DRAIN_PER_SECOND = 1;


    private static final Map<UUID, Integer> grief = new HashMap<>();
    private static final Map<UUID, Long> lastCountedDay = new HashMap<>();

    public static int getGrief(ServerPlayer player) {
        return grief.getOrDefault(player.getUUID(), 0);
    }

    public static void addGrief(ServerPlayer player, int amount) {
        int current = getGrief(player);
        int updated = Math.min(current + amount, MAX_GRIEF);
        grief.put(player.getUUID(), updated);

        GriefNetworking.sendToPlayer(player, updated);

        if (updated >= MAX_GRIEF) {
            killFromGrief(player);
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

        int current = getGrief(player);
        if (current <= 0) return;

        int updated = Math.max(current - DRAIN_PER_SECOND, 0);
        grief.put(player.getUUID(), updated);
        GriefNetworking.sendToPlayer(player, updated);
    }

}