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

    public static final DroneSpawnEggItem ironDroneSpawnEgg = new DroneSpawnEggItem(drone, 0xFFFFFF, 0xFFFFFF, (new Item.Properties().group(dronezGroup)), IRON, IRON, IRON);
    public static final DroneSpawnEggItem goldDroneSpawnEgg = new DroneSpawnEggItem(drone, 0xFFFFFF, 0xFFFFFF, (new Item.Properties().group(dronezGroup)), GOLD, GOLD, GOLD);
    public static final DroneSpawnEggItem diamondDroneSpawnEgg = new DroneSpawnEggItem(drone, 0xFFFFFF, 0xFFFFFF, (new Item.Properties().group(dronezGroup)), DIAMOND, DIAMOND, DIAMOND);

    public static void registerEggs() {
        ironDroneSpawnEgg.addInformation(ironDroneSpawnEgg.getDefaultInstance(), null, new ArrayList<ITextComponent>(), null);
        goldDroneSpawnEgg.addInformation(goldDroneSpawnEgg.getDefaultInstance(), null, new ArrayList<ITextComponent>(), null);
        diamondDroneSpawnEgg.addInformation(diamondDroneSpawnEgg.getDefaultInstance(), null, new ArrayList<ITextComponent>(), null);

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
        DroneSpawnEggItem newEgg;
        switch (core.getValue()) {
            case 1:
                newEgg = ironDroneSpawnEgg;
                break;
            case 2:
                newEgg = goldDroneSpawnEgg;
                break;
            case 3:
                newEgg = diamondDroneSpawnEgg;
                break;
            default:
                return null;
        }
        return newEgg.setMaterials(blades, shell);
    }
}
