//package com.abysslasea.scoutreforked.item;
//
//import net.minecraft.world.entity.EquipmentSlot;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.ItemStack;
//import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//import top.theillusivec4.curios.api.CuriosCapability;
//
//import java.util.stream.IntStream;
//
//@Mod.EventBusSubscriber(modid = "scoutreforked", bus = Mod.EventBusSubscriber.Bus.FORGE)
//public class EquipmentConflictHandler {
//    /**
//     * 盔甲栏装备事件：阻止胸甲与饰品栏重复装备同款挎包
//     * 同时将被阻止装备的物品放回玩家背包，避免物品丢失
//     */
//    @SubscribeEvent
//    public static void onArmorEquip(LivingEquipmentChangeEvent event) {
//        if (event.getSlot() == EquipmentSlot.CHEST) {
//            LivingEntity entity = event.getEntity();
//            ItemStack newItem = event.getTo();
//
//            if (!newItem.isEmpty() && newItem.getItem() instanceof SatchelArmorItem) {
//                boolean hasSameInCurios = entity.getCapability(CuriosCapability.INVENTORY)
//                        .map(curiosHandler -> {
//                            return curiosHandler.getCurios().values().stream()
//                                    .flatMap(handler -> IntStream.range(0, handler.getStacks().getSlots())
//                                            .mapToObj(handler.getStacks()::getStackInSlot))
//                                    .anyMatch(stack -> !stack.isEmpty() && ItemStack.isSameItemSameTags(stack, newItem));
//                        }).orElse(false);
//
//                if (hasSameInCurios) {
//                    // 恢复之前的胸甲装备，阻止新装备生效
//                    entity.setItemSlot(EquipmentSlot.CHEST, event.getFrom());
//
//                    // 放回玩家背包或丢弃，避免物品消失
//                    if (entity instanceof Player player) {
//                        ItemStack rejected = newItem.copy(); // 复制一份避免修改原引用
//                        if (!player.getInventory().add(rejected)) {
//                            player.drop(rejected, false);
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
