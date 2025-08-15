package com.abysslasea.scoutreforked.util;

import com.abysslasea.scoutreforked.item.SatchelArmorItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

public class SatchelManager {

    private static boolean curiosLoaded = false;
    private static boolean curiosInitialized = false;

    private static Class<?> curiosCapabilityClass;
    private static Object curiosInventoryCapability;
    private static Method getCapabilityMethod;
    private static Method getCuriosMethod;
    private static Method getStacksMethod;

    static {
        initCurios();
    }

    private static void initCurios() {
        try {
            curiosLoaded = ModList.get().isLoaded("curios");
            if (!curiosLoaded) return;

            curiosCapabilityClass = Class.forName("top.theillusivec4.curios.api.CuriosCapability");
            curiosInventoryCapability = curiosCapabilityClass.getField("INVENTORY").get(null);

            getCapabilityMethod = net.minecraft.world.entity.LivingEntity.class.getMethod("getCapability",
                    Class.forName("net.minecraftforge.common.capabilities.Capability"),
                    Class.forName("net.minecraft.core.Direction"));

            curiosInitialized = true;
        } catch (Exception e) {
            curiosLoaded = false;
            curiosInitialized = false;
        }
    }

    public static boolean isCuriosAvailable() {
        return curiosLoaded && curiosInitialized;
    }

    @Nullable
    public static IItemHandler getSatchelHandler(Player player) {
        if (player == null) return null;

        try {
            IItemHandler chestHandler = getChestSatchelHandler(player);
            if (chestHandler != null) return chestHandler;

            return getCurioSatchelHandler(player);
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    public static IItemHandler getChestSatchelHandler(Player player) {
        if (player == null) return null;

        try {
            ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
            if (chestStack.getItem() instanceof SatchelArmorItem) {
                return SatchelArmorItem.getItemHandler(chestStack);
            }
        } catch (Exception e) {
        }
        return null;
    }

    @Nullable
    public static IItemHandler getCurioSatchelHandler(Player player) {
        if (!isCuriosAvailable() || player == null) return null;

        try {
            Object curiosHandler = getCuriosCapability(player);
            if (curiosHandler == null) return null;

            Map<String, Object> curios = getCurios(curiosHandler);
            if (curios == null || curios.isEmpty()) return null;

            return curios.values().stream()
                    .map(SatchelManager::getStacksHandler)
                    .filter(Objects::nonNull)
                    .flatMap(handler -> IntStream.range(0, handler.getSlots())
                            .mapToObj(handler::getStackInSlot))
                    .filter(stack -> !stack.isEmpty() && stack.getItem() instanceof SatchelArmorItem)
                    .map(SatchelArmorItem::getItemHandler)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean hasChestSatchel(Player player) {
        return getChestSatchelHandler(player) != null;
    }

    public static boolean hasCurioSatchel(Player player) {
        return getCurioSatchelHandler(player) != null;
    }

    public static boolean hasAnySatchel(Player player) {
        return hasChestSatchel(player) || hasCurioSatchel(player);
    }

    @Nullable
    public static ItemStack findCurioSatchelStack(Player player) {
        if (!isCuriosAvailable() || player == null) return null;

        try {
            Object curiosHandler = getCuriosCapability(player);
            if (curiosHandler == null) return null;

            Map<String, Object> curios = getCurios(curiosHandler);
            if (curios == null || curios.isEmpty()) return null;

            return curios.values().stream()
                    .map(SatchelManager::getStacksHandler)
                    .filter(Objects::nonNull)
                    .flatMap(handler -> IntStream.range(0, handler.getSlots())
                            .mapToObj(handler::getStackInSlot))
                    .filter(stack -> !stack.isEmpty() && stack.getItem() instanceof SatchelArmorItem)
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    public static void handleChestSatchelEquip(Player player, ItemStack newSatchel) {
        if (player == null || !(newSatchel.getItem() instanceof SatchelArmorItem)) return;

        try {
            if (hasCurioSatchel(player)) {
                ItemStack curioSatchel = findCurioSatchelStack(player);
                if (curioSatchel != null) {
                    dropAndClearSatchelContents(player, curioSatchel);
                    removeCurioSatchel(player);

                    if (!player.getInventory().add(curioSatchel.copy())) {
                        player.drop(curioSatchel.copy(), false);
                    }
                }
            }

            syncPlayerContainer(player);
        } catch (Exception e) {
        }
    }

    public static void handleChestSatchelUnequip(Player player, ItemStack oldSatchel) {
        if (player == null || player.level().isClientSide) return;

        try {
            dropAndClearSatchelContents(player, oldSatchel);
            syncPlayerContainer(player);
        } catch (Exception e) {
        }
    }

    public static void handleCurioSatchelEquip(Player player, ItemStack newSatchel) {
        if (player == null || !(newSatchel.getItem() instanceof SatchelArmorItem)) return;

        try {
            if (hasChestSatchel(player)) {
                ItemStack chestSatchel = player.getItemBySlot(EquipmentSlot.CHEST);
                dropAndClearSatchelContents(player, chestSatchel);
                player.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);

                if (!player.getInventory().add(chestSatchel.copy())) {
                    player.drop(chestSatchel.copy(), false);
                }
            }

            syncPlayerContainer(player);
        } catch (Exception e) {
        }
    }

    public static void handleCurioSatchelUnequip(Player player, ItemStack oldSatchel) {
        if (player == null || player.level().isClientSide) return;

        try {
            dropAndClearSatchelContents(player, oldSatchel);
            syncPlayerContainer(player);
        } catch (Exception e) {
        }
    }

    public static boolean canEquipCurioSatchel(Player player) {
        if (!isCuriosAvailable() || player == null) return false;

        try {
            return !hasCurioSatchel(player);
        } catch (Exception e) {
            return false;
        }
    }

    private static void removeCurioSatchel(Player player) {
        if (!isCuriosAvailable() || player == null) return;

        try {
            Object curiosHandler = getCuriosCapability(player);
            if (curiosHandler == null) return;

            Map<String, Object> curios = getCurios(curiosHandler);
            if (curios == null || curios.isEmpty()) return;

            for (Object slotHandler : curios.values()) {
                IItemHandler stacks = getStacksHandler(slotHandler);
                if (stacks == null) continue;

                for (int i = 0; i < stacks.getSlots(); i++) {
                    ItemStack slotStack = stacks.getStackInSlot(i);
                    if (!slotStack.isEmpty() && slotStack.getItem() instanceof SatchelArmorItem) {
                        stacks.extractItem(i, slotStack.getCount(), false);
                        return;
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private static void dropAndClearSatchelContents(Player player, ItemStack satchelStack) {
        if (player == null || player.level().isClientSide || satchelStack.isEmpty()) return;

        try {
            IItemHandler handler = SatchelArmorItem.getItemHandler(satchelStack);
            if (handler != null) {
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack itemStack = handler.extractItem(i, 64, false);
                    if (!itemStack.isEmpty()) {
                        player.drop(itemStack, false);
                    }
                }
            }

            SatchelArmorItem.clearSatchelContents(satchelStack);

        } catch (Exception e) {
        }
    }

    public static void syncPlayerContainer(Player player) {
        if (player == null || player.level().isClientSide) return;

        try {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.getServer().execute(() -> {
                    try {
                        if (serverPlayer.containerMenu != null) {
                            serverPlayer.containerMenu.broadcastChanges();
                            serverPlayer.containerMenu.broadcastFullState();
                        }
                    } catch (Exception e) {
                    }
                });
            }
        } catch (Exception e) {
        }
    }

    @Nullable
    private static Object getCuriosCapability(Player player) {
        if (!isCuriosAvailable() || player == null) return null;

        try {
            Object lazyOptional = getCapabilityMethod.invoke(player, curiosInventoryCapability, null);
            Method resolveMethod = lazyOptional.getClass().getMethod("resolve");
            java.util.Optional<?> optional = (java.util.Optional<?>) resolveMethod.invoke(lazyOptional);
            return optional.orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private static Map<String, Object> getCurios(Object curiosHandler) {
        if (!isCuriosAvailable() || curiosHandler == null) return null;

        try {
            if (getCuriosMethod == null) {
                getCuriosMethod = curiosHandler.getClass().getMethod("getCurios");
            }
            Object curiosMap = getCuriosMethod.invoke(curiosHandler);
            return (curiosMap instanceof Map) ? (Map<String, Object>) curiosMap : null;
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    private static IItemHandler getStacksHandler(Object slotHandler) {
        if (!isCuriosAvailable() || slotHandler == null) return null;

        try {
            if (getStacksMethod == null) {
                getStacksMethod = slotHandler.getClass().getMethod("getStacks");
            }
            Object stacks = getStacksMethod.invoke(slotHandler);
            return (stacks instanceof IItemHandler) ? (IItemHandler) stacks : null;
        } catch (Exception e) {
            return null;
        }
    }
}