package me.drex.villagerfix.mixin;

import me.drex.villagerfix.VillagerFix;
import me.drex.villagerfix.config.ConfigEntries;
import me.drex.villagerfix.util.Helper;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

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
    public void onKilledOther(LivingEntity other) {
        //Vanilla copy
        super.onKilledOther(serverWorld, other);
        double conversionchance = ConfigEntries.features.conversionChance;
        if (!(other instanceof VillagerEntity)) {
            return;
        }
        if (conversionchance == -1) {
            //Vanilla behaviour
            if ((world.getDifficulty() == Difficulty.NORMAL || world.getDifficulty() == Difficulty.HARD)) {
                //50% chance on normal
                if (world.getDifficulty() == Difficulty.NORMAL && this.random.nextBoolean()) {
                    return;
                }
            } else {
                return;
            }
        } else {
            //Custom chance
            if (Helper.chance(conversionchance)) {
                return;
            }
        }
        VillagerEntity villagerEntity = (VillagerEntity)other;
        ZombieVillagerEntity zombieVillagerEntity = (ZombieVillagerEntity)EntityType.ZOMBIE_VILLAGER.create(this.world);
        zombieVillagerEntity.copyPositionAndRotation(villagerEntity);
        villagerEntity.remove();
        zombieVillagerEntity.initialize(this.world, this.world.getLocalDifficulty(zombieVillagerEntity.getBlockPos()), SpawnReason.CONVERSION, new ZombieEntity.ZombieData(false, true), (CompoundTag)null);
        zombieVillagerEntity.setVillagerData(villagerEntity.getVillagerData());
        zombieVillagerEntity.setGossipData((Tag)villagerEntity.getGossip().serialize(NbtOps.INSTANCE).getValue());
        zombieVillagerEntity.setOfferData(villagerEntity.getOffers().toTag());
        zombieVillagerEntity.setXp(villagerEntity.getExperience());
        zombieVillagerEntity.setBaby(villagerEntity.isBaby());
        zombieVillagerEntity.setAiDisabled(villagerEntity.isAiDisabled());
        if (villagerEntity.hasCustomName()) {
            zombieVillagerEntity.setCustomName(villagerEntity.getCustomName());
            zombieVillagerEntity.setCustomNameVisible(villagerEntity.isCustomNameVisible());
        }

        if (villagerEntity.isPersistent()) {
            zombieVillagerEntity.setPersistent();
        }

        zombieVillagerEntity.setInvulnerable(this.isInvulnerable());
        this.world.spawnEntity(zombieVillagerEntity);
        if (!this.isSilent()) {
            this.world.syncWorldEvent((PlayerEntity)null, 1026, this.getBlockPos(), 0);
        }

    }

}
