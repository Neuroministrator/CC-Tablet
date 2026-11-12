package dev.cctablet.mixin;

import dan200.computercraft.api.network.Packet;
import dan200.computercraft.api.network.PacketReceiver;
import dan200.computercraft.shared.peripheral.modem.wireless.WirelessNetwork;
import dev.cctablet.peripheral.UniversalModemPeripheral;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WirelessNetwork.class, remap = false)
public abstract class UniversalModemNetworkMixin {
    @Inject(method = "tryTransmit", at = @At("HEAD"), cancellable = true)
    private static void cctablet$enforceUniversalSenderRange(
        PacketReceiver receiver, Packet packet, double ignoredRange, boolean interdimensional, CallbackInfo callback
    ) {
        if (!(packet.sender() instanceof UniversalModemPeripheral sender) || !sender.isEnderMode()) return;

        var senderLevel = sender.getLevel();
        var senderPosition = sender.getPosition();
        var receiverLevel = receiver.getLevel();
        var receiverPosition = receiver.getPosition();
        if (senderLevel == null || senderPosition == null || receiverLevel == null || receiverPosition == null) {
            callback.cancel();
            return;
        }

        if (receiverLevel == senderLevel) {
            receiver.receiveSameDimension(packet, receiverPosition.distanceTo(senderPosition));
        } else {
            receiver.receiveDifferentDimension(packet);
        }
        callback.cancel();
    }
}
