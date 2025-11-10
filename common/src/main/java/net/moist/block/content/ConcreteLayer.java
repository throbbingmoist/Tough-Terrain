package net.moist.block.content;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import static net.moist.Terrain.getLookGranular;

public class ConcreteLayer extends Block {
	public static final IntegerProperty LAYERS = FallingLayer.LAYERS;
	public static final int MAX_LAYERS = FallingLayer.MAX_LAYERS;

	public ConcreteLayer(Block block_based_on) {
		super(Properties.ofFullCopy(block_based_on));
		this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, 1));
	}
	public ConcreteLayer(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, 1));
	}

	@Override public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		BlockState aboveState = level.getBlockState(pos.above());
		double snowBoost = aboveState.is(Blocks.SNOW) ? aboveState.getValue(BlockStateProperties.LAYERS) : 0.0D;
		double offset = state.getValue(LAYERS)*2.0D;
		VoxelShape layerShape = Shapes.box(0.0D, 0.0D, 0.0D, 16.0D/16f, offset/16f, 16.0D/16f);
		VoxelShape snowShape = Shapes.box(0.0D, offset/16f, 0.0D, 16.0D/16f, (offset+snowBoost*2.0D)/16f, 16.0D/16f);
		if ((snowBoost == 0.0) || !(getLookGranular(Minecraft.getInstance().hitResult) >= state.getValue(FallingLayer.LAYERS)/8f)) {return layerShape;}
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
		if ((snowBoost == 0.0) || !(getLookGranular(Minecraft.getInstance().hitResult) >= state.getValue(FallingLayer.LAYERS)/8f)) {return layerShape;}
		return Shapes.join(layerShape, snowShape, BooleanOp.OR);
	}
}
