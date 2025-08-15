package com.abysslasea.scoutreforked;

import com.abysslasea.scoutreforked.event.SatchelEventHandler;
import com.abysslasea.scoutreforked.integration.Curio.CuriosIntegration;
import com.abysslasea.scoutreforked.item.ModItems;
import com.abysslasea.scoutreforked.registry.ModCreativeModTabs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

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
        MinecraftForge.EVENT_BUS.register(SatchelEventHandler.class);

        try {
            boolean curiosLoaded = ModList.get().isLoaded("curios");

            if (curiosLoaded) {
                CuriosIntegration.init();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
    }

    private void clientSetup(final FMLClientSetupEvent evt) {
        evt.enqueueWork(() -> {
            try {
                boolean curiosLoaded = ModList.get().isLoaded("curios");

                if (curiosLoaded) {
                    EntityModelSet modelSet = Minecraft.getInstance().getEntityModels();
                    CuriosIntegration.clientInit(modelSet);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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