package net.moist.util;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.moist.block.content.FallingLayer;
import org.jetbrains.annotations.Nullable;

public class LayerTintProvider implements BlockColor {
	@Override
	public int getColor(BlockState blockState, @Nullable BlockAndTintGetter blockAndTintGetter, @Nullable BlockPos blockPos, int tintIndex) {
		if (tintIndex == 0) {
			if (blockState.getBlock() instanceof FallingLayer) {
				if (blockState.getValue(BlockStateProperties.WATERLOGGED)) {
					return 0xd2b5b5;
				}
			}
			return 0xf2e2d1;
		}
		return -1;
	}
}
