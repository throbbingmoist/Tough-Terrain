package net.moist.block.content;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class LayerBlockNoFall extends Block implements SimpleWaterloggedBlock {
	public static final IntegerProperty LAYERS = LayerBlock.LAYERS;
	public static final int MAX_LAYERS = 8;
	//public static final BooleanProperty SNOWY = BlockStateProperties.SNOWY;


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


	public LayerBlockNoFall(BlockBehaviour.Properties properties) {
		super(properties.randomTicks());
		this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, 1)
			.setValue(BlockStateProperties.WATERLOGGED, false)
			//.setValue(BlockStateProperties.SNOWY, false)
		);
	}

	@Override
	public @NotNull FluidState getFluidState(BlockState state) {
		return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.WATERLOGGED).add(LAYERS);
			//.add(SNOWY);
	}

	@Override
	public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE_BY_LAYER[state.getValue(LAYERS)];
	}

	// --- Core fix: Can be replaced if not max layers ---
	@Override
	public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
		return false;
		//return context.getItemInHand().getItem() == this.asItem() && state.getValue(LAYERS) < MAX_LAYERS;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState existingState = level.getBlockState(pos);


		if (existingState.getBlock() instanceof LayerBlockNoFall) {
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


	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
		super.randomTick(state, level, pos, randomSource);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!level.isClientSide) {
			super.tick(state, level, pos, random);
		}
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return true;
	}
}
