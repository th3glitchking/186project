package com.dronez.entities.storage;

import com.dronez.entities.Drone;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class StorageDrone extends Drone implements INamedContainerProvider {

    private Inventory inv;
    private LazyOptional<?> itemHandler = null;

    public StorageDrone(EntityType<Drone> type, World p_i48578_2_) {
        super(type, p_i48578_2_);
        this.inv = new Inventory(27);
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);

        // Drop all items in chest
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            entityDropItem(inv.getStackInSlot(i));
        }
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        ListNBT listnbt = new ListNBT();

        for (int i = 0; i < 27; ++i) {
            ItemStack itemstack = this.inv.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
                CompoundNBT compoundnbt = new CompoundNBT();
                compoundnbt.putByte("Slot", (byte)i);
                itemstack.write(compoundnbt);
                listnbt.add(compoundnbt);
            }
        }

        compound.put("Items", listnbt);

    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);

        ListNBT listnbt = compound.getList("Items", 10);
        this.initChest();

        for (int i = 0; i < listnbt.size(); ++i) {
            CompoundNBT compoundnbt = listnbt.getCompound(i);
            int j = compoundnbt.getByte("Slot") & 255;
            if (j < 27) {
                this.inv.setInventorySlotContents(j, ItemStack.read(compoundnbt));
            }
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new Retrieve(this));
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("Storage Drone");
    }

    @Nullable
    @ParametersAreNonnullByDefault
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new StorageContainer(i, playerInventory, inv, 3);
    }

    protected void initChest() {
        Inventory inventory = this.inv;
        this.inv = new Inventory(27);
        if (inventory != null) {
            int i = Math.min(inventory.getSizeInventory(), this.inv.getSizeInventory());

            for(int j = 0; j < i; ++j) {
                ItemStack itemstack = inventory.getStackInSlot(j);
                if (!itemstack.isEmpty()) {
                    this.inv.setInventorySlotContents(j, itemstack.copy());
                }
            }
        }

        this.itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this.inv));
    }

    public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
        if (inventorySlot == 499) {
            if (itemStackIn.isEmpty()) {
                this.initChest();
                return true;
            }

            if (itemStackIn.getItem() == Blocks.CHEST.asItem()) {
                this.initChest();
                return true;
            }
        }

        return super.replaceItemInInventory(inventorySlot, itemStackIn);
    }

    @Override
    public boolean processInteract(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        Item item = itemstack.getItem();
        if(itemstack.isEmpty() || item.isFood()){
            if(!this.world.isRemote) {
                NetworkHooks.openGui((ServerPlayerEntity) player, this);
            }
        }
        return super.processInteract(player, hand);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (this.isAlive() && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && itemHandler != null)
            return itemHandler.cast();
        return super.getCapability(capability, facing);
    }



    static class Retrieve extends Goal {

        StorageDrone drone;
        ChestTileEntity target;
        ItemStack item;
        int chestSlot;

        public Retrieve(StorageDrone droneIn){
            drone = droneIn;
        }

        @Override
        public boolean shouldExecute() {
            boolean flag = false;
            for (int i = 0; i < drone.inv.getSizeInventory() && !flag; i++){
                if (drone.inv.getStackInSlot(i).isEmpty()) {
                    continue;
                }
                target = nearbyChestHasItem(drone.inv.getStackInSlot(i));
                if (target != null){
                    item = target.getStackInSlot(chestSlot);
                    flag = true;
                }
            }
            return flag;
        }

        public boolean shouldContinueExecuting() {
            if (target == null) {
                return false;
            }

            return !drone.getNavigator().noPath() && drone.getDistanceSq(target.getPos().getX(), target.getPos().getY() + 5, target.getPos().getZ()) > 100 && !drone.isCharging();
        }

        public void resetTask() {
            drone.getNavigator().clearPath();
            target = null;
        }

        public void tick() {
            if (target == null || drone.isCharging()) {
                return;
            }

            drone.getLookController().setLookPosition(target.getPos().getX(), target.getPos().getY() + 5, target.getPos().getZ(), 10.0F, (float) drone.getVerticalFaceSpeed());
            drone.getNavigator().tryMoveToXYZ(target.getPos().getX(), target.getPos().getY() + 1, target.getPos().getZ(), drone.getSpeed());

            if (drone.getPos().withinDistance(target.getPos(), 2)) {
                ItemStack temp = drone.inv.addItem(item);
                target.setInventorySlotContents(chestSlot, temp);
                target = null;
            }
        }

        public ChestTileEntity nearbyChestHasItem(ItemStack item) {
            List<TileEntity> near = drone.world.loadedTileEntityList;
            for (TileEntity x : near) {
                if (x instanceof ChestTileEntity) {
                    for (int i = 0; i < ((ChestTileEntity) x).getSizeInventory(); i++) {
                        if (((ChestTileEntity) x).getStackInSlot(i).getItem().equals(item.getItem())) {
                            chestSlot = i;
                            return (ChestTileEntity)x;
                        }
                    }
                }
            }
            return null;
        }


    }
}
