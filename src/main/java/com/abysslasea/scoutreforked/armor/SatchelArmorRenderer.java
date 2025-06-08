package com.abysslasea.scoutreforked.armor;

import com.abysslasea.scoutreforked.item.SatchelArmorItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class SatchelArmorRenderer extends GeoArmorRenderer<SatchelArmorItem> {

    public SatchelArmorRenderer() {
        super(new SatchelArmorModel());
    }
    public void renderForCurio(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int light,
            LivingEntity entity,
            ItemStack stack,
            EquipmentSlot slot,
            HumanoidModel<?> baseModel,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        this.prepForRender(entity, stack, slot, baseModel);
        ResourceLocation texture = this.getTextureLocation((SatchelArmorItem) stack.getItem());
        RenderType renderType = this.getRenderType((SatchelArmorItem) stack.getItem(), texture, bufferSource, partialTicks);
        ResourceLocation modelLocation = this.getGeoModel().getModelResource((SatchelArmorItem) stack.getItem());
        VertexConsumer buffer = bufferSource.getBuffer(renderType);

        this.actuallyRender(
                poseStack,
                (SatchelArmorItem) stack.getItem(),
                this.getGeoModel().getBakedModel(modelLocation),
                renderType,
                bufferSource,
                buffer,
                false,
                partialTicks,
                light,
                OverlayTexture.NO_OVERLAY,
                1f,
                1f,
                1f,
                1f
        );
    }

}