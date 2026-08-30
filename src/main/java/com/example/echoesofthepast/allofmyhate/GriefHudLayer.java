package com.example.echoesofthepast.allofmyhate;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@EventBusSubscriber(modid = "echoesofthepast", value = Dist.CLIENT)
public class GriefHudLayer {

    private static final Identifier[] GRIEF_TEXTURES = {
            Identifier.fromNamespaceAndPath("echoesofthepast", "textures/gui/grief_0.png"),
            Identifier.fromNamespaceAndPath("echoesofthepast", "textures/gui/grief_10.png"),
            Identifier.fromNamespaceAndPath("echoesofthepast", "textures/gui/grief_20.png"),
            Identifier.fromNamespaceAndPath("echoesofthepast", "textures/gui/grief_30.png"),
            Identifier.fromNamespaceAndPath("echoesofthepast", "textures/gui/grief_40.png"),
            Identifier.fromNamespaceAndPath("echoesofthepast", "textures/gui/grief_50.png"),
            Identifier.fromNamespaceAndPath("echoesofthepast", "textures/gui/grief_60.png"),
            Identifier.fromNamespaceAndPath("echoesofthepast", "textures/gui/grief_70.png"),
            Identifier.fromNamespaceAndPath("echoesofthepast", "textures/gui/grief_80.png"),
            Identifier.fromNamespaceAndPath("echoesofthepast", "textures/gui/grief_90.png"),
            Identifier.fromNamespaceAndPath("echoesofthepast", "textures/gui/grief_100.png")
    };
// I KNOW I COULD'VE DONE IT BETTER THAN THAT BUT ;-; no time damn  thats a lot of spam
   @SubscribeEvent
    public static void registerLayer(RegisterGuiLayersEvent event) {

        event.registerAboveAll(
                Identifier.fromNamespaceAndPath(
                        "echoesofthepast",
                        "grief_hud"
                ),
                (guiGraphics, deltaTracker) -> {

                    int grief = GriefNetworking.getClientGrief();

                    // Keep grief safely inside 0–100.
                    grief = Math.clamp(grief, 0, 100);

                    // Convert grief into the nearest 10% texture.
                    int textureIndex = Math.round(grief / 10.0f);

                    Identifier texture = GRIEF_TEXTURES[textureIndex];

                    float scale = 1F;

                    int width = (int)(128 * scale);
                    int height = (int)(32 * scale);

                    guiGraphics.blit(
                            RenderPipelines.GUI_TEXTURED,
                            texture,
                            10,
                            10,
                            0,
                            0,
                            width,
                            height,
                            128,
                            32
                    );
                }
        );
    }
}

