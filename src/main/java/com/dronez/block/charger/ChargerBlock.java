package com.dronez.block.charger;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class ChargerBlock extends FenceBlock {
    public static final ChargerBlock CHARGER_BLOCK = new ChargerBlock();

    public static ResourceLocation IDENTIFIER = new ResourceLocation("dronez" ,"charger_block");

    private ChargerBlock() {
        super(Block.Properties.create(Material.GLASS));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ChargerBlockTileEntity();
    }
}
