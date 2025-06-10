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
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;

@Mod(ScoutreForked.MODID)
public class ScoutreForked {
    public static final String MODID = "scoutreforked";

    public ScoutreForked(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        ModItems.register(modEventBus);
        ModCreativeModTabs.register(modEventBus);

        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::onCommonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
    }

    private void clientSetup(final FMLClientSetupEvent evt) {
        if (SatchelCurioRenderer.isCuriosLoaded()) {
            CuriosRendererRegistry.register(
                    ModItems.SATCHEL.get(),
                    () -> (ICurioRenderer) SatchelCurioRenderer.getSatchelRenderer().get()
            );
        }
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == ModCreativeModTabs.SCOUT_TAB.getKey()) {
            event.accept(ModItems.SATCHEL.get());
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }
}
