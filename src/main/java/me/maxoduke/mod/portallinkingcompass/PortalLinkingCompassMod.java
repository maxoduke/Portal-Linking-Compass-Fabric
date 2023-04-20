package me.maxoduke.mod.portallinkingcompass;

import me.maxoduke.mod.portallinkingcompass.module.PortalLinkingCompass;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortalLinkingCompassMod implements ModInitializer
{
    public static final String MOD_ID = "portallinkingcompass";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize()
    {
        PortalLinkingCompass.init();

        LOGGER.info("Portal Linking Compass initialized!");
    }
}
