package com.dronez.block.charger;

import com.dronez.DronezMod;
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
    public static final String REGISTRY_NAME = "charger_block";
    public static ResourceLocation IDENTIFIER = new ResourceLocation(DronezMod.MODID, REGISTRY_NAME);

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
