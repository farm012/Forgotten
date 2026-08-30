package com.example.echoesofthepast.item;


import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ZeusLastOathItem extends Item {
    private static final Map<UUID, Integer> hitCounts = new HashMap<>();
    private static final int HITS_PER_PROC = 2;
    private static final float BONUS_DAMAGE = 4.0F; //adds 2 heats of extra damage

    public ZeusLastOathItem(Properties properties) {
        super(properties);
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        UUID id = attacker.getUUID();
        int count = hitCounts.getOrDefault(id, 0) + 1;

        if (count >= HITS_PER_PROC) {
            hitCounts.put(id, 0);

            if (target.level() instanceof ServerLevel serverLevel) {
                var lightning = EntityType.LIGHTNING_BOLT.create(serverLevel, EntitySpawnReason.TRIGGERED);
                if (lightning != null) {
                    lightning.setPos(target.getX(), target.getY(), target.getZ());
                    serverLevel.addFreshEntity(lightning);
                }
                target.hurt(target.damageSources().generic(), BONUS_DAMAGE);
            }
        } else {
            hitCounts.put(id, count);
        }

    }
}