package com.dronez.block.workshop;

import com.dronez.DronezMod;
import com.dronez.PartMaterial;
import com.dronez.items.DroneSpawnEggItem;
import com.dronez.items.DronezCore;
import com.dronez.items.EggFactory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorkshopAssembleItemHandler implements IItemHandler, IItemHandlerModifiable {
    public static final int TOP_LEFT_BLADE = 500;
    public static final int TOP_RIGHT_BLADE = 501;
    public static final int BOTTOM_LEFT_BLADE = 502;
    public static final int BOTTOM_RIGHT_BLADE = 503;
    public static final int SHELL = 504;
    public static final int CORE = 505;
    public static final int OUTPUT = 506;

    private final HashMap<Integer, ItemStack> itemStacks;
    private final ArrayList<Integer> bladeSlotKeys;

    public WorkshopAssembleItemHandler() {
        itemStacks = new HashMap<>();
        itemStacks.put(TOP_LEFT_BLADE, ItemStack.EMPTY);
        itemStacks.put(TOP_RIGHT_BLADE, ItemStack.EMPTY);
        itemStacks.put(BOTTOM_LEFT_BLADE, ItemStack.EMPTY);
        itemStacks.put(BOTTOM_RIGHT_BLADE, ItemStack.EMPTY);
        itemStacks.put(SHELL, ItemStack.EMPTY);
        itemStacks.put(CORE, ItemStack.EMPTY);
        itemStacks.put(OUTPUT, ItemStack.EMPTY);
        bladeSlotKeys = new ArrayList<>(Arrays.asList(TOP_LEFT_BLADE, TOP_RIGHT_BLADE, BOTTOM_LEFT_BLADE, BOTTOM_RIGHT_BLADE));
    }

    /**
     * Called each time an item is inserted/extracted. Determine if
     * the current items on the Workshop are able to build an output.
     * @return the egg the input is able to craft
     */
    public DroneSpawnEggItem determineOutput() {
        PartMaterial coreMaterial = PartMaterial.fromItem(itemStacks.get(CORE).getItem());
        if (coreMaterial == null) {
            return null;
        }

        PartMaterial shellMaterial = PartMaterial.fromItem(itemStacks.get(SHELL).getItem());
        if (shellMaterial == null) {
            return null;
        }

        // Collect all Blade PartMaterials into a Set
        Set<PartMaterial> bladeMaterials = bladeSlotKeys.stream()
                .map(key -> itemStacks.get(key).getItem())
                .map(PartMaterial::fromItem)
                .collect(Collectors.toSet());

        // No null and size of 1 means all are present with same PartMaterial
        if (bladeMaterials.contains(null) || bladeMaterials.size() != 1) {
            return null;
        }

        // TODO - implement AI Core type
        // String coreType = DronezCore.getType(itemStacks.get(CORE));

        PartMaterial bladeMaterial = bladeMaterials.iterator().next();
        return EggFactory.getEgg(bladeMaterial, shellMaterial, coreMaterial);
    }

    /**
     * Attempt to put an egg into the output
     */
    public void attemptProduceOutput() {
        DroneSpawnEggItem output = determineOutput();
        if (output == null) {
            // No output produced
            itemStacks.put(OUTPUT, ItemStack.EMPTY);
        } else {
            // Output produced
            ItemStack outputStack = new ItemStack(output, 1);
            itemStacks.put(OUTPUT, outputStack);
        }
    }

    /**
     * Return all of the ItemStacks that have input items in them
     * @return ItemStacks that hold blades, shells, and cores
     */
    public Stream<ItemStack> getInputStacks() {
        return itemStacks.entrySet().stream().filter(entry -> entry.getKey() != OUTPUT).map(Map.Entry::getValue);
    }

    /**
     * Returns the number of slots available
     *
     * @return The number of slots available
     **/
    @Override
    public int getSlots() {
        return itemStacks.size();
    }

    /**
     * Returns the ItemStack in a given slot.
     * <p>
     * The result's stack size may be greater than the itemstack's max size.
     * <p>
     * If the result is empty, then the slot is empty.
     *
     * <p>
     * <strong>IMPORTANT:</strong> This ItemStack <em>MUST NOT</em> be modified. This method is not for
     * altering an inventory's contents. Any implementers who are able to detect
     * modification through this method should throw an exception.
     * </p>
     * <p>
     * <strong><em>SERIOUSLY: DO NOT MODIFY THE RETURNED ITEMSTACK</em></strong>
     * </p>
     *
     * @param slot Slot to query
     * @return ItemStack in given slot. Empty ItemStack if the slot is empty.
     **/
    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return itemStacks.get(slot);
    }

    /**
     * <p>
     * Inserts an ItemStack into the given slot and return the remainder.
     * The ItemStack <em>should not</em> be modified in this function!
     * </p>
     * Note: This behaviour is subtly different from {@link net.minecraftforge.fluids.capability.IFluidHandler#fill(FluidStack, IFluidHandler.FluidAction)}}
     *
     * @param slot     Slot to insert into.
     * @param stack    ItemStack to insert. This must not be modified by the item handler.
     * @param simulate If true, the insertion is only simulated
     * @return The remaining ItemStack that was not inserted (if the entire stack is accepted, then return an empty ItemStack).
     * May be the same as the input ItemStack if unchanged, otherwise a new ItemStack.
     * The returned ItemStack can be safely modified after.
     **/
    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        ItemStack currentStack = itemStacks.get(slot);
        if (currentStack != ItemStack.EMPTY) {
            return stack;
        }

        ItemStack givenStack = stack.copy();
        ItemStack deposit = givenStack.split(1);

        itemStacks.put(slot, deposit);

        // Fire Dronez items' NBT building
        DronezCore.attemptInit(deposit);

        attemptProduceOutput();

        return givenStack;
    }

    /**
     * Extracts an ItemStack from the given slot.
     * <p>
     * The returned value must be empty if nothing is extracted,
     * otherwise its stack size must be less than or equal to {@code amount} and {@link ItemStack#getMaxStackSize()}.
     * </p>
     *
     * @param slot     Slot to extract from.
     * @param amount   Amount to extract (may be greater than the current stack's max limit)
     * @param simulate If true, the extraction is only simulated
     * @return ItemStack extracted from the slot, must be empty if nothing can be extracted.
     * The returned ItemStack can be safely modified after, so item handlers should return a new or copied stack.
     **/
    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack returnValue = itemStacks.get(slot);
        if (returnValue == ItemStack.EMPTY) {
            return returnValue;
        }

        if (!simulate) {
            returnValue = itemStacks.get(slot).split(amount);
            if (slot == OUTPUT) {
                itemStacks.keySet().stream()
                        .filter(key -> key != OUTPUT)
                        .forEach(key -> itemStacks.put(key, ItemStack.EMPTY));
            }
            attemptProduceOutput();
        }

        return returnValue;
    }

    /**
     * Retrieves the maximum stack size allowed to exist in the given slot.
     *
     * @param slot Slot to query.
     * @return The maximum stack size allowed in the slot.
     */
    @Override
    public int getSlotLimit(int slot) {
        return 1;
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
            return DronezCore.is(stack);
        }

        return false;
    }

    /**
     * Overrides the stack in the given slot. This method is used by the
     * standard Forge helper methods and classes. It is not intended for
     * general use by other mods, and the handler may throw an error if it
     * is called unexpectedly.
     *
     * @param slot  Slot to modify
     * @param stack ItemStack to set slot to (may be empty).
     * @throws RuntimeException if the handler is called in a way that the handler
     *                          was not expecting.
     **/
    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        itemStacks.put(slot, stack);
    }
}
