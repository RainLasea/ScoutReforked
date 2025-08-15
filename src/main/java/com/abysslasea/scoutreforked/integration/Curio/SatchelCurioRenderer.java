package com.abysslasea.scoutreforked.integration.Curio;

import com.abysslasea.scoutreforked.client.model.SatchelsModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class SatchelCurioRenderer {

    private static final ResourceLocation SATCHEL_TEXTURE = new ResourceLocation("scoutreforked", "textures/models/armor/satchel.png");

    public static Object createCurioRenderer(EntityModelSet modelSet) {
        try {
            Class<?> iCurioRendererClass = Class.forName("top.theillusivec4.curios.api.client.ICurioRenderer");
            return Proxy.newProxyInstance(
                    iCurioRendererClass.getClassLoader(),
                    new Class<?>[]{iCurioRendererClass},
                    new CurioRendererHandler(modelSet)
            );
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static class CurioRendererHandler implements InvocationHandler {
        private final SatchelsModel<LivingEntity> satchelModel;

        public CurioRendererHandler(EntityModelSet modelSet) {
            this.satchelModel = new SatchelsModel<>(modelSet.bakeLayer(SatchelsModel.LAYER_LOCATION));
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("render".equals(method.getName()) && args.length >= 12) {
                handleRender(args);
            }
            return null;
        }

        private void handleRender(Object[] args) {
            try {
                ItemStack stack = (ItemStack) args[0];
                Object slotContext = args[1];
                PoseStack poseStack = (PoseStack) args[2];
                Object renderLayerParent = args[3];
                MultiBufferSource bufferSource = (MultiBufferSource) args[4];
                int light = (Integer) args[5];
                float limbSwing = (Float) args[6];
                float limbSwingAmount = (Float) args[7];
                float partialTicks = (Float) args[8];
                float ageInTicks = (Float) args[9];
                float netHeadYaw = (Float) args[10];
                float headPitch = (Float) args[11];

                Method entityMethod = slotContext.getClass().getMethod("entity");
                LivingEntity entity = (LivingEntity) entityMethod.invoke(slotContext);

                poseStack.pushPose();

                satchelModel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

                try {
                    Method getModelMethod = renderLayerParent.getClass().getMethod("getModel");
                    Object parentModel = getModelMethod.invoke(renderLayerParent);

                    Method copyPropertiesMethod = null;
                    try {
                        copyPropertiesMethod = parentModel.getClass().getMethod("copyPropertiesTo", satchelModel.getClass());
                    } catch (NoSuchMethodException e1) {
                        try {
                            Class<?> humanoidModelClass = Class.forName("net.minecraft.client.model.HumanoidModel");
                            copyPropertiesMethod = parentModel.getClass().getMethod("copyPropertiesTo", humanoidModelClass);
                        } catch (NoSuchMethodException e2) {
                            try {
                                Class<?> entityModelClass = Class.forName("net.minecraft.client.model.EntityModel");
                                copyPropertiesMethod = parentModel.getClass().getMethod("copyPropertiesTo", entityModelClass);
                            } catch (NoSuchMethodException e3) {
                            }
                        }
                    }

                    if (copyPropertiesMethod != null) {
                        copyPropertiesMethod.invoke(parentModel, satchelModel);
                    }
                } catch (Exception e) {
                }

                VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(SATCHEL_TEXTURE));
                satchelModel.renderSatchelOnly(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

                poseStack.popPose();

            } catch (Exception e) {
            }
        }
    }
}