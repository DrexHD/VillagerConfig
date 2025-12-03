package me.drex.villagerconfig.common.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.drex.villagerconfig.common.platform.PlatformHooks;
import me.drex.villagerconfig.common.protocol.ClientboundMerchantXpPacket;
import me.drex.villagerconfig.common.util.CustomVillagerData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.Merchant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.OptionalInt;

@Mixin(Merchant.class)
public interface MerchantMixin {
    @Inject(method = "openTradingScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;sendMerchantOffers(ILnet/minecraft/world/item/trading/MerchantOffers;IIZZ)V"))
    default void onSendOffers(CallbackInfo ci, @Local OptionalInt optionalInt, @Local Player player) {
        if ((Object) this instanceof Villager villager && player instanceof ServerPlayer serverPlayer) {
            ClientboundMerchantXpPacket packet = new ClientboundMerchantXpPacket(optionalInt.getAsInt(), CustomVillagerData.getMaxLevel(villager), CustomVillagerData.getNextLevelXpThresholds(villager));
            PlatformHooks.PLATFORM_HELPER.sendPacket(serverPlayer, packet);
        }
    }
}
