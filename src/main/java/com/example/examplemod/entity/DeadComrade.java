package com.example.examplemod.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.EnumSet;


public class DeadComrade extends Zombie {
    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(DeadComrade.class, EntityDataSerializers.INT);

    public DeadComrade(EntityType<? extends Zombie> entityType, Level level) {
        super(entityType, level);
    }

    private static final String[] BLAME_LINES = {
            "Why didn't you come back for me...",
            "You left me out there.",
            "I called your name. You didn't answer.",
            "It should have been you.",
            "I trusted you.",
            "You promised."
    };
    private int blameCooldown = 0;


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
    }

    @Override
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(
            net.minecraft.world.level.ServerLevelAccessor level,
            net.minecraft.world.DifficultyInstance difficulty,
            net.minecraft.world.entity.EntitySpawnReason spawnReason,
            net.minecraft.world.entity.SpawnGroupData groupData) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, spawnReason, groupData);
        this.entityData.set(VARIANT, this.random.nextInt(3));
        return result;
    }

    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, variant);
    }

    @Override
    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(2, new ApproachAndStareGoal(this, 1.8D, 6.0D));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean doHurtTarget(net.minecraft.server.level.ServerLevel level, net.minecraft.world.entity.Entity target) {
        return false;
    }

    private static class ApproachAndStareGoal extends Goal {
        private final DeadComrade mob;
        private final double speed;
        private final double stopDistance;

        public ApproachAndStareGoal(DeadComrade mob, double speed, double stopDistance) {
            this.mob = mob;
            this.speed = speed;
            this.stopDistance = stopDistance;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return mob.getTarget() != null;
        }

        @Override
        public void tick() {
            LivingEntity target = mob.getTarget();
            if (target == null) return;
            mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

            double dist = mob.distanceTo(target);
            if (dist > stopDistance) {
                mob.getNavigation().moveTo(target, speed);
            } else {
                mob.getNavigation().stop();
            }

            if (dist <= 8.0D && target instanceof net.minecraft.server.level.ServerPlayer player) {
                mob.blameCooldown--;
                if (mob.blameCooldown <= 0) {
                    String line = BLAME_LINES[mob.getRandom().nextInt(BLAME_LINES.length)];
                    player.sendSystemMessage(
                            net.minecraft.network.chat.Component.literal(line)
                                    .withStyle(net.minecraft.ChatFormatting.GRAY, net.minecraft.ChatFormatting.ITALIC)
                    );

                    // distorted whisper - low pitch, slight random variation each time
                    float pitch = 0.5F + mob.getRandom().nextFloat() * 0.3F;
                    mob.level().playSound(null, mob.blockPosition(),
                            SoundEvents.ENDERMAN_SCREAM,
                            net.minecraft.sounds.SoundSource.HOSTILE, 0.9F, pitch);

                    // the closer it is, the more urgently it talks - down to a 2-4s floor
                    double closeness = Math.max(0, (8.0D - dist) / 8.0D); // 0 far, 1 right on top of you
                    int baseCooldown = (int)(100 - closeness * 60); // 100 far -> 40 close, in ticks
                    mob.blameCooldown = baseCooldown + mob.getRandom().nextInt(60);
                }
            }
        }
    }
}//THIS FILE GOT TOO PACKED omg