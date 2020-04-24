package com.dronez.block.charger;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ChargerBlockTileEntity extends TileEntity implements Supplier<ChargerBlockTileEntity>, ITickableTileEntity, ICapabilityProvider {
    public static final TileEntityType<ChargerBlockTileEntity> TYPE = (TileEntityType<ChargerBlockTileEntity>)TileEntityType.Builder.create(ChargerBlockTileEntity::new, ChargerBlock.CHARGER_BLOCK).build(null).setRegistryName("dronez", "charger_block");
    private static final String COMPOUND_ENERGY_NAME = "Energy";

    private final ChargerBlockEnergy energyStorage;

    public ChargerBlockTileEntity() {
        super(TYPE);
        energyStorage = new ChargerBlockEnergy();
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        energyStorage.deserializeNBT(compound.getCompound(COMPOUND_ENERGY_NAME));
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        // Don't forget to call markDirty if something was changed
        compound.put(COMPOUND_ENERGY_NAME, energyStorage.serializeNBT());

        return compound;
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        read(pkt.getNbtCompound());
    }

    @Override
    public ChargerBlockTileEntity get() {
        return this;
    }

    @Override
    public void tick() {
        acceptEnergy();
    }

    private void acceptEnergy() {
        if (world == null || !energyStorage.canReceive() || !energyStorage.canAcceptEnergy()) {
            return;
        }

        for (Direction direction : Direction.values()) {
            TileEntity entity = world.getTileEntity(pos.offset(direction));
            if (entity == null) continue;
            LazyOptional<IEnergyStorage> entityCapability = entity.getCapability(CapabilityEnergy.ENERGY);
            if (!entityCapability.isPresent()) continue;
            IEnergyStorage entityEnergy = entityCapability.orElseThrow(IllegalStateException::new);
            if (!entityEnergy.canExtract()) continue;

            int energyAccepted = energyStorage.receiveEnergy(1, false);
            entityEnergy.extractEnergy(energyAccepted, false);
        }
    }

    public ChargerBlockEnergy getEnergyStorage() {
        return energyStorage;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY) {
            return LazyOptional.of(CapabilityEnergy.ENERGY::getDefaultInstance).cast();
        } else {
            return LazyOptional.empty();
        }
    }
}