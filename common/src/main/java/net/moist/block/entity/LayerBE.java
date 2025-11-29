package net.moist.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.moist.block.content.FallingLayer;

public class LayerBE extends BlockEntity {

	public LayerBE(BlockPos blockPos, BlockState blockState) {
		super(TerrainBlockEntities.LAYER_BE.get(), blockPos, blockState);
	}

	public int getTotalLayers() {
		int state_layers = getLayers();
		int above_layers = getAboveLayers();
		return state_layers + above_layers;
	}
	public int getLayers() {
		if (this.getLevel() == null) return 0;
		BlockState state = this.getBlockState();
		return state.getOptionalValue(FallingLayer.LAYERS).orElse(0);
	}
	public int getAboveLayers() {
		if (this.getLevel() == null) return 0;
		BlockState aboveState = this.getLevel().getBlockState(getBlockPos().above());
		return aboveState.hasProperty(BlockStateProperties.LAYERS) ? aboveState.getValue(BlockStateProperties.LAYERS) : 0;
	}
}
