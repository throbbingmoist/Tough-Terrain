package net.moist.neoforge.client;

import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.moist.Terrain;
import net.moist.block.content.FallingLayer;
import net.moist.block.entity.LayerBER;
import net.moist.block.entity.TerrainBlockEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.SimpleModelState;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;


@EventBusSubscriber(modid = Terrain.MOD_ID, value = Dist.CLIENT)
public class TerrainNeoforgeClient {
	@SubscribeEvent
	public static void modifyBakingResult(ModelEvent.ModifyBakingResult event) {
		Map<ModelResourceLocation, BakedModel> models = event.getModels();

		models.keySet().stream().filter(modelLocation -> modelLocation.id().getNamespace().equals("minecraft") && modelLocation.id().getPath().contains("snow") && modelLocation.getVariant().contains("layers")).toList()
			.forEach(modelLocation -> {
				Terrain.LOGGER.info("Loading " + modelLocation.id() + " , " + modelLocation.getVariant() + " of " + modelLocation);
				event.getModelBakery().getBakedTopLevelModels().compute(modelLocation, (z, originalModel) -> new HideableSnowModel(originalModel));
			});
	}

	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(TerrainBlockEntities.LAYER_BE.get(), context -> new LayerBER());
	}
}
