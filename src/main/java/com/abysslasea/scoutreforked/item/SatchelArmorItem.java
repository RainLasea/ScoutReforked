package com.abysslasea.scoutreforked.item;

import com.abysslasea.scoutreforked.Curio.SatchelCurioWrapperItem;
import com.abysslasea.scoutreforked.armor.SatchelArmorRenderer;
import net.minecraft.client.model.HumanoidModel;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.function.Consumer;

public class SatchelArmorItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public SatchelArmorItem(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, state -> PlayState.STOP));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private SatchelArmorRenderer renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                if (renderer == null) {
                    renderer = new SatchelArmorRenderer();
                }
                renderer.prepForRender(entity, stack, slot, original);
                return renderer;
            }
        });
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilitySerializable<CompoundTag>() {

            private final SatchelItemHandler itemHandler = new SatchelItemHandler(stack);
            private final LazyOptional<IItemHandler> itemOpt = LazyOptional.of(() -> itemHandler);
            private final LazyOptional<ICurio> curioOpt = LazyOptional.of(() -> new SatchelCurioWrapperItem(stack));

            @Override
            public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
                if (cap == ForgeCapabilities.ITEM_HANDLER)
                    return itemOpt.cast();
                if (cap == CuriosCapability.ITEM)
                    return curioOpt.cast();
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
                if (nbt.contains("Inventory"))
                    itemHandler.deserializeNBT(nbt.getCompound("Inventory"));
            }
        };
    }

    @Nullable
    public static IItemHandler getItemHandler(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .orElse(null);
    }

    public ICurio getCurio(ItemStack stack) {
        return new SatchelCurioWrapperItem(stack);
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