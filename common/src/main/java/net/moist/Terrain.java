package net.moist;

import net.minecraft.resources.ResourceLocation;
import net.moist.block.ModBlocks;
import net.moist.event.ToughTerrainEvents;
import net.moist.item.ModCreativeTabs;
import net.moist.item.ModItems;
import net.moist.recipe.ModRecipes;
import net.moist.util.ColorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public final class Terrain {
	public static final String MOD_ID = "tough_terrain";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	public static ResourceLocation getID(String id) {
		return ResourceLocation.fromNamespaceAndPath(Terrain.MOD_ID, id);
	}


	public static void init() {
		ModCreativeTabs.initTabs();

		ModBlocks.register();
		ModItems.register();

		ColorHandler.registerBlockColor(ModBlocks.LOOSE_DIRT.placedLayer(), ModBlocks.LOOSE_SAND.placedLayer(), ModBlocks.LOOSE_RED_SAND.placedLayer(), ModBlocks.LOOSE_GRAVEL.placedLayer());
		ColorHandler.registerGrassColor(ModBlocks.GRASS_LAYER.placedLayer());
		ColorHandler.registerGrassItemColor(ModBlocks.GRASS_LAYER.heldItem(), ModBlocks.GRASS_LAYER.heldSlab(), ModBlocks.GRASS_LAYER.heldBlock());

		ModRecipes.setup();

		ToughTerrainEvents.subscribe();
	}
}
