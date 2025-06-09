package com.abysslasea.scoutreforked.Curio;

import com.abysslasea.scoutreforked.item.SatchelArmorItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.stream.IntStream;

public class SatchelCurioWrapperItem implements ICurio {
    private final ItemStack stack;

    public SatchelCurioWrapperItem(ItemStack stack) {
        this.stack = stack;
    }

    /**
     * 只要玩家在胸甲槽或任何 Curios 槽已有一个 SatchelArmorItem，就禁止再次装备。
     */
    @Override
    public boolean canEquip(SlotContext context) {
        LivingEntity entity = context.entity();

        // 1. 胸甲槽检查
        ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.getItem() instanceof SatchelArmorItem) {
            return false;
        }

        // 2. Curios 插槽检查
        return entity.getCapability(top.theillusivec4.curios.api.CuriosCapability.INVENTORY)
                .map(curios -> curios.getCurios().values().stream()
                        .flatMap(handler -> {
                            var stacks = handler.getStacks();
                            return IntStream.range(0, stacks.getSlots())
                                    .mapToObj(stacks::getStackInSlot);
                        })
                        // 过滤掉当前这只“正要装备”的挎包
                        .filter(s -> !s.isEmpty() && s != this.stack)
                        // 确保其余插槽没有任何 SatchelArmorItem
                        .noneMatch(s -> s.getItem() instanceof SatchelArmorItem)
                ).orElse(true);
    }

    @Override
    public ItemStack getStack() {
        return this.stack;
    }
}