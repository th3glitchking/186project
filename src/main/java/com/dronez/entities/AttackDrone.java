package com.dronez.entities;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.entity.ai.goal.OwnerHurtByTargetGoal;
import net.minecraft.entity.ai.goal.OwnerHurtTargetGoal;

import java.util.EnumSet;

public class AttackDrone extends Drone {
    public AttackDrone(EntityType<Drone> type, World p_i48578_2_) {
        super(type, p_i48578_2_);
    }

    @Override
    protected void registerGoals() {
        //this is a basic goal registration, I will need to make custom goal classes to have it follow the player or return to charger
        super.registerGoals();
        //this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        //this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));

    }

    public boolean shouldAttackEntity(LivingEntity target, LivingEntity owner) {
        if (target instanceof Drone) {
            Drone drone = (Drone) target;
            if (drone.getOwner() == owner) {
                return false;
            }
        }

        if (target instanceof PlayerEntity && owner instanceof PlayerEntity && !((PlayerEntity) owner).canAttackPlayer((PlayerEntity) target)) {
            return false;
        } else if (target instanceof AbstractHorseEntity && ((AbstractHorseEntity) target).isTame()) {
            return false;
        } else {
            return !(target instanceof CatEntity) || !((CatEntity) target).isTamed();
        }
    }






}
