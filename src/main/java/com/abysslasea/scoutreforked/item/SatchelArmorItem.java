package com.abysslasea.scoutreforked.item;

import com.abysslasea.scoutreforked.armor.SatchelArmorRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SatchelArmorItem extends ArmorItem {

    public SatchelArmorItem(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @Nullable HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                try {
                    EntityModelSet modelSet = Minecraft.getInstance().getEntityModels();
                    SatchelArmorRenderer renderer = new SatchelArmorRenderer(modelSet);

                    if (original != null) {
                        try {
                            @SuppressWarnings("unchecked")
                            HumanoidModel<LivingEntity> originalTyped = (HumanoidModel<LivingEntity>) original;
                            originalTyped.copyPropertiesTo(renderer);
                        } catch (ClassCastException e) {
                            copyBasicProperties(original, renderer);
                        } catch (Exception e) {
                        }
                    }

                    return renderer;
                } catch (Exception e) {
                    return null;
                }
            }

            private void copyBasicProperties(HumanoidModel<?> from, HumanoidModel<LivingEntity> to) {
                try {
                    to.attackTime = from.attackTime;
                    to.riding = from.riding;
                    to.young = from.young;
                    to.crouching = from.crouching;
                    to.leftArmPose = from.leftArmPose;
                    to.rightArmPose = from.rightArmPose;
                } catch (Exception e) {
                }
            }
        });
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilitySerializable<CompoundTag>() {
            private final SatchelItemHandler itemHandler = new SatchelItemHandler(stack);
            private final LazyOptional<IItemHandler> itemOpt = LazyOptional.of(() -> itemHandler);

            @Override
            public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
                if (cap == ForgeCapabilities.ITEM_HANDLER) {
                    return itemOpt.cast();
                }
                return LazyOptional.empty();
            }

            @Override
            public CompoundTag serializeNBT() {
                try {
                    CompoundTag tag = new CompoundTag();
                    tag.put("Inventory", itemHandler.serializeNBT());
                    return tag;
                } catch (Exception e) {
                    return new CompoundTag();
                }
            }

            @Override
            public void deserializeNBT(CompoundTag nbt) {
                try {
                    if (nbt.contains("Inventory")) {
                        itemHandler.deserializeNBT(nbt.getCompound("Inventory"));
                    }
                } catch (Exception e) {
                }
            }
        };
    }

    @Nullable
    public static IItemHandler getItemHandler(ItemStack stack) {
        try {
            return stack.getCapability(ForgeCapabilities.ITEM_HANDLER)
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    public static void clearSatchelContents(ItemStack satchelStack) {
        if (satchelStack.isEmpty() || !(satchelStack.getItem() instanceof SatchelArmorItem)) return;

        try {
            CompoundTag tag = satchelStack.getTag();
            if (tag != null && tag.contains("Inventory")) {
                tag.remove("Inventory");
            }

            IItemHandler handler = getItemHandler(satchelStack);
            if (handler instanceof SatchelItemHandler) {
                ((SatchelItemHandler) handler).clearAll();
            }
        } catch (Exception e) {
        }
    }

    public static class SatchelItemHandler extends ItemStackHandler {
        private final ItemStack stack;

        public SatchelItemHandler(ItemStack stack) {
            super(9);
            this.stack = stack;
            try {
                CompoundTag tag = stack.getOrCreateTag();
                if (tag.contains("Inventory")) {
                    CompoundTag inventoryTag = tag.getCompound("Inventory");
                    if (!inventoryTag.isEmpty()) {
                        deserializeNBT(inventoryTag);
                    }
                }
            } catch (Exception e) {
            }
        }

        @Override
        protected void onContentsChanged(int slot) {
            try {
                super.onContentsChanged(slot);
                CompoundTag tag = stack.getOrCreateTag();
                CompoundTag inventoryTag = serializeNBT();

                boolean isEmpty = true;
                for (int i = 0; i < getSlots(); i++) {
                    if (!getStackInSlot(i).isEmpty()) {
                        isEmpty = false;
                        break;
                    }
                }

                if (isEmpty) {
                    tag.remove("Inventory");
                } else {
                    tag.put("Inventory", inventoryTag);
                }
            } catch (Exception e) {
            }
        }

        public void clearAll() {
            try {
                for (int i = 0; i < getSlots(); i++) {
                    setStackInSlot(i, ItemStack.EMPTY);
                }
            } catch (Exception e) {
            }
        }
    }
}