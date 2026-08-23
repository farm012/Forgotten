package com.example.examplemod.grief;

import com.example.examplemod.allofmyhate.GriefNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
//i kinda forgot to comment on this one but it seems straight forward, just grief system i didn't finish yet
public class GriefManager {
    private static final int MAX_GRIEF = 100;
    private static final int GRIEF_PER_DREAM = 15;
    private static final int GRIEF_PER_DAY = 5;

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
}