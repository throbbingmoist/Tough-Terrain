package net.moist.util;

import net.minecraft.client.resources.model.BakedModel;

public interface ForceRenderableModel extends BakedModel {
	default BakedModel shouldForceRender() {
		return this;
	}
}
