package net.shadowfacts.funnels;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.shadowfacts.shadowmc.block.BlockTE;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author shadowfacts
 */
public class BlockFunnel extends BlockTE<TileEntityFunnel> {

	public static final PropertyDirection FACING = PropertyDirection.create("facing", side -> side != EnumFacing.UP);

	protected static final AxisAlignedBB BASE_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.625D, 1.0D);
	protected static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.125D);
	protected static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.875D, 1.0D, 1.0D, 1.0D);
	protected static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.875D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
	protected static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.125D, 1.0D, 1.0D);

	public BlockFunnel() {
		super(Material.ROCK, "funnel");

		setCreativeTab(CreativeTabs.MISC);

		setDefaultState(getDefaultState()
				.withProperty(FACING, EnumFacing.DOWN));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return FULL_BLOCK_AABB;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn) {
		addCollisionBoxToList(pos, entityBox, collidingBoxes, BASE_AABB);
		addCollisionBoxToList(pos, entityBox, collidingBoxes, EAST_AABB);
		addCollisionBoxToList(pos, entityBox, collidingBoxes, WEST_AABB);
		addCollisionBoxToList(pos, entityBox, collidingBoxes, SOUTH_AABB);
		addCollisionBoxToList(pos, entityBox, collidingBoxes, NORTH_AABB);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
	}

	@Override
	public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		EnumFacing side = facing.getOpposite();
		if (side == EnumFacing.UP) side = EnumFacing.DOWN;
		return getDefaultState().withProperty(FACING, side);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!player.isSneaking()) {
			TileEntityFunnel te = getTileEntity(world, pos);

			if (FluidUtils.fillHandlerWithContainer(world, te.tank, player, hand)) {
				te.save();
				return true;
			} else if (FluidUtils.fillContainerFromHandler(world, te.tank, player, hand, te.tank.getFluid())) {
				te.save();
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public Class<TileEntityFunnel> getTileEntityClass() {
		return TileEntityFunnel.class;
	}

	@Nonnull
	@Override
	public TileEntityFunnel createTileEntity(World world, IBlockState state) {
		return new TileEntityFunnel();
	}

}
