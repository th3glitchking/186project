package com.dronez.entities.storage;

import com.dronez.block.workshop.WorkshopContainer;
import com.dronez.entities.Drone;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumSet;

public class StorageDrone extends Drone implements INamedContainerProvider {

    Inventory inv;

    public StorageDrone(EntityType<Drone> type, World p_i48578_2_) {
        super(type, p_i48578_2_);
        this.inv = new Inventory(27);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("Storage Drone");
    }

    @Nullable
    @ParametersAreNonnullByDefault
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new StorageContainer(i, playerInventory, inv, 3);
    }

    @Override
    protected boolean processInteract(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        Item item = itemstack.getItem();
        if(itemstack.isEmpty() || item.isFood()){
            if(!this.world.isRemote) {
                NetworkHooks.openGui((ServerPlayerEntity) player, this);
            }
        }
        return super.processInteract(player, hand);
    }
}
