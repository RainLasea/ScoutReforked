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
import net.minecraftforge.fml.ModList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SatchelCurioCapabilityHandler {

    @SubscribeEvent
    public void attachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        if (!ModList.get().isLoaded("curios")) return;

        ItemStack stack = event.getObject();
        if (stack.getItem() instanceof SatchelArmorItem satchelItem) {
            event.addCapability(
                    new ResourceLocation("scoutreforked", "curio_capability"),
                    new ICapabilityProvider() {

                        private final LazyOptional<Object> curioOpt;

                        {
                            if (ModList.get().isLoaded("curios")) {
                                LazyOptional<Object> tempOpt;
                                try {
                                    Class<?> wrapperClass = Class.forName("com.abysslasea.scoutreforked.Curio.SatchelCurioWrapperItem");
                                    Object instance = wrapperClass.getConstructor(ItemStack.class).newInstance(stack);
                                    tempOpt = instance != null ? LazyOptional.of(() -> instance) : LazyOptional.empty();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    tempOpt = LazyOptional.empty();
                                }
                                curioOpt = tempOpt;
                            } else {
                                curioOpt = LazyOptional.empty();
                            }
                        }

                        @Override
                        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                            try {
                                Class<?> curiosCapClass = Class.forName("top.theillusivec4.curios.api.CuriosCapability");
                                Object itemCapObj = curiosCapClass.getField("ITEM").get(null);
                                @SuppressWarnings("unchecked")
                                Capability<?> itemCap = (Capability<?>) itemCapObj;

                                if (cap == itemCap) {
                                    return curioOpt.cast();
                                }
                            } catch (Throwable e) {
                            }
                            return LazyOptional.empty();
                        }
                    }
            );
        }
    }
}