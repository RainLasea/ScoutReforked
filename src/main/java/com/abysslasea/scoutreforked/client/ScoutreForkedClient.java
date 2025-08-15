package com.abysslasea.scoutreforked.client;

import com.abysslasea.scoutreforked.armor.SatchelRenderLayer;
import com.abysslasea.scoutreforked.client.model.SatchelsModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "scoutreforked", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ScoutreForkedClient {

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SatchelsModel.LAYER_LOCATION, SatchelsModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        EntityModelSet modelSet = event.getEntityModels();
        PlayerRenderer defaultPlayerRenderer = event.getSkin("default");
        if (defaultPlayerRenderer != null) {
            defaultPlayerRenderer.addLayer(new SatchelRenderLayer<>(defaultPlayerRenderer, modelSet));
        }
        PlayerRenderer slimPlayerRenderer = event.getSkin("slim");
        if (slimPlayerRenderer != null) {
            slimPlayerRenderer.addLayer(new SatchelRenderLayer<>(slimPlayerRenderer, modelSet));
        }
    }
}