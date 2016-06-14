package net.shadowfacts.funnels;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.shadowfacts.shadowmc.ShadowMC;
import net.shadowfacts.shadowmc.network.PacketUpdateTE;
import net.shadowfacts.shadowmc.tileentity.BaseTileEntity;

/**
 * @author shadowfacts
 */
public class TileEntityFunnel extends BaseTileEntity implements ITickable {

	FluidTank tank = new FluidTank(FunnelsConfig.size);

	private int tick;

	private void save() {
		markDirty();
		ShadowMC.network.sendToAllAround(new PacketUpdateTE(this), new NetworkRegistry.TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
	}

	@Override
	public void update() {
		if (!worldObj.isRemote) {
			if (tank.getFluidAmount() > 0) {
				EnumFacing facing = worldObj.getBlockState(pos).getValue(BlockFunnel.FACING);
				BlockPos handlerPos = pos.offset(facing);
				TileEntity te = worldObj.getTileEntity(handlerPos);
				if (te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite())) {
					IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite());
					tank.drain(handler.fill(tank.drain(20, false), true), true);
					save();
				}
			}
			tick++;
			if (tick % 40 == 0) {
				if (FunnelsConfig.pickupWorldFluids) {
					tick = 0;
					if (FluidUtils.isFluidBlock(worldObj, pos.up())) {
						FluidStack toDrain = FluidUtils.drainFluidBlock(worldObj, pos.up(), false);
						if (toDrain.amount <= tank.getCapacity() - tank.getFluidAmount()) {
							tank.fill(FluidUtils.drainFluidBlock(worldObj, pos.up(), true), true);
							save();
						}
					}
				}
			} else {
				if (FunnelsConfig.placeFluidsInWorld && tank.getFluidAmount() >= Fluid.BUCKET_VOLUME) {
					FluidStack fluid = tank.getFluid();
					if (fluid.getFluid().canBePlacedInWorld()) {
						Block fluidBlock = fluid.getFluid().getBlock();
						BlockPos newPos = pos.offset(worldObj.getBlockState(pos).getValue(BlockFunnel.FACING));
						if (fluidBlock.canPlaceBlockAt(worldObj, newPos)) {
							tank.drain(Fluid.BUCKET_VOLUME, true);
							save();
							worldObj.setBlockState(newPos, fluidBlock.getDefaultState());
						}
					}
				}
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		tank.readFromNBT(tag);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tank.writeToNBT(tag);
		return tag;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return (T)tank;
		} else {
			return super.getCapability(capability, facing);
		}
	}

}
