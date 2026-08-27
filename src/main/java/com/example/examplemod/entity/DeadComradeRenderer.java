package com.example.examplemod.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.zombie.Zombie;

public class DeadComradeRenderer extends ZombieRenderer {
    private static final Identifier[] TEXTURES = {
            Identifier.fromNamespaceAndPath("examplemod", "textures/entity/dead_comrade_0.png"),
            Identifier.fromNamespaceAndPath("examplemod", "textures/entity/dead_comrade_1.png"),
            Identifier.fromNamespaceAndPath("examplemod", "textures/entity/dead_comrade_2.png")
    };

    public DeadComradeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ZombieRenderState createRenderState() {
        return new DeadComradeRenderState();
    }

    @Override
    public void extractRenderState(Zombie entity, ZombieRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        if (entity instanceof DeadComrade comrade && state instanceof DeadComradeRenderState comradeState) {
            comradeState.variant = comrade.getVariant();
        }
    }

    @Override
    public Identifier getTextureLocation(ZombieRenderState state) {
        if (state instanceof DeadComradeRenderState comradeState) {
            return TEXTURES[comradeState.variant % TEXTURES.length];
        }
        return super.getTextureLocation(state);
    }
}