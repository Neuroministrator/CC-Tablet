package dev.cctablet.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dan200.computercraft.client.ClientHooks;
import dev.cctablet.item.SmartwatchComputerItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClientHooks.class, remap = false)
public abstract class ClientHooksMixin {
    @Inject(method = "onRenderHeldItem", at = @At("HEAD"), cancellable = true, remap = false)
    private static void cctablet$useVanillaHeldRendererForSmartwatch(
        PoseStack transform,
        MultiBufferSource render,
        int lightTexture,
        InteractionHand hand,
        float pitch,
        float equipProgress,
        float swingProgress,
        ItemStack stack,
        CallbackInfoReturnable<Boolean> callback
    ) {
        if (stack.getItem() instanceof SmartwatchComputerItem) {
            callback.setReturnValue(false);
        }
    }
}
