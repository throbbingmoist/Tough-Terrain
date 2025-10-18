package net.moist.util;

import net.minecraft.world.level.block.Block;
import net.moist.Terrain;

public record ConcretePowderHelper(Block powder, Block concrete) {
	public ConcretePowderHelper {
	}

	public Block getPowder() {
		return powder;
	}

	public Block getConcrete() {
		return concrete;
	}
}
