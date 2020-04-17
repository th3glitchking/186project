package com.dronez;

import net.minecraft.item.Item;

public class PartMaterial {
    public static final byte MATERIAL_IRON = 1;
    public static final byte MATERIAL_GOLD = 2;
    public static final byte MATERIAL_DIAMOND = 3;

    /**
     * Checks input item to see if it is an item from this mod. If it is, return the type of
     * material it is made out of (IRON, GOLD, DIAMOND).
     *
     * Returns {@code null} if the item is not a part of this mod
     * @param item An item to be checked for it's material type
     * @return The type of material of this item
     */
    public static byte fromItem(Item item) {
        if (item == DronezMod.ironDroneBlade || item == DronezMod.ironDroneCore || item == DronezMod.ironDroneShell) {
            return MATERIAL_IRON;
        } else if (item == DronezMod.goldDroneBlade || item == DronezMod.goldDroneCore || item == DronezMod.goldDroneShell) {
            return MATERIAL_GOLD;
        } else if (item == DronezMod.diamondDroneBlade || item == DronezMod.diamondDroneCore || item == DronezMod.diamondDroneShell) {
            return MATERIAL_DIAMOND;
        }

        return -1;
    }
}
