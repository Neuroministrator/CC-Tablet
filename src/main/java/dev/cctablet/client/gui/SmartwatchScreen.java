package dev.cctablet.client.gui;

import dan200.computercraft.client.gui.AbstractComputerScreen;
import dan200.computercraft.client.gui.widgets.TerminalWidget;
import dan200.computercraft.shared.computer.inventory.AbstractComputerMenu;
import dev.cctablet.CCTablet;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class SmartwatchScreen extends AbstractComputerScreen<AbstractComputerMenu> {
    private static final int BEZEL = 9;
    private static final int SIDEBAR_HEIGHT = 38;

    private static final int TEXTURE_WIDTH = 148;
    private static final int TEXTURE_HEIGHT = 138;
    private static final int TEXTURE_TOP_OFFSET = 15;

    private static final ResourceLocation TEXTURE = new ResourceLocation(CCTablet.MOD_ID, "textures/gui/smartwatch.png");

    public SmartwatchScreen(AbstractComputerMenu menu, Inventory player, Component title) {
        super(menu, player, title, sidebarYOffset(menu));

        imageWidth = AbstractComputerMenu.SIDEBAR_WIDTH
            + BEZEL * 2
            + TerminalWidget.getWidth(terminalData.getWidth());
        imageHeight = BEZEL * 2 + TerminalWidget.getHeight(terminalData.getHeight());
    }

    private static int sidebarYOffset(AbstractComputerMenu menu) {
        var height = BEZEL * 2 + TerminalWidget.getHeight(menu.getTerminal().getHeight());
        return Math.max(0, (height - SIDEBAR_HEIGHT) / 2);
    }

    @Override
    protected TerminalWidget createTerminal() {
        return new TerminalWidget(
            terminalData,
            computerInput,
            computerActions,
            leftPos + AbstractComputerMenu.SIDEBAR_WIDTH + BEZEL,
            topPos + BEZEL
        );
    }

    @Override
    public void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos - TEXTURE_TOP_OFFSET, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }
}
