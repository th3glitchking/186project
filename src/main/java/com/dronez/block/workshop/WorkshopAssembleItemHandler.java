package com.dronez.block.workshop;

import com.dronez.DronezMod;
import com.dronez.PartMaterial;
import com.dronez.dronedata.DroneCoreAiHelper;
import com.dronez.dronedata.DroneTagFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class WorkshopAssembleItemHandler extends WorkshopAbstractItemHandler {
    public static final int TOP_LEFT_BLADE = 500;
    public static final int TOP_RIGHT_BLADE = 501;
    public static final int BOTTOM_LEFT_BLADE = 502;
    public static final int BOTTOM_RIGHT_BLADE = 503;
    public static final int SHELL = 504;
    public static final int CORE = 505;
    public static final int OUTPUT = 506;

    private final ArrayList<Integer> bladeSlotKeys;

    public WorkshopAssembleItemHandler() {
        itemStacks.put(TOP_LEFT_BLADE, ItemStack.EMPTY);
        itemStacks.put(TOP_RIGHT_BLADE, ItemStack.EMPTY);
        itemStacks.put(BOTTOM_LEFT_BLADE, ItemStack.EMPTY);
        itemStacks.put(BOTTOM_RIGHT_BLADE, ItemStack.EMPTY);
        itemStacks.put(SHELL, ItemStack.EMPTY);
        itemStacks.put(CORE, ItemStack.EMPTY);
        itemStacks.put(OUTPUT, ItemStack.EMPTY);
        bladeSlotKeys = new ArrayList<>(Arrays.asList(TOP_LEFT_BLADE, TOP_RIGHT_BLADE, BOTTOM_LEFT_BLADE, BOTTOM_RIGHT_BLADE));
    }

    @Override
    public int getOutputSlotIndex() {
        return OUTPUT;
    }

    /**
     * Called each time an item is inserted/extracted. Determine if
     * the current items on the Workshop are able to build an output.
     * @return the egg the input is able to craft
     */
    public ItemStack determineOutput() {
        if (itemStacks.get(CORE).getItem() == Items.AIR) {
            return ItemStack.EMPTY;
        }

        byte shellMaterial = PartMaterial.fromItem(itemStacks.get(SHELL).getItem());
        if (shellMaterial == -1) {
            return ItemStack.EMPTY;
        }

        // Collect all Blade PartMaterials into a Set
        Set<Byte> bladeMaterials = bladeSlotKeys.stream()
                .map(key -> itemStacks.get(key).getItem())
                .map(PartMaterial::fromItem)
                .collect(Collectors.toSet());

        // No null and size of 1 means all are present with same PartMaterial
        if (bladeMaterials.contains(PartMaterial.ERROR) || bladeMaterials.size() != 1) {
            return ItemStack.EMPTY;
        }
        byte bladeMaterial = bladeMaterials.iterator().next();

        ItemStack outputStack = new ItemStack(DronezMod.dronePackageItem, 1);
        DroneTagFactory.Builder()
                .blades(bladeMaterial)
                .shell(shellMaterial)
                .core(itemStacks.get(CORE))
                .output(outputStack);

        return outputStack;
    }

    /**
     * Attempt to put an egg into the output
     */
    public void attemptProduceOutput() {
        itemStacks.put(OUTPUT, determineOutput());
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
        if (slot == TOP_LEFT_BLADE || slot == TOP_RIGHT_BLADE || slot == BOTTOM_LEFT_BLADE || slot == BOTTOM_RIGHT_BLADE) {
            return stack.getItem() == DronezMod.ironDroneBlade || stack.getItem() == DronezMod.goldDroneBlade || stack.getItem() == DronezMod.diamondDroneBlade;
        } else if (slot == SHELL) {
            return stack.getItem() == DronezMod.ironDroneShell || stack.getItem() == DronezMod.goldDroneShell || stack.getItem() == DronezMod.diamondDroneShell;
        } else if (slot == CORE) {
            return DroneCoreAiHelper.is(stack);
        }

        return false;
    }
}
