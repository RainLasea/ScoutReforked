package com.abysslasea.scoutreforked.event;

import com.abysslasea.scoutreforked.item.SatchelArmorItem;
import com.abysslasea.scoutreforked.util.SatchelManager;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "scoutreforked", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SatchelEventHandler {

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide) return;
        if (event.getSlot() != EquipmentSlot.CHEST) return;

        try {
            ItemStack from = event.getFrom();
            ItemStack to = event.getTo();

            boolean wasWearingSatchel = from.getItem() instanceof SatchelArmorItem;
            boolean nowWearingSatchel = to.getItem() instanceof SatchelArmorItem;

            if (nowWearingSatchel && !wasWearingSatchel) {
                player.level().getServer().execute(() -> {
                    SatchelManager.handleChestSatchelEquip(player, to);
                });
            } else if (wasWearingSatchel && !nowWearingSatchel) {
                player.level().getServer().execute(() -> {
                    SatchelManager.handleChestSatchelUnequip(player, from);
                });
            } else if (wasWearingSatchel || nowWearingSatchel) {
                player.level().getServer().execute(() -> {
                    SatchelManager.syncPlayerContainer(player);
                });
            }
        } catch (Exception e) {
        }
    }
}