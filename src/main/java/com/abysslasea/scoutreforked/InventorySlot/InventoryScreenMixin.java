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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

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

        IItemHandler handler = getSatchelHandlerFromPlayer(minecraft.player);
        if (handler == null) return;

        int guiLeft = this.leftPos;
        int guiTop = this.topPos;

        int slotCount = handler.getSlots();
        int cols = 3;
        int rows = (slotCount + cols - 1) / cols;

        int startX = guiLeft;
        int startY = guiTop + imageHeight;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, CUSTOM_SLOT_TEXTURE);

        graphics.blit(CUSTOM_SLOT_TEXTURE, startX, startY, 0, 32, 176, 28);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;renderTooltip(Lnet/minecraft/client/gui/GuiGraphics;II)V"))
    private void renderSatchelItems(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;

        IItemHandler handler = getSatchelHandlerFromPlayer(minecraft.player);
        if (handler == null) return;

        int guiLeft = this.leftPos;
        int guiTop = this.topPos;
        int slotSize = 18;

        int slotCount = handler.getSlots();
        int cols = 3;

        int startX = guiLeft;
        int startY = guiTop + imageHeight;
    }

    @Nullable
    private IItemHandler getSatchelHandlerFromPlayer(Player player) {
        ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chestStack.getItem() instanceof SatchelArmorItem) {
            return SatchelArmorItem.getItemHandler(chestStack);
        }

        LazyOptional<ICuriosItemHandler> optional = player.getCapability(CuriosCapability.INVENTORY);
        if (!optional.isPresent()) return null;

        ICuriosItemHandler curiosHandler = optional.orElseThrow(() -> new IllegalStateException("Curios capability missing"));
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