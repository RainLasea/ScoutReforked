package com.abysslasea.scoutreforked.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public abstract class CuriosItem extends Item implements ICurioItem {
    public CuriosItem(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        this.equipmentTick(slotContext.entity());
    }

    protected void equipmentTick(LivingEntity livingEntity) {
        livingEntity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 20, 0, false, false, true));
    }
}
