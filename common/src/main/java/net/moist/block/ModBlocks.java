package net.moist.block;

import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.state.BlockState;
import net.moist.block.builder.LayerBuilder;
import net.moist.block.content.FallingLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.moist.Terrain;
import net.moist.block.content.SpreadingLayer;
import net.moist.item.ModCreativeTabs;
import net.moist.block.record.LayerBlockSet;

public class ModBlocks {
	public static DeferredRegister<Block> BLOCKS = DeferredRegister.create(Terrain.MOD_ID, Registries.BLOCK);

	public static final TagKey<Block> LOOSENS_SURROUNDINGS = TagKey.create(Registries.BLOCK, Terrain.getID("loosens_surroundings"));

	public static final LayerBlockSet LOOSE_DIRT = LayerBlockSet.layerSet(LayerBuilder.falling().overgrowable().named("loose_dirt").withTab(ModCreativeTabs.TOUGH_TERRAIN_TAB).basedOn(Blocks.DIRT));
	public static final LayerBlockSet LOOSE_SAND = LayerBlockSet.layerSet(LayerBuilder.falling().named("loose_sand").withTab(ModCreativeTabs.TOUGH_TERRAIN_TAB).basedOn(Blocks.SAND));
	public static final LayerBlockSet LOOSE_RED_SAND = LayerBlockSet.layerSet(LayerBuilder.falling().named("loose_red_sand").withTab(ModCreativeTabs.TOUGH_TERRAIN_TAB).basedOn(Blocks.RED_SAND));
	public static final LayerBlockSet LOOSE_GRAVEL = LayerBlockSet.layerSet(LayerBuilder.falling().named("loose_gravel").withTab(ModCreativeTabs.TOUGH_TERRAIN_TAB).basedOn(Blocks.GRAVEL));

	public static final LayerBlockSet GRASS_LAYER = LayerBlockSet.layerSet(LayerBuilder.spreading().named("grass_layer").withTab(ModCreativeTabs.TOUGH_TERRAIN_TAB).basedOn(Blocks.GRASS_BLOCK));
	public static final LayerBlockSet MYCELIUM_LAYER = LayerBlockSet.layerSet(LayerBuilder.spreading().named("mycelium_layer").withTab(ModCreativeTabs.TOUGH_TERRAIN_TAB).basedOn(Blocks.MYCELIUM));

	public static boolean IsLayerOvergrowable(BlockState state) {
		return IsLayerOvergrowable(state.getBlock());
	}
	public static boolean IsLayerOvergrowable(Block state) {
		if (state instanceof FallingLayer) { return ((FallingLayer) state).overgrowable();}
		if (state instanceof SpreadingLayer) { return ((SpreadingLayer) state).overgrowable();}
		return false;
	}

	public static BlockState GetFullBlockToGrow(BlockState state) {
		if (state.is(ModBlocks.GRASS_LAYER.getPlacedLayer())) { return Blocks.GRASS_BLOCK.defaultBlockState(); }
		if (state.is(ModBlocks.MYCELIUM_LAYER.getPlacedLayer())) { return Blocks.MYCELIUM.defaultBlockState(); }
		return null;
	}
	public static BlockState GetLayerBlockToGrow(BlockState state, int i) {
		if (state.is(Blocks.GRASS_BLOCK)) { return GRASS_LAYER.getPlacedLayer().defaultBlockState().setValue(FallingLayer.LAYERS, i); }
		if (state.is(Blocks.MYCELIUM)) { return MYCELIUM_LAYER.getPlacedLayer().defaultBlockState().setValue(FallingLayer.LAYERS, i); }
		return null;
	}

	public static void register() {
		Terrain.LOGGER.debug("Registering blocks!");

		BLOCKS.register();
	}



}
