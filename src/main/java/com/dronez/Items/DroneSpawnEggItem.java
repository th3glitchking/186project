package com.dronez.Items;

import com.dronez.entities.Drone;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Stream;

public class DroneSpawnEggItem extends SpawnEggItem {

    //0 for air, 1 for iron, 2 for gold, 3 for diamond
    private String blades;
    private String shell;
    private String core;
    //private CompoundNBT compound;

    public DroneSpawnEggItem(EntityType<Drone> typeIn, int primaryColorIn, int secondaryColorIn, Item.Properties builder)
    {//May want to change the input of the types to a list to be cleaner, then add constants for the indexes of each item like BLADE1_POSITION = 0;
        super(typeIn, primaryColorIn, secondaryColorIn, builder);
        //Decode the NBT, first digit is blades, second digit is shell, third digit is core 1 = iron, 2 = gold, 3 = diamond
        //compound = new CompoundNBT();
        //compound.write();
        this.blades = "Iron";
        this.shell = "Iron";
        this.core = "Iron";

    }



    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent("Blades: " + blades));
        tooltip.add(new StringTextComponent("Blade shell: " + shell));
        tooltip.add(new StringTextComponent("Blade core: " + core));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

}
