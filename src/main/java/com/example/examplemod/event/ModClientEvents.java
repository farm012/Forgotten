package com.example.examplemod.event;

import com.example.examplemod.entity.DeadComradeRenderer;
import com.example.examplemod.entity.GrievingSoulRenderer;
import com.example.examplemod.entity.ModEntities;
import com.example.examplemod.entity.TombGuardianRenderer;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = "examplemod", value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.DEAD_COMRADE.get(), DeadComradeRenderer::new);
        event.registerEntityRenderer(ModEntities.TOMB_GUARDIAN.get(), TombGuardianRenderer::new);
        event.registerEntityRenderer(ModEntities.GRIEVING_SOUL.get(), GrievingSoulRenderer::new);
    }
}