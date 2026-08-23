package com.example.examplemod.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.EnumSet;

public class DeadComrade extends Zombie {
    public DeadComrade(EntityType<? extends Zombie> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(2, new ApproachAndStareGoal(this, 1.8D, 4.0D));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
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

            // always locked onto the player, whether moving or stopped
            mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

            double dist = mob.distanceTo(target);
            if (dist > stopDistance) {
                mob.getNavigation().moveTo(target, speed);
            } else {
                mob.getNavigation().stop();
            }
        }
    }
}