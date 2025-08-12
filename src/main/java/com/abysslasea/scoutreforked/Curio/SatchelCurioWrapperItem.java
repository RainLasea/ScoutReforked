package com.abysslasea.scoutreforked.Curio;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.lang.reflect.Method;

public class SatchelCurioWrapperItem implements ICurio {

    private final ItemStack stack;
    private final Object curiosWrapper;

    public SatchelCurioWrapperItem(ItemStack stack) {
        this.stack = stack;
        this.curiosWrapper = createCuriosWrapper(stack);
    }

    private Object createCuriosWrapper(ItemStack stack) {
        if (!ModList.get().isLoaded("curios")) return null;
        try {
            Class<?> clazz = Class.forName("com.abysslasea.scoutreforked.Curio.internal.SatchelCurioWrapperItemImpl");
            return clazz.getConstructor(ItemStack.class).newInstance(stack);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean canEquip(SlotContext context) {
        if (curiosWrapper == null) return true; // 无Curios时默认允许
        try {
            Method canEquipMethod = curiosWrapper.getClass().getMethod("canEquip", SlotContext.class);
            return (boolean) canEquipMethod.invoke(curiosWrapper, context);
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    @Override
    public ItemStack getStack() {
        return stack;
    }
    @Override
    public void curioTick(SlotContext slotContext) {
        if (curiosWrapper == null) return;
        try {
            Method method = curiosWrapper.getClass().getMethod("curioTick", SlotContext.class);
            method.invoke(curiosWrapper, slotContext);
        } catch (Exception e) {
        }
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack) {
        if (curiosWrapper == null) return;
        try {
            Method method = curiosWrapper.getClass().getMethod("onEquip", SlotContext.class, ItemStack.class);
            method.invoke(curiosWrapper, slotContext, prevStack);
        } catch (Exception e) {
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack) {
        if (curiosWrapper == null) return;
        try {
            Method method = curiosWrapper.getClass().getMethod("onUnequip", SlotContext.class, ItemStack.class);
            method.invoke(curiosWrapper, slotContext, newStack);
        } catch (Exception e) {
        }
    }
}