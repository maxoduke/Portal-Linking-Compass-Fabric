package me.maxoduke.mod.portallinkingcompass;

import net.fabricmc.api.ClientModInitializer;
import me.maxoduke.mod.portallinkingcompass.module.PortalLinkingCompass;

public class PortalLinkingCompassClientMod implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        PortalLinkingCompass.initClient();
    }
}
