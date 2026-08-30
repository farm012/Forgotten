package com.example.echoesofthepast.allofmyhate;

import com.example.echoesofthepast.allofmyhate.GriefNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@EventBusSubscriber(modid = "echoesofthepast", value = Dist.CLIENT)
public class SleepPromptLayer {

    @SubscribeEvent
    public static void registerLayer(RegisterGuiLayersEvent event) {
        event.registerAboveAll(
                Identifier.fromNamespaceAndPath("echoesofthepast", "sleep_prompt"),
                (guiGraphics, deltaTracker) -> {
                    if (System.currentTimeMillis() > GriefNetworking.getSleepPromptShowUntil()) return;

                    var mc = Minecraft.getInstance();
                    int screenW = mc.getWindow().getGuiScaledWidth();
                    int screenH = mc.getWindow().getGuiScaledHeight();

                    guiGraphics.pose().pushMatrix();
                    guiGraphics.pose().translate(screenW / 2f, screenH / 2f);
                    guiGraphics.pose().scale(3f, 3f); // big text

                    var text = Component.literal("GO SLEEP")
                            .withStyle(net.minecraft.ChatFormatting.DARK_RED, net.minecraft.ChatFormatting.BOLD);
                    int textWidth = mc.font.width(text);
                    guiGraphics.text(mc.font, text, -textWidth / 2, -4, 0xFFFFFFFF, true);

                    guiGraphics.pose().popMatrix();
                }
        );
    }
}