package com.example.echoesofthepast.entity;

import com.example.echoesofthepast.ambient.AmbientSoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.EnumSet;

public class TombGuardian extends Zombie {
    private static final EntityDataAccessor<BlockPos> BOUND_POS =
            SynchedEntityData.defineId(TombGuardian.class, EntityDataSerializers.BLOCK_POS);

    private static final double DETECT_RADIUS = 12.0D;
    private static final double LEASH_RADIUS = 20.0D;

    private static final int LIGHTNING_COOLDOWN_TICKS = 200;
    private static final int SLAM_COOLDOWN_TICKS = 80;

    private int lightningCooldown = 0;
    private int slamCooldown = 0;


    private static final int GAZE_COOLDOWN_TICKS = 140; // 7s
    private static final double GAZE_RANGE = 6.0D;
    private int gazeCooldown = 0;

    private boolean hasRoared = false;

    private static final int SUMMON_COOLDOWN_TICKS = 400;
    private int summonCooldown = 0;

    private static final int HEAL_TICK_INTERVAL = 40;
    private static final float HEAL_AMOUNT = 2.0F;
    private int healCheckTimer = 0;

    private static final EntityDataAccessor<Integer> ABILITY =
            SynchedEntityData.defineId(TombGuardian.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(TombGuardian.class, EntityDataSerializers.INT);



    private static final int ABILITY_COUNT = 5;
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

        if (!hasRoared) {
            AmbientSoundManager.playGuardianRoar(this);
            hasRoared = true;
        }


        // gate each ability behind which one this specific guardian rolled

        // THE ABILITY ROLLER DON'T MIND THAT IT IS IN THE ENTITY NOT IN A CEPERATE CLASS IT WORKS WELL ENOUGH
        if (getAbility() == 0) {
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
        }  else if (getAbility() == 1) {
            if (slamCooldown > 0) {
                slamCooldown--;
            } else {
                double dx = target.getX() - this.getX();
                double dz = target.getZ() - this.getZ();
                double horizontalDist = Math.sqrt(dx * dx + dz * dz);

                if (horizontalDist > 0.001) {
                    double pushStrength = 1.2D;
                    double normalizedX = (dx / horizontalDist) * pushStrength;
                    double normalizedZ = (dz / horizontalDist) * pushStrength;

                    target.setDeltaMovement(normalizedX, 0.5D, normalizedZ); // 0.5 = upward pop
                    target.hurtMarked = true; //forces the velocity change to actually sync to the client (that took a long time to figure out)
                }

                // Slam impact particles
                if (this.level() instanceof ServerLevel serverLevel) {
                    BlockPos groundPos = this.blockPosition().below();
                    BlockState groundState = this.level().getBlockState(groundPos);

                    if (!groundState.isAir()) {
                        serverLevel.sendParticles(
                                new BlockParticleOption(
                                        ParticleTypes.BLOCK,
                                        groundState
                                ),
                                this.getX(),
                                this.getY() + 0.1D,
                                this.getZ(),
                                20,
                                0.8D,
                                0.15D,
                                0.8D,
                                0.15D
                        );
                    }
                }

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
     else if (getAbility() == 2) {
        // Dread Gaze
        if (gazeCooldown > 0) {
            gazeCooldown--;
        } else if (this.distanceToSqr(target) <= GAZE_RANGE * GAZE_RANGE) {
            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.BLINDNESS, 60, 0, false, true));
            this.level().playSound(null, this.blockPosition(),
                    net.minecraft.sounds.SoundEvents.ZOMBIE_AMBIENT,
                    net.minecraft.sounds.SoundSource.HOSTILE, 0.8F, 0.4F);
            gazeCooldown = GAZE_COOLDOWN_TICKS;
        }

    } else if (getAbility() == 3) {
        // Spectral Escort (cool names)
        if (summonCooldown > 0) {
            summonCooldown--;
        } else if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            var comrade = com.example.echoesofthepast.entity.ModEntities.DEAD_COMRADE.get()
                    .create(serverLevel, net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED);
            if (comrade != null) {
                comrade.snapTo(this.getX() + 1, this.getY(), this.getZ() + 1, 0, 0);
                comrade.setTarget(target);
                serverLevel.addFreshEntity(comrade);
            }
            summonCooldown = SUMMON_COOLDOWN_TICKS;
        }

    } else if (getAbility() == 4) {
        // Undying Vigil
        healCheckTimer--;
        if (healCheckTimer <= 0) {
            if (this.getHealth() < this.getMaxHealth() * 0.5F) {
                this.heal(HEAL_AMOUNT);
                //the heart particle handler or wtv i'm tried
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                            ParticleTypes.HEART,
                            this.getX(),
                            this.getY() + this.getBbHeight() * 0.5,
                            this.getZ(),
                            5,
                            0.3,
                            0.5,
                            0.3,
                            0.0
                    );
                }

            }
            healCheckTimer = HEAL_TICK_INTERVAL;
        }

    }
     // END OF ABILITY ROLLER UH JUST YEY... IT WAS the funnest part at least
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
        BlockPos loadedBound = new BlockPos(x, y, z);
        boolean loadedHasBound = input.getBooleanOr("HasBound", false);

        //well a bug .. i hate bugs
        double distSq = loadedBound.distToCenterSqr(this.getX(), this.getY(), this.getZ());
        double sanityThreshold = LEASH_RADIUS * 3; //generous margin past normal leash range

        if (loadedHasBound && distSq > sanityThreshold * sanityThreshold) {
            hasBound = false; // this should force it to refresh the bound position
        } else {
            this.entityData.set(BOUND_POS, loadedBound);
            hasBound = loadedHasBound;
        }
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