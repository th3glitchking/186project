package com.dronez.block.workshop;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public abstract class WorkshopAbstractItemHandler implements IItemHandler, IItemHandlerModifiable {
    protected final HashMap<Integer, ItemStack> itemStacks;

    public WorkshopAbstractItemHandler() {
        itemStacks = new HashMap<>();
    }

    public abstract int getOutputSlotIndex();
    public abstract void attemptProduceOutput();

    /**
     * Return all of the ItemStacks that have input items in them
     * @return ItemStacks that hold blades, shells, and cores
     */
    public Stream<ItemStack> getInputStacks() {
        return itemStacks.entrySet().stream().filter(entry -> entry.getKey() != getOutputSlotIndex()).map(Map.Entry::getValue);
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
            if (slot == getOutputSlotIndex()) {
                itemStacks.keySet().stream()
                        .filter(key -> key != getOutputSlotIndex())
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
}
