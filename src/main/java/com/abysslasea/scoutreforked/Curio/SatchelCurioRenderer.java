package com.abysslasea.scoutreforked.Curio;

import com.abysslasea.scoutreforked.armor.SatchelArmorRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class SatchelCurioRenderer {

    public static Object createCurioRenderer(EntityModelSet modelSet) {
        try {
            Class<?> iCurioRendererClass = Class.forName("top.theillusivec4.curios.api.client.ICurioRenderer");

            return Proxy.newProxyInstance(
                    iCurioRendererClass.getClassLoader(),
                    new Class<?>[]{iCurioRendererClass},
                    new CurioRendererProxy(modelSet)
            );
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class CurioRendererProxy implements InvocationHandler {
        private final SatchelArmorRenderer renderer;

        public CurioRendererProxy(EntityModelSet modelSet) {
            this.renderer = new SatchelArmorRenderer(modelSet);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("render".equals(method.getName()) && args.length >= 12) {
                try {
                    ItemStack stack = (ItemStack) args[0];
                    Object slotContext = args[1];
                    PoseStack matrixStack = (PoseStack) args[2];
                    Object renderLayerParent = args[3];
                    MultiBufferSource renderTypeBuffer = (MultiBufferSource) args[4];
                    int light = (Integer) args[5];
                    float limbSwing = (Float) args[6];
                    float limbSwingAmount = (Float) args[7];
                    float partialTicks = (Float) args[8];
                    float ageInTicks = (Float) args[9];
                    float netHeadYaw = (Float) args[10];
                    float headPitch = (Float) args[11];

                    Method entityMethod = slotContext.getClass().getMethod("entity");
                    Object entity = entityMethod.invoke(slotContext);

                    if (entity instanceof LivingEntity) {
                        renderer.renderSatchel(matrixStack, renderTypeBuffer, light, (LivingEntity)entity, stack,
                                EquipmentSlot.CHEST, limbSwing, limbSwingAmount, partialTicks,
                                ageInTicks, netHeadYaw, headPitch);
                    }

                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            return null;
        }
    }
}