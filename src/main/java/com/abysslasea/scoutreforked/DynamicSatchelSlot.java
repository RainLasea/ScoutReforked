package com.abysslasea.scoutreforked;

import com.abysslasea.scoutreforked.Curio.CuriosCompat;
import com.abysslasea.scoutreforked.item.SatchelArmorItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

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

        if (!CuriosCompat.isCuriosLoaded()) return null;

        Object curiosHandler = CuriosCompat.getCuriosCapability(player);
        if (curiosHandler == null) return null;

        Map<String, Object> curiosMap = CuriosCompat.getCurios(curiosHandler);
        if (curiosMap == null) return null;

        return curiosMap.values().stream()
                .map(CuriosCompat::getStacksHandler)
                .filter(Objects::nonNull)
                .flatMap(stacks -> IntStream.range(0, stacks.getSlots())
                        .mapToObj(stacks::getStackInSlot))
                .filter(stack -> !stack.isEmpty() && stack.getItem() instanceof SatchelArmorItem)
                .map(SatchelArmorItem::getItemHandler)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}