package com.dronez.entities;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.EnumSet;

public class AttackDrone extends Drone {


    public AttackDrone(EntityType<Drone> type, World p_i48578_2_) {
        super(type, p_i48578_2_);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new AttackDrone.OwnerHurtByTargetGoal(this));
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

    public class OwnerHurtByTargetGoal extends TargetGoal {
        private final AttackDrone drone;
        private LivingEntity attacker;
        private int timestamp;

        public OwnerHurtByTargetGoal(AttackDrone theDefendingDroneIn) {
            super(theDefendingDroneIn, false);
            this.drone = theDefendingDroneIn;
            this.setMutexFlags(EnumSet.of(Goal.Flag.TARGET));
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
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

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
            this.goalOwner.setAttackTarget(this.attacker);
            LivingEntity livingentity = this.drone.getOwner();
            if (livingentity != null) {
                this.timestamp = livingentity.getRevengeTimer();
            }

            super.startExecuting();
        }

    }
}
