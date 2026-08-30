package com.example.echoesofthepast.event;

import com.example.echoesofthepast.entity.DeadComradeRenderer;
import com.example.echoesofthepast.entity.GrievingSoulRenderer;
import com.example.echoesofthepast.entity.ModEntities;
import com.example.echoesofthepast.entity.TombGuardianRenderer;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = "echoesofthepast", value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.DEAD_COMRADE.get(), DeadComradeRenderer::new);
        event.registerEntityRenderer(ModEntities.TOMB_GUARDIAN.get(), TombGuardianRenderer::new);
        event.registerEntityRenderer(ModEntities.GRIEVING_SOUL.get(), GrievingSoulRenderer::new);
    }
}