package net.moist.util;

import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.moist.Terrain;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class ColorHandler {
	private static final BlockColor LAYER_TINT_PROVIDER = new LayerTintProvider();
	private static final ItemColor ITEM_TINT_PROVIDER = new PowderTintProvider();

	public static void registerBlockColor(Supplier<? extends Block> block) {
		ColorHandlerRegistry.registerBlockColors(LAYER_TINT_PROVIDER,  block);
	}
	public static void registerPowderColor(Supplier<? extends ItemLike> item) {
		ColorHandlerRegistry.registerItemColors(ITEM_TINT_PROVIDER,  item);
	}
	public static void registerBlockColor(Supplier<? extends Block>... blocks) {
		for (Supplier<? extends Block> block : blocks) {
			ColorHandlerRegistry.registerBlockColors(LAYER_TINT_PROVIDER, block);
	}
	}
	public static void registerPowderColor(Supplier<? extends ItemLike>... items) {
		for (Supplier<? extends ItemLike> item:items) {
			ColorHandlerRegistry.registerItemColors(ITEM_TINT_PROVIDER, item);
		}
	}
}

