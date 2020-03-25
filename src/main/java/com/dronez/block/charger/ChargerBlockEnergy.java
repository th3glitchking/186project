package com.dronez.block.charger;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;

public class ChargerBlockEnergy extends EnergyStorage implements INBTSerializable<CompoundNBT> {
    private static final String CAPACITY = "capacity";
    private static final String MAX_RECEIVE = "maxReceive";
    private static final String MAX_EXTRACT = "maxExtract";
    private static final String ENERGY = "energy";

    private static final int K = 1000;

    public ChargerBlockEnergy() {
        super(10 * K, 100, 100, 10 * K);
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

    /**
     * Get whether this block can accept energy (the current energy is lower than capacity)
     * @return Can we accept energy?
     */
    public boolean canAcceptEnergy() {
        return capacity - energy > 0;
    }
}
