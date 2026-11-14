package dev.cctablet.api;

/** Bridge for resizing a cached CC:Tweaked terminal after an item swap. */
public interface ServerComputerTerminalAccess {
    void cctablet$resizeTerminal(int width, int height);
}
