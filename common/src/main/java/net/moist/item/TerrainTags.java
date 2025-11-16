package net.moist.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.moist.Terrain;

public class TerrainTags {
	public static final TagKey<Item> NON_DISTURBING_SHOVELS = TagKey.create(Registries.ITEM, Terrain.getID("packing_shovels"));
}
