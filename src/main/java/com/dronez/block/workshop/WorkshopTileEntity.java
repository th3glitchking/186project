package com.dronez.block.workshop;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class WorkshopTileEntity extends TileEntity implements INamedContainerProvider {
    public static final TileEntityType<WorkshopTileEntity> TYPE = makeType();

    private static TileEntityType<WorkshopTileEntity> makeType() {
        TileEntityType.Builder<WorkshopTileEntity> builder = TileEntityType.Builder.create(WorkshopTileEntity::new, WorkshopBlock.WORKSHOP_BLOCK);
        TileEntityType<WorkshopTileEntity> type = builder.build(null);
        TileEntityType<? extends TileEntity> withName = type.setRegistryName("dronez", "workshop_block");

        return (TileEntityType<WorkshopTileEntity>)withName;
    }

    public WorkshopTileEntity() {
        super(TYPE);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("Workshop");
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new WorkshopContainer(i, playerEntity.world, pos, playerInventory);
    }
}
