package net.moist.block.content;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class FallingLayer extends FallingBlock implements SimpleWaterloggedBlock {
	public static final IntegerProperty LAYERS = IntegerProperty.create("layers", 1, 8);
	public static final int MAX_LAYERS = 8;

	public static final MapCodec<FallingLayer> CODEC = RecordCodecBuilder.mapCodec(instance ->
		instance.group(
			propertiesCodec()
		).apply(instance, FallingLayer::new
		));
	private boolean overgrowable;
	private Block packingBlock;
	private boolean packInstantly;

	@Override
	public MapCodec<FallingLayer> codec() {
		return CODEC;
	}
	// ----------------------------

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


	public FallingLayer(BlockBehaviour.Properties properties) {
		super(properties.randomTicks());
		this.overgrowable = false;
		this.packingBlock = Blocks.AIR;
		this.packInstantly = false;
		this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, 1).setValue(BlockStateProperties.WATERLOGGED, false));
	}
	public FallingLayer(BlockBehaviour.Properties properties, boolean overgrowable) {
		super(properties.randomTicks());
		this.overgrowable = overgrowable;
		this.packingBlock = Blocks.AIR;
		this.packInstantly = false;
		this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, 1).setValue(BlockStateProperties.WATERLOGGED, false));
	}
	public FallingLayer(BlockBehaviour.Properties properties, boolean overgrowable, Block packingBlock) {
		super(properties.randomTicks());
		this.overgrowable = overgrowable;
		this.packingBlock = packingBlock;
		this.packInstantly = false;
		this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, 1).setValue(BlockStateProperties.WATERLOGGED, false));
	}
	public FallingLayer(BlockBehaviour.Properties properties, boolean overgrowable, Block packingBlock, boolean packInstantly) {
		super(properties.randomTicks());
		this.overgrowable = overgrowable;
		this.packingBlock = packingBlock;
		this.packInstantly = packInstantly;
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

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE_BY_LAYER[state.getValue(LAYERS)];
	}

	// --- Core fix: Can be replaced if not max layers ---
	@Override
	public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
		return false;
		//return context.getItemInHand().getItem() == this.asItem() && state.getValue(LAYERS) < MAX_LAYERS;
	}

	public boolean isOvergrowable() {
		return this.overgrowable;
	}
	public FallingLayer overgrowable(boolean bool) {
		this.overgrowable = bool;
		return this;
	}
	public FallingLayer packsTo(Block block) {
		return packsTo(block, false);
	}
	public FallingLayer packsTo(Block block, boolean packInstantly) {
		this.packingBlock = block;
		this.packInstantly = packInstantly;
		return this;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState existingState = level.getBlockState(pos);


		if (existingState.getBlock() instanceof FallingLayer) {
			int currentLayers = existingState.getValue(LAYERS);
			if (currentLayers < MAX_LAYERS) {
				return existingState.setValue(LAYERS, currentLayers + 1);
			}
		}
		if (level.getFluidState(pos).is(Fluids.WATER)) {
			return existingState.setValue(BlockStateProperties.WATERLOGGED, level.getFluidState(pos).is(Fluids.WATER));
		}
		return super.getStateForPlacement(context);
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		level.scheduleTick(pos, this, 2);
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block changedBlock, BlockPos changedPos, boolean isMoving) {
		level.scheduleTick(pos, this, 2);
	}

	public void packBlock(BlockState state, ServerLevel level, BlockPos pos) {
		if (this.packingBlock != Blocks.AIR) {
			if (state.getValue(BlockStateProperties.WATERLOGGED)) {
				level.setBlock(pos, this.packingBlock.defaultBlockState(), 11);
			}
		}
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
		if (this.packingBlock != Blocks.AIR) {
			packBlock(state, level, pos);
		}
		super.randomTick(state, level, pos, randomSource);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if ((this.packInstantly)&&(this.packingBlock != Blocks.AIR)) {
			packBlock(state, level, pos);
		}
		if (!level.isClientSide) {
			BlockState blockStateBelow = level.getBlockState(pos.below());
			if (!supported(state, level, pos)) {
				boolean isWet = state.getValue(BlockStateProperties.WATERLOGGED);
				BlockState state2 = state.setValue(BlockStateProperties.WATERLOGGED, false);
				FallingBlockEntity fallingBlockEntity = FallingBlockEntity.fall(level, pos, state2);
				if (isWet) {this.placeLiquid(level, pos, state, Fluids.WATER.defaultFluidState());}
			}
			if (supported(state, level, pos) && !canSurviveLongTerm(state, level, pos)) {
				level.destroyBlock(pos, false);
			}
			super.tick(state, level, pos, random);
		}
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return true;
	}

	public boolean supported(BlockState state, LevelReader level, BlockPos pos) {
		BlockState blockStateBelow = level.getBlockState(pos.below());
		return !(blockStateBelow.getBlock() instanceof FallingLayer) || !(blockStateBelow.getValue(LAYERS) < 8);
	}


	public boolean canSurviveLongTerm(BlockState state, LevelReader level, BlockPos pos) {
		BlockState blockStateBelow = level.getBlockState(pos.below());
		return !isPathfindable(blockStateBelow);
	}
	@Override
	public void onLand(Level level, BlockPos pos, BlockState fallingState, BlockState impactState, FallingBlockEntity fallingBlock) {
		super.onLand(level, pos, fallingState, impactState, fallingBlock);
	}
	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
		if (direction == Direction.DOWN && !this.canSurviveLongTerm(state, level, currentPos)) {
			level.scheduleTick(currentPos, this, 2);
		}
		return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
	}

	private static boolean isPathfindable(BlockState state) {
		return state.canBeReplaced() && !state.getFluidState().is(FluidTags.WATER);
	}
}
