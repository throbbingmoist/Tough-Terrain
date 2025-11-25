package net.moist.block.content;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

public class SpreadingLayer extends SpreadingSnowyDirtBlock implements SimpleWaterloggedBlock, EntityBlock {
	public static final IntegerProperty LAYERS = FallingLayer.LAYERS;
	public static final int MAX_LAYERS = 8;
	public static final BooleanProperty SNOWY = BlockStateProperties.SNOWY;

	public static final MapCodec<SpreadingLayer> CODEC = RecordCodecBuilder.mapCodec(instance ->
		instance.group(
			propertiesCodec()
		).apply(instance, SpreadingLayer::new
		));
	private final boolean overgrowable;

	protected @NotNull MapCodec<? extends SpreadingSnowyDirtBlock> codec() {
		return CODEC;
	}

	public SpreadingLayer(BlockBehaviour.Properties properties) {this(properties, false);}
	public SpreadingLayer(BlockBehaviour.Properties properties, boolean overgrowable) {
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
//	@Override protected float getShadeBrightness(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
//		return blockState.getValue(LAYERS) >= 8 ? 1.0F : 0.2F;
//	}

	@Override protected boolean skipRendering(BlockState blockState, BlockState blockState2, Direction direction) {
		return blockState2.is(Blocks.SNOW) && direction.equals(Direction.UP);
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
	@Override public BlockState getStateForPlacement(BlockPlaceContext context) {return super.getStateForPlacement(context);}

	@Override public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {level.scheduleTick(pos, this, 2);}
	@Override public void neighborChanged(BlockState state, Level level, BlockPos pos, Block changedBlock, BlockPos changedPos, boolean isMoving) {state.setValue(SNOWY, level.getBlockState(pos.above()).is(Blocks.SNOW));level.scheduleTick(pos, this, 2);}
	public boolean overgrowable() {return this.overgrowable;}

	@Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {super.randomTick(state, level, pos, randomSource);}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {

		if (!level.isClientSide) {
			if (state.getValue(BlockStateProperties.WATERLOGGED) && !(state.getValue(BlockStateProperties.LAYERS) < 8)) {
				state.setValue(BlockStateProperties.WATERLOGGED, false);
			}
			super.tick(state, level, pos, random);
		}
	}
	@Override public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {return state.getValue(FallingLayer.LAYERS) != 8;}
	@Override public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {return 0;}
	@Override public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return true;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new LayerBE(blockPos, blockState);
	}

	@Override
	protected RenderShape getRenderShape(BlockState blockState) {
		return RenderShape.MODEL;
	}
}
