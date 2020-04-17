package com.dronez.items;

import com.dronez.PartMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import static com.dronez.PartMaterial.MATERIAL_IRON;

public class PackageFactory {
    private final CompoundNBT eggTags;

    private PackageFactory() {
        eggTags = new CompoundNBT();
    }

    /**
     * This returns tags with all iron material types and a drone of type FOLLOW
     * @return
     */
    public static CompoundNBT allIron() {
        return Builder().blades(MATERIAL_IRON).core(DroneCoreTypeHelper.CORE_TYPE_FOLLOW, MATERIAL_IRON).shell(MATERIAL_IRON).build();
    }

    public static PackageFactory Builder() {
        return new PackageFactory();
    }

    public PackageFactory blades(byte material) {
        CompoundNBT bladesTags = new CompoundNBT();
        bladesTags.putByte(DronePackageItem.DRONE_PACKAGE_MATERIAL_KEY, material);
        eggTags.put("Blades", bladesTags);
        return this;
    }

    public PackageFactory shell(byte material) {
        CompoundNBT shellTags = new CompoundNBT();
        shellTags.putByte(DronePackageItem.DRONE_PACKAGE_MATERIAL_KEY, material);
        eggTags.put("Shell", shellTags);
        return this;
    }
    
    private PackageFactory core(String coreType, byte coreMaterial) {
        CompoundNBT coreTags = new CompoundNBT();
        coreTags.putString(DronePackageItem.DRONE_PACKAGE_CORE_TYPE_KEY, coreType);
        coreTags.putByte(DronePackageItem.DRONE_PACKAGE_MATERIAL_KEY, coreMaterial);

        eggTags.put(DronePackageItem.DRONE_PACKAGE_CORE_KEY, coreTags);
        return this;
    }

    public PackageFactory core(ItemStack core) {
        String coreType = DroneCoreTypeHelper.getType(core);
        byte coreMaterial = PartMaterial.fromItem(core.getItem());
        if (coreType == null) {
            throw new IllegalArgumentException("Core should have a type by now");
        }

        return core(coreType, coreMaterial);
    }

    public CompoundNBT build() {
        return eggTags;
    }
}
