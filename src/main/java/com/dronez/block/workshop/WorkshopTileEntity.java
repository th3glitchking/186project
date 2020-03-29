package com.dronez.block.workshop;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

public class WorkshopTileEntity extends TileEntity implements INamedContainerProvider, Supplier<WorkshopTileEntity>, ICapabilityProvider {
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
    public WorkshopTileEntity get() {
        return this;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return LazyOptional.of(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY::getDefaultInstance).cast();
        } else {
            return super.getCapability(cap, side);
        }
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("Workshop");
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new WorkshopContainer(id, playerEntity.world, pos, playerInventory);
    }
}
