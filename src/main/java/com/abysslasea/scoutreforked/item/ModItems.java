package com.abysslasea.scoutreforked.item;

import com.abysslasea.scoutreforked.armor.SatchelArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "scoutreforked");

    public static final RegistryObject<ArmorItem> SATCHEL = ITEMS.register("satchel",
            () -> new SatchelArmorItem(SatchelArmorMaterial.SATCHEL, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
