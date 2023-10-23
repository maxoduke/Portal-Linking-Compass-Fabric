package dev.maxoduke.mods.portallinkingcompass;

import dev.maxoduke.mods.portallinkingcompass.item.PortalLinkingCompassItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("SpellCheckingInspection")
public class PortalLinkingCompass
{
    public static final String MOD_ID = "portallinkingcompass";
    public static final String MOD_NAME = "Portal Linking Compass";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static final String ITEM_NAME = "portal_linking_compass";
    public static final Item ITEM = new PortalLinkingCompassItem(new Item.Properties());

    public static final String COMPASS_LOCKS_SOUND_NAME = "item.portal_linking_compass.lock";
    public static final ResourceLocation COMPASS_LOCKS_SOUND_RESOURCE = new ResourceLocation(PortalLinkingCompass.MOD_ID, COMPASS_LOCKS_SOUND_NAME);
    public static final SoundEvent COMPASS_LOCKS_SOUND_EVENT = SoundEvent.createVariableRangeEvent(COMPASS_LOCKS_SOUND_RESOURCE);
}