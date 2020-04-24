package com.dronez.dronedata;

import com.dronez.DronezMod;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

/**
 * Class adding convenience methods for Drone Core items.
 */
public class DroneCoreAiHelper {
    public static final byte CORE_TYPE_FOLLOW = 1;
    public static final byte CORE_TYPE_ATTACK = 2;
    public static final byte CORE_TYPE_STORAGE = 3;
    public static final String CORE_TYPE_TAG = "Type";
    private static final List<Byte> TYPES = Arrays.asList(CORE_TYPE_FOLLOW, CORE_TYPE_ATTACK, CORE_TYPE_STORAGE);

    /**
     * Sets the proper tag on an {@link ItemStack} to mark it as a certain
     * Drone type.
     * @param stack the input stack
     * @param type the type of drone. Must be CORE_TYPE_FOLLOW, CORE_TYPE_ATTACK, or CORE_TYPE_STORAGE
     */
    public static void setType(ItemStack stack, byte type) {
        assertValidCoreAi(type);
        assertValidItemStack(stack);
        stack.getOrCreateTag().putByte(CORE_TYPE_TAG, type);
    }

    /**
     * If this stack has the DronezCoreType tag, get the value of it.
     * Otherwise return null.
     * @param stack the input stack
     * @return the potential core type
     */
    public static byte getType(ItemStack stack) {
        assertValidItemStack(stack);

        CompoundNBT tag = stack.getTag();
        if (tag == null || !tag.contains(CORE_TYPE_TAG)) {
            // The stack has no tag
            return -1;
        }

        byte coreType = tag.getByte(CORE_TYPE_TAG);

        assertValidCoreAi(coreType);
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

    public static EntityType<?> from(byte type) {
        assertValidCoreAi(type);
        if (type == CORE_TYPE_FOLLOW) {
            return DronezMod.RegistryEvents.drone;
        } else if (type == CORE_TYPE_ATTACK) {
            return DronezMod.RegistryEvents.attack;
        } else if (type == CORE_TYPE_STORAGE) {
            return DronezMod.RegistryEvents.storage;
        }
        throw new IllegalArgumentException("Invalid DronezCore type");
    }

    @Nullable
    public static String stringFrom(byte type) {
        switch (type) {
            case CORE_TYPE_FOLLOW:
                return "Follow";
            case CORE_TYPE_ATTACK:
                return "Attack";
            case CORE_TYPE_STORAGE:
                return "Storage";
            default:
                return null;
        }
    }

    /**
     * Throws if the input isn't CORE_TYPE_FOLLOW, CORE_TYPE_ATTACK, or CORE_TYPE_STORAGE
     * @param input the input type
     */
    private static void assertValidCoreAi(byte input) {
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
