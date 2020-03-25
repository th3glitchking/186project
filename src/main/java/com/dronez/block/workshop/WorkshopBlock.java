package com.dronez.block.workshop;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class WorkshopBlock extends ContainerBlock {
    public static final WorkshopBlock WORKSHOP_BLOCK = new WorkshopBlock();

    public WorkshopBlock() {
        super(Block.Properties.create(Material.GLASS));
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isRemote) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof WorkshopTileEntity) {
                WorkshopTileEntity workshopTe = (WorkshopTileEntity)te;
                NetworkHooks.openGui((ServerPlayerEntity)player, workshopTe, pos);
            }
        }

        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    @MethodsReturnNonnullByDefault
    public BlockRenderType getRenderType(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new WorkshopTileEntity();
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return createTileEntity(null, worldIn);
    }
}

