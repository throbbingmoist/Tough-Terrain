package net.moist.neoforge;

import com.sun.jdi.InvalidTypeException;
import net.minecraft.client.resources.model.BakedModel;
import net.moist.neoforge.client.HideableSnowModel;

public class HideableSnowModelHelperImpl {
	public static BakedModel shouldForceRender(BakedModel model) throws Exception {
		if (model instanceof HideableSnowModel) return ((HideableSnowModel) model).shouldForceRender();
		else throw new InvalidTypeException("model '"+model+"'is not HideableSnowModel!");
	}
}
