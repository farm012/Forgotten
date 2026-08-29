package com.example.examplemod.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.zombie.Zombie;

public class TombGuardianRenderer extends ZombieRenderer {
    private static final Identifier[] TEXTURES = {
            Identifier.fromNamespaceAndPath("examplemod", "textures/entity/tomb_guardian_0.png"),
            Identifier.fromNamespaceAndPath("examplemod", "textures/entity/tomb_guardian_1.png"),
            Identifier.fromNamespaceAndPath("examplemod", "textures/entity/tomb_guardian_2.png")
    };

    public TombGuardianRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ZombieRenderState createRenderState() {
        return new TombGuardianRenderState();
    }

    @Override
    public void extractRenderState(Zombie entity, ZombieRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        if (entity instanceof TombGuardian guardian && state instanceof TombGuardianRenderState guardianState) {
            guardianState.variant = guardian.getVariant();
        }
    }

    @Override
    public Identifier getTextureLocation(ZombieRenderState state) {
        if (state instanceof TombGuardianRenderState guardianState) {
            return TEXTURES[guardianState.variant % TEXTURES.length];
        }
        return super.getTextureLocation(state);
    }
}