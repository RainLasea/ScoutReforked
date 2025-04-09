package com.abysslasea.item;

import com.abysslasea.scoutreforked;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
    DeferredRegister.create(ForgeRegistries.ITEMS, scoutreforked.MODID);

    public static final RegistryObject<Item> SATCHELS = ITEMS.register("satchels",
            () -> new Item(new Item.Properties()));

    public static void rgeister(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
