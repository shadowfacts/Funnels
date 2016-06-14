package net.shadowfacts.funnels;

import net.minecraft.client.gui.GuiScreen;
import net.shadowfacts.shadowmc.config.GUIConfig;

/**
 * @author shadowfacts
 */
public class FunnelsConfigGUI extends GUIConfig {

	public FunnelsConfigGUI(GuiScreen parent) {
		super(parent, Funnels.modId, FunnelsConfig.config);
	}

}
