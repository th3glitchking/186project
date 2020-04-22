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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.EnumSet;

public class AttackDrone extends Drone {
    public AttackDrone(EntityType<Drone> type, World p_i48578_2_) {
        super(type, p_i48578_2_);
    }

    @Override
    protected void registerGoals() {
        //this is a basic goal registration, I will need to make custom goal classes to have it follow the player or return to charger
        super.registerGoals();
        this.goalSelector.addGoal(3, new AttackDrone.DefendTargetGoal(this));
        this.goalSelector.addGoal(3, new AttackDrone.TargetHurtByOwnerGoal(this));
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

    static class DefendTargetGoal extends NearestAttackableTargetGoal<WolfEntity> {
        public DefendTargetGoal(AttackDrone drone) {
            super(drone, WolfEntity.class, 16, false, true, (p_220789_0_) -> {
                return !((WolfEntity)p_220789_0_).isTamed();//may not be the best thing in the world
            });
        }

        protected double getTargetDistance() {
            return super.getTargetDistance() * 0.25D;
        }
    }

    /*public class OwnerHurtByTargetGoal extends TargetGoal {
        private final AttackDrone drone;
        private LivingEntity attacker;
        private int timestamp;

        public OwnerHurtByTargetGoal(AttackDrone theDefendingDroneIn) {
            super(theDefendingDroneIn, false);
            this.drone = theDefendingDroneIn;
            this.setMutexFlags(EnumSet.of(Goal.Flag.TARGET));
        }


         //Returns whether the EntityAIBase should begin execution.

        public boolean shouldExecute() {
            if (!this.drone.isCharging()) {
                LivingEntity livingentity = this.drone.getOwner();
                if (livingentity == null) {
                    return false;
                } else {
                    this.attacker = livingentity.getRevengeTarget();
                    int i = livingentity.getRevengeTimer();
                    return i != this.timestamp && this.isSuitableTarget(this.attacker, EntityPredicate.DEFAULT) && this.drone.shouldAttackEntity(this.attacker, livingentity);
                }
            } else {
                return false;
            }
        }


         // Execute a one shot task or start executing a continuous task

        public void startExecuting() {
            this.goalOwner.setAttackTarget(this.attacker);
            LivingEntity livingentity = this.drone.getOwner();
            if (livingentity != null) {
                this.timestamp = livingentity.getRevengeTimer();
            }

            super.startExecuting();
        }

    }*/
    public class TargetHurtByOwnerGoal extends TargetGoal{
        private final AttackDrone drone;
        private LivingEntity attacker;
        private int timestamp;


        public TargetHurtByOwnerGoal(AttackDrone theAttackingDroneIn) {
            super(theAttackingDroneIn, false);
            this.drone = theAttackingDroneIn;
            this.setMutexFlags(EnumSet.of(Goal.Flag.TARGET));
        }

        @Override
        public boolean shouldExecute() {
            if (!this.drone.isCharging()) {
                LivingEntity livingentity = this.drone.getOwner();
                if (livingentity == null) {
                    return false;
                } else {//Not sure about what is in the else
                    this.attacker = livingentity.getRevengeTarget();
                    int i = livingentity.getRevengeTimer();
                    return i != this.timestamp && this.isSuitableTarget(this.attacker, EntityPredicate.DEFAULT) && this.drone.shouldAttackEntity(this.attacker, livingentity);
                }
            } else {
                return false;
            }
        }
    }
}
