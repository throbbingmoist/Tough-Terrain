package net.moist.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.moist.Terrain;
import net.moist.block.ModBlocks;

public final class ModFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		Terrain.LOGGER.info("Putting Block Render types in.");
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(), ModBlocks.GRASS_LAYER.get(), ModBlocks.MYCELIUM_LAYER.get());
	}

	public void putInstance(Block block, RenderType type) {
	}
}
