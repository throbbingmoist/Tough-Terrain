package net.moist.neoforge;

import dev.architectury.platform.Platform;
import net.moist.Terrain;
import net.moist.TerrainClient;
import net.neoforged.fml.common.Mod;

@Mod(Terrain.MOD_ID)
public final class TerrainNeoForge {
	public TerrainNeoForge() {
		Terrain.init();
	}
}
