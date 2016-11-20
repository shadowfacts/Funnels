package net.shadowfacts.funnels;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.shadowfacts.shadowmc.ShadowMC;
import net.shadowfacts.shadowmc.capability.CapHolder;
import net.shadowfacts.shadowmc.fluid.FluidTank;
import net.shadowfacts.shadowmc.nbt.AutoSerializeNBT;
import net.shadowfacts.shadowmc.network.PacketUpdateTE;
import net.shadowfacts.shadowmc.tileentity.BaseTileEntity;

/**
 * @author shadowfacts
 */
public class TileEntityFunnel extends BaseTileEntity implements ITickable {

	@AutoSerializeNBT
	@CapHolder(capabilities = IFluidHandler.class)
	FluidTank tank = new FluidTank(FunnelsConfig.size);

	private int tick;

	void save() {
		markDirty();
		ShadowMC.network.sendToAllAround(new PacketUpdateTE(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
	}

	@Override
	public void update() {
		if (!world.isRemote) {
			handlers:
			{
//			up handler -> tank
				if (tank.getFluidAmount() < tank.getCapacity()) {
					BlockPos handlerPos = pos.up();
					TileEntity te = world.getTileEntity(handlerPos);
					if (te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN)) {
						IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN);
						tank.fill(handler.drain(tank.getCapacity() - tank.getFluidAmount(), true), true);
						save();
						break handlers;
					}
				}
//			tank -> front handler
				if (tank.getFluidAmount() > 0) {
					EnumFacing facing = world.getBlockState(pos).getValue(BlockFunnel.FACING);
					BlockPos handlerPos = pos.offset(facing);
					TileEntity te = world.getTileEntity(handlerPos);
					if (te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite())) {
						IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite());
						tank.drain(handler.fill(tank.drain(20, false), true), true);
						save();
					}
				}
			}

			world:
			{
				tick++;
				if (tick % 40 == 0) {
//				pickup from world
					if (FunnelsConfig.pickupWorldFluids && tank.getFluidAmount() <= tank.getCapacity() - Fluid.BUCKET_VOLUME) {
						tick = 0;
						if (FluidUtils.isFluidBlock(world, pos.up())) {
							FluidStack toDrain = FluidUtils.drainFluidBlock(world, pos.up(), false);
							if (toDrain.amount <= tank.getCapacity() - tank.getFluidAmount()) {
								tank.fill(FluidUtils.drainFluidBlock(world, pos.up(), true), true);
								save();
								break world;
							}
						}
					}
//				place in world
					if (FunnelsConfig.placeFluidsInWorld && tank.getFluidAmount() >= Fluid.BUCKET_VOLUME) {
						FluidStack fluid = tank.getFluid();
						if (fluid.getFluid().canBePlacedInWorld()) {
							Block fluidBlock = fluid.getFluid().getBlock();
							BlockPos newPos = pos.offset(world.getBlockState(pos).getValue(BlockFunnel.FACING));
							if (fluidBlock.canPlaceBlockAt(world, newPos)) {
								tank.drain(Fluid.BUCKET_VOLUME, true);
								save();
								world.setBlockState(newPos, fluidBlock.getDefaultState());
							}
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

}
