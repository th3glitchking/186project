package com.dronez.block;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;
import org.apache.logging.log4j.LogManager;

public class ChargerBlockEnergy extends EnergyStorage implements INBTSerializable<CompoundNBT> {
    private static final String CAPACITY = "capacity";
    private static final String MAX_RECEIVE = "maxReceive";
    private static final String MAX_EXTRACT = "maxExtract";
    private static final String ENERGY = "energy";

    public ChargerBlockEnergy(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = super.receiveEnergy(maxReceive, simulate);

        if (received != 0) {
            printPower();
        }

        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int sent = super.extractEnergy(maxExtract, simulate);

        if (sent != 0) {
            printPower();
        }

        return sent;
    }

    private void printPower() {
        LogManager.getLogger().info(String.format("Power changed! Energy: %d", energy));
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = new CompoundNBT();
        compound.putInt(CAPACITY, capacity);
        compound.putInt(MAX_RECEIVE, maxReceive);
        compound.putInt(MAX_EXTRACT, maxExtract);
        compound.putInt(ENERGY, energy);

        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        capacity = nbt.getInt(CAPACITY);
        maxReceive = nbt.getInt(MAX_RECEIVE);
        maxExtract = nbt.getInt(MAX_EXTRACT);
        energy = nbt.getInt(ENERGY);
    }

    @Override
    public String toString() {
        return String.format("ChargerBlockEnergy: capacity: %d, maxReceive: %d, maxExtract: %d, energy: %d", capacity, maxReceive, maxExtract, energy);
    }
}
