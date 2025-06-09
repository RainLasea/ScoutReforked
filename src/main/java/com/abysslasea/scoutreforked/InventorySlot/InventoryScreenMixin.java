package com.abysslasea.scoutreforked.InventorySlot;

import com.abysslasea.scoutreforked.item.SatchelArmorItem;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractContainerScreen<InventoryMenu> {
    private static final ResourceLocation CUSTOM_SLOT_TEXTURE = new ResourceLocation("scoutreforked", "textures/gui/slots.png");

    protected InventoryScreenMixin() {
        super(null, null, null);
    }

    @Inject(method = "renderBg", at = @At("TAIL"))
    private void renderSatchelSlotBackground(GuiGraphics graphics, float partialTick, int mouseX, int mouseY, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;

        ItemStack chestStack = minecraft.player.getItemBySlot(EquipmentSlot.CHEST);
        if (!(chestStack.getItem() instanceof SatchelArmorItem)) return;

        LazyOptional<IItemHandler> handlerOpt = chestStack.getCapability(ForgeCapabilities.ITEM_HANDLER);
        if (!handlerOpt.isPresent()) return;

        IItemHandler handler = handlerOpt.orElse(null);
        if (handler == null) return;

        int guiLeft = this.leftPos;
        int guiTop = this.topPos;

        int startX = guiLeft;
        int startY = guiTop + this.imageHeight;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, CUSTOM_SLOT_TEXTURE);

        // 一次性绘制9个格子的背景图
        graphics.blit(CUSTOM_SLOT_TEXTURE, startX, startY, 0, 32, 176, 28);
    }

    // 在渲染尾部绘制物品和装饰，保持物品可见且工具提示在最上层
    @Inject(method = "render", at = @At("TAIL"))
    private void renderSatchelSlotItems(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;

        ItemStack chestStack = minecraft.player.getItemBySlot(EquipmentSlot.CHEST);
        if (!(chestStack.getItem() instanceof SatchelArmorItem)) return;

        LazyOptional<IItemHandler> handlerOpt = chestStack.getCapability(ForgeCapabilities.ITEM_HANDLER);
        if (!handlerOpt.isPresent()) return;

        IItemHandler handler = handlerOpt.orElse(null);
        if (handler == null) return;

        int guiLeft = this.leftPos;
        int guiTop = this.topPos;
        int slotSize = 18;

        int slotCount = handler.getSlots();
        int cols = 9;

        int startX = guiLeft;
        int startY = guiTop + this.imageHeight;

        for (int i = 0; i < slotCount && i < cols; i++) {
            int x = startX + i * slotSize;
            int y = startY;

            ItemStack stack = handler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                graphics.renderItem(stack, x + 1, y + 1);
                graphics.renderItemDecorations(this.font, stack, x + 1, y + 1);
            }
        }
    }
}