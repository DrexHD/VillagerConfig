package me.drex.villagerconfig.common.protocol;

import me.drex.villagerconfig.common.VillagerConfig;
import me.drex.villagerconfig.common.util.duck.IMerchantMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public record ClientboundMerchantXpPacket(int containerId, int maxLevel,
                                          int[] nextLevelXpThresholds) implements CustomPacketPayload {
    public static final Identifier PACKET_ID = Identifier.fromNamespaceAndPath(VillagerConfig.MOD_ID, "merchant_xp_packet");
    public static final CustomPacketPayload.Type<@NotNull ClientboundMerchantXpPacket> ID = new CustomPacketPayload.Type<>(PACKET_ID);
    public static final StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull ClientboundMerchantXpPacket> CODEC = StreamCodec.of(
        ClientboundMerchantXpPacket::writeToStream,
        ClientboundMerchantXpPacket::createFromStream
    );

    private static @NotNull ClientboundMerchantXpPacket createFromStream(@NotNull RegistryFriendlyByteBuf buf) {
        return new ClientboundMerchantXpPacket(buf.readInt(), buf.readInt(), Arrays.stream(buf.readLongArray()).mapToInt(i -> (int) i).toArray());
    }

    private static void writeToStream(@NotNull RegistryFriendlyByteBuf buf, @NotNull ClientboundMerchantXpPacket packet) {
        buf.writeInt(packet.containerId);
        buf.writeInt(packet.maxLevel);
        buf.writeLongArray(Arrays.stream(packet.nextLevelXpThresholds).mapToLong(i -> i).toArray());
    }

    @Override
    public Type<? extends @NotNull CustomPacketPayload> type() {
        return ID;
    }

    public void handle(Player player) {
        AbstractContainerMenu menu = player.containerMenu;
        if (containerId == menu.containerId && menu instanceof IMerchantMenu duck) {
            duck.villagerConfig$init(maxLevel, nextLevelXpThresholds);
        }
    }
}
