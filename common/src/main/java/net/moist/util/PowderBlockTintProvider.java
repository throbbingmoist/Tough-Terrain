package net.moist.util;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.moist.block.content.ConcreteLayer;
import net.moist.item.content.LayerItem;
import org.jetbrains.annotations.Nullable;

public class PowderBlockTintProvider implements BlockColor {
	@Override
	public int getColor(BlockState blockState, @Nullable BlockAndTintGetter blockAndTintGetter, @Nullable BlockPos blockPos, int i) {
//		if (tintIndex == 0) {
//			if (blockState.getBlock() instanceof ConcreteLayer) {
//				int rgb = ((LayerItem) blockState.getBlock()).defaultMapColor().calculateRGBColor(MapColor.Brightness.HIGH);
//				int r = (rgb >> 16) & 0xFF;
//				int g = (rgb >> 8) & 0xFF;
//				int b = rgb & 0xFF;
//				return ((b << 16) | (g << 8) | r);
//			}
//		}
		return -1;
	}
}
