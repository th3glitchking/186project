package com.dronez.entities;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.world.World;

import java.util.EnumSet;

public class StorageDrone extends Drone {

    Inventory inv;

    public StorageDrone(EntityType<Drone> type, World p_i48578_2_) {
        super(type, p_i48578_2_);
        this.inv = new Inventory(10);
    }

    @Override
    protected void registerGoals() {
        //this is a basic goal registration, I will need to make custom goal classes to have it follow the player or return to charger
        this.goalSelector.addGoal(1, new FollowOwner(this, this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue(), 10.0F, 2.0F));
        //this.goalSelector.addGoal(3, new Drone.Charge(this.battery));
    }

    

}
