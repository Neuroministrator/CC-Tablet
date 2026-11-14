package dev.cctablet.client.gui;

import dev.cctablet.CCTablet;
import dev.cctablet.menu.HddCaseMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class HddCaseScreen extends AbstractContainerScreen<HddCaseMenu> {
    private static final ResourceLocation BACKGROUND =
        new ResourceLocation(CCTablet.MOD_ID, "textures/gui/hdd_case.png");

    public HddCaseScreen(HddCaseMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 166;
        inventoryLabelY = 72;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        if (menu.getCarried().isEmpty() && hoveredSlot != null && hoveredSlot.hasItem()) {
            var stack = hoveredSlot.getItem();
            graphics.renderTooltip(font, getTooltipFromContainerItem(stack),
                stack.getTooltipImage(), stack, mouseX, mouseY);
        }
    }
}
