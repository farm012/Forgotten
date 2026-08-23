package com.example.examplemod.event;

import com.example.examplemod.dream.DreamManager;
import com.example.examplemod.grief.GriefManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = "examplemod")
public class ItemPickupHandler {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity().level().isClientSide()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        GriefManager.checkDailyGrief(player);

        if (player.isSleeping() && DreamManager.isPending(player)) {
            DreamManager.enterDream(player);
        }

        DreamManager.tick(player);
    }
}