package com.abysslasea.scoutreforked.Curio;

import com.abysslasea.scoutreforked.armor.SatchelArmorRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class SatchelCurioRenderer implements ICurioRenderer {

    private static final SatchelArmorRenderer RENDERER = new SatchelArmorRenderer();

    @Override
    public <T extends LivingEntity, M extends net.minecraft.client.model.EntityModel<T>> void render(
            ItemStack stack,
            SlotContext slotContext,
            PoseStack matrixStack,
            RenderLayerParent<T, M> renderLayerParent,
            MultiBufferSource renderTypeBuffer,
            int light,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        LivingEntity entity = slotContext.entity();
        HumanoidModel<?> baseModel = (HumanoidModel<?>) renderLayerParent.getModel();
        RENDERER.renderForCurio(
                matrixStack,
                renderTypeBuffer,
                light,
                entity,
                stack,
                EquipmentSlot.CHEST,
                baseModel,
                limbSwing,
                limbSwingAmount,
                partialTicks,
                ageInTicks,
                netHeadYaw,
                headPitch
        );
    }
}