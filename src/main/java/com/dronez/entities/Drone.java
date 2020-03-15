package com.dronez.entities;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.energy.EnergyStorage;

import javax.annotation.Nullable;
import java.util.*;

//For now, this will be the pre-optimization Drone Entity class. Later, this class can easily be modified to support multiple Drone Types via inheritance.
public class Drone extends FlyingEntity {

    //add material type tracking and texture locations here
    protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.createKey(Drone.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    protected static final DataParameter<Byte> SHELL = EntityDataManager.createKey(Drone.class, DataSerializers.BYTE);
    protected static final DataParameter<Byte> CORE = EntityDataManager.createKey(Drone.class, DataSerializers.BYTE);
    protected static final DataParameter<Byte> BLADE = EntityDataManager.createKey(Drone.class, DataSerializers.BYTE);
    private EnergyStorage battery;
    private boolean charging;



    public Drone(EntityType<Drone> type, World p_i48578_2_) {
        super(type, p_i48578_2_);
        this.moveController = new MoveHelperController(this);

    }

    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D * this.dataManager.get(CORE));// * this.core.getValue());
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(2.0D * this.dataManager.get(BLADE));// * this.blade.getValue());
        this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(5.0D * this.dataManager.get(SHELL));// * this.shell.getValue());
    }

    protected void registerGoals() {
        //this is a basic goal registration, I will need to make custom goal classes to have it follow the player or return to charger
        this.goalSelector.addGoal(1, new Drone.FollowOwner(this, this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue(), 10.0F, 2.0F));
        //this.goalSelector.addGoal(3, new Drone.Charge(this.battery));
    }

    public void tick() {
        super.tick();
        //and then add anything else that needs to be updated every tick
    }

    protected void registerData() {
        super.registerData();
        //and then add any other data that needs to be registered upon spawning
        this.dataManager.register(OWNER_UNIQUE_ID, Optional.empty());
        this.dataManager.register(SHELL, (byte)1);
        this.dataManager.register(CORE, (byte)1);
        this.dataManager.register(BLADE, (byte)1);
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        if (this.getOwnerId() == null) {
            compound.putString("OwnerUUID", "");
        } else {
            compound.putString("OwnerUUID", this.getOwnerId().toString());
        }
        compound.putByte("Shell", this.dataManager.get(SHELL));
        compound.putByte("Core", this.dataManager.get(CORE));
        compound.putByte("Blade", this.dataManager.get(BLADE));
        compound.putBoolean("Charging", this.isCharging());
    }
    @Override
    public void readAdditional(CompoundNBT compound) {
        String s;
        if (compound.contains("OwnerUUID")) {
            s = compound.getString("OwnerUUID");
        } else {
            String s1 = compound.getString("Owner");
            s = PreYggdrasilConverter.convertMobOwnerIfNeeded(Objects.requireNonNull(this.getServer()), s1);
        }

        if (!s.isEmpty()) {
            try {
                this.setOwnerId(UUID.fromString(s));
            } catch (Throwable var4) {
                this.setOwnerId(Objects.requireNonNull(this.world.getClosestPlayer(this, 100)).getUniqueID());
            }
        }
        this.dataManager.set(SHELL, compound.getByte("Shell"));
        this.dataManager.set(CORE, compound.getByte("Core"));
        this.dataManager.set(BLADE, compound.getByte("Blade"));
        super.readAdditional(compound);

    }

    @Override
    @Nullable
    public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        if(dataTag != null) this.readAdditional(dataTag);
        return spawnDataIn;
    }

    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return 0.3F;
    }

    @Nullable
    public UUID getOwnerId() {
        return this.dataManager.get(OWNER_UNIQUE_ID).orElse((UUID)null);
    }

    public void setOwnerId(@Nullable UUID p_184754_1_) {
        this.dataManager.set(OWNER_UNIQUE_ID, Optional.ofNullable(p_184754_1_));
    }

    @Nullable
    public LivingEntity getOwner() {
        try {
            UUID uuid = this.getOwnerId();
            return uuid == null ? null : this.world.getPlayerByUuid(uuid);
        } catch (IllegalArgumentException var2) {
            return null;
        }
    }

    public boolean isCharging() {
        return charging;
    }

    public Drone initBattery(int capacity){
        this.battery = new EnergyStorage(capacity, capacity, capacity, capacity);
        return this;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        super.getHurtSound(damageSourceIn);
        return SoundEvents.ENTITY_BLAZE_HURT;
    }




    static class MoveHelperController extends MovementController {
        private final Drone parentEntity;
        private int courseChangeCooldown = 0;

        public MoveHelperController(Drone drone) {
            super(drone);
            this.parentEntity = drone;
        }

        public void tick() {
            if (this.action == MovementController.Action.MOVE_TO) {
                if (this.courseChangeCooldown-- <= 0) {
                    this.courseChangeCooldown += this.parentEntity.getRNG().nextInt(5) + 2;
                    Vec3d vec3d = new Vec3d(this.posX - this.parentEntity.posX, this.posY - this.parentEntity.posY, this.posZ - this.parentEntity.posZ);
                    double d0 = vec3d.length();
                    vec3d = vec3d.normalize();
                    if (this.func_220673_a(vec3d, MathHelper.ceil(d0))) {
                        this.parentEntity.setMotion(this.parentEntity.getMotion().add(vec3d.scale(0.1D)));
                    } else {
                        this.action = MovementController.Action.WAIT;
                    }
                }

            }
        }

        private boolean func_220673_a(Vec3d p_220673_1_, int p_220673_2_) {
            AxisAlignedBB axisalignedbb = this.parentEntity.getBoundingBox();

            for(int i = 1; i < p_220673_2_; ++i) {
                axisalignedbb = axisalignedbb.offset(p_220673_1_);
                if (!this.parentEntity.world.isCollisionBoxesEmpty(this.parentEntity, axisalignedbb)) {
                    return false;
                }
            }

            return true;
        }
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
        protected EnergyStorage battery;

        public Charge(EnergyStorage battery){

        }

        @Override
        public boolean shouldExecute() {
            return battery.getEnergyStored() < 100;
        }
    }
}

