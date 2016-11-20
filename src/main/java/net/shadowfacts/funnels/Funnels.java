package net.shadowfacts.funnels;

import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * @author shadowfacts
 */
@Mod(modid = Funnels.modId, name = Funnels.name, version = Funnels.version, dependencies = "required-after:shadowmc@[3.4.0,);", guiFactory = "net.shadowfacts.funnels.GUIFactory")
public class Funnels {

	public static final String modId = "Funnels";
	public static final String name = "Funnels";
	public static final String version = "@VERSION@";

//	Content
	public static BlockFunnel funnel;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		FunnelsConfig.init(event.getModConfigurationDirectory());
		FunnelsConfig.load();

		funnel = GameRegistry.register(new BlockFunnel());
		GameRegistry.register(new ItemBlock(funnel).setRegistryName(funnel.getRegistryName()));

		GameRegistry.registerTileEntity(TileEntityFunnel.class, "funnel");

		if (event.getSide() == Side.CLIENT) {
			preInitClient();
		}

		GameRegistry.addRecipe(new ShapedOreRecipe(funnel, "I I", "I I", " B ", 'I', "ingotIron", 'B', Items.BUCKET));
	}

	@SideOnly(Side.CLIENT)
	private void preInitClient() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFunnel.class, new TESRFunnel());
		funnel.initItemModel();
	}

}
