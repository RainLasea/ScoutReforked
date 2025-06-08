package com.abysslasea.scoutreforked.Curio;

import com.abysslasea.scoutreforked.item.SatchelArmorItem;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = "scoutreforked", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SatchelCurioCapabilityHandler {

    @SubscribeEvent
    public static void attachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        if (stack.getItem() instanceof SatchelArmorItem satchelItem) {
            event.addCapability(
                    new ResourceLocation("scoutreforked", "curio_capability"),
                    new ICapabilityProvider() {
                        private final LazyOptional<ICurio> curio = LazyOptional.of(() -> satchelItem.getCurio(stack));

                        @Override
                        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                            return CuriosCapability.ITEM.orEmpty(cap, curio);
                        }
                    }
            );
        }
    }
}
