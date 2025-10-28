package net.moist.neoforge;

import net.moist.Terrain;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

@Mod(Terrain.MOD_ID)
public final class TerrainNeoForge {
	public TerrainNeoForge() {
		Terrain.init();
	}
}
