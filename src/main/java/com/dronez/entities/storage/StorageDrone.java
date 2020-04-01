package com.dronez.entities.storage;

import com.dronez.entities.Drone;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
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
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class StorageDrone extends Drone implements INamedContainerProvider {

    Inventory inv;
    private net.minecraftforge.common.util.LazyOptional<?> itemHandler = null;

    public StorageDrone(EntityType<Drone> type, World p_i48578_2_) {
        super(type, p_i48578_2_);
        this.inv = new Inventory(27);
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        ListNBT listnbt = new ListNBT();

        for(int i = 0; i < 27; ++i) {
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

        for(int i = 0; i < listnbt.size(); ++i) {
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
    protected boolean processInteract(PlayerEntity player, Hand hand) {
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
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.util.Direction facing) {
        if (this.isAlive() && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && itemHandler != null)
            return itemHandler.cast();
        return super.getCapability(capability, facing);
    }
}
