package me.drex.villagerconfig.neoforge;


import me.drex.villagerconfig.common.VillagerConfig;
import me.drex.villagerconfig.common.commands.VillagerConfigCommand;
import me.drex.villagerconfig.common.config.ConfigScreen;
import me.drex.villagerconfig.common.protocol.ClientboundMerchantXpPacket;
import me.drex.villagerconfig.neoforge.util.NeoForgeTradeManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
//? if >= 1.21.6 {
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
//? } else {
/*import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
*///? }
import net.neoforged.neoforge.common.NeoForge;
//? if >= 1.21.4 {
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import me.drex.villagerconfig.common.util.TradeManager;
//?} else {
/*import net.neoforged.neoforge.event.AddReloadListenerEvent;
 *///?}
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(VillagerConfig.MOD_ID)
public final class VillagerConfigNeoForge {
    public static String MOD_VERSION = null;

    public VillagerConfigNeoForge(ModContainer modContainer, IEventBus modEventBus) {
        IEventBus eventBus = NeoForge.EVENT_BUS;

        MOD_VERSION = modContainer.getModInfo().getVersion().toString();

        VillagerConfig.initialize();
        eventBus.addListener(VillagerConfigNeoForge::onRegisterCommands);
        eventBus.addListener(VillagerConfigNeoForge::onAddReloadListener);
        modEventBus.addListener(VillagerConfigNeoForge::registerCommon);

        if (FMLEnvironment./*? if >= 1.21.9 {*/ getDist() /*?} else {*/ /*dist *//*?}*/ == Dist.CLIENT) {
            if (ModList.get().isLoaded("cloth_config")) {
                modEventBus.addListener(VillagerConfigNeoForge::onClientSetup);
            }
            //? if >= 1.21.6 {
            modEventBus.addListener(VillagerConfigNeoForge::registerClient);
            //? }
        }
    }

    private static void onRegisterCommands(RegisterCommandsEvent event) {
        VillagerConfigCommand.register(event.getDispatcher(), event.getBuildContext());
    }

    private static void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (client, screen) -> ConfigScreen.getConfigScreen(screen)));
    }

    //? if >= 1.21.6 {
    public static void registerClient(RegisterClientPayloadHandlersEvent event) {
        event.register(
            ClientboundMerchantXpPacket.ID,
            (payload, context) -> payload.handle(context.player())
        );
    }
    //? }

    public static void registerCommon(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        //? if >= 1.21.6 {
        registrar.playBidirectional(
            ClientboundMerchantXpPacket.ID,
            ClientboundMerchantXpPacket.CODEC,
            (payload, context) -> payload.handle(context.player())
        );
        //? } else {
        /*registrar.playBidirectional(
            ClientboundMerchantXpPacket.ID,
            ClientboundMerchantXpPacket.CODEC,
            new DirectionalPayloadHandler<>(
                (payload, context) -> payload.handle(context.player()),
                (payload, context) -> payload.handle(context.player())
            )
        );
        *///? }

    }

    private static void onAddReloadListener(/*? if >= 1.21.4 {*/ AddServerReloadListenersEvent /*?} else {*/ /*AddReloadListenerEvent *//*?}*/ event) {
        VillagerConfig.TRADE_MANAGER = new NeoForgeTradeManager(event.getRegistryAccess());
        event.addListener(/*? if >= 1.21.4 {*/TradeManager.ID,  /*?}*/VillagerConfig.TRADE_MANAGER);
    }

}
