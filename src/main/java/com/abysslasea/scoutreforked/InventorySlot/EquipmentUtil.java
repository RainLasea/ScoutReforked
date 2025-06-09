package com.abysslasea.scoutreforked.InventorySlot;

import com.abysslasea.scoutreforked.item.SatchelArmorItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;

public class EquipmentUtil {
    public static boolean isWearingSatchel(Player player) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        return chest.getItem() instanceof SatchelArmorItem;
    }
}
