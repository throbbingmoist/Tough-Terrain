package net.moist.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.moist.Terrain;
import net.moist.item.content.LayerItem;

import java.util.function.Supplier;

public class ModItems {
	public static DeferredRegister<Item> ITEMS = DeferredRegister.create(Terrain.MOD_ID, Registries.ITEM);

	public static RegistrySupplier<Item> registerLayerItemVariants(String id, RegistrySupplier<Block> layer) {
		ITEMS.register(Terrain.getID("loose_"+id+"_block"), () -> new LayerItem(layer.get(), new Item.Properties(), 8, "_block"));
		ITEMS.register(Terrain.getID("loose_"+id+"_slab"), () -> new LayerItem(layer.get(), new Item.Properties(), 4, "_slab"));
		return ITEMS.register(Terrain.getID("loose_"+id), () -> new LayerItem(layer.get(), new Item.Properties(), 1));
	}
	public static RegistrySupplier<Item> registerLayerItemVariants(String id, RegistrySupplier<Block> layer, RegistrySupplier<CreativeModeTab> tab) {
		ITEMS.register(Terrain.getID("loose_"+id+"_block"), () -> new LayerItem(layer.get(), new Item.Properties().arch$tab(tab), 8, "_block"));
		ITEMS.register(Terrain.getID("loose_"+id+"_slab"), () -> new LayerItem(layer.get(), new Item.Properties().arch$tab(tab), 4, "_slab"));
		return ITEMS.register(Terrain.getID("loose_"+id), () -> new LayerItem(layer.get(), new Item.Properties().arch$tab(tab), 1));
	}
	public static RegistrySupplier<Item> registerLayerItemVariants(String id, RegistrySupplier<Block> layer, ResourceKey<CreativeModeTab> tab) {
		ITEMS.register(Terrain.getID("loose_"+id+"_block"), () -> new LayerItem(layer.get(), new Item.Properties().arch$tab(tab), 8, "_block"));
		ITEMS.register(Terrain.getID("loose_"+id+"_slab"), () -> new LayerItem(layer.get(), new Item.Properties().arch$tab(tab), 4, "_slab"));
		return ITEMS.register(Terrain.getID("loose_"+id), () -> new LayerItem(layer.get(), new Item.Properties().arch$tab(tab), 1));
	}

	public static void registerLayerItemVariants(String id, Block layer) {
		ITEMS.register(Terrain.getID("loose_"+id+"_block"), () -> new LayerItem(layer, new Item.Properties(), 8, "_block"));
		ITEMS.register(Terrain.getID("loose_"+id+"_slab"), () -> new LayerItem(layer, new Item.Properties(), 4, "_slab"));
		ITEMS.register(Terrain.getID("loose_"+id), () -> new LayerItem(layer, new Item.Properties(), 1));
	}
	public static RegistrySupplier<Item> registerLayerItemVariants(String id, Block layer, RegistrySupplier<CreativeModeTab> tab) {
		ITEMS.register(Terrain.getID("loose_"+id+"_block"), () -> new LayerItem(layer, new Item.Properties().arch$tab(tab), 8, "_block"));
		ITEMS.register(Terrain.getID("loose_"+id+"_slab"), () -> new LayerItem(layer, new Item.Properties().arch$tab(tab), 4, "_slab"));
		return ITEMS.register(Terrain.getID("loose_"+id), () -> new LayerItem(layer, new Item.Properties().arch$tab(tab), 1));
	}
	public static RegistrySupplier<Item> registerLayerItemVariants(String id, Block layer, ResourceKey<CreativeModeTab> tab) {
		ITEMS.register(Terrain.getID("loose_"+id+"_block"), () -> new LayerItem(layer, new Item.Properties().arch$tab(tab), 8, "_block"));
		ITEMS.register(Terrain.getID("loose_"+id+"_slab"), () -> new LayerItem(layer, new Item.Properties().arch$tab(tab), 4, "_slab"));
		return ITEMS.register(Terrain.getID("loose_"+id), () -> new LayerItem(layer, new Item.Properties().arch$tab(tab), 1));
	}

	public static void register() {
		Terrain.LOGGER.info("Registering items!");
		ITEMS.register();
	}
}
