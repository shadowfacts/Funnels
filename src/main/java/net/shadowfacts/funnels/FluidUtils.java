package net.shadowfacts.funnels;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * @author shadowfacts
 */
public class FluidUtils {

	public static boolean isFluidBlock(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof BlockLiquid) {
			return state.getValue(BlockLiquid.LEVEL) == 0;
		}
		if (state.getBlock() instanceof IFluidBlock) {
			return ((IFluidBlock)state.getBlock()).canDrain(world, pos);
		}
		return false;
	}

	public static FluidStack drainFluidBlock(World world, BlockPos pos, boolean doDrain) {
		FluidStack stack = null;

		IBlockState state = world.getBlockState(pos);

		if (state.getBlock() instanceof BlockLiquid && state.getValue(BlockLiquid.LEVEL) == 0) {
			if (state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.FLOWING_WATER) {
				stack = new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME);
				if (doDrain) world.setBlockToAir(pos);
			} else if (state.getBlock() == Blocks.LAVA|| state.getBlock() == Blocks.FLOWING_LAVA) {
				stack = new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME);
				if (doDrain) world.setBlockToAir(pos);
			}
		} else if (state.getBlock() instanceof IFluidBlock) {
			stack = ((IFluidBlock)state.getBlock()).drain(world, pos, doDrain);
		}

		return stack;
	}

}
