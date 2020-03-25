package com.dronez.block.workshop;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.ParametersAreNonnullByDefault;

public class WorkshopContainer extends Container {
    public static ContainerType<WorkshopContainer> TYPE = (ContainerType<WorkshopContainer>) IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        return new WorkshopContainer(windowId, Minecraft.getInstance().world, pos, inv);
    }).setRegistryName("dronez", "workshop_block");

    private WorkshopTileEntity te;
    private InvWrapper inv;

    public WorkshopContainer(int id, World world, BlockPos pos, PlayerInventory playerInventory) {
        super(TYPE, id);
        this.te = (WorkshopTileEntity)world.getTileEntity(pos);
        this.inv = new InvWrapper(playerInventory);

        layoutPlayedInventorySlots(10, 70);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(IWorldPosCallable.of(te.getWorld(), te.getPos()), playerIn, WorkshopBlock.WORKSHOP_BLOCK);
    }

    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }

        return index;
    }

    private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }

        return index;
    }

    private void layoutPlayedInventorySlots(int leftCol, int topRow) {
        // Player inventory
        int index = addSlotBox(inv, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(inv, 0, leftCol, topRow, 9, 18);

        // Workshop slots
        addSlot(new SlotItemHandler(inv, index, 64, 24));
    }
}
