package dev.maxoduke.mods.portallinkingcompass;

import dev.maxoduke.mods.portallinkingcompass.item.PortalLinkingCompassItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;

public class FabricInitializer implements ModInitializer, ClientModInitializer
{
    @Override
    public void onInitialize()
    {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register((content) -> content.accept(PortalLinkingCompass.ITEM));

        Registry.register(
            BuiltInRegistries.SOUND_EVENT,
            PortalLinkingCompass.COMPASS_LOCKS_SOUND_RESOURCE,
            PortalLinkingCompass.COMPASS_LOCKS_SOUND_EVENT
        );

        Registry.register(
            BuiltInRegistries.ITEM,
            PortalLinkingCompass.ITEM_RESOURCE,
            PortalLinkingCompass.ITEM
        );

        Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            PortalLinkingCompass.LINKED_PORTAL_TRACKER_COMPONENT_RESOURCE,
            PortalLinkingCompass.LINKED_PORTAL_TRACKER_COMPONENT
        );
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void onInitializeClient()
    {
        ItemProperties.register(
            PortalLinkingCompass.ITEM,
            ResourceLocation.withDefaultNamespace("angle"),
            new CompassItemPropertyFunction(PortalLinkingCompassItem::pointToTarget)
        );
    }
}
