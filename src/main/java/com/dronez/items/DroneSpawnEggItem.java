package com.dronez.items;

import com.dronez.DronezMod;
import com.dronez.PartMaterial;
import com.dronez.entities.Drone;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

import static com.dronez.DronezMod.RegistryEvents.drone;
import static com.dronez.DronezMod.dronezGroup;

public class DroneSpawnEggItem extends SpawnEggItem implements INBTSerializable<CompoundNBT>, INBT{


    private PartMaterial blades;
    private PartMaterial shell;
    private PartMaterial core;
    private String user;
    CompoundNBT thisNbt = new CompoundNBT();


    public DroneSpawnEggItem(PartMaterial blades, PartMaterial shell, PartMaterial core){
        this(drone, 0xFFFFFF, 0xFFFFFF, (new Item.Properties().group(dronezGroup).maxStackSize(1)), blades, shell, core);
    }

    public DroneSpawnEggItem(EntityType<Drone> typeIn, int primaryColorIn, int secondaryColorIn, Item.Properties builder, PartMaterial blades, PartMaterial shell, PartMaterial core) {
        //May want to change the input of the types to a list to be cleaner, then add constants for the indexes of each item like BLADE1_POSITION = 0;
        super(typeIn, primaryColorIn, secondaryColorIn, builder);
        //Decode the NBT, first digit is blades, second digit is shell, third digit is core 1 = iron, 2 = gold, 3 = diamond
        //compound = new CompoundNBT();
        //compound.write();
        this.blades = blades;
        this.shell = shell;
        this.core = core;
        this.user = "";
        thisNbt.putByte("Core", core.getValue());
        thisNbt.putByte("Shell", shell.getValue());
        thisNbt.putByte("Blades", blades.getValue());
        thisNbt.putString("Owner", user);
    }

    public DroneSpawnEggItem setMaterials(PartMaterial blades, PartMaterial shell) {
        this.blades = blades;
        this.shell = shell;

        thisNbt.putByte("Shell", shell.getValue());
        thisNbt.putByte("Blades", blades.getValue());
        return this;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent("Blades: " + blades.getMaterial()));
        tooltip.add(new StringTextComponent("Shell: " + shell.getMaterial()));
        tooltip.add(new StringTextComponent("Core: " + core.getMaterial()));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {

        ItemStack itemstack = playerIn.getHeldItem(handIn);
        user = playerIn.getName().getString();

        thisNbt.putString("Owner", playerIn.getName().getString());
        //DronezMod.LOGGER.debug("Testing in the onItemRightClock: " + playerIn.getName().getString());
        itemstack.write(thisNbt);
        itemstack.deserializeNBT(thisNbt);
        if (worldIn.isRemote) {
            return new ActionResult<>(ActionResultType.PASS, itemstack);
        } else {
            RayTraceResult raytraceresult = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.SOURCE_ONLY);
            if (raytraceresult.getType() != RayTraceResult.Type.BLOCK) {
                return new ActionResult<>(ActionResultType.PASS, itemstack);
            } else {
                BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)raytraceresult;
                BlockPos blockpos = blockraytraceresult.getPos();
                if (!(worldIn.getBlockState(blockpos).getBlock() instanceof FlowingFluidBlock)) {
                    return new ActionResult<>(ActionResultType.PASS, itemstack);
                } else if (worldIn.isBlockModifiable(playerIn, blockpos) && playerIn.canPlayerEdit(blockpos, blockraytraceresult.getFace(), itemstack)) {
                    EntityType<?> entitytype = this.getType(itemstack.getTag());
                    if (entitytype.spawn(worldIn, thisNbt, null, playerIn, blockpos, SpawnReason.SPAWN_EGG, false, false) == null) {
                        return new ActionResult<>(ActionResultType.PASS, itemstack);
                    } else {
                        if (!playerIn.abilities.isCreativeMode) {
                            itemstack.shrink(1);
                        }

                        playerIn.addStat(Stats.ITEM_USED.get(this));
                        return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
                    }
                } else {
                    return new ActionResult<>(ActionResultType.FAIL, itemstack);
                }
            }
        }
    }

    @Override
    public void write(DataOutput output) throws IOException {

    }

    @Override
    public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {

    }

    @Override
    public byte getId() {
        return 0;
    }

    @Override
    public INBT copy() {
        return null;
    }

    @Override
    public ITextComponent toFormattedComponent(String indentation, int indentDepth) {
        return null;
    }


    public CompoundNBT serializeNBT(CompoundNBT nbt) {
        DronezMod.LOGGER.debug(toString());
        nbt.putByte("Core", core.getValue());
        nbt.putByte("Shell", shell.getValue());
        nbt.putByte("Blades", blades.getValue());
        nbt.putString("Owner", user);
        return nbt;
    }

    @Override
    public CompoundNBT serializeNBT() {
        DronezMod.LOGGER.debug(toString());
        CompoundNBT nbt = new CompoundNBT();
        nbt.putByte("Core", core.getValue());
        nbt.putByte("Shell", shell.getValue());
        nbt.putByte("Blades", blades.getValue());
        nbt.putString("Owner", user);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {

    }

    @Override
    public String toString() {
        return String.format("[DroneSpawnEgg] Core: %s, Shell: %s, Blades: %s, User: %s", core.getMaterial(), shell.getMaterial(), blades.getMaterial(), user);
    }
}

