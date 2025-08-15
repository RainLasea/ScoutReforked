package com.abysslasea.scoutreforked.integration.Curio;

import com.abysslasea.scoutreforked.integration.Curio.SatchelCurioRenderer;
import com.abysslasea.scoutreforked.item.ModItems;
import com.abysslasea.scoutreforked.item.SatchelArmorItem;
import com.abysslasea.scoutreforked.util.SatchelManager;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class CuriosIntegration {

    private static boolean curiosLoaded = false;
    private static Capability<?> CURIO_ITEM_CAPABILITY;

    private static Class<?> iCurioClass;
    private static Class<?> slotContextClass;
    private static Method entityMethod;
    private static Method canEquipMethod;
    private static Method getStackMethod;
    private static Method curioTickMethod;
    private static Method onEquipMethod;
    private static Method onUnequipMethod;

    public static void init() {
        try {
            curiosLoaded = ModList.get().isLoaded("curios");
            if (!curiosLoaded) return;

            MinecraftForge.EVENT_BUS.register(new CapabilityHandler());

            Class<?> curiosCapabilityClass = Class.forName("top.theillusivec4.curios.api.CuriosCapability");
            CURIO_ITEM_CAPABILITY = (Capability<?>) curiosCapabilityClass.getField("ITEM").get(null);

            iCurioClass = Class.forName("top.theillusivec4.curios.api.type.capability.ICurio");
            slotContextClass = Class.forName("top.theillusivec4.curios.api.SlotContext");

            entityMethod = slotContextClass.getMethod("entity");
            canEquipMethod = iCurioClass.getMethod("canEquip", slotContextClass);
            getStackMethod = iCurioClass.getMethod("getStack");
            curioTickMethod = iCurioClass.getMethod("curioTick", slotContextClass);
            onEquipMethod = iCurioClass.getMethod("onEquip", slotContextClass, ItemStack.class);
            onUnequipMethod = iCurioClass.getMethod("onUnequip", slotContextClass, ItemStack.class);

        } catch (Exception e) {
            curiosLoaded = false;
        }
    }

    public static void clientInit(EntityModelSet modelSet) {
        if (!curiosLoaded) return;

        try {
            Class<?> curiosRendererRegistryClass = Class.forName("top.theillusivec4.curios.api.client.CuriosRendererRegistry");
            Method registerMethod = null;

            try {
                Class<?> itemClass = Class.forName("net.minecraft.world.item.Item");
                Class<?> supplierClass = Class.forName("java.util.function.Supplier");
                registerMethod = curiosRendererRegistryClass.getMethod("register", itemClass, supplierClass);
            } catch (NoSuchMethodException e1) {
                try {
                    Class<?> itemLikeClass = Class.forName("net.minecraft.world.level.ItemLike");
                    Class<?> supplierClass = Class.forName("java.util.function.Supplier");
                    registerMethod = curiosRendererRegistryClass.getMethod("register", itemLikeClass, supplierClass);
                } catch (NoSuchMethodException e2) {
                    Class<?> supplierClass = Class.forName("java.util.function.Supplier");
                    registerMethod = curiosRendererRegistryClass.getMethod("register", Object.class, supplierClass);
                }
            }

            Object rendererSupplier = new java.util.function.Supplier<Object>() {
                @Override
                public Object get() {
                    return SatchelCurioRenderer.createCurioRenderer(modelSet);
                }
            };

            registerMethod.invoke(null, ModItems.SATCHEL.get(), rendererSupplier);

        } catch (Exception e) {
        }
    }

    public static boolean isCuriosLoaded() {
        return curiosLoaded;
    }

    public static class CapabilityHandler {
        @SubscribeEvent
        public void attachCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
            ItemStack stack = event.getObject();

            if (!(stack.getItem() instanceof SatchelArmorItem) || !curiosLoaded || CURIO_ITEM_CAPABILITY == null) {
                return;
            }

            event.addCapability(
                    new net.minecraft.resources.ResourceLocation("scoutreforked", "curio_item"),
                    new SatchelCurioCapabilityProvider(stack)
            );
        }
    }

    private static class SatchelCurioCapabilityProvider implements ICapabilityProvider {
        private final LazyOptional<Object> curioInstance;

        public SatchelCurioCapabilityProvider(ItemStack stack) {
            this.curioInstance = LazyOptional.of(() -> createCurioProxy(stack));
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == CURIO_ITEM_CAPABILITY) {
                return curioInstance.cast();
            }
            return LazyOptional.empty();
        }

        private Object createCurioProxy(ItemStack stack) {
            if (!curiosLoaded || iCurioClass == null) return null;

            try {
                SatchelCurioWrapper wrapper = new SatchelCurioWrapper(stack);
                return Proxy.newProxyInstance(
                        iCurioClass.getClassLoader(),
                        new Class<?>[]{iCurioClass},
                        wrapper
                );
            } catch (Exception e) {
                return null;
            }
        }
    }

    private static class SatchelCurioWrapper implements InvocationHandler {
        private final ItemStack stack;

        public SatchelCurioWrapper(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();

            try {
                switch (methodName) {
                    case "canEquip":
                        if (args.length == 1) {
                            return handleCanEquip(args[0]);
                        }
                        return true;

                    case "getStack":
                        return stack;

                    case "curioTick":
                        return null;

                    case "onEquip":
                        if (args.length == 2) {
                            handleOnEquip(args[0], (ItemStack) args[1]);
                        }
                        return null;

                    case "onUnequip":
                        if (args.length == 2) {
                            handleOnUnequip(args[0], (ItemStack) args[1]);
                        }
                        return null;

                    case "getSlotsTooltip":
                    case "getAttributesTooltip":
                        return java.util.Collections.emptyList();

                    case "getAttributeModifiers":
                        return com.google.common.collect.HashMultimap.create();

                    case "playEquipSound":
                    case "playRightClickEquipSound":
                    case "getEquipSound":
                    case "readSyncData":
                    case "writeSyncData":
                    case "getEquipAnimation":
                    case "curioAnimate":
                    case "curioBreak":
                    case "onCraftedBy":
                        return null;

                    case "canSync":
                    case "canRightClickEquip":
                    case "canUnequip":
                    case "showAttributesTooltip":
                    case "hasCurioCapability":
                    case "canWalkOnPowderedSnow":
                    case "makesPiglinsNeutral":
                    case "isEnderMask":
                    case "canEquipFromUse":
                    case "isStackable":
                        return true;

                    case "getFortuneLevel":
                    case "getLootingLevel":
                    case "getMaxStackSize":
                        return 0;

                    case "getDropRule":
                    case "getRenderTag":
                        return null;
                }

            } catch (Exception e) {
            }

            return getDefaultReturnValue(method.getReturnType());
        }

        private boolean handleCanEquip(Object context) {
            try {
                if (entityMethod == null) return true;

                Object entityObj = entityMethod.invoke(context);
                if (!(entityObj instanceof Player player)) return true;

                return SatchelManager.canEquipCurioSatchel(player);

            } catch (Exception e) {
                return false;
            }
        }

        private void handleOnEquip(Object context, ItemStack prevStack) {
            try {
                if (entityMethod == null) return;

                Object entityObj = entityMethod.invoke(context);
                if (!(entityObj instanceof Player player)) return;

                player.level().getServer().execute(() -> {
                    SatchelManager.handleCurioSatchelEquip(player, stack);
                });

            } catch (Exception e) {
            }
        }

        private void handleOnUnequip(Object context, ItemStack newStack) {
            try {
                if (entityMethod == null) return;

                Object entityObj = entityMethod.invoke(context);
                if (!(entityObj instanceof Player player)) return;

                if (!player.level().isClientSide) {
                    SatchelManager.handleCurioSatchelUnequip(player, stack);
                }

            } catch (Exception e) {
            }
        }

        private Object getDefaultReturnValue(Class<?> returnType) {
            if (returnType == boolean.class) return false;
            if (returnType == int.class) return 0;
            if (returnType == float.class) return 0.0f;
            if (returnType == double.class) return 0.0d;
            if (returnType == void.class) return null;
            if (returnType == ItemStack.class) return stack;

            try {
                if (returnType.getName().contains("Multimap")) {
                    return com.google.common.collect.HashMultimap.create();
                }
                if (returnType.getName().contains("List")) {
                    return java.util.Collections.emptyList();
                }
                if (returnType.getName().contains("Map")) {
                    return java.util.Collections.emptyMap();
                }
                if (returnType.getName().contains("Set")) {
                    return java.util.Collections.emptySet();
                }
                if (returnType.getName().contains("Optional")) {
                    return java.util.Optional.empty();
                }
            } catch (Exception e) {
            }

            return null;
        }
    }
}