package net.moist.util;

import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class ColorHandler {
	public static final BlockColor LAYER_TINT_PROVIDER = new LayerTintProvider();

	public static final ItemColor COLORED_ITEM_PROVIDER = new PowderItemTintProvider();
	public static final BlockColor COLORED_BLOCK_PROVIDER = new PowderBlockTintProvider();

	public static final BlockColor GRASS_TINT_PROVIDER = new GrassLayerTintProvider.BlockProvider();
	public static final ItemColor GRASS_ITEM_TINT_PROVIDER = new GrassLayerTintProvider.ItemProvider();

	@SafeVarargs
	public static void registerBlockColor(Supplier<? extends Block>... blocks) {
		for (Supplier<? extends Block> block : blocks) {
			ColorHandlerRegistry.registerBlockColors(LAYER_TINT_PROVIDER, block);
		}
	}
	@SafeVarargs
	public static void registerPowderColor(Supplier<? extends ItemLike>... items) {
		for (Supplier<? extends ItemLike> item:items) {
			ColorHandlerRegistry.registerItemColors(COLORED_ITEM_PROVIDER, item);
		}
	}
	@SafeVarargs
	public static void registerGrassColor(Supplier<? extends Block>... blocks) {
		for (Supplier<? extends Block> block : blocks) {
			ColorHandlerRegistry.registerBlockColors(GRASS_TINT_PROVIDER, block);
		}
	}
	@SafeVarargs
	public static void registerGrassItemColor(Supplier<? extends ItemLike>... items) {
		for (Supplier<? extends ItemLike> item:items) {
			ColorHandlerRegistry.registerItemColors(GRASS_ITEM_TINT_PROVIDER, item);
		}
	}

}

