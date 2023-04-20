package me.maxoduke.mod.portallinkingcompass.module;

import me.maxoduke.mod.portallinkingcompass.PortalLinkingCompassMod;
import me.maxoduke.mod.portallinkingcompass.item.PortalLinkingCompassItem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public class PortalLinkingCompass
{
    public static final String ITEM_NAME = "portal_linking_compass";
    public static final Item ITEM = new PortalLinkingCompassItem(new FabricItemSettings());
    public static final ResourceLocation SOUND_ID = new ResourceLocation(PortalLinkingCompassMod.MOD_ID, "item.portal_linking_compass.lock");
    public static final SoundEvent SOUND_EVENT = SoundEvent.createVariableRangeEvent(SOUND_ID);

    public static void init()
    {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register((content) -> content.accept(ITEM));
        Registry.register(BuiltInRegistries.SOUND_EVENT, SOUND_ID, SOUND_EVENT);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(PortalLinkingCompassMod.MOD_ID, ITEM_NAME), ITEM);
    }

    @Environment(EnvType.CLIENT)
    public static void initClient()
    {
        ItemProperties.register(ITEM, new ResourceLocation("angle"), new CompassItemPropertyFunction(PortalLinkingCompassItem::pointToTarget));
    }
}