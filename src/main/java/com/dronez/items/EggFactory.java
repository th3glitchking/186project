package com.dronez.items;

import com.dronez.PartMaterial;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.ArrayList;

import static com.dronez.DronezMod.RegistryEvents.drone;
import static com.dronez.DronezMod.dronezGroup;
import static com.dronez.PartMaterial.*;

public class EggFactory {

    public static final DroneSpawnEggItem goldDroneSpawnEgg = new DroneSpawnEggItem(GOLD, GOLD, GOLD);
    public static final DroneSpawnEggItem diamondDroneSpawnEgg = new DroneSpawnEggItem(DIAMOND, DIAMOND, DIAMOND);
    public static final DroneSpawnEggItem ironDroneSpawnEgg = new DroneSpawnEggItem(IRON, IRON, IRON);

    public static void registerEggs() {
        ironDroneSpawnEgg.addInformation(ironDroneSpawnEgg.getDefaultInstance(), null, new ArrayList<ITextComponent>(), null);
        goldDroneSpawnEgg.addInformation(ironDroneSpawnEgg.getDefaultInstance(), null, new ArrayList<ITextComponent>(), null);
        diamondDroneSpawnEgg.addInformation(ironDroneSpawnEgg.getDefaultInstance(), null, new ArrayList<ITextComponent>(), null);

        Registry.register(Registry.ITEM, "dronez:iron_drone_spawn_egg", ironDroneSpawnEgg);
        Registry.register(Registry.ITEM, "dronez:gold_drone_spawn_egg", goldDroneSpawnEgg);
        Registry.register(Registry.ITEM, "dronez:diamond_drone_spawn_egg", diamondDroneSpawnEgg);
    }

    /**
     * Get the type of Drone to be made from this material.
     *
     * Returns null if material is not set.
     * @return drone spawn egg of this type of material
     */
    @Nullable
    public static DroneSpawnEggItem getEgg(PartMaterial blades, PartMaterial shell, PartMaterial core) {
        switch (core.getValue()) {
            case 1:
                return ironDroneSpawnEgg.setMaterials(blades, shell);
            case 2:
                return goldDroneSpawnEgg.setMaterials(blades, shell);
            case 3:
                return diamondDroneSpawnEgg.setMaterials(blades, shell);
            default:
                return null;
        }
    }
}
