package com.dronez.dronedata;

import com.dronez.PartMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import static com.dronez.PartMaterial.MATERIAL_IRON;

public class DroneTagFactory {
    private final CompoundNBT eggTags;

    private DroneTagFactory() {
        eggTags = new CompoundNBT();
    }

    /**
     * This returns tags with all iron material types and a drone of type FOLLOW
     * @return
     */
    public static CompoundNBT allIron() {
        return Builder().blades(MATERIAL_IRON).core(DroneCoreAiHelper.CORE_TYPE_FOLLOW, MATERIAL_IRON).shell(MATERIAL_IRON).build();
    }

    public static DroneTagFactory Builder() {
        return new DroneTagFactory();
    }

    public DroneTagFactory blades(byte material) {
        CompoundNBT bladesTags = new CompoundNBT();
        bladesTags.putByte(DroneTagWrapper.DRONE_PACKAGE_MATERIAL_KEY, material);
        eggTags.put(DroneTagWrapper.DRONE_PACKAGE_BLADES_KEY, bladesTags);
        return this;
    }

    public DroneTagFactory shell(byte material) {
        CompoundNBT shellTags = new CompoundNBT();
        shellTags.putByte(DroneTagWrapper.DRONE_PACKAGE_MATERIAL_KEY, material);
        eggTags.put(DroneTagWrapper.DRONE_PACKAGE_SHELL_KEY, shellTags);
        return this;
    }

    private DroneTagFactory core(byte coreType, byte coreMaterial) {
        CompoundNBT coreTags = new CompoundNBT();
        coreTags.putByte(DroneTagWrapper.DRONE_PACKAGE_CORE_AI_KEY, coreType);
        coreTags.putByte(DroneTagWrapper.DRONE_PACKAGE_MATERIAL_KEY, coreMaterial);

        eggTags.put(DroneTagWrapper.DRONE_PACKAGE_CORE_KEY, coreTags);
        return this;
    }

    public DroneTagFactory core(ItemStack core) {
        byte coreType = DroneCoreAiHelper.getType(core);
        byte coreMaterial = PartMaterial.fromItem(core.getItem());

        return core(coreType, coreMaterial);
    }

    public CompoundNBT build() {
        return eggTags;
    }

    public void output(ItemStack stack) {
        DroneTagWrapper wrapper = new DroneTagWrapper(stack);
        wrapper.set(eggTags);
    }
}
