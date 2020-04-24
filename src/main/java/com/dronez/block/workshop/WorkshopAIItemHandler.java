package com.dronez.block.workshop;

import com.dronez.DronezMod;
import com.dronez.dronedata.DroneCoreAiHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;

public class WorkshopAIItemHandler extends WorkshopAbstractItemHandler {
    public static final int CORE = 507;
    public static final int MODIFIER = 508;
    public static final int OUTPUT = 509;

    private final ArrayList<Item> swords;

    public WorkshopAIItemHandler() {
        itemStacks.put(CORE, ItemStack.EMPTY);
        itemStacks.put(MODIFIER, ItemStack.EMPTY);
        itemStacks.put(OUTPUT, ItemStack.EMPTY);
        swords = new ArrayList<>(Arrays.asList(
                Items.WOODEN_SWORD,
                Items.STONE_SWORD,
                Items.IRON_SWORD,
                Items.GOLDEN_SWORD,
                Items.DIAMOND_SWORD
        ));
    }

    @Override
    public int getOutputSlotIndex() {
        return OUTPUT;
    }

    /**
     * Called each time an item is inserted/extracted. Determine if
     * the current items on the Workshop are able to build an output.
     * @return the type of core this core is transforming into
     */
    public byte determineOutputType() {
        if (itemStacks.get(CORE).getItem() == Items.AIR) {
            // We don't do anything if there isn't an input core preset
            return -1;
        }

        if (itemStacks.get(MODIFIER).getItem() == Items.CHEST) {
            // If chest, set core to storage
            return DroneCoreAiHelper.CORE_TYPE_STORAGE;
        } else if (itemStacks.get(MODIFIER).getItem() == Items.AIR) {
            // If no modifier, reset core to follow
            return DroneCoreAiHelper.CORE_TYPE_FOLLOW;
        } else {
            // If it's not empty and not a chest, it must be a sword
            return DroneCoreAiHelper.CORE_TYPE_ATTACK;
        }
    }

    /**
     * Attempt to put a Core into the output
     */
    @Override
    public void attemptProduceOutput() {
        byte output = determineOutputType();
        if (output == -1) {
            // No output produced
            itemStacks.put(OUTPUT, ItemStack.EMPTY);
        } else {
            // Output produced
            ItemStack outputStack = new ItemStack(itemStacks.get(CORE).getItem(), 1);
            DroneCoreAiHelper.setType(outputStack, output);
            itemStacks.put(OUTPUT, outputStack);
            LogManager.getLogger().debug("Output Core Type: {}", DroneCoreAiHelper.getType(outputStack));
        }
    }

    /**
     * <p>
     * This function re-implements the vanilla function {@link net.minecraft.inventory.IInventory#isItemValidForSlot(int, ItemStack)}.
     * It should be used instead of simulated insertions in cases where the contents and state of the inventory are
     * irrelevant, mainly for the purpose of automation and logic (for instance, testing if a minecart can wait
     * to deposit its items into a full inventory, or if the items in the minecart can never be placed into the
     * inventory and should move on).
     * </p>
     * <ul>
     * <li>isItemValid is false when insertion of the item is never valid.</li>
     * <li>When isItemValid is true, no assumptions can be made and insertion must be simulated case-by-case.</li>
     * <li>The actual items in the inventory, its fullness, or any other state are <strong>not</strong> considered by isItemValid.</li>
     * </ul>
     *
     * @param slot  Slot to query for validity
     * @param stack Stack to test with for validity
     * @return true if the slot can insert the ItemStack, not considering the current state of the inventory.
     * false if the slot can never insert the ItemStack in any situation.
     */
    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        if (slot == CORE) {
            return stack.getItem() == DronezMod.ironDroneCore || stack.getItem() == DronezMod.goldDroneCore || stack.getItem() == DronezMod.diamondDroneCore;
        } else if (slot == MODIFIER) {
            return stack.getItem() == Items.CHEST || swords.contains(stack.getItem());
        }

        return false;
    }
}
