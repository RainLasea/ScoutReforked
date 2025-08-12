package com.abysslasea.scoutreforked.Curio;

import com.abysslasea.scoutreforked.item.SatchelArmorItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.stream.IntStream;

public class SatchelCurioWrapperItemImpl implements ICurio {

    private final ItemStack stack;

    public SatchelCurioWrapperItemImpl(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public boolean canEquip(SlotContext context) {
        LivingEntity entity = context.entity();

        ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.getItem() instanceof SatchelArmorItem) {
            return false;
        }

        return entity.getCapability(top.theillusivec4.curios.api.CuriosCapability.INVENTORY)
                .map(curios -> curios.getCurios().values().stream()
                        .flatMap(handler -> {
                            var stacks = handler.getStacks();
                            return IntStream.range(0, stacks.getSlots())
                                    .mapToObj(stacks::getStackInSlot);
                        })
                        .filter(s -> !s.isEmpty() && s != this.stack)
                        .noneMatch(s -> s.getItem() instanceof SatchelArmorItem)
                ).orElse(true);
    }

    @Override
    public ItemStack getStack() {
        return this.stack;
    }
}