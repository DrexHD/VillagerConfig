package me.drex.villagerconfig.fabric;

import me.drex.villagerconfig.common.protocol.ClientboundMerchantXpPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class VillagerConfigFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ClientboundMerchantXpPacket.ID, (packet, context) -> {
            packet.handle(context.player());
        });
    }
}
