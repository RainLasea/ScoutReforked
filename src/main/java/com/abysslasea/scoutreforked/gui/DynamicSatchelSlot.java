package com.abysslasea.scoutreforked.gui;

import com.abysslasea.scoutreforked.util.SatchelManager;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class DynamicSatchelSlot extends SlotItemHandler {
    private final Player player;
    private final int slotIndex;
    private final IItemHandler fallbackHandler;

    public DynamicSatchelSlot(IItemHandler handler, Player player, int index, int x, int y) {
        super(handler, index, x, y);
        this.player = player;
        this.slotIndex = index;
        this.fallbackHandler = handler;
    }

    @Override
    public boolean isActive() {
        try {
            boolean hasEquipped = SatchelManager.hasAnySatchel(player);
            if (hasEquipped) {
                IItemHandler handler = SatchelManager.getSatchelHandler(player);
                return handler != null && slotIndex < handler.getSlots();
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public IItemHandler getItemHandler() {
        try {
            IItemHandler handler = SatchelManager.getSatchelHandler(player);
            return handler != null ? handler : fallbackHandler;
        } catch (Exception e) {
            return fallbackHandler;
        }
    }

    @Override
    public boolean hasItem() {
        if (!isActive()) return false;
        try {
            IItemHandler handler = getItemHandler();
            return handler != null && slotIndex < handler.getSlots() && !handler.getStackInSlot(slotIndex).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void setChanged() {
        if (isActive()) {
            try {
                super.setChanged();
                if (player.containerMenu != null) {
                    player.containerMenu.broadcastChanges();
                }
            } catch (Exception e) {
            }
        }
    }
}