package com.dronez;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Stream;

public class DroneSpawnEggItem extends SpawnEggItem {

    //0 for air, 1 for iron, 2 for gold, 3 for diamond
    private String blade1;
    private String blade2;
    private String blade3;
    private String blade4;
    private String shell;
    private String core;

    public DroneSpawnEggItem(EntityType<?> typeIn, int primaryColorIn, int secondaryColorIn, Item.Properties builder, String blade1, String blade2, String blade3, String blade4, String shell, String core)
    {//May want to change the input of the types to a list to be cleaner, then add constants for the indexes of each item like BLADE1_POSITION = 0;
        super(typeIn, primaryColorIn, secondaryColorIn, builder);
        this.blade1 = blade1;
        this.blade2 = blade2;
        this.blade3 = blade3;
        this.blade4 = blade4;
        this.shell  = shell;
        this.core = core;
    }



    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new StringTextComponent("Blade 1: " + blade1));
        tooltip.add(new StringTextComponent("Blade 2: " + blade2));
        tooltip.add(new StringTextComponent("Blade 3: " + blade3));
        tooltip.add(new StringTextComponent("Blade 4: " + blade4));
        tooltip.add(new StringTextComponent("Blade shell: " + shell));
        tooltip.add(new StringTextComponent("Blade core: " + core));
    }
}
