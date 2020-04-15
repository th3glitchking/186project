package com.dronez;

import net.minecraft.item.Item;

import javax.annotation.Nullable;
import java.util.Objects;

public class PartMaterial {

    public static final PartMaterial IRON = new PartMaterial((byte) 1), GOLD = new PartMaterial((byte) 2), DIAMOND = new PartMaterial((byte) 3);

    private final byte material;

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
            return IRON;
        } else if (item == DronezMod.goldDroneBlade || item == DronezMod.goldDroneCore || item == DronezMod.goldDroneShell) {
            return GOLD;
        } else if (item == DronezMod.diamondDroneBlade || item == DronezMod.diamondDroneCore || item == DronezMod.diamondDroneShell) {
            return DIAMOND;
        }

        return null;
    }
}
