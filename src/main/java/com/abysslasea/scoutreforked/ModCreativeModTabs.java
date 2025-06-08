package com.abysslasea.scoutreforked;

import com.abysslasea.scoutreforked.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MOD_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, scoutreforked.MODID); // 注意这里改为 ScoutReforked.MOD_ID

    public static final RegistryObject<CreativeModeTab> SCOUT_TAB = CREATIVE_MOD_TABS.register("scoutreforked_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.SATCHEL.get()))
                    .title(Component.translatable("creativetab.scoutreforked_tab"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.SATCHEL.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MOD_TABS.register(eventBus);
    }
}
