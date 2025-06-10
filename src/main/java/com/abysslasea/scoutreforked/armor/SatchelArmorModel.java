package com.abysslasea.scoutreforked.armor;

import com.abysslasea.scoutreforked.ScoutreForked;
import com.abysslasea.scoutreforked.item.SatchelArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SatchelArmorModel extends GeoModel<SatchelArmorItem> {
    @Override
    public ResourceLocation getModelResource(SatchelArmorItem animatable) {
        return new ResourceLocation(ScoutreForked.MODID, "geo/satchel.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SatchelArmorItem animatable) {
        return new ResourceLocation(ScoutreForked.MODID, "textures/models/armor/satchel.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SatchelArmorItem animatable) {
        return null;
    }
}
