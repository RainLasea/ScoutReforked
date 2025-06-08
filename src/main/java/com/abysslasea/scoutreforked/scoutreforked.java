package com.abysslasea.scoutreforked;

import com.abysslasea.scoutreforked.Curio.SatchelCurioRenderer;
import com.abysslasea.scoutreforked.item.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@Mod(scoutreforked.MODID)
public class scoutreforked {
    public static final String MODID = "scoutreforked";

    @SuppressWarnings("removal")
    public scoutreforked() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        ModItems.register(modEventBus);
        ModCreativeModTabs.register(modEventBus);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::addCreative);
    }

    private void clientSetup(final FMLClientSetupEvent evt) {
        CuriosRendererRegistry.register(ModItems.SATCHEL.get(), () -> new SatchelCurioRenderer());
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == ModCreativeModTabs.SCOUT_TAB.getKey()) {
            event.accept(ModItems.SATCHEL.get());
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // 服务器启动逻辑
    }
}
