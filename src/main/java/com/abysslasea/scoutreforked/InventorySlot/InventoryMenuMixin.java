package com.abysslasea.scoutreforked.InventorySlot;

import com.abysslasea.scoutreforked.DynamicSatchelSlot;
import com.abysslasea.scoutreforked.item.SatchelArmorItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(InventoryMenu.class)
public abstract class InventoryMenuMixin extends AbstractContainerMenu {

    private Player currentPlayer;
    private int satchelSlotsStartIndex = -1;

    private static final Map<Player, Integer> playerSatchelSlotStartIndexMap = new ConcurrentHashMap<>();

    protected InventoryMenuMixin(MenuType<?> type, int id) {
        super(type, id);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/entity/player/Inventory;ZLnet/minecraft/world/entity/player/Player;)V",
            at = @At("TAIL"))
    private void addSatchelSlots(Inventory playerInventory, boolean active, Player player, CallbackInfo ci) {
        this.currentPlayer = player;

        ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
        IItemHandler satchelHandler = SatchelArmorItem.getItemHandler(chestStack);

        if (satchelHandler == null) {
            satchelHandler = new ItemStackHandler(9);
        }

        int baseX = 8;
        int baseY = 171;
        int spacing = 18;
        int slots = 9;

        satchelSlotsStartIndex = this.slots.size();
        playerSatchelSlotStartIndexMap.put(player, satchelSlotsStartIndex);

        for (int i = 0; i < slots; i++) {
            int x = baseX + i * spacing;
            int y = baseY;
            this.addSlot(new DynamicSatchelSlot(satchelHandler, player, i, x, y));
        }
    }

    private static Integer getSatchelSlotsStartIndex(Player player) {
        return playerSatchelSlotStartIndexMap.get(player);
    }

    private static void removePlayer(Player player) {
        playerSatchelSlotStartIndexMap.remove(player);
    }
}

