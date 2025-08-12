package com.abysslasea.scoutreforked.armor;

import com.abysslasea.scoutreforked.client.model.SatchelsModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class SatchelArmorRenderer extends HumanoidModel<LivingEntity> {

    private final SatchelsModel<LivingEntity> satchelModel;

    public SatchelArmorRenderer(EntityModelSet modelSet) {
        super(modelSet != null ? modelSet.bakeLayer(SatchelsModel.LAYER_LOCATION) : createEmptyModelPart());
        this.satchelModel = modelSet != null ? new SatchelsModel<>(modelSet.bakeLayer(SatchelsModel.LAYER_LOCATION)) : null;
    }

    public SatchelArmorRenderer() {
        super(createEmptyModelPart());
        this.satchelModel = null;
    }

    private static ModelPart createEmptyModelPart() {
        try {
            MeshDefinition meshDefinition = new MeshDefinition();
            PartDefinition partDefinition = meshDefinition.getRoot();

            partDefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
            partDefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
            partDefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
            partDefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
            partDefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
            partDefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
            partDefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);

            return LayerDefinition.create(meshDefinition, 64, 32).bakeRoot();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void prepForRender(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<LivingEntity> original) {
        if (original != null) {
            try {
                original.copyPropertiesTo(this);
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
    }

    @Override
    public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (satchelModel != null) {
            satchelModel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
    }

    public void renderSatchelOnly(PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, float red, float green, float blue, float alpha) {
        if (satchelModel == null) return;

        poseStack.pushPose();

        ResourceLocation texture = new ResourceLocation("scoutreforked", "textures/models/armor/satchel.png");
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(texture));

        satchelModel.renderSatchelOnly(poseStack, vertexConsumer, light, overlay, red, green, blue, alpha);

        poseStack.popPose();
    }

    public void renderSatchel(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int light,
            LivingEntity entity,
            ItemStack stack,
            EquipmentSlot slot,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        if (satchelModel == null) return;

        poseStack.pushPose();

        satchelModel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        ResourceLocation texture = new ResourceLocation("scoutreforked", "textures/models/armor/satchel.png");
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(texture));

        satchelModel.renderSatchelOnly(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);

        poseStack.popPose();
    }

    public void renderForCurio(
            PoseStack matrixStack,
            MultiBufferSource renderTypeBuffer,
            int light,
            LivingEntity entity,
            ItemStack stack,
            EquipmentSlot equipmentSlot,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch) {

        renderSatchel(matrixStack, renderTypeBuffer, light, entity, stack, equipmentSlot,
                limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
    }
}