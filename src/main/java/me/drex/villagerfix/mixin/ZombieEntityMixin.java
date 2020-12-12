package me.drex.villagerfix.mixin;

import me.drex.villagerfix.VillagerFix;
import me.drex.villagerfix.config.ConfigEntries;
import me.drex.villagerfix.util.Helper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(ZombieEntity.class)
public class ZombieEntityMixin extends HostileEntity {

    protected ZombieEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    /**
     * @author Drex
     * @reason Manipulate villager conversion rate
     */
    @Overwrite
    public void onKilledOther(ServerWorld serverWorld, LivingEntity livingEntity) {
        //Vanilla copy
        super.onKilledOther(serverWorld, livingEntity);
        double conversionchance = ConfigEntries.features.conversionChance;
        if (!(livingEntity instanceof VillagerEntity)) {
            return;
        }
        if (conversionchance == -1) {
            //Vanilla behaviour
            if ((serverWorld.getDifficulty() == Difficulty.NORMAL || serverWorld.getDifficulty() == Difficulty.HARD)) {
                //50% chance on normal
                if (serverWorld.getDifficulty() == Difficulty.NORMAL && this.random.nextBoolean()) {
                    return;
                }
            } else {
                return;
            }
        } else {
            //Custom chance
            if (!Helper.chance(conversionchance)) {
                return;
            }
        }
        VillagerEntity villagerEntity = (VillagerEntity)livingEntity;
        ZombieVillagerEntity zombieVillagerEntity = villagerEntity.method_29243(EntityType.ZOMBIE_VILLAGER, false);
        zombieVillagerEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(zombieVillagerEntity.getBlockPos()), SpawnReason.CONVERSION, new ZombieEntity.ZombieData(false, true), (CompoundTag)null);
        zombieVillagerEntity.setVillagerData(villagerEntity.getVillagerData());
        zombieVillagerEntity.setGossipData(villagerEntity.getGossip().serialize(NbtOps.INSTANCE).getValue());
        zombieVillagerEntity.setOfferData(villagerEntity.getOffers().toTag());
        zombieVillagerEntity.setXp(villagerEntity.getExperience());
        if (!this.isSilent()) {
            serverWorld.syncWorldEvent(null, 1026, this.getBlockPos(), 0);
        }

    }

}
