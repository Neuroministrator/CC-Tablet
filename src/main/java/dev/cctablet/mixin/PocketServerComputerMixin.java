package dev.cctablet.mixin;

import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.pocket.core.PocketBrain;
import dan200.computercraft.shared.pocket.core.PocketHolder;
import dan200.computercraft.shared.pocket.core.PocketServerComputer;
import dev.cctablet.TabletTerminalContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// this sets the terminal size
@Mixin(value = PocketServerComputer.class, remap = false)
public abstract class PocketServerComputerMixin {
    @Redirect(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Ldan200/computercraft/shared/computer/core/ServerComputer$Properties;terminalSize(II)Ldan200/computercraft/shared/computer/core/ServerComputer$Properties;",
            remap = false
        ),
        remap = false
    )
    private static ServerComputer.Properties cctablet$useDeviceTerminalSize(
        ServerComputer.Properties properties,
        int width,
        int height,
        PocketBrain brain,
        PocketHolder holder,
        ServerComputer.Properties constructorProperties
    ) {
        return properties.terminalSize(
            TabletTerminalContext.widthFor(holder, width),
            TabletTerminalContext.heightFor(holder, height)
        );
    }
}
