package com.abysslasea.scoutreforked.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class SatchelsModel<T extends LivingEntity> extends HumanoidModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("scoutreforked", "satchel_armor"), "main");

	private final ModelPart satchelPart;

	public SatchelsModel(ModelPart root) {
		super(root);
		this.satchelPart = root.getChild("body").getChild("armorBody");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = HumanoidModel.createMesh(new CubeDeformation(0.0F), 0);
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bodyPart = partdefinition.getChild("body");

		PartDefinition armorBody = bodyPart.addOrReplaceChild("armorBody",
				CubeListBuilder.create()
						.texOffs(0, 0).addBox(-9.0F, 12.0F, -1.0F, 3.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
						.texOffs(18, 25).addBox(2.0F, -0.5F, -1.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)),
				PartPose.offset(2.0F, 0.0F, -2.0F)
		);

		armorBody.addOrReplaceChild("cube_r1",
				CubeListBuilder.create()
						.texOffs(0, 15).addBox(-0.5F, -8.0F, -0.5F, 1.0F, 17.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(5, 14).addBox(-0.5F, -8.0F, -5.5F, 1.0F, 17.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.7593F, 5.9498F, 4.5F, 0.0F, 0.0F, 0.6981F)
		);

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if (satchelPart != null) {
			satchelPart.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		}
	}

	public void renderSatchelOnly(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if (satchelPart != null) {
			satchelPart.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		}
	}
}