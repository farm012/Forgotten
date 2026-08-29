package com.example.examplemod.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.createEntities("examplemod");

    public static final Supplier<EntityType<DeadComrade>> DEAD_COMRADE = ENTITY_TYPES.register(
            "dead_comrade",
            () -> EntityType.Builder.of(DeadComrade::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f) // gojo level tall
                    .build(ResourceKey.create(Registries.ENTITY_TYPE,
                            Identifier.fromNamespaceAndPath("examplemod", "dead_comrade")))
    );

    public static final Supplier<EntityType<TombGuardian>> TOMB_GUARDIAN = ENTITY_TYPES.register(
            "tomb_guardian",
            () -> EntityType.Builder.of(TombGuardian::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE,
                            Identifier.fromNamespaceAndPath("examplemod", "tomb_guardian")))
    );

    public static final Supplier<EntityType<GrievingSoul>> GRIEVING_SOUL = ENTITY_TYPES.register(
            "grieving_soul",
            () -> EntityType.Builder.of(GrievingSoul::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.95f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE,
                            Identifier.fromNamespaceAndPath("examplemod", "grieving_soul")))
    );
}