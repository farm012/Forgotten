package com.example.examplemod.allofmyhate;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@EventBusSubscriber(modid = "examplemod", value = Dist.CLIENT)
public class GriefHudLayer {

    @SubscribeEvent
    public static void registerLayer(RegisterGuiLayersEvent event) {
        event.registerAboveAll(
                Identifier.fromNamespaceAndPath("examplemod", "grief_hud"),
                (guiGraphics, deltaTracker) -> {
                    int grief = GriefNetworking.getClientGrief();
                    var font = Minecraft.getInstance().font;
                    guiGraphics.text(font, Component.literal("Grief: " + grief), 10, 10, 0xFFFFFFFF, true);
                }
        );
    }
}