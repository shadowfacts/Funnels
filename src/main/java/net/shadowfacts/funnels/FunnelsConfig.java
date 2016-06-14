package net.shadowfacts.funnels;

import net.minecraftforge.common.config.Configuration;
import net.shadowfacts.config.Config;
import net.shadowfacts.config.ConfigManager;

import java.io.File;

/**
 * @author shadowfacts
 */
@Config(name = Funnels.modId)
public class FunnelsConfig {

	static Configuration config;

	@Config.Prop
	public static int size = 4000;

	@Config.Prop
	public static boolean pickupWorldFluids = true;

	@Config.Prop
	public static boolean placeFluidsInWorld = true;

	public static void init(File configDir) {
		config = new Configuration(new File(configDir, "shadowfacts/Funnels.cfg"));
	}

	public static void load() {
		ConfigManager.load(FunnelsConfig.class, Configuration.class, config);
		if (config.hasChanged()) config.save();
	}

}
