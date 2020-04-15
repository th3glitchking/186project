package com.dronez.items;

import com.dronez.DronezMod;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

/**
 * Class adding convenience methods for Drone Core items.
 */
public class DronezCore {
    public static final String CORE_TYPE_FOLLOW = "Follow";
    public static final String CORE_TYPE_ATTACK = "Attack";
    public static final String CORE_TYPE_STORAGE = "Storage";
    private static final String CORE_TYPE_TAG = "DronezCoreType";
    private static final List<String> TYPES = Arrays.asList(CORE_TYPE_FOLLOW, CORE_TYPE_ATTACK, CORE_TYPE_STORAGE);

    /**
     * If this stack does not have an existing type, set it to {@link DronezCore#CORE_TYPE_FOLLOW}.
     * This is used every time interaction occurs with a Core to make
     * sure it has a type at the proper time.
     * @param stack the input stack
     */
    public static void attemptInit(ItemStack stack) {
        // Check if this stack isn't a Core
        if (!is(stack)) {
            return;
        }

        CompoundNBT tag = stack.getTag();
        if (tag == null || tag.getString(CORE_TYPE_TAG).isEmpty()) {
            // If there's no value for the type, set it to follow
            setType(stack, CORE_TYPE_FOLLOW);
        }
    }

    /**
     * Sets the proper tag on an {@link ItemStack} to mark it as a certain
     * Drone type.
     * @param stack the input stack
     * @param type the type of drone. Must be CORE_TYPE_FOLLOW, CORE_TYPE_ATTACK, or CORE_TYPE_STORAGE
     */
    public static void setType(ItemStack stack, String type) {
        assertValidCoreType(type);
        assertValidItemStack(stack);
        stack.getOrCreateTag().putString(CORE_TYPE_TAG, type);
    }

    /**
     * If this stack has the DronezCoreType tag, get the value of it.
     * Otherwise return null.
     * @param stack the input stack
     * @return the potential core type
     */
    @Nullable
    public static String getType(ItemStack stack) {
        assertValidItemStack(stack);

        CompoundNBT tag = stack.getTag();
        if (tag == null) {
            // The stack has no tag
            return null;
        }

        String coreType = tag.getString(CORE_TYPE_TAG);
        if (coreType.isEmpty()) {
            // The stack's tag doesn't contain a type
            return null;
        }

        assertValidCoreType(coreType);
        return coreType;
    }

    /**
     * Returns if an {@link ItemStack} is a Drone Core
     * @param stack the input stack
     * @return whether the input stack is a Drone Core
     */
    public static boolean is(ItemStack stack) {
        return stack.getItem() == DronezMod.ironDroneCore ||
                stack.getItem() == DronezMod.goldDroneCore ||
                stack.getItem() == DronezMod.diamondDroneCore;
    }

    /**
     * Throws if the input isn't CORE_TYPE_FOLLOW, CORE_TYPE_ATTACK, or CORE_TYPE_STORAGE
     * @param input the input type
     */
    private static void assertValidCoreType(String input) {
        if (!TYPES.contains(input)) {
            String msg = String.format("FakeCore type should be of type %s: . Presented: %s", TYPES, input);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Throws if the input {@link ItemStack} isn't a Drone Core.
     * @param stack the input stack
     */
    private static void assertValidItemStack(ItemStack stack) {
        if (!is(stack)) {
            String msg = String.format("FakeCore stack item should be a DronezCore. Given: %s", stack.getItem());
            throw new IllegalArgumentException(msg);
        }
    }
}
