package com.dronez.entities;

import com.dronez.PartMaterial;
import com.dronez.block.charger.ChargerBlockEnergy;
import com.dronez.block.charger.ChargerBlockTileEntity;
import com.dronez.dronedata.DroneTagWrapper;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.energy.EnergyStorage;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.ref.WeakReference;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class Drone extends FlyingEntity {

    // Material type tracking and texture locations
    protected static final DataParameter<Optional<UUID>> OWNER_UUID = EntityDataManager.createKey(Drone.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    protected static final DataParameter<Byte> SHELL = EntityDataManager.createKey(Drone.class, DataSerializers.BYTE);
    protected static final DataParameter<Byte> CORE = EntityDataManager.createKey(Drone.class, DataSerializers.BYTE);
    protected static final DataParameter<Byte> BLADE = EntityDataManager.createKey(Drone.class, DataSerializers.BYTE);

    // Tag keys
    private final String OWNER_UUID_TAG = "OwnerUUID";
    private final String SHELL_TAG = "Shell";
    private final String BLADE_TAG = "Blade";
    private final String CORE_TAG = "Core";

    // Energy tracking
    private final EnergyStorage battery;
    private boolean charging;
    private int previousChargeValue;

    public Drone(EntityType<Drone> type, World worldIn) {
        super(type, worldIn);
        this.moveController = new MoveHelperController(this);
        this.navigator = new FlyingPathNavigator(this, worldIn);
        this.battery = new EnergyStorage(10000, 10000, 10000, 10000);
        this.charging = false;
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D * this.dataManager.get(CORE));// * this.core.getValue());
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(2.0D * this.dataManager.get(BLADE));// * this.blade.getValue());
        this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(5.0D * this.dataManager.get(SHELL));// * this.shell.getValue());
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(4, new FollowOwner(this, 8.0F, 2.0F));
        this.goalSelector.addGoal(5, new ChargingGoal(this));
    }

    /**
     * Set attributes that rely on core, blade and shell
     */
    private void setCustomAttributes() {
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D * this.dataManager.get(CORE));
        this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(5.0D * this.dataManager.get(SHELL));
        updateSpeed();
    }

    private void updateSpeed() {
        double baseValue = 2.0D * this.dataManager.get(BLADE);
        if (battery.getEnergyStored() == 0) {
            baseValue /= 2;
        }
        LOGGER.info("Setting drone speed to {}", baseValue);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(baseValue);
    }

    /**
     * Called when spawned from a Drone Package
     */
    public void onSpawn(DroneTagWrapper tags, PlayerEntity owner) {
        dataManager.set(BLADE, tags.getBladeMaterial());
        dataManager.set(CORE, tags.getCoreMaterial());
        dataManager.set(SHELL, tags.getShellMaterial());
        dataManager.set(OWNER_UUID, Optional.of(owner.getUniqueID()));

        setCustomAttributes();
    }

    public double getSpeed() {
        return getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue();
    }

    /**
     * Returns whether the battery is below 1000 FE
     * @return we need a charge ASAP
     */
    public boolean criticalCharge() {
        return battery.getEnergyStored() < 1000;
    }

    /**
     * Returns whether the battery is less than full energy
     * @return energy is less than full
     */
    public boolean needsCharge() {
        return battery.getEnergyStored() < battery.getMaxEnergyStored();
    }

    @Override
    public void tick() {
        super.tick();

        if (!charging && ticksExisted % 10 == 0) {
            previousChargeValue = battery.getEnergyStored();
            battery.extractEnergy(1, false);
            if (battery.getEnergyStored() == 0 && previousChargeValue != 0) {
                // Just ran out of battery
                LOGGER.info("Trying to set speed to half");
                updateSpeed();
            }
        }

        if (charging && previousChargeValue == 0 && battery.getEnergyStored() > 0) {
            // We've gotten at least 1 more FE since last tick.
            LOGGER.info("Trying to set speed to normal");
            updateSpeed();
        }
    }

    /**
     * Get the position of this entity as a {@link BlockPos}
     * @return the current position
     */
    public BlockPos getPos() {
        return new BlockPos(posX, posY, posZ);
    }

    @Override
    protected void registerData() {
        super.registerData();
        //and then add any other data that needs to be registered upon spawning
        this.dataManager.register(OWNER_UUID, Optional.empty());
        this.dataManager.register(SHELL, PartMaterial.MATERIAL_IRON);
        this.dataManager.register(CORE, PartMaterial.MATERIAL_IRON);
        this.dataManager.register(BLADE, PartMaterial.MATERIAL_IRON);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);

        if (this.dataManager.get(OWNER_UUID).isPresent()) {
            compound.putString(OWNER_UUID_TAG, this.dataManager.get(OWNER_UUID).get().toString());
        }

        compound.putByte(SHELL_TAG, this.dataManager.get(SHELL));
        compound.putByte(CORE_TAG, this.dataManager.get(CORE));
        compound.putByte(BLADE_TAG, this.dataManager.get(BLADE));
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        String ownerUUIDString;

        if (compound.contains(OWNER_UUID_TAG, 8)) {
            ownerUUIDString = compound.getString(OWNER_UUID_TAG);
        } else {
            String s1 = compound.getString("Owner");
            ownerUUIDString = PreYggdrasilConverter.convertMobOwnerIfNeeded(Objects.requireNonNull(this.getServer()), s1);
        }

        if (!ownerUUIDString.isEmpty()) {
            try {
                setOwnerId(UUID.fromString(ownerUUIDString));
            } catch (Throwable var4) {
                PlayerEntity closestPlayer = this.world.getClosestPlayer(this, 100);
                if (closestPlayer != null) {
                    setOwnerId(closestPlayer.getUniqueID());
                }
            }
        }

        this.dataManager.set(SHELL, compound.getByte(SHELL_TAG));
        this.dataManager.set(CORE, compound.getByte(CORE_TAG));
        this.dataManager.set(BLADE, compound.getByte(BLADE_TAG));
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);

        Item dropItem = Items.IRON_INGOT;
        if (dataManager.get(SHELL) == PartMaterial.MATERIAL_GOLD)
            dropItem = Items.GOLD_INGOT;
        else if (dataManager.get(SHELL) == PartMaterial.MATERIAL_DIAMOND)
            dropItem = Items.DIAMOND;

        this.entityDropItem(new ItemStack(dropItem, 4));
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return 0.3F;
    }

    @Nullable
    public UUID getOwnerId() {
        return this.dataManager.get(OWNER_UUID).orElse(null);
    }

    public void setOwnerId(@Nullable UUID uuid) {
        this.dataManager.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    @Nullable
    public PlayerEntity getOwner() {
        try {
            UUID uuid = this.getOwnerId();
            return uuid == null ? null : this.world.getPlayerByUuid(uuid);
        } catch (IllegalArgumentException var2) {
            return null;
        }
    }

    public byte getShell() {
        return this.dataManager.get(SHELL);
    }

    public boolean isCharging() {
        return charging;
    }

    public void setCharging(boolean charging) {
        this.charging = charging;
    }

    public void addCharge(int amount) {
        this.battery.receiveEnergy(amount, false);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
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
        private final float maxDist;
        private final float minDist;

        public FollowOwner(Drone droneIn, float minDistIn, float maxDistIn) {
            this.drone = droneIn;
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
        @Override
        public boolean shouldExecute() {
            PlayerEntity droneOwner = drone.getOwner();
            if (droneOwner == null || droneOwner.isSpectator() || drone.isCharging()) {
                return false;
            }

            return drone.getDistanceSq(droneOwner) >= this.minDist * this.minDist;
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            if (drone.getOwner() == null) {
                return false;
            }

            return !drone.getNavigator().noPath() && drone.getDistanceSq(drone.getOwner()) > (this.maxDist * this.maxDist) && !drone.isCharging();
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void resetTask() {
            drone.getNavigator().clearPath();
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            if (drone.getOwner() == null || drone.isCharging()) {
                return;
            }

            drone.getLookController().setLookPositionWithEntity(drone.getOwner(), 10.0F, (float) drone.getVerticalFaceSpeed());
            drone.getNavigator().tryMoveToXYZ(drone.getOwner().posX, drone.getOwner().posY + 5, drone.getOwner().posZ, drone.getSpeed());
        }
    }

    /**
     * Send a chat to the player from the Drone
     * @param format the string to type and send
     * @param args any arguments to the string to format in
     */
    public static void say(String format, Object... args) {
        Minecraft.getInstance().player.sendMessage(new StringTextComponent(String.format("[Drone]: " + format, args)));
    }

    /**
     * A goal that:
     * 1) Finds nearest charging block
     * 2) Moves towards the charging block
     * 3) Rests on the charging block until energy is restored
     */
    static class ChargingGoal extends Goal {
        private final Drone drone;
        private BlockPos chargerPos;
        private BlockPos targetPos;
        private ChargerBlockEnergy energySource;

        /**
         * The amount of FE grabbed each tick by the Drone
         */
        private static final int ENERGY_ACCEPT_RATE = 10;

        /**
         * The block radius the Drone uses to scan for a Charger block.
         */
        private static final float SCAN_RADIUS = 100f;

        /**
         * Constructs a new goal governing charging of the Drone
         * @param droneIn the drone
         */
        public ChargingGoal(Drone droneIn) {
            EnumSet<Flag> flags = EnumSet.noneOf(Flag.class);
            flags.add(Flag.MOVE);
            this.setMutexFlags(flags);
            drone = droneIn;
        }

        @Override
        public boolean shouldExecute() {
            return drone.criticalCharge();
        }

        @Override
        public void resetTask() {
            chargerPos = null;
            targetPos = null;
            energySource = null;
        }

        @Override
        public boolean shouldContinueExecuting() {
            // If we don't need a charge anymore, we should exit
            if (!drone.needsCharge()) {
                say("I'm done charging!");
                return false;
            }

            // If our target doesn't exist, we should exit
            if (targetPos == null) {
                return false;
            }

            // If the block disappears, stop charging
            return isChargerBlock(chargerPos);
        }

        /**
         * Checks whether or not the TileEntity at a BlockPos is a Charger Block
         * @param pos the position in the world to check
         * @return true if the block at pos is a Charger Block
         */
        private boolean isChargerBlock(BlockPos pos) {
            TileEntity te = drone.world.getTileEntity(pos);
            if (te == null) return false;
            return te instanceof ChargerBlockTileEntity;
        }

        @Override
        public void startExecuting() {
            // Scan for nearest ChargerBlock
            BlockPos scanVertex1 = drone.getPos().add(-SCAN_RADIUS, -SCAN_RADIUS, -SCAN_RADIUS);
            BlockPos scanVertex2 = drone.getPos().add(SCAN_RADIUS, SCAN_RADIUS, SCAN_RADIUS);
            Stream<BlockPos> positions = BlockPos.getAllInBox(scanVertex1, scanVertex2);
            Optional<BlockPos> nearestBlockPos = positions.filter(this::isChargerBlock).findFirst();
            if (!nearestBlockPos.isPresent()) {
                return;
            }

            BlockPos blockPos = nearestBlockPos.get();
            ChargerBlockTileEntity te = (ChargerBlockTileEntity) drone.world.getTileEntity(blockPos);
            if (te == null) return;
            chargerPos = blockPos;
            targetPos = blockPos.offset(Direction.UP, 1);
            energySource = te.getEnergyStorage();
        }

        /**
         * This method checks if the current distance is close enough to the Charger Block to charge.
         * @return Drone is close enough to Charger Block to accept energy
         */
        private boolean isCloseEnough() {
            return drone.getPos().distanceSq(targetPos) <= 4;
        }

        @Override
        public void tick() {
            if (targetPos == null) {
                return;
            }

            if (isCloseEnough()) {
                if (!drone.isCharging()) {
                    drone.setCharging(true);
                }

                // Grab energy
                int grabbed = energySource.extractEnergy(ENERGY_ACCEPT_RATE, false);
                drone.addCharge(grabbed);
            } else {
                if (drone.isCharging()) {
                    drone.setCharging(false);
                }

                // Get closer to charger
                drone.getMoveHelper().setMoveTo(targetPos.getX(), targetPos.getY(), targetPos.getZ(), 1D);
                //LOGGER.info("({}) -> ({})", drone.getPos(), targetPos);
            }
        }
    }
}

