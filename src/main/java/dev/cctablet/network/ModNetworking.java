package dev.cctablet.network;

import dev.cctablet.CCTablet;
import dev.cctablet.curios.SmartwatchCurios;
import dev.cctablet.item.SmartwatchComputerItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public final class ModNetworking {
    private static final String PROTOCOL = "1";

    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(CCTablet.MOD_ID, "main"),
        () -> PROTOCOL,
        PROTOCOL::equals,
        PROTOCOL::equals
    );

    private ModNetworking() {
    }

    public static void register() {
        CHANNEL.registerMessage(
            0,
            OpenSmartwatchPacket.class,
            OpenSmartwatchPacket::encode,
            OpenSmartwatchPacket::decode,
            OpenSmartwatchPacket::handle
        );
    }

    public static void sendOpenSmartwatch() {
        CHANNEL.sendToServer(new OpenSmartwatchPacket());
    }

    public record OpenSmartwatchPacket() {
        private static void encode(OpenSmartwatchPacket packet, FriendlyByteBuf buffer) {
        }

        private static OpenSmartwatchPacket decode(FriendlyByteBuf buffer) {
            return new OpenSmartwatchPacket();
        }

        private static void handle(OpenSmartwatchPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
            var context = contextSupplier.get();
            context.enqueueWork(() -> {
                var player = context.getSender();
                if (player == null) return;

                var stack = SmartwatchCurios.getEquippedWatch(player);
                if (stack.getItem() instanceof SmartwatchComputerItem watch) {
                    watch.openFromWrist(player, stack);
                }
            });
            context.setPacketHandled(true);
        }
    }
}
