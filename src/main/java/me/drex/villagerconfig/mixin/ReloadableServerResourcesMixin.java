package me.drex.villagerconfig.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.drex.villagerconfig.VillagerConfig;
import me.drex.villagerconfig.util.TradeManager;
import net.minecraft.commands.Commands;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ReloadableServerResources.class)
public abstract class ReloadableServerResourcesMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(
        LayeredRegistryAccess<RegistryLayer> layeredRegistryAccess, HolderLookup.Provider provider, FeatureFlagSet featureFlagSet, Commands.CommandSelection commandSelection, List<Registry.PendingTags<?>> list, int i, CallbackInfo ci
    ) {
        VillagerConfig.TRADE_MANAGER = new TradeManager(provider);
    }

    @WrapOperation(
        method = "listeners",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;"
        )
    )
    public <E> List<E> addListener(E e1, E e2, E e3, Operation<List<E>> original) {
        //noinspection MixinExtrasOperationParameters
        List<E> list = original.call(e1, e2, e3);
        //noinspection unchecked
        return (List<E>) ImmutableList.builder()
            .addAll(list)
            .add(VillagerConfig.TRADE_MANAGER)
            .build();

    }
}
