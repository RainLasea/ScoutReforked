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
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
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
                EntityModelSet modelSet = Minecraft.getInstance().getEntityModels();
                SatchelArmorRenderer renderer = new SatchelArmorRenderer(modelSet);

                // 安全地复制属性
                if (original != null) {
                    try {
                        // 使用类型转换来解决泛型问题
                        @SuppressWarnings("unchecked")
                        HumanoidModel<LivingEntity> originalTyped = (HumanoidModel<LivingEntity>) original;
                        originalTyped.copyPropertiesTo(renderer);
                    } catch (ClassCastException e) {
                        // 如果类型转换失败，手动复制关键属性
                        copyBasicProperties(original, renderer);
                    } catch (Exception e) {
                        // 其他异常的静默处理
                    }
                }

                return renderer;
            }

            // 手动复制基础属性的方法
            private void copyBasicProperties(HumanoidModel<?> from, HumanoidModel<LivingEntity> to) {
                try {
                    to.attackTime = from.attackTime;
                    to.riding = from.riding;
                    to.young = from.young;
                    to.crouching = from.crouching;
                    to.leftArmPose = from.leftArmPose;
                    to.rightArmPose = from.rightArmPose;
                } catch (Exception e) {
                    // 静默处理任何属性复制错误
                }
            }
        });
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilitySerializable<CompoundTag>() {
            private final SatchelItemHandler itemHandler = new SatchelItemHandler(stack);
            private final LazyOptional<IItemHandler> itemOpt = LazyOptional.of(() -> itemHandler);
            private final LazyOptional<Object> curioOpt = createCurioOptional(stack);

            @Override
            public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
                if (cap == ForgeCapabilities.ITEM_HANDLER) {
                    return itemOpt.cast();
                }
                if (ModList.get().isLoaded("curios")) {
                    try {
                        Class<?> curiosCapClass = Class.forName("top.theillusivec4.curios.api.CuriosCapability");
                        Object curiosCap = curiosCapClass.getField("ITEM").get(null);
                        if (cap == curiosCap) {
                            return curioOpt.cast();
                        }
                    } catch (Exception ignored) {
                    }
                }
                return LazyOptional.empty();
            }

            @Override
            public CompoundTag serializeNBT() {
                CompoundTag tag = new CompoundTag();
                tag.put("Inventory", itemHandler.serializeNBT());
                return tag;
            }

            @Override
            public void deserializeNBT(CompoundTag nbt) {
                if (nbt.contains("Inventory")) {
                    itemHandler.deserializeNBT(nbt.getCompound("Inventory"));
                }
            }

            private LazyOptional<Object> createCurioOptional(ItemStack stack) {
                if (ModList.get().isLoaded("curios")) {
                    try {
                        Class<?> wrapperClass = Class.forName("com.abysslasea.scoutreforked.Curio.SatchelCurioWrapperItem");
                        Object wrapperInstance = wrapperClass.getConstructor(ItemStack.class).newInstance(stack);
                        if (wrapperInstance != null) {
                            return LazyOptional.of(() -> wrapperInstance);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return LazyOptional.empty();
            }
        };
    }

    @Nullable
    public static IItemHandler getItemHandler(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .orElse(null);
    }

    public static class SatchelItemHandler extends ItemStackHandler {
        private final ItemStack stack;

        public SatchelItemHandler(ItemStack stack) {
            super(9);
            this.stack = stack;
            CompoundTag tag = stack.getOrCreateTag();
            if (tag.contains("Inventory")) {
                deserializeNBT(tag.getCompound("Inventory"));
            }
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            CompoundTag tag = stack.getOrCreateTag();
            tag.put("Inventory", serializeNBT());
        }
    }
}