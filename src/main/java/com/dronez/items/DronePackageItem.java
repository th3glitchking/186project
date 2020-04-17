package com.dronez.items;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import static com.dronez.DronezMod.RegistryEvents.drone;
import static com.dronez.DronezMod.dronezGroup;

public class DronePackageItem extends SpawnEggItem {
    public static final String DRONE_PACKAGE_ITEM_ID = "drone_package_item";
    public static final String DRONE_PACKAGE_TAG_KEY = "DronePackage";
    public static final String DRONE_PACKAGE_CORE_KEY = "Core";
    public static final String DRONE_PACKAGE_CORE_TYPE_KEY = "Type";
    public static final String DRONE_PACKAGE_MATERIAL_KEY = "Material";

    public DronePackageItem() {
        super(drone, 0xFFFFFF, 0xFFFFFF, new Item.Properties().group(dronezGroup));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack droneEggItemStack = playerIn.getHeldItem(handIn);
        CompoundNBT eggTags = droneEggItemStack.getChildTag(DRONE_PACKAGE_TAG_KEY);

        if (eggTags == null) {
            // If there is no Egg tag, Drone could've been created in Creative
            // or spawned in. Use IRON as default
            eggTags = PackageFactory.allIron();
        }

        String coreType = eggTags.getCompound(DRONE_PACKAGE_CORE_KEY).getString(DRONE_PACKAGE_CORE_TYPE_KEY);

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

        EntityType<?> droneEntity = DroneCoreTypeHelper.from(coreType);
        if (droneEntity.spawn(worldIn, droneEggItemStack, playerIn, blockPos, SpawnReason.SPAWN_EGG, false, false) == null) {
            return new ActionResult<>(ActionResultType.PASS, droneEggItemStack);
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
}

