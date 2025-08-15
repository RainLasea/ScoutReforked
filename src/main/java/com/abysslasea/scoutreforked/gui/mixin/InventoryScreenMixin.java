package com.abysslasea.scoutreforked.gui.mixin;

import com.abysslasea.scoutreforked.util.SatchelManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractContainerScreen<InventoryMenu> {
    private static final ResourceLocation CUSTOM_SLOT_TEXTURE = new ResourceLocation("scoutreforked", "textures/gui/slots.png");

    protected InventoryScreenMixin() {
        super(null, null, null);
    }

    @Inject(method = "renderBg", at = @At("TAIL"))
    private void renderSatchelSlotBackground(GuiGraphics graphics, float partialTick, int mouseX, int mouseY, CallbackInfo ci) {
        try {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player == null) return;

            IItemHandler handler = getSatchelHandlerFromPlayer(minecraft.player);
            if (handler == null) return;

            int guiLeft = this.leftPos;
            int guiTop = this.topPos;

            int startX = guiLeft;
            int startY = guiTop + imageHeight;

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, CUSTOM_SLOT_TEXTURE);

            graphics.blit(CUSTOM_SLOT_TEXTURE, startX, startY, 0, 32, 176, 28);
        } catch (Exception e) {
        }
    }

    @Nullable
    private IItemHandler getSatchelHandlerFromPlayer(Player player) {
        try {
            if (player.isCreative()) {
                return null;
            }

            return SatchelManager.getSatchelHandler(player);
        } catch (Exception e) {
            return null;
        }
    }
}