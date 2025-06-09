package com.abysslasea.scoutreforked;

import com.abysslasea.scoutreforked.item.SatchelArmorItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

public class DynamicSatchelSlot extends SlotItemHandler {
    private final Player player;
    private final int slotIndex;
    private final IItemHandler fallbackHandler;

    public DynamicSatchelSlot(IItemHandler handler, Player player, int index, int x, int y) {
        super(handler, index, x, y);
        this.player = player;
        this.slotIndex = index;
        this.fallbackHandler = handler; // fallback 用于没有背包时防止崩溃
    }

    @Override
    public boolean isActive() {
        return getSatchelHandlerFromPlayer(player) != null;
    }

    @Override
    public IItemHandler getItemHandler() {
        IItemHandler handler = getSatchelHandlerFromPlayer(player);
        return handler != null ? handler : fallbackHandler;
    }

    @Nullable
    private IItemHandler getSatchelHandlerFromPlayer(Player player) {
        ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chestStack.getItem() instanceof SatchelArmorItem) {
            return SatchelArmorItem.getItemHandler(chestStack);
        }

        LazyOptional<ICuriosItemHandler> optional = player.getCapability(CuriosCapability.INVENTORY);
        if (!optional.isPresent()) return null;

        ICuriosItemHandler curiosHandler = optional.orElse(null);
        if (curiosHandler == null) return null;

        Map<String, ICurioStacksHandler> curios = curiosHandler.getCurios();
        if (curios == null) return null;

        return curios.values().stream()
                .flatMap(h -> {
                    IItemHandler stacks = h.getStacks();
                    return IntStream.range(0, stacks.getSlots())
                            .mapToObj(stacks::getStackInSlot);
                })
                .filter(stack -> !stack.isEmpty() && stack.getItem() instanceof SatchelArmorItem)
                .map(SatchelArmorItem::getItemHandler)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}