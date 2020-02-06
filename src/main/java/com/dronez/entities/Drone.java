package com.dronez.entities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

//For now, this will be the pre-optimization Drone Entity class. Later, this class can easily be modified to support multiple Drone Types via inheritance.
public class Drone extends FlyingEntity {

    private enum PartMaterial {
        iron, gold, diamond
    }
    //add material type tracking and texture locations here
    private LivingEntity owner;
    private PartMaterial lfBlade, rfBlade, lbBlade, rbBlade, shell, core;
    private boolean charging;



    public Drone(EntityType<Drone> type, World p_i48578_2_) {
        super(type, p_i48578_2_);
        this.owner = null;
        this.lfBlade = null;
        this.rfBlade = null;
        this.lbBlade = null;
        this.rbBlade = null;
        this.shell = null;
        this.core = null;
    }

    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
    }

    protected void registerGoals() {
        //this is a basic goal registration, I will need to make custom goal classes to have it follow the player or return to charger
        this.goalSelector.addGoal(1, new Drone.FollowOwner(this, 1.0D, 1.0F, 4.0F));
        this.goalSelector.addGoal(3, new Drone.Charge());
        this.goalSelector.addGoal(5, new Drone.Wander());
    }

    public void tick() {
        super.tick();
        //and then add anything else that needs to be updated every tick
    }

    protected void registerData() {
        super.registerData();
        //and then add any other data that needs to be registered upon spawning
    }

    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return 2.6F;
    }

    public LivingEntity getOwner() {
        return owner;
    }

    public boolean isCharging() {
        return charging;
    }

    public Drone setMaterial(PartMaterial material){
        this.shell = material;
        this.core = material;
        this.lfBlade = material;
        this.rfBlade = material;
        this.lbBlade = material;
        this.rbBlade = material;
        return this;
    }
    public Drone setOwner(LivingEntity player){
        this.owner = player;
        return this;
    }


    static class FollowOwner extends Goal {
        protected final Drone drone;
        private LivingEntity owner;
        protected final IWorldReader world;
        private final double followSpeed;
        private final PathNavigator navigator;
        private int timeToRecalcPath;
        private final float maxDist;
        private final float minDist;
        private float oldWaterCost;

        public FollowOwner(Drone droneIn, double followSpeedIn, float minDistIn, float maxDistIn) {
            this.drone = droneIn;
            this.world = droneIn.world;
            this.followSpeed = followSpeedIn;
            this.navigator = droneIn.getNavigator();
            this.minDist = minDistIn;
            this.maxDist = maxDistIn;
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
            if (!(droneIn.getNavigator() instanceof GroundPathNavigator) && !(droneIn.getNavigator() instanceof FlyingPathNavigator)) {
                throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
            }
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute() {
            LivingEntity livingentity = this.drone.getOwner();
            if (livingentity == null) {
                return false;
            } else if (livingentity instanceof PlayerEntity && ((PlayerEntity)livingentity).isSpectator()) {
                return false;
            } else if (this.drone.isCharging()) {
                return false;
            } else if (this.drone.getDistanceSq(livingentity) < (double)(this.minDist * this.minDist)) {
                return false;
            } else {
                this.owner = livingentity;
                return true;
            }
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            return !this.navigator.noPath() && this.drone.getDistanceSq(this.owner) > (double)(this.maxDist * this.maxDist) && !this.drone.isCharging();
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
            this.timeToRecalcPath = 0;
            this.oldWaterCost = this.drone.getPathPriority(PathNodeType.WATER);
            this.drone.setPathPriority(PathNodeType.WATER, 0.0F);
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void resetTask() {
            this.owner = null;
            this.navigator.clearPath();
            this.drone.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            this.drone.getLookController().setLookPositionWithEntity(this.owner, 10.0F, (float)this.drone.getVerticalFaceSpeed());
            if (!this.drone.isCharging()) {
                if (--this.timeToRecalcPath <= 0) {
                    this.timeToRecalcPath = 10;
                    if (!this.navigator.tryMoveToEntityLiving(this.owner, this.followSpeed)) {
                        if (!this.drone.getLeashed() && !this.drone.isPassenger()) {
                            if (!(this.drone.getDistanceSq(this.owner) < 144.0D)) {
                                int i = MathHelper.floor(this.owner.posX) - 2;
                                int j = MathHelper.floor(this.owner.posZ) - 2;
                                int k = MathHelper.floor(this.owner.getBoundingBox().minY);

                                for(int l = 0; l <= 4; ++l) {
                                    for(int i1 = 0; i1 <= 4; ++i1) {
                                        if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && this.canTeleportToBlock(new BlockPos(i + l, k - 1, j + i1))) {
                                            this.drone.setLocationAndAngles((double)((float)(i + l) + 0.5F), (double)k, (double)((float)(j + i1) + 0.5F), this.drone.rotationYaw, this.drone.rotationPitch);
                                            this.navigator.clearPath();
                                            return;
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }

        protected boolean canTeleportToBlock(BlockPos pos) {
            BlockState blockstate = this.world.getBlockState(pos);
            return (blockstate.func_215682_a(this.world, pos, this.drone) || blockstate.isIn(BlockTags.LEAVES)) && this.world.isAirBlock(pos.up()) && this.world.isAirBlock(pos.up(2));
        }
    }

    static class Charge extends Goal {

        
        @Override
        public boolean shouldExecute() {
            return false;
        }
    }

    static class Wander extends Goal {


        @Override
        public boolean shouldExecute() {
            return false;
        }
    }


}

