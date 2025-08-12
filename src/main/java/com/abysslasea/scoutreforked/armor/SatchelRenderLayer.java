package com.abysslasea.scoutreforked.armor;

import com.abysslasea.scoutreforked.client.model.SatchelsModel;
import com.abysslasea.scoutreforked.item.SatchelArmorItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class SatchelRenderLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation SATCHEL_TEXTURE = new ResourceLocation("scoutreforked", "textures/models/armor/satchel.png");

    private final SatchelsModel<T> satchelModel;

    public SatchelRenderLayer(RenderLayerParent<T, M> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.satchelModel = new SatchelsModel<>(modelSet.bakeLayer(SatchelsModel.LAYER_LOCATION));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack chestStack = entity.getItemBySlot(EquipmentSlot.CHEST);

        if (chestStack.getItem() instanceof SatchelArmorItem) {
            poseStack.pushPose();

            this.satchelModel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

            this.getParentModel().copyPropertiesTo(this.satchelModel);

            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(SATCHEL_TEXTURE));

            this.satchelModel.renderSatchelOnly(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

            poseStack.popPose();
        }
    }
}