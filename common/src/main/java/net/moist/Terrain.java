package net.moist;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.moist.block.ModBlocks;
import net.moist.event.LoosenSoilEvent;
import net.moist.item.ModCreativeTabs;
import net.moist.item.ModItems;
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

		ColorHandler.registerBlockColor(ModBlocks.LOOSE_DIRT, ModBlocks.LOOSE_SAND, ModBlocks.LOOSE_RED_SAND, ModBlocks.LOOSE_GRAVEL);
		ColorHandler.registerGrassColor(ModBlocks.GRASS_LAYER);

		LoosenSoilEvent.subscribe();
	}
}
