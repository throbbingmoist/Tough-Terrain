package net.moist.util;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.MapColor;
import net.moist.item.content.LayerItem;

public class PowderItemTintProvider implements ItemColor {
	@Override
	public int getColor(ItemStack itemStack, int tintIndex) {
		if (tintIndex == 0) {
			if (itemStack.getItem() instanceof LayerItem) {
				int rgb = ((LayerItem) itemStack.getItem()).getBlock().defaultMapColor().calculateRGBColor(MapColor.Brightness.HIGH);
				int r = (rgb >> 16) & 0xFF;
				int g = (rgb >> 8) & 0xFF;
				int b = rgb & 0xFF;
				return ((b << 16) | (g << 8) | r);
			}
		}
		return -1;
	}
}
