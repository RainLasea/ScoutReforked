package com.abysslasea.scoutreforked.Curio;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.stream.IntStream;

@Mod.EventBusSubscriber(modid = "scoutreforked", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SatchelCurioWrapperItem implements ICurio {
    private final ItemStack stack;

    public SatchelCurioWrapperItem(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public boolean canEquip(SlotContext context) {
        LivingEntity entity = context.entity();
        ItemStack chestStack = entity.getItemBySlot(EquipmentSlot.CHEST);
        return chestStack.isEmpty() || !ItemStack.isSameItemSameTags(chestStack, stack);
    }

    @Override
    public boolean canUnequip(SlotContext context) {
        return true;
    }

    @Override
    public boolean canRightClickEquip() {
        return true;
    }
    @SubscribeEvent
    public static void onArmorEquip(LivingEquipmentChangeEvent event) {
        if (event.getSlot() == EquipmentSlot.CHEST) {
            LivingEntity entity = event.getEntity();
            ItemStack newItem = event.getTo();

            if (!newItem.isEmpty() && newItem.getItem() instanceof net.minecraft.world.item.ArmorItem) {
                boolean hasSameInCurios = entity.getCapability(CuriosCapability.INVENTORY)
                        .map(curiosHandler -> {
                            return curiosHandler.getCurios().values().stream()
                                    .flatMap(handler -> IntStream.range(0, handler.getStacks().getSlots())
                                            .mapToObj(handler.getStacks()::getStackInSlot))
                                    .anyMatch(stack -> !stack.isEmpty() && ItemStack.isSameItemSameTags(stack, newItem));
                        }).orElse(false);

                if (hasSameInCurios) {

                    entity.setItemSlot(EquipmentSlot.CHEST, event.getFrom());

                    if (entity instanceof Player player) {
                        ItemStack rejected = newItem.copy();
                        if (!player.getInventory().add(rejected)) {
                            player.drop(rejected, false);
                        }
                    }
                }
            }
        }
    }
    @Override
    public ItemStack getStack() {
        return this.stack;
    }
}
