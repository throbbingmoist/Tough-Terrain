package net.moist.neoforge.client;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.moist.Terrain;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.windows.MONITORINFOEX;

import java.util.Map;


@EventBusSubscriber(modid = Terrain.MOD_ID, value = Dist.CLIENT)
public class TerrainNeoforgeClient {

	@SubscribeEvent
	public static void modifyBakingResult(ModelEvent.ModifyBakingResult event) {
		Map<ModelResourceLocation, BakedModel> models = event.getModels();
		models.keySet().stream()
			.filter(modelLocation -> modelLocation.id().getNamespace().equals("minecraft") && modelLocation.id().getPath().contains("snow") && modelLocation.getVariant().contains("layers"))
			.toList()
			.forEach(modelLocation -> {
				Terrain.LOGGER.info("Loading " + modelLocation.id() + " , " + modelLocation.getVariant() + " of " + modelLocation);
				event.getModelBakery().getBakedTopLevelModels().compute(modelLocation, (z, originalModel) -> new ShiftedSnowModel(originalModel));
			});
	}
}
