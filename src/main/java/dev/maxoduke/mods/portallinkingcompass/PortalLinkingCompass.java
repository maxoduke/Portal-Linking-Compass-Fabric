package dev.maxoduke.mods.portallinkingcompass;

import dev.maxoduke.mods.portallinkingcompass.item.PortalLinkingCompassItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("SpellCheckingInspection")
public class PortalLinkingCompass implements ModInitializer, ClientModInitializer
{
    public static final String MOD_ID = "portallinkingcompass";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final String ITEM_NAME = "portal_linking_compass";
    public static final Item ITEM = new PortalLinkingCompassItem(new FabricItemSettings());
    public static final ResourceLocation SOUND_ID = new ResourceLocation(MOD_ID, "item.portal_linking_compass.lock");
    public static final SoundEvent SOUND_EVENT = SoundEvent.createVariableRangeEvent(SOUND_ID);

    @Override
    public void onInitialize()
    {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register((content) -> content.accept(ITEM));

        Registry.register(BuiltInRegistries.SOUND_EVENT, SOUND_ID, SOUND_EVENT);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, ITEM_NAME), ITEM);

        LOGGER.info("Portal Linking Compass initialized!");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void onInitializeClient()
    {
        ItemProperties.register(ITEM, new ResourceLocation("angle"), new CompassItemPropertyFunction(PortalLinkingCompassItem::pointToTarget));
    }
}