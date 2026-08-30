package com.example.echoesofthepast.allofmyhate;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@EventBusSubscriber(modid = "echoesofthepast", value = Dist.CLIENT)
public class FizzleOverlayLayer {

    @SubscribeEvent
    public static void registerLayer(RegisterGuiLayersEvent event) {
        event.registerAboveAll(
                Identifier.fromNamespaceAndPath("echoesofthepast", "fizzle_overlay"),
                (guiGraphics, deltaTracker) -> {
                    float progress = GriefNetworking.getClientFizzle();
                    if (progress <= 0f) return;

                    int screenW = Minecraft.getInstance().getWindow().getGuiScaledWidth();
                    int screenH = Minecraft.getInstance().getWindow().getGuiScaledHeight();

                    float flicker = (float) (Math.random() * 0.15); // small random jitter for "fizzing"
                    int alpha = (int) (Math.min(1f, progress + flicker) * 255);
                    int color = (alpha << 24);

                    guiGraphics.fill(0, 0, screenW, screenH, color); //WWWWWWWWW
                }
        );
    }
}