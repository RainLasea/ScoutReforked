// SatchelEquipListener.java - 合并后的事件处理器
package com.abysslasea.scoutreforked;

import com.abysslasea.scoutreforked.InventorySlot.InventoryMenuMixin;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import com.abysslasea.scoutreforked.item.SatchelArmorItem;

public class SatchelEquipListener {

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (event.getSlot() == EquipmentSlot.CHEST) {
            ItemStack from = event.getFrom();
            ItemStack to = event.getTo();

            boolean wasWearingSatchel = from.getItem() instanceof SatchelArmorItem;
            boolean nowWearingSatchel = to.getItem() instanceof SatchelArmorItem;

            // 脱下背包时处理物品
            if (wasWearingSatchel && !nowWearingSatchel) {
                dropAndClearSatchelContents(player, from);
            }

            // 无论穿脱都刷新容器
            syncContainer(player);
        }
    }

    private static void dropAndClearSatchelContents(Player player, ItemStack satchelStack) {
        if (player.level().isClientSide) return;

        IItemHandler handler = SatchelArmorItem.getItemHandler(satchelStack);
        if (handler == null) return;

        // 直接通过处理器处理物品
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                player.drop(stack.copy(), false);
                handler.extractItem(i, stack.getCount(), false);
            }
        }
    }

    private static void syncContainer(Player player) {
        if (!player.level().isClientSide && player.containerMenu != null) {
            player.containerMenu.broadcastChanges();
        }
    }
}