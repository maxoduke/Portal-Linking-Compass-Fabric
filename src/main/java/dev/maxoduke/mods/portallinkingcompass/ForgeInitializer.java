package dev.maxoduke.mods.portallinkingcompass;

import dev.maxoduke.mods.portallinkingcompass.item.PortalLinkingCompassItem;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("unused")
@Mod(PortalLinkingCompass.MOD_ID)
public class ForgeInitializer
{
    private static final DeferredRegister<Item> ITEMS;
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS;
    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES;

    static
    {
        ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, PortalLinkingCompass.MOD_ID);
        SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, PortalLinkingCompass.MOD_ID);
        DATA_COMPONENT_TYPES = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE.key(), PortalLinkingCompass.MOD_ID);

        ITEMS.register(PortalLinkingCompass.ITEM_NAME, () -> PortalLinkingCompass.ITEM);
        SOUND_EVENTS.register(PortalLinkingCompass.COMPASS_LOCKS_SOUND_NAME, () -> PortalLinkingCompass.COMPASS_LOCKS_SOUND_EVENT);
        DATA_COMPONENT_TYPES.register(PortalLinkingCompass.LINKED_PORTAL_TRACKER_COMPONENT_NAME, () -> PortalLinkingCompass.LINKED_PORTAL_TRACKER_COMPONENT);
    }

    public ForgeInitializer()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ITEMS.register(modEventBus);
        SOUND_EVENTS.register(modEventBus);
        DATA_COMPONENT_TYPES.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES)
            event.accept(PortalLinkingCompass.ITEM);
    }

    @Mod.EventBusSubscriber(modid = PortalLinkingCompass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            ItemProperties.register(
                PortalLinkingCompass.ITEM,
                ResourceLocation.withDefaultNamespace("angle"),
                new CompassItemPropertyFunction(PortalLinkingCompassItem::pointToTarget)
            );
        }
    }
}
