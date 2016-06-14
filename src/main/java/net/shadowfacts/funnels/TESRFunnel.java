package net.shadowfacts.funnels;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.shadowfacts.shadowmc.util.RenderHelper;
import org.lwjgl.opengl.GL11;

/**
 * @author shadowfacts
 */
public class TESRFunnel extends TileEntitySpecialRenderer<TileEntityFunnel> {

	@Override
	public void renderTileEntityAt(TileEntityFunnel te, double x, double y, double z, float partialTicks, int destroyStage) {
		if (te.tank.getFluid() != null) {
			FluidStack fluid = te.tank.getFluid();

			Tessellator tessellator = Tessellator.getInstance();
			VertexBuffer renderer = tessellator.getBuffer();
			renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
			Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			int color = fluid.getFluid().getColor(fluid);
			int brightness = Minecraft.getMinecraft().theWorld.getCombinedLight(te.getPos(), fluid.getFluid().getLuminosity());

			GlStateManager.pushMatrix();

			GlStateManager.disableLighting();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			if (Minecraft.isAmbientOcclusionEnabled()) {
				GL11.glShadeModel(GL11.GL_SMOOTH);
			} else {
				GL11.glShadeModel(GL11.GL_FLAT);
			}

			GlStateManager.translate(x, y, z);

			TextureAtlasSprite still = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getFluid().getStill(fluid).toString());

			RenderHelper.putTexturedQuad(renderer, still, 2/16d, 14/16d, 2/16d, 12/16d, 0, 12/16d, EnumFacing.UP, color, brightness);

			tessellator.draw();

			GlStateManager.disableBlend();
			GlStateManager.enableLighting();
			GlStateManager.popMatrix();
		}
	}

}
