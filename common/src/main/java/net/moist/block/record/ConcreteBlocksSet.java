package net.moist.block.record;

import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.moist.Terrain;
import net.moist.item.ModItems;
import net.moist.item.content.LayerItem;
import net.moist.util.ColorHandler;
import org.jetbrains.annotations.Nullable;

public record ConcreteBlocksSet(
	RegistrySupplier<Block> concreteLayer,
	RegistrySupplier<Block> powderLayer,
	RegistrySupplier<Item> heldConcreteBlock,
	RegistrySupplier<Item> heldConcreteSlab,
	RegistrySupplier<Item> heldConcreteItem,
	RegistrySupplier<Item> heldPowderBlock,
	RegistrySupplier<Item> heldPowderSlab,
	RegistrySupplier<Item> heldPowderItem,
	ConcreteHelper concreteReference) {
//	public static ConcreteBlocksSet powderSet(LayerBuilder concreteBuilder, ConcreteHelper concreteReference) {
//		return new ConcreteBlocksSet(concreteBuilder.buildAndRegister(ModBlocks.BLOCKS), concreteBuilder.name(), concreteReference, builder.tab().getKey());
//	}

	public ConcreteBlocksSet {
		RegisterTints();
	}
	public ConcreteBlocksSet(RegistrySupplier<Block> concreteLayer, RegistrySupplier<Block> powderLayer, String name, ConcreteHelper concreteReference, @Nullable ResourceKey<CreativeModeTab> tab) {
		this(
			concreteLayer,
			powderLayer,
			ModItems.ITEMS.register(Terrain.getID(name+"_block"), () -> new LayerItem(concreteLayer.get(), new Item.Properties().arch$tab(tab != null ? tab : CreativeModeTabs.BUILDING_BLOCKS), 8, "_block")),
			ModItems.ITEMS.register(Terrain.getID(name+"_slab"), () -> new LayerItem(concreteLayer.get(), new Item.Properties().arch$tab(tab != null ? tab : CreativeModeTabs.BUILDING_BLOCKS), 4, "_slab")),
			ModItems.ITEMS.register(Terrain.getID(name), () -> new LayerItem(concreteLayer.get(), new Item.Properties().arch$tab(tab != null ? tab : CreativeModeTabs.BUILDING_BLOCKS), 1)),
			ModItems.ITEMS.register(Terrain.getID(name+"_block"), () -> new LayerItem(powderLayer.get(), new Item.Properties().arch$tab(tab != null ? tab : CreativeModeTabs.BUILDING_BLOCKS), 8, "_block")),
			ModItems.ITEMS.register(Terrain.getID(name+"_slab"), () -> new LayerItem(powderLayer.get(), new Item.Properties().arch$tab(tab != null ? tab : CreativeModeTabs.BUILDING_BLOCKS), 4, "_slab")),
			ModItems.ITEMS.register(Terrain.getID(name), () -> new LayerItem(powderLayer.get(), new Item.Properties().arch$tab(tab != null ? tab : CreativeModeTabs.BUILDING_BLOCKS), 1)),
			concreteReference
		);
	}



	public void RegisterTints() {
		this.RegisterItemTint();
		this.RegisterBlockTint();
	}
	public void RegisterBlockTint() {
		ColorHandlerRegistry.registerBlockColors(ColorHandler.COLORED_BLOCK_PROVIDER, getConcreteLayer(), getPowderLayer());}
	public void RegisterItemTint() {
		ColorHandlerRegistry.registerItemColors(ColorHandler.COLORED_ITEM_PROVIDER, getHeldPowderBlock(), getHeldPowderSlab(), getHeldPowderItem(), getHeldConcreteBlock(), getHeldConcreteSlab(), getHeldConcreteItem());
	}

	public ResourceKey<Block> getConcreteKey() {return concreteLayer.getKey();}

	public Block getConcreteLayer() {return concreteLayer.get();}
	public Item getHeldConcreteBlock() {return heldConcreteBlock.get();}
	public Item getHeldConcreteSlab() {return heldConcreteSlab.get();}
	public Item getHeldConcreteItem() {return heldConcreteItem.get();}

	public Block getPowderLayer() {return concreteLayer.get();}
	public Item getHeldPowderBlock() {return heldPowderBlock.get();}
	public Item getHeldPowderSlab() {return heldPowderSlab.get();}
	public Item getHeldPowderItem() {return heldPowderItem.get();}

}
