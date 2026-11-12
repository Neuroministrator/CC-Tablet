package dev.cctablet.mixin;

import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.terminal.NetworkedTerminal;
import dev.cctablet.api.ServerComputerTerminalAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ServerComputer.class, remap = false)
public abstract class ServerComputerTerminalMixin implements ServerComputerTerminalAccess {
    @Shadow @Final private NetworkedTerminal terminal;

    @Shadow
    protected abstract void markTerminalChanged();

    @Override
    public void cctablet$resizeTerminal(int width, int height) {
        if (terminal.getWidth() == width && terminal.getHeight() == height) return;

        terminal.resize(width, height);
        markTerminalChanged();
    }
}
