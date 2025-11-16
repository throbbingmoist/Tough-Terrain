package net.moist;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.resources.model.BakedModel;
public class HideableSnowModelHelper {
	@ExpectPlatform
	public static BakedModel shouldForceRender(BakedModel blockModel) {
		throw new RuntimeException();
	}
}
