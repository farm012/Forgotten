package com.example.examplemod.event;

import com.example.examplemod.dream.DreamManager;
import com.example.examplemod.grief.GriefManager;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = "examplemod")
public class SleepHandler {

    @SubscribeEvent
    public static void onSleep(CanPlayerSleepEvent event) {
        ServerPlayer player = event.getEntity();
       if (GriefManager.getGrief(player) < 0) return; // no grief nothing to dream about bruh..., i will make it negative tho
        if (player.getRandom().nextFloat() < 0.30F) {
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

        DreamManager.tick(player);
    }
}