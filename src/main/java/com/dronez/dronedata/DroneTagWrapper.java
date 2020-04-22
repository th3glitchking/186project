package com.dronez.dronedata;

import com.dronez.PartMaterial;
import com.dronez.dronedata.DroneCoreAiHelper;
import com.dronez.dronedata.DroneTagFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import java.util.List;

public class DroneTagWrapper {
    public static final String DRONE_PACKAGE_TAG_KEY = "DronePackage";
    public static final String DRONE_PACKAGE_CORE_KEY = "Core";
    public static final String DRONE_PACKAGE_CORE_AI_KEY = "AI";
    public static final String DRONE_PACKAGE_SHELL_KEY = "Shell";
    public static final String DRONE_PACKAGE_BLADES_KEY = "Blades";
    public static final String DRONE_PACKAGE_MATERIAL_KEY = "Material";

    private final ItemStack stack;

    public DroneTagWrapper(@Nonnull ItemStack givenStack) {
        stack = givenStack;
        stack.getOrCreateTag();
    }

    public void set(CompoundNBT tags) {
        stack.getOrCreateTag().put(DRONE_PACKAGE_TAG_KEY, tags);
    }

    public boolean isCoreEmpty() {
        return stack.getChildTag(DRONE_PACKAGE_TAG_KEY) == null || stack.getChildTag(DRONE_PACKAGE_TAG_KEY).get(DRONE_PACKAGE_CORE_KEY) == null;
    }

    /**
     * Fills all materials with IRON and sets the AI to FOLLOW.
     * This is used when trying to place an empty Drone Package, usually
     * meaning it was grabbed in creative mode.
     */
    public void fillIron() {
        stack.getOrCreateTag().put(DRONE_PACKAGE_TAG_KEY, DroneTagFactory.allIron());
    }

    /**
     * Return the AI type of this Drone's Core
     * @return ai type of core
     */
    public byte getCoreAi() {
        if (isCoreEmpty()) {
            return PartMaterial.ERROR;
        }

        return stack.getChildTag(DRONE_PACKAGE_TAG_KEY).getCompound(DRONE_PACKAGE_CORE_KEY).getByte(DRONE_PACKAGE_CORE_AI_KEY);
    }

    public byte getBladeMaterial() {
        return stack.getChildTag(DRONE_PACKAGE_TAG_KEY).getCompound(DRONE_PACKAGE_BLADES_KEY).getByte(DRONE_PACKAGE_MATERIAL_KEY);
    }

    public static void setTooltip(List<ITextComponent> tooltip, CompoundNBT tag) {
        if (tag == null) {
            return;
        }

        tag = tag.getCompound(DRONE_PACKAGE_TAG_KEY);

        String core = "Core: " + PartMaterial.stringFrom(tag.getCompound(DRONE_PACKAGE_CORE_KEY).getByte(DRONE_PACKAGE_MATERIAL_KEY));
        String coreAi = "Core AI: " + DroneCoreAiHelper.stringFrom(tag.getCompound(DRONE_PACKAGE_CORE_KEY).getByte(DRONE_PACKAGE_CORE_AI_KEY));
        String shell = "Shell: " + PartMaterial.stringFrom(tag.getCompound(DRONE_PACKAGE_SHELL_KEY).getByte(DRONE_PACKAGE_MATERIAL_KEY));
        String blades = "Blades: " + PartMaterial.stringFrom(tag.getCompound(DRONE_PACKAGE_BLADES_KEY).getByte(DRONE_PACKAGE_MATERIAL_KEY));
        tooltip.add(new StringTextComponent(core));
        tooltip.add(new StringTextComponent(coreAi));
        tooltip.add(new StringTextComponent(shell));
        tooltip.add(new StringTextComponent(blades));
    }
}
