package com.example.examplemod.event;

import com.example.examplemod.dream.DreamManager;
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
        if (player.getRandom().nextFloat() < 0.30F) {
            DreamManager.markPending(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity().level().isClientSide()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (player.isSleeping() && DreamManager.isPending(player)) {
            DreamManager.enterDream(player);
        }

        DreamManager.tick(player);
    }
}