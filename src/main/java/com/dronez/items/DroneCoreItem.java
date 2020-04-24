package com.dronez.items;

import com.dronez.DronezMod;
import com.dronez.dronedata.DroneCoreAiHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class DroneCoreItem extends Item {
    public DroneCoreItem() {
        super(new Item.Properties().group(DronezMod.dronezGroup).maxStackSize(1));
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, PlayerEntity playerIn) {
        super.onCreated(stack, worldIn, playerIn);
        DroneCoreAiHelper.setType(stack, DroneCoreAiHelper.CORE_TYPE_FOLLOW);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        CompoundNBT tag = stack.getTag();
        if (tag == null) {
            return;
        }

        byte aiType = tag.getByte(DroneCoreAiHelper.CORE_TYPE_TAG);
        tooltip.add(new StringTextComponent("AI: " + DroneCoreAiHelper.stringFrom(aiType)));
    }
}
