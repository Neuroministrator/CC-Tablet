package dev.cctablet.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dan200.computercraft.client.render.PocketItemRenderer;
import dan200.computercraft.shared.config.Config;
import dev.cctablet.TabletTerminalContext;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = PocketItemRenderer.class, remap = false)
public abstract class PocketItemRendererMixin {
    @ModifyConstant(
        method = "renderItem",
        constant = @Constant(intValue = Config.DEFAULT_POCKET_TERM_WIDTH),
        remap = false
    )
    private int cctablet$useDeviceFallbackWidth(
        int original,
        PoseStack transform,
        MultiBufferSource bufferSource,
        ItemStack stack,
        int light
    ) {
        return TabletTerminalContext.widthFor(stack, original);
    }

    @ModifyConstant(
        method = "renderItem",
        constant = @Constant(intValue = Config.DEFAULT_POCKET_TERM_HEIGHT),
        remap = false
    )
    private int cctablet$useDeviceFallbackHeight(
        int original,
        PoseStack transform,
        MultiBufferSource bufferSource,
        ItemStack stack,
        int light
    ) {
        return TabletTerminalContext.heightFor(stack, original);
    }
}
