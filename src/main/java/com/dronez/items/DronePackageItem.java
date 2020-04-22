package com.dronez.items;

import com.dronez.dronedata.DroneCoreAiHelper;
import com.dronez.dronedata.DroneTagWrapper;
import com.dronez.entities.Drone;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import static com.dronez.DronezMod.RegistryEvents.drone;
import static com.dronez.DronezMod.dronezGroup;

public class DronePackageItem extends SpawnEggItem {
    public static final String DRONE_PACKAGE_ITEM_ID = "drone_package_item";

    public DronePackageItem() {
        super(drone, 0xFFFFFF, 0xFFFFFF, new Item.Properties().group(dronezGroup));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        DroneTagWrapper.setTooltip(tooltip, stack.getTag());
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack droneEggItemStack = playerIn.getHeldItem(handIn);
        DroneTagWrapper droneTags = new DroneTagWrapper(droneEggItemStack);

        if (droneTags.isCoreEmpty()) {
            // If there is no Egg tag, Drone could've been created in Creative
            // or spawned in. Use IRON as default
            droneTags.fillIron();
        }

        byte coreType = droneTags.getCoreAi();

        if (worldIn.isRemote) {
            return new ActionResult<>(ActionResultType.PASS, droneEggItemStack);
        }

        RayTraceResult raytraceresult = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.SOURCE_ONLY);
        if (raytraceresult.getType() != RayTraceResult.Type.BLOCK) {
            return new ActionResult<>(ActionResultType.PASS, droneEggItemStack);
        }

        BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult)raytraceresult;
        BlockPos blockPos = blockRayTraceResult.getPos();
        BlockState state = worldIn.getBlockState(blockPos);
        if (!state.getCollisionShape(worldIn, blockPos).isEmpty()) {
            blockPos = blockPos.offset(blockRayTraceResult.getFace());
        }

        EntityType<?> droneEntityType = DroneCoreAiHelper.from(coreType);
        Drone droneEntity = (Drone)droneEntityType.spawn(worldIn, droneEggItemStack, playerIn, blockPos, SpawnReason.SPAWN_EGG, false, false);
        if (droneEntity == null) {
            return new ActionResult<>(ActionResultType.PASS, droneEggItemStack);
        }

        droneEntity.onSpawn(droneTags);

        if (coreType == DroneCoreAiHelper.CORE_TYPE_STORAGE)
            droneEntity.setItemStackToSlot(EquipmentSlotType.HEAD, new ItemStack(Items.CHEST));
        else if (coreType == DroneCoreAiHelper.CORE_TYPE_ATTACK) {
            droneEntity.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(getSwordType(droneTags.getCoreMaterial())));
        }

        if (!playerIn.abilities.isCreativeMode) {
            droneEggItemStack.shrink(1);
        }

        playerIn.addStat(Stats.ITEM_USED.get(this));
        return new ActionResult<>(ActionResultType.SUCCESS, droneEggItemStack);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        // Don't allow default spawning of Entity. We do it ourselves in onItemRightClick
        return ActionResultType.PASS;
    }

    private Item getSwordType(byte coreType) {
        return Items.DIAMOND_SWORD;
    }
}

