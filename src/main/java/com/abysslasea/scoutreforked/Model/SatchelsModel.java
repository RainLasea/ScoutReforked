package com.abysslasea.scoutreforked.Model;// Made with Blockbench 4.12.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class SatchelsModel extends HumanoidModel<LivingEntity> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("scoutreforked", "satchels"), "main");
	private final ModelPart Satchels;

	public SatchelsModel(ModelPart root) {
		super(root);
		this.Satchels = root.getChild("Satchels");
	}


	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Satchels = partdefinition.addOrReplaceChild("Satchels", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -6.0F, -3.0F, 3.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(18, 25).addBox(2.0F, -18.5F, -3.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 18.0F, 2.0F));

		PartDefinition cube_r1 = Satchels.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 15).addBox(-0.5F, -8.0F, -0.5F, 1.0F, 17.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(5, 15).addBox(-0.5F, -8.0F, -5.5F, 1.0F, 17.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.7593F, -12.0503F, 2.5F, 0.0F, 0.0F, 0.6981F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		Minecraft.getInstance().getTextureManager().bindForSetup(new ResourceLocation("scoutreforked", "textures/models/armor/satchel.png"));
		Satchels.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}