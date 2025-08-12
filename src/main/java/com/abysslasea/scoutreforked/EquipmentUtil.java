package com.abysslasea.scoutreforked;

import com.abysslasea.scoutreforked.Curio.CuriosCompat;
import com.abysslasea.scoutreforked.item.SatchelArmorItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

@Mod.EventBusSubscriber(modid = "scoutreforked", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EquipmentUtil {

    @SubscribeEvent
    public static void onChestEquip(LivingEquipmentChangeEvent event) {
        if (event.getSlot() != EquipmentSlot.CHEST) return;
        LivingEntity entity = event.getEntity();
        ItemStack to = event.getTo();

        if (!(to.getItem() instanceof SatchelArmorItem)) return;

        if (!CuriosCompat.isCuriosLoaded()) return;

        Object curiosHandler = CuriosCompat.getCuriosCapability(entity instanceof Player ? (Player) entity : null);
        if (curiosHandler == null) return;

        Map<String, Object> curiosMap = CuriosCompat.getCurios(curiosHandler);
        if (curiosMap == null) return;

        boolean hasInCurios = curiosMap.values().stream()
                .map(CuriosCompat::getStacksHandler)
                .filter(Objects::nonNull)
                .flatMap(stacks -> IntStream.range(0, stacks.getSlots())
                        .mapToObj(stacks::getStackInSlot))
                .anyMatch(s -> !s.isEmpty() && s.getItem() instanceof SatchelArmorItem);

        if (hasInCurios && entity instanceof Player player) {
            entity.setItemSlot(EquipmentSlot.CHEST, event.getFrom());
            ItemStack rejected = to.copy();
            if (!player.getInventory().add(rejected)) {
                player.drop(rejected, false);
            }
        }
    }
}