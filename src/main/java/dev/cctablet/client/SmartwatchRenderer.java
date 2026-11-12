package dev.cctablet.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public final class SmartwatchRenderer implements ICurioRenderer {
    private static final double WRIST_Y = 0.58D;

    private static final double WRIST_SURFACE_Z = -0.145D;

    private static final double DEFAULT_ARM_CENTER_X = 1.0D / 16.0D;
    private static final double SLIM_ARM_CENTER_X = 0.5D / 16.0D;

    private static final float DEFAULT_SCALE = 0.23F;
    private static final float SLIM_SCALE = 0.215F;

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(
        ItemStack stack,
        SlotContext slotContext,
        PoseStack poseStack,
        RenderLayerParent<T, M> renderLayerParent,
        MultiBufferSource bufferSource,
        int light,
        float limbSwing,
        float limbSwingAmount,
        float partialTicks,
        float ageInTicks,
        float netHeadYaw,
        float headPitch
    ) {
        if (!(renderLayerParent.getModel() instanceof HumanoidModel<?> humanoid)) return;

        var slim = slotContext.entity() instanceof AbstractClientPlayer player
            && "slim".equals(player.getModelName());

        var armCenterX = slim ? SLIM_ARM_CENTER_X : DEFAULT_ARM_CENTER_X;
        var scale = slim ? SLIM_SCALE : DEFAULT_SCALE;

        poseStack.pushPose();
        humanoid.leftArm.translateAndRotate(poseStack);
        poseStack.translate(armCenterX, WRIST_Y, WRIST_SURFACE_Z);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(0.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        poseStack.scale(scale, scale, scale);

        Minecraft.getInstance().getItemRenderer().renderStatic(
            stack,
            ItemDisplayContext.FIXED,
            light,
            OverlayTexture.NO_OVERLAY,
            poseStack,
            bufferSource,
            slotContext.entity().level(),
            0
        );

        poseStack.popPose();
    }
}
