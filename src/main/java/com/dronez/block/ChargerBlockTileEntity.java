package com.dronez.block;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ChargerBlockTileEntity extends TileEntity implements Supplier<ChargerBlockTileEntity>, ITickableTileEntity, ICapabilityProvider {
    public static final TileEntityType<ChargerBlockTileEntity> TYPE = makeType();
    private static final String COMPOUND_ENERGY_NAME = "Energy";

    private static TileEntityType<ChargerBlockTileEntity> makeType() {
        TileEntityType.Builder<ChargerBlockTileEntity> builder = TileEntityType.Builder.create(ChargerBlockTileEntity::new, ChargerBlock.CHARGER_BLOCK);
        TileEntityType<ChargerBlockTileEntity> type = builder.build(null);
        TileEntityType<? extends TileEntity> withName = type.setRegistryName("dronez", "charger_block");

        return (TileEntityType<ChargerBlockTileEntity>)withName;
    }

    private ChargerBlockEnergy energyStorage;
    private LazyOptional<IEnergyStorage> energyInterface = LazyOptional.of(CapabilityEnergy.ENERGY::getDefaultInstance);

    public ChargerBlockTileEntity() {
        super(TYPE);
        energyStorage = new ChargerBlockEnergy(100, 100, 100, 10);
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        energyStorage.deserializeNBT(compound.getCompound(COMPOUND_ENERGY_NAME));
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        // Don't forget to call markDirty if something was changed

        CompoundNBT energyCompound = energyStorage.serializeNBT();
        if (energyCompound != null) {
            compound.put(COMPOUND_ENERGY_NAME, energyStorage.serializeNBT());
        }

        return compound;
    }

    @Override
    public ChargerBlockTileEntity get() {
        return new ChargerBlockTileEntity();
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return super.getUpdateTag();
    }

    @Override
    public void handleUpdateTag(CompoundNBT tag) {
        write(tag);
    }

    @Override
    public void tick() {
        //energyStorage.receiveEnergy(1, false);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY) {
            return energyInterface.cast();
        } else {
            return LazyOptional.empty();
        }
    }
}