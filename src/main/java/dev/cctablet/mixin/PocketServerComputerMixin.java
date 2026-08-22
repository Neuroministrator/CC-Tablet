package dev.cctablet.mixin;

import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.pocket.core.PocketServerComputer;
import dev.cctablet.TabletTerminalContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * changes the pocket terminal size only while a tablet is being created
 * redirect is used because modifyargs caused class loading problems on forge
 */
@Mixin(PocketServerComputer.class)
public abstract class PocketServerComputerMixin {
    @Redirect(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Ldan200/computercraft/shared/computer/core/ServerComputer$Properties;terminalSize(II)Ldan200/computercraft/shared/computer/core/ServerComputer$Properties;"
        )
    )
    private static ServerComputer.Properties cctablet$useTabletTerminalSize(
        ServerComputer.Properties properties, int width, int height
    ) {
        if (TabletTerminalContext.isCreatingTablet()) {
            return properties.terminalSize(TabletTerminalContext.WIDTH, TabletTerminalContext.HEIGHT);
        }

        return properties.terminalSize(width, height);
    }
}
