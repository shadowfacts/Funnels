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

	public static boolean fillHandlerWithContainer(World world, IFluidHandler handler, EntityPlayer player, EnumHand hand) {
		ItemStack container = player.getHeldItem(hand);
		FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(container);

		if (fluid != null) {
			if (handler.fill(fluid, false) == fluid.amount || player.capabilities.isCreativeMode) {
				ItemStack returnStack = FluidContainerRegistry.drainFluidContainer(container);
				if (world.isRemote) {
					return true;
				}
				if (!player.capabilities.isCreativeMode) {
					if (PlayerUtils.disposePlayerItem(player.getHeldItem(hand), returnStack, player, true)) {
						player.openContainer.detectAndSendChanges();
						((EntityPlayerMP)player).sendContainerToPlayer(player.openContainer);
					}
				}
				handler.fill(fluid, true);
				return true;
			}
		}
		return false;
	}

	public static boolean fillContainerFromHandler(World world, IFluidHandler handler, EntityPlayer player, EnumHand hand, FluidStack tankFluid) {
		ItemStack container = player.getHeldItem(hand);

		if (FluidContainerRegistry.isEmptyContainer(container)) {
			ItemStack returnStack = FluidContainerRegistry.fillFluidContainer(tankFluid, container);
			FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(returnStack);

			if (fluid == null || returnStack == null) {
				return false;
			}
			if (world.isRemote) {
				return true;
			}
			if (!player.capabilities.isCreativeMode) {
				if (container.stackSize == 1) {
					player.inventory.setInventorySlotContents(player.inventory.currentItem, returnStack);
					container.stackSize--;
					if (container.stackSize <= 0) {
						container = null;
					}
				} else {
					if (PlayerUtils.disposePlayerItem(player.getHeldItem(hand), returnStack, player, true)) {
						player.openContainer.detectAndSendChanges();
						((EntityPlayerMP) player).sendContainerToPlayer(player.openContainer);
					}
				}
			}
			handler.drain(fluid.amount, true);
			return true;
		}
		return false;
	}

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
