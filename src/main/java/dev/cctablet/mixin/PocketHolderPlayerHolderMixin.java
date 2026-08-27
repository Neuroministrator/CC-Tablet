package dev.cctablet.mixin;

import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.pocket.core.PocketHolder;
import dan200.computercraft.shared.pocket.items.PocketComputerItem;
import dev.cctablet.curios.SmartwatchCurios;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// this handles the watch slot
@Mixin(value = PocketHolder.PlayerHolder.class, remap = false)
public abstract class PocketHolderPlayerHolderMixin {
    @Inject(method = "isValid", at = @At("HEAD"), cancellable = true, remap = false)
    private void cctablet$validateCuriosWatch(ServerComputer computer, CallbackInfoReturnable<Boolean> cir) {
        var self = (PocketHolder.PlayerHolder) (Object) this;
        if (self.slot() != SmartwatchCurios.SYNTHETIC_PLAYER_SLOT) return;

        var stack = SmartwatchCurios.getEquippedWatch(self.entity());
        cir.setReturnValue(
            self.entity().isAlive()
                && !stack.isEmpty()
                && PocketComputerItem.isServerComputer(computer, stack)
        );
    }

    @Inject(method = "setChanged", at = @At("HEAD"), cancellable = true, remap = false)
    private void cctablet$markCuriosWatchChanged(CallbackInfo ci) {
        var self = (PocketHolder.PlayerHolder) (Object) this;
        if (self.slot() != SmartwatchCurios.SYNTHETIC_PLAYER_SLOT) return;

        SmartwatchCurios.setChanged(self.entity());
        ci.cancel();
    }
}
