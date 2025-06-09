package com.abysslasea.scoutreforked;

import com.abysslasea.scoutreforked.item.SatchelArmorItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosCapability;

import java.util.stream.IntStream;

@Mod.EventBusSubscriber(modid = "scoutreforked", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EquipmentUtil {

    @SubscribeEvent
    public static void onChestEquip(LivingEquipmentChangeEvent event) {
        if (event.getSlot() != EquipmentSlot.CHEST) return;
        LivingEntity entity = event.getEntity();
        ItemStack to = event.getTo();

        if (!(to.getItem() instanceof SatchelArmorItem)) return;

        boolean hasInCurios = entity.getCapability(CuriosCapability.INVENTORY)
                .map(curios -> curios.getCurios().values().stream()
                        .flatMap(handler -> {
                            var stacks = handler.getStacks();
                            return IntStream.range(0, stacks.getSlots())
                                    .mapToObj(stacks::getStackInSlot);
                        })
                        .anyMatch(s -> !s.isEmpty() && s.getItem() instanceof SatchelArmorItem)
                ).orElse(false);

        if (hasInCurios && entity instanceof Player player) {
            entity.setItemSlot(EquipmentSlot.CHEST, event.getFrom());
            ItemStack rejected = to.copy();
            if (!player.getInventory().add(rejected)) {
                player.drop(rejected, false);
            }
        }
    }
}
