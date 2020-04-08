package com.dronez.block.workshop;

import com.dronez.DronezMod;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.apache.logging.log4j.LogManager;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.stream.Stream;

public class WorkshopContainer extends Container {
    public static ContainerType<WorkshopContainer> TYPE = (ContainerType<WorkshopContainer>) IForgeContainerType.create(
            (windowId, inv, data) -> new WorkshopContainer(windowId, Minecraft.getInstance().world, data.readBlockPos(), inv)
    ).setRegistryName(DronezMod.MODID, WorkshopBlock.REGISTRY_NAME);

    private WorkshopTileEntity te;
    private InvWrapper inv;
    private WorkshopAssembleItemHandler assembleItemInv;
    private WorkshopAIItemHandler aiItemInv;

    /**
     * The slot handlers that are apart of the ASSEMBLE WorkshopDisplayType
     */
    private ArrayList<WorkshopHideableSlotHandler> assembleSlots;

    /**
     * The slot handlers that are apart of the AI WorkshopDisplayType
     */
    private ArrayList<WorkshopHideableSlotHandler> aiSlots;

    /**
     * The starting slot number of the current inventory
     */
    private int startSlotNumber;

    /**
     * The ending slot number of the current inventory
     */
    private int endSlotNumber;

    public WorkshopContainer(int id, World world, BlockPos pos, PlayerInventory playerInventory) {
        super(TYPE, id);
        te = (WorkshopTileEntity)world.getTileEntity(pos);
        inv = new InvWrapper(playerInventory);
        assembleItemInv = new WorkshopAssembleItemHandler();
        aiItemInv = new WorkshopAIItemHandler();
        assembleSlots = new ArrayList<>();
        aiSlots = new ArrayList<>();

        layoutPlayerInventorySlots();
        layoutWorkshopAssembleInventorySlots();
        layoutWorkshopAIInventorySlots();
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canInteractWith(PlayerEntity playerIn) {
        World world = te.getWorld();
        if (world == null) {
            LogManager.getLogger().warn("WorkshopContainer TileEntity's world is null");
            return false;
        }

        return isWithinUsableDistance(IWorldPosCallable.of(world, te.getPos()), playerIn, WorkshopBlock.WORKSHOP_BLOCK);
    }

    /**
     * Change the layout of this container to match the current display type
     * @param displayType the new display type
     */
    public void setDisplayType(WorkshopDisplayType displayType) {
        if (displayType == WorkshopDisplayType.ASSEMBLE) {
            // Hide all AI, show all ASSEMBLE
            aiSlots.forEach(WorkshopHideableSlotHandler::hide);
            assembleSlots.forEach(WorkshopHideableSlotHandler::show);

            startSlotNumber = assembleSlots.get(0).slotNumber;
            endSlotNumber = assembleSlots.get(assembleSlots.size()-1).slotNumber;
        } else {
            // Hide all ASSEMBLE, show all AI
            aiSlots.forEach(WorkshopHideableSlotHandler::show);
            assembleSlots.forEach(WorkshopHideableSlotHandler::hide);

            startSlotNumber = aiSlots.get(0).slotNumber;
            endSlotNumber = aiSlots.get(aiSlots.size()-1).slotNumber;
        }
    }

    private int addSlotRow(IItemHandler handler, int index, int x, int y) {
        for (int i = 0; i < 9; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += 18;
            index++;
        }

        return index;
    }

    private void addSlotBox(IItemHandler handler, int x, int y) {
        int index = 9;
        for (int j = 0; j < 3; j++) {
            index = addSlotRow(handler, index, x, y);
            y += 18;
        }
    }

    /**
     * Create player's hotbar and inventory slots
     */
    private void layoutPlayerInventorySlots() {
        int leftCol = 8, topRow = 84;

        // Player inventory
        addSlotBox(inv, leftCol, topRow);

        // Player hotbar
        topRow += 58;
        addSlotRow(inv, 0, leftCol, topRow);
    }

    /**
     * Add slots for Assemble layout
     */
    private void layoutWorkshopAssembleInventorySlots() {
        addCustomSlot(assembleItemInv, WorkshopAssembleItemHandler.TOP_LEFT_BLADE, 30, 17); // Top left blade
        addCustomSlot(assembleItemInv, WorkshopAssembleItemHandler.SHELL, 48, 17); // Shell
        addCustomSlot(assembleItemInv, WorkshopAssembleItemHandler.TOP_RIGHT_BLADE, 66, 17); // Top right blade
        addCustomSlot(assembleItemInv, WorkshopAssembleItemHandler.CORE, 48, 35); // Core
        addCustomSlot(assembleItemInv, WorkshopAssembleItemHandler.BOTTOM_LEFT_BLADE, 30, 53); // Bottom left blade
        addCustomSlot(assembleItemInv, WorkshopAssembleItemHandler.BOTTOM_RIGHT_BLADE, 66, 53); // Bottom right blade
        addCustomSlot(assembleItemInv, WorkshopAssembleItemHandler.OUTPUT, 124, 35); // Output Slot
    }

    /**
     * Add slots for AI layout
     */
    private void layoutWorkshopAIInventorySlots() {
        addCustomSlot(aiItemInv, WorkshopAIItemHandler.CORE, 27, 47); // Input Core
        addCustomSlot(aiItemInv, WorkshopAIItemHandler.MODIFIER, 76, 47); // Modifier
        addCustomSlot(aiItemInv, WorkshopAIItemHandler.OUTPUT, 134, 47); // Output Core
    }

    /**
     * Creates a {@link WorkshopHideableSlotHandler} and adds it to this container's inventory
     * and the corresponding list of SlotHandlers
     * @param itemHandler the item handler of the slot (assembleItemInv or aiItemInv)
     * @param index the index of the slot
     * @param xPosition the desired x position of the slot
     * @param yPosition the desired y position of the slot
     */
    public void addCustomSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        WorkshopHideableSlotHandler handler = new WorkshopHideableSlotHandler(itemHandler, index, xPosition, yPosition);
        addSlot(handler);
        if (itemHandler == assembleItemInv) {
            assembleSlots.add(handler);
        } else if (itemHandler == aiItemInv) {
            aiSlots.add(handler);
        }
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        int numRows = 4;
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index < numRows * 9) {
                if (!this.mergeItemStack(itemstack1, startSlotNumber, endSlotNumber, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, numRows * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);

        // Throw out any unused input items in Workshop
        Stream.concat(assembleItemInv.getInputStacks(), aiItemInv.getInputStacks()).forEach(stack -> {
            if (!stack.isEmpty()) {
                playerIn.dropItem(stack, false);
            }
        });
    }
}
