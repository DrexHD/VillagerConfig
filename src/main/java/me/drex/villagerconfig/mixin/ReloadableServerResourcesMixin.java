package me.drex.villagerconfig.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.drex.villagerconfig.VillagerConfig;
import me.drex.villagerconfig.util.TradeManager;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
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
    @Shadow
    @Final
    private ReloadableServerResources.ConfigurableRegistryLookup registryLookup;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(
        RegistryAccess.Frozen frozen, FeatureFlagSet featureFlagSet, Commands.CommandSelection commandSelection, int i,
        CallbackInfo ci
    ) {
        VillagerConfig.TRADE_MANAGER = new TradeManager(this.registryLookup);
    }

    @WrapOperation(
        method = "listeners",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;"
        )
    )
    public <E> List<E> addListener(E e1, E e2, E e3, E e4, Operation<List<E>> original) {
        //noinspection MixinExtrasOperationParameters
        List<E> list = original.call(e1, e2, e3, e4);
        //noinspection unchecked
        return (List<E>) ImmutableList.builder()
            .addAll(list)
            .add(VillagerConfig.TRADE_MANAGER)
            .build();

    }
}
