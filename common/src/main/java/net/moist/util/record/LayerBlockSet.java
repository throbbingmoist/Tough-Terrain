package net.moist.util.record;

import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.moist.Terrain;
import net.moist.block.ModBlocks;
import net.moist.block.builder.LayerBuilder;
import net.moist.item.ModItems;
import net.moist.item.content.LayerItem;
import org.jetbrains.annotations.Nullable;

public record LayerBlockSet(RegistrySupplier<Block> placedLayer, RegistrySupplier<Item> heldBlock, RegistrySupplier<Item> heldSlab, RegistrySupplier<Item> heldItem) {
	public static LayerBlockSet layerSet(LayerBuilder builder) {return new LayerBlockSet(builder.buildAndRegister(ModBlocks.BLOCKS), builder.name(), builder.tab().getKey());}
	public LayerBlockSet(RegistrySupplier<Block> layerBlock, String name, @Nullable ResourceKey<CreativeModeTab> tab) {
		this(layerBlock,
			ModItems.ITEMS.register(Terrain.getID(name+"_block"), () -> new LayerItem(layerBlock.get(), new Item.Properties().arch$tab(tab != null ? tab : CreativeModeTabs.BUILDING_BLOCKS), 8, "_block")),
			ModItems.ITEMS.register(Terrain.getID(name+"_slab"), () -> new LayerItem(layerBlock.get(), new Item.Properties().arch$tab(tab != null ? tab : CreativeModeTabs.BUILDING_BLOCKS), 4, "_slab")),
			ModItems.ITEMS.register(Terrain.getID(name), () -> new LayerItem(layerBlock.get(), new Item.Properties().arch$tab(tab != null ? tab : CreativeModeTabs.BUILDING_BLOCKS), 1))
		);
	}



	public void RegisterTints(BlockColor blockColor, ItemColor itemColor) {this.RegisterItemTint(itemColor);this.RegisterBlockTint(blockColor);}
	public void RegisterBlockTint(BlockColor color) {ColorHandlerRegistry.registerBlockColors(color, getPlacedLayer());}
	public void RegisterItemTint(ItemColor color) {ColorHandlerRegistry.registerItemColors(color, getHeldBlock(), getHeldSlab(), getHeldItem());}

	public ResourceKey<Block> getBlockKey() {return placedLayer.getKey();}

	public Block getPlacedLayer() {return placedLayer.get();}
	public Item getHeldBlock() {return heldBlock.get();}
	public Item getHeldSlab() {return heldSlab.get();}
	public Item getHeldItem() {return heldItem.get();}
}
