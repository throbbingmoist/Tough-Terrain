package net.moist.fabric.client;

import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.impl.client.model.loading.ModelLoaderHooks;
import net.fabricmc.fabric.impl.client.model.loading.ModelLoadingConstants;
import net.fabricmc.fabric.impl.client.model.loading.ModelLoadingEventDispatcher;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.moist.Terrain;
import net.moist.TerrainClient;
import net.moist.block.ModBlocks;
import net.moist.block.entity.LayerBER;
import net.moist.block.entity.TerrainBlockEntities;
import org.jetbrains.annotations.Nullable;

public final class ModFabricClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
//		BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(), ModBlocks.GRASS_LAYER.getPlacedLayer(), ModBlocks.MYCELIUM_LAYER.getPlacedLayer());

		BlockEntityRendererRegistry.register(TerrainBlockEntities.LAYER_BE.get(), context -> new LayerBER());
		ModelLoadingPlugin.register(pluginContext -> {
			pluginContext.modifyModelAfterBake().register((model, context) -> {
				@Nullable ResourceLocation modelID = context.resourceId();
				if (modelID != null) {
					if (modelID.getNamespace().equals("minecraft") && modelID.getPath().contains("block/snow")) {
						return new HideableSnowModel(model);
					}
				}
				return model;
			});
		});
	}


}
