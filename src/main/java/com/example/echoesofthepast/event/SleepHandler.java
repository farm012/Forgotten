package com.example.echoesofthepast.event;

import com.example.echoesofthepast.allofmyhate.GriefNetworking;
import com.example.echoesofthepast.dream.DreamManager;
import com.example.echoesofthepast.grief.GriefManager;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = "echoesofthepast")
public class SleepHandler {



    private static final Map<UUID, Long> lastNaggedDay = new HashMap<>();


    @SubscribeEvent
    public static void onSleep(CanPlayerSleepEvent event) {
        ServerPlayer player = event.getEntity();
       if (GriefManager.getGrief(player) == 0) return; // no grief nothing to dream about bruh..., i will make it negative tho
        if (player.getRandom().nextFloat() < 0.25F) {
            DreamManager.markPending(player);
        } else {
            player.sendSystemMessage(
                    net.minecraft.network.chat.Component.literal("You feel a comforting sense of relief after a dreamless night")
                            .withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC)
            );
        }
    }
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity().level().isClientSide()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;




        if (player.isSleeping() && DreamManager.isPending(player)) {
            DreamManager.enterDream(player);
        }
        GriefManager.checkDailyGrief(player);


        GriefManager.checkEmblemDrain(player);


        com.example.echoesofthepast.hallucination.HallucinationManager.tick(player);


        checkNightNag(player);

        GriefManager.tickDeathCountdown(player);


        DreamManager.tick(player);

        com.example.echoesofthepast.item.AnkhManager.tickPassive(player); // this is way out of place but too lazy to add another method
    }


    private static void checkNightNag(ServerPlayer player) {
        if (DreamManager.hasEnteredDreamBefore(player)) return;
        if (player.isSleeping()) return;

        ServerLevel level = (ServerLevel) player.level();
        if (!level.isDarkOutside()) return;

        long currentDay = level.getGameTime() / 24000L;
        UUID id = player.getUUID();
        if (lastNaggedDay.getOrDefault(id, -1L) == currentDay) return;

        net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, new GriefNetworking.SleepPromptPacket());
        player.level().playSound(null, player.blockPosition(),
                net.minecraft.sounds.SoundEvents.NOTE_BLOCK_PLING.value(),
                net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.4F);


        lastNaggedDay.put(id, currentDay);
    }
}