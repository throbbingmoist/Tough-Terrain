package net.moist.block.content;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.moist.block.entity.LayerBE;
import net.moist.item.content.LayerItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.moist.Terrain.getLookGranular;

public class FloatingLayer extends Block implements SimpleWaterloggedBlock, EntityBlock {
	public static final IntegerProperty LAYERS = FallingLayer.LAYERS;
	public static final int MAX_LAYERS = 8;
	public static final BooleanProperty SNOWY = BlockStateProperties.SNOWY;

	private final boolean overgrowable;


	protected static final VoxelShape[] SHAPE_BY_LAYER = new VoxelShape[]{
		Shapes.empty(),
		Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
		Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
		Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
		Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
		Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
		Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D),
		Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D),
		Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
	};

	public FloatingLayer(Properties properties) {
		this(properties, false);
	}
	public FloatingLayer(Properties properties, boolean overgrowable) {
		super(properties.randomTicks());
		this.overgrowable = overgrowable;
		this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, 1)
			.setValue(BlockStateProperties.WATERLOGGED, false)
			.setValue(BlockStateProperties.SNOWY, false)
		);
	}

	@Override public @NotNull FluidState getFluidState(BlockState state) {
		return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}
	@Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.WATERLOGGED).add(LAYERS);
		builder.add(SNOWY);
	}

	@Override protected BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {return direction == Direction.UP ? (BlockState)blockState.setValue(SNOWY, isSnowySetting(blockState2)) : super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);}
	private static boolean isSnowySetting(BlockState blockState) {return blockState.is(BlockTags.SNOW);}
//	@Override protected float getShadeBrightness(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
//		return (Integer)blockState.getValue(LAYERS) == 8 ? 0.2F : 1.0F;
//	}
@Override
protected boolean useShapeForLightOcclusion(BlockState blockState) {
	return true;
}
	@Override public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		BlockState aboveState = level.getBlockState(pos.above());
		double snowBoost = aboveState.is(Blocks.SNOW) ? aboveState.getValue(BlockStateProperties.LAYERS) : 0.0D;
		double offset = state.getValue(LAYERS)*2.0D;
		VoxelShape layerShape = Shapes.box(0.0D, 0.0D, 0.0D, 16.0D/16f, offset/16f, 16.0D/16f);
		VoxelShape snowShape = Shapes.box(0.0D, offset/16f, 0.0D, 16.0D/16f, (offset+snowBoost*2.0D)/16f, 16.0D/16f);
		if ((snowBoost == 0.0) || !(getLookGranular(Minecraft.getInstance().hitResult) >= state.getValue(FallingLayer.LAYERS)/8f)) {
			return layerShape;
		}
		return Shapes.join(layerShape, snowShape, BooleanOp.OR);
	}
	@Override public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		BlockState aboveState = level.getBlockState(pos.above());
		double snowBoost = aboveState.is(Blocks.SNOW) ? aboveState.getValue(BlockStateProperties.LAYERS)-1 : 0.0D;
		double offset = state.getValue(LAYERS)*2.0D;
		VoxelShape layerShape = Shapes.box(0.0D, 0.0D, 0.0D, 16.0D/16f, offset/16f, 16.0D/16f);
		VoxelShape snowShape = Shapes.box(0.0D, offset/16f, 0.0D, 16.0D/16f, (offset+snowBoost*2.0D)/16f, 16.0D/16f);
		return layerShape;
	}
	@Override public @NotNull VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		BlockState aboveState = level.getBlockState(pos.above());
		double snowBoost = aboveState.is(Blocks.SNOW) ? aboveState.getValue(BlockStateProperties.LAYERS) : 0.0D;
		double offset = state.getValue(LAYERS)*2.0D;
		VoxelShape layerShape = Shapes.box(0.0D, 0.0D, 0.0D, 16.0D/16f, offset/16f, 16.0D/16f);
		VoxelShape snowShape = Shapes.box(0.0D, offset/16f, 0.0D, 16.0D/16f, (offset+snowBoost*2.0D)/16f, 16.0D/16f);
		if ((snowBoost == 0.0) || !(getLookGranular(Minecraft.getInstance().hitResult) >= state.getValue(FallingLayer.LAYERS)/8f)) {
			return layerShape;
		}
		return Shapes.join(layerShape, snowShape, BooleanOp.OR);
	}
	@Override public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
		return context.getItemInHand().getItem() instanceof LayerItem && ((LayerItem) context.getItemInHand().getItem()).getBlock().equals(this) && state.getValue(LAYERS) < MAX_LAYERS;
	}

	@Override public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context);
	}

	@Override public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		level.scheduleTick(pos, this, 2);
	}
	@Override public void neighborChanged(BlockState state, Level level, BlockPos pos, Block changedBlock, BlockPos changedPos, boolean isMoving) {state.setValue(SNOWY, level.getBlockState(pos.above()).is(Blocks.SNOW));level.scheduleTick(pos, this, 2);}
	public boolean isOvergrowable() {
		return this.overgrowable;
	}

	@Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {super.randomTick(state, level, pos, randomSource);}

	@Override public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!level.isClientSide) {if (state.getValue(BlockStateProperties.WATERLOGGED) && !(state.getValue(BlockStateProperties.LAYERS) < 8)) {
			state.setValue(BlockStateProperties.WATERLOGGED, false);}
			super.tick(state, level, pos, random);
		}}

	@Override public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {return true;}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new LayerBE(blockPos, blockState);

	}
	@Override public RenderShape getRenderShape(BlockState state) {return RenderShape.MODEL;}
}
