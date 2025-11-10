package net.moist.util;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.moist.Terrain;
import net.moist.item.content.LayerItem;
import org.jetbrains.annotations.Nullable;

import static net.moist.Terrain.*;

public class GrassLayerTintProvider {
	public static class BlockProvider implements BlockColor {
		@Override
		public int getColor(BlockState blockState, @Nullable BlockAndTintGetter blockAndTintGetter, @Nullable BlockPos blockPos, int tintIndex) {
			int color;
			if (tintIndex == 0) {
				if (blockAndTintGetter != null || blockPos != null) {
					color = blockAndTintGetter.getBlockTint(blockPos, BiomeColors.GRASS_COLOR_RESOLVER);
					if (blockState.getOptionalValue(BlockStateProperties.SNOWY).orElse(false)) {
						return 0xffffff;
					}
					return color;
				} else {
					color = GrassColor.get(0.5, 1.0);
					return color;
				}
			}
			return -1;
		}
	}
	public static class ItemProvider implements ItemColor {
		@Override
		public int getColor(ItemStack itemStack, int tintIndex) {
			if (tintIndex == 0) {
				return GrassColor.get(0.5, 1.0);
			}
			return -1;
		}
	}
}
