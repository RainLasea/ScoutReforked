package com.abysslasea.scoutreforked.Curio;

import com.abysslasea.scoutreforked.item.ModItems;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.IItemHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

public class CuriosCompat {

    private static boolean curiosLoaded = false;
    private static Class<?> curiosCapabilityClass;
    private static Field inventoryField;
    private static Class<?> iCuriosItemHandlerClass;
    private static Method getCuriosMethod;
    private static Class<?> iCurioStacksHandlerClass;
    private static Method getStacksMethod;

    public static void init() {
        if (ModList.get().isLoaded("curios")) {
            MinecraftForge.EVENT_BUS.register(new SatchelCurioCapabilityHandler());

            try {
                curiosCapabilityClass = Class.forName("top.theillusivec4.curios.api.CuriosCapability");
                inventoryField = curiosCapabilityClass.getField("INVENTORY");

                iCuriosItemHandlerClass = Class.forName("top.theillusivec4.curios.api.type.capability.ICuriosItemHandler");
                getCuriosMethod = iCuriosItemHandlerClass.getMethod("getCurios");

                iCurioStacksHandlerClass = Class.forName("top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler");
                getStacksMethod = iCurioStacksHandlerClass.getMethod("getStacks");

                curiosLoaded = true;
            } catch (ClassNotFoundException | NoSuchFieldException | NoSuchMethodException e) {
                curiosLoaded = false;
            }
        }
    }

    public static void clientInit(EntityModelSet modelSet) {
        if (ModList.get().isLoaded("curios")) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                registerCurioRenderer(modelSet);
            });
        }
    }

    private static void registerCurioRenderer(EntityModelSet modelSet) {
        try {
            Class<?> registryClass = Class.forName("top.theillusivec4.curios.api.client.CuriosRendererRegistry");
            Method registerMethod = registryClass.getMethod("register", Item.class, java.util.function.Supplier.class);

            registerMethod.invoke(null, ModItems.SATCHEL.get(),
                    (java.util.function.Supplier<?>) () -> SatchelCurioRenderer.createCurioRenderer(modelSet));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isCuriosLoaded() {
        return curiosLoaded;
    }

    public static LazyOptional<?> getCuriosLazyOptional(Player player) {
        if (!curiosLoaded) return LazyOptional.empty();
        try {
            return (LazyOptional<?>) player.getClass()
                    .getMethod("getCapability", Class.class)
                    .invoke(player, inventoryField.get(null));
        } catch (Exception e) {
            return LazyOptional.empty();
        }
    }

    public static Object getCuriosCapability(Player player) {
        LazyOptional<?> optional = getCuriosLazyOptional(player);
        return optional.orElse(null);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getCurios(Object curiosHandler) {
        if (!curiosLoaded || curiosHandler == null) return Collections.emptyMap();
        try {
            return (Map<String, Object>) getCuriosMethod.invoke(curiosHandler);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    public static IItemHandler getStacksHandler(Object curioStacksHandler) {
        if (!curiosLoaded || curioStacksHandler == null) return null;
        try {
            return (IItemHandler) getStacksMethod.invoke(curioStacksHandler);
        } catch (Exception e) {
            return null;
        }
    }
}