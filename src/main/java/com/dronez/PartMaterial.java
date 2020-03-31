package com.dronez;

import com.dronez.Items.DroneSpawnEggItem;
import net.minecraft.item.Item;

import javax.annotation.Nullable;
import java.util.Objects;

public class PartMaterial {

    public static final byte IRON = 1, GOLD = 2, DIAMOND = 3;

    private byte material;

    public PartMaterial(byte material){
        this.material = material;
    }

    public byte getValue() {
        return material;
    }
    public String getMaterial() {
        switch (material) {
            case 1:
                return "iron";
            case 2:
                return "gold";
            case 3:
                return "diamond";
            default:
                return null;
        }
    }

    /**
     * Get the type of Drone to be made from this material.
     *
     * Returns null if material is not set.
     * @return drone spawn egg of this type of material
     */
    @Nullable
    public DroneSpawnEggItem getEgg() {
        switch (material) {
            case IRON:
                return DroneSpawnEggItem.ironDroneSpawnEgg;
            case GOLD:
                return DroneSpawnEggItem.goldDroneSpawnEgg;
            case DIAMOND:
                return DroneSpawnEggItem.diamondDroneSpawnEgg;
            default:
                return null;
        }
    }

    /**
     * Checks if the materials are equal between this and another PartMaterial
     * @param o the other PartMaterial
     * @return whether the materials are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartMaterial that = (PartMaterial) o;
        return material == that.material;
    }

    @Override
    public int hashCode() {
        return Objects.hash(material);
    }

    /**
     * Checks input item to see if it is an item from this mod. If it is, return the type of
     * material it is made out of (IRON, GOLD, DIAMOND).
     *
     * Returns {@code null} if the item is not a part of this mod
     * @param item An item to be checked for it's material type
     * @return The type of material of this item
     */
    @Nullable
    public static PartMaterial fromItem(Item item) {
        if (item == DronezMod.ironDroneBlade || item == DronezMod.ironDroneCore || item == DronezMod.ironDroneShell) {
            return new PartMaterial(IRON);
        } else if (item == DronezMod.goldDroneBlade || item == DronezMod.goldDroneCore || item == DronezMod.goldDroneShell) {
            return new PartMaterial(GOLD);
        } else if (item == DronezMod.diamondDroneBlade || item == DronezMod.diamondDroneCore || item == DronezMod.diamondDroneShell) {
            return new PartMaterial(DIAMOND);
        }

        return null;
    }
}
