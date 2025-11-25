package net.moist.block.content;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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

public class FallingLayer extends FallingBlock implements SimpleWaterloggedBlock, EntityBlock {
	public static final IntegerProperty LAYERS = IntegerProperty.create("layers", 1, 8);
	public static final int MAX_LAYERS = 8;

	public static final MapCodec<FallingLayer> CODEC = RecordCodecBuilder.mapCodec(instance ->
		instance.group(
			propertiesCodec()
		).apply(instance, FallingLayer::new
		));
	private boolean overgrowable;

	@Override public MapCodec<FallingLayer> codec() {return CODEC;}
	// ----------------------------

	public FallingLayer(BlockBehaviour.Properties properties) {
		this(properties, false);
	}
	public FallingLayer(BlockBehaviour.Properties properties, boolean overgrowable) {
		super(properties.randomTicks());
		this.overgrowable = overgrowable;
		this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, 1).setValue(BlockStateProperties.WATERLOGGED, false));

	}

	@Override
	public @NotNull FluidState getFluidState(BlockState state) {
		return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.WATERLOGGED).add(LAYERS);
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

	@Override public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {return context.getItemInHand().getItem() instanceof LayerItem && ((LayerItem) context.getItemInHand().getItem()).getBlock().equals(this) && state.getValue(LAYERS) < MAX_LAYERS;}
	public boolean overgrowable() {return this.overgrowable;}
	@Override public BlockState getStateForPlacement(BlockPlaceContext context) {return super.getStateForPlacement(context);}
	@Override public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {level.scheduleTick(pos, this, 2);}
	@Override public void neighborChanged(BlockState state, Level level, BlockPos pos, Block changedBlock, BlockPos changedPos, boolean isMoving) {level.scheduleTick(pos, this, 2);}
	@Override protected boolean skipRendering(BlockState blockState, BlockState blockState2, Direction direction) {return blockState2.is(Blocks.SNOW) && direction.equals(Direction.UP);}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!level.isClientSide) {
			BlockState blockStateBelow = level.getBlockState(pos.below());
			if (!supported(state, level, pos)) {
				boolean isWet = state.getValue(BlockStateProperties.WATERLOGGED);
				BlockState state2 = state.setValue(BlockStateProperties.WATERLOGGED, false);
				FallingBlockEntity fallingBlockEntity = FallingBlockEntity.fall(level, pos, state2);
				if (isWet) {this.placeLiquid(level, pos, state, Fluids.WATER.defaultFluidState());}
			}
			//super.tick(state, level, pos, random);
		}
	}

	@Override public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return supported(state, level, pos);
	}

	public boolean supported(BlockState state, LevelReader level, BlockPos pos) {
		BlockState blockStateBelow = level.getBlockState(pos.below());
		if (blockStateBelow.hasProperty(LAYERS) && blockStateBelow.getValue(LAYERS) == 8) {
			return true;
		}
		return blockStateBelow.isFaceSturdy(level, pos.below(), Direction.UP) || blockStateBelow.isCollisionShapeFullBlock(level, pos.below());
	}

	@Override public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
		if (direction == Direction.DOWN && !this.supported(state, level, currentPos)) {
			level.scheduleTick(currentPos, this, 2);
		}
		return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
	}

	private static boolean isPathfindable(BlockState state) {
		return state.canBeReplaced() && !state.getFluidState().is(FluidTags.WATER);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new LayerBE(blockPos, blockState);

	}
}
