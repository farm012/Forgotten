package com.example.examplemod.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.illager.Vindicator;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.EnumSet;

public class TombGuardian extends Zombie {
    private static final EntityDataAccessor<BlockPos> BOUND_POS =
            SynchedEntityData.defineId(TombGuardian.class, EntityDataSerializers.BLOCK_POS);

    private static final double DETECT_RADIUS = 12.0D;
    private static final double LEASH_RADIUS = 20.0D;

    private static final int LIGHTNING_COOLDOWN_TICKS = 200;
    private static final int SLAM_COOLDOWN_TICKS = 100;
    private static final double SLAM_RANGE = 3.0D;

    private int lightningCooldown = 0;
    private int slamCooldown = 0;

    private static final EntityDataAccessor<Integer> ABILITY =
            SynchedEntityData.defineId(TombGuardian.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(TombGuardian.class, EntityDataSerializers.INT);



    private static final int ABILITY_COUNT = 2;
    private static final int SKIN_COUNT = 3;



    private boolean hasBound = false;




    public TombGuardian(EntityType<? extends Zombie> entityType, Level level) {
        super(entityType, level);
        this.setPersistenceRequired();
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(BOUND_POS, BlockPos.ZERO);
        builder.define(ABILITY, 0);
        builder.define(VARIANT, 0);
    }
    public int getAbility() { return this.entityData.get(ABILITY); }   // that is just useless i guess
    public int getVariant() { return this.entityData.get(VARIANT); }


    @Override
    public void tick() {
        super.tick();

        if (!hasBound && !this.level().isClientSide()) {
            this.entityData.set(BOUND_POS, this.blockPosition());
            this.entityData.set(ABILITY, this.random.nextInt(ABILITY_COUNT));
            this.entityData.set(VARIANT, this.random.nextInt(SKIN_COUNT));
            hasBound = true;
        }

        if (this.level().isClientSide()) return;

        LivingEntity target = this.getTarget();
        if (target == null) return;

        // gate each ability behind which one this specific guardian rolled
        if (getAbility() == 0) {
            // --- lightning logic from before, unchanged ---
            if (lightningCooldown > 0) {
                lightningCooldown--;
            } else {
                var lightning = net.minecraft.world.entity.EntityType.LIGHTNING_BOLT.create(
                        this.level(), net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
                if (lightning != null) {
                    lightning.setPos(target.getX(), target.getY(), target.getZ());
                    this.level().addFreshEntity(lightning);
                }
                lightningCooldown = LIGHTNING_COOLDOWN_TICKS;
            }
        } else if (getAbility() == 1) {
            // --- slam logic from before, unchanged ---
            double distSq = this.distanceToSqr(target);
            if (distSq <= SLAM_RANGE * SLAM_RANGE) {
                if (slamCooldown > 0) {
                    slamCooldown--;
                } else {
                    double dx = target.getX() - this.getX();
                    double dz = target.getZ() - this.getZ();
                    target.knockback(1.6D, -dx, -dz);
                    this.level().playSound(
                            null,
                            this.blockPosition(),
                            SoundEvents.GENERIC_EXPLODE.value(),
                            SoundSource.HOSTILE,
                            1.0F,
                            0.6F
                    );
                    slamCooldown = SLAM_COOLDOWN_TICKS;
                }
            }
        }
    }


    public BlockPos getBoundPos() {
        return this.entityData.get(BOUND_POS);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        BlockPos pos = getBoundPos();
        output.putInt("BoundX", pos.getX());
        output.putInt("BoundY", pos.getY());
        output.putInt("BoundZ", pos.getZ());
        output.putBoolean("HasBound", hasBound);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        int x = input.getIntOr("BoundX", 0);
        int y = input.getIntOr("BoundY", 0);
        int z = input.getIntOr("BoundZ", 0);
        this.entityData.set(BOUND_POS, new BlockPos(x, y, z));
        hasBound = input.getBooleanOr("HasBound", false);
    }

    @Override
    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 2D, false));
        this.goalSelector.addGoal(1, new ReturnToBoundGoal(this, 2D));
        this.targetSelector.addGoal(1, new BoundedTargetGoal(this));
    }

    private static class BoundedTargetGoal extends NearestAttackableTargetGoal<Player> {
        private final TombGuardian guardian;

        public BoundedTargetGoal(TombGuardian guardian) {
            super(guardian, Player.class, true);
            this.guardian = guardian;
        }

        @Override
        public boolean canUse() {
            if (!super.canUse()) return false;
            if (this.target == null) return false;
            return guardian.getBoundPos().distToCenterSqr(this.target.getX(), this.target.getY(), this.target.getZ())
                    <= DETECT_RADIUS * DETECT_RADIUS;
        }
    }

    private static class ReturnToBoundGoal extends Goal {
        private final TombGuardian guardian;
        private final double speed;

        public ReturnToBoundGoal(TombGuardian guardian, double speed) {
            this.guardian = guardian;
            this.speed = speed;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            BlockPos bound = guardian.getBoundPos();
            return bound.distToCenterSqr(guardian.getX(), guardian.getY(), guardian.getZ())
                    > LEASH_RADIUS * LEASH_RADIUS;
        }

        @Override
        public void start() {
            guardian.setTarget(null); // drop aggro, we're going home
        }

        @Override
        public void tick() {
            BlockPos bound = guardian.getBoundPos();
            guardian.getNavigation().moveTo(bound.getX() + 0.5, bound.getY(), bound.getZ() + 0.5, speed);
        }

        @Override
        public boolean canContinueToUse() {
            return canUse();
        }
    }
    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }
}