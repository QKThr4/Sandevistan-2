package com.sandevistanmod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sandevistanmod.SandevistanMod;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;

/** Desenha o implante Sandevistan (coluna verde) nas costas do jogador, permanentemente enquanto estiver implantado. */
public class BackLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final ResourceLocation TEX =
            ResourceLocation.fromNamespaceAndPath(SandevistanMod.MODID, "textures/entity/sandevistan_back.png");

    public BackLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        int code = ClientState.get(player.getUUID());
        if ((code & 1) == 0 || player.isInvisible() || player.isSpectator()) return;

        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(TEX));
        poseStack.pushPose();
        this.getParentModel().body.translateAndRotate(poseStack);
        PoseStack.Pose pose = poseStack.last();
        // a placa de peito do armor chega a z = 3: sem armor fica colada no corpo, com armor fica por cima
        float z = (player.getItemBySlot(EquipmentSlot.CHEST).isEmpty() ? 2.45f : 3.1f) / 16.0f;
        int light = (code & 2) != 0 ? LightTexture.FULL_BRIGHT : packedLight;
        float s = 1.0f / 16.0f;
        // visto de tras, o lado esquerdo do jogador (+x do modelo) fica a esquerda de quem olha
        vertex(vc, pose, 4 * s, 0f, z, 0f, 0f, light);
        vertex(vc, pose, -4 * s, 0f, z, 1f, 0f, light);
        vertex(vc, pose, -4 * s, 12 * s, z, 1f, 1f, light);
        vertex(vc, pose, 4 * s, 12 * s, z, 0f, 1f, light);
        poseStack.popPose();
    }

    private static void vertex(VertexConsumer vc, PoseStack.Pose pose, float x, float y, float z, float u, float v,
                               int light) {
        vc.addVertex(pose, x, y, z)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0.0f, 0.0f, 1.0f);
    }
}
