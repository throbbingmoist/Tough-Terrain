package net.moist;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.HitResult;
import net.moist.block.ModBlocks;
import net.moist.block.entity.TerrainBlockEntities;
import net.moist.event.ToughTerrainEvents;
import net.moist.item.ModCreativeTabs;
import net.moist.item.ModItems;
import net.moist.recipe.ModRecipes;
import net.moist.util.ColorHandler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public final class Terrain {
	public static final String MOD_ID = "tough_terrain";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	public static ResourceLocation getID(String id) {
		return ResourceLocation.fromNamespaceAndPath(Terrain.MOD_ID, id);
	}



	public static double getLookGranular(@Nullable HitResult hitResult) {
		if (hitResult != null) {
			return hitResult.getLocation().get(Direction.Axis.Y) % 1;
		}
		return 0.0d;
	}

	public static void init() {
		ModCreativeTabs.initTabs();

		ModBlocks.register();
		ModItems.register();
		TerrainBlockEntities.register();


		ColorHandler.registerBlockColor(ModBlocks.LOOSE_DIRT.placedLayer(), ModBlocks.LOOSE_SAND.placedLayer(), ModBlocks.LOOSE_RED_SAND.placedLayer(), ModBlocks.LOOSE_GRAVEL.placedLayer());
		ColorHandler.registerGrassColor(ModBlocks.GRASS_LAYER.placedLayer());
		ColorHandler.registerGrassItemColor(ModBlocks.GRASS_LAYER.heldItem(), ModBlocks.GRASS_LAYER.heldSlab(), ModBlocks.GRASS_LAYER.heldBlock());

		ModRecipes.setup();


		ToughTerrainEvents.subscribe();
	}
}
