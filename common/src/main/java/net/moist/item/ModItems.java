package net.moist.item;

import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.moist.Terrain;

public class ModItems {
	public static DeferredRegister<Item> ITEMS = DeferredRegister.create(Terrain.MOD_ID, Registries.ITEM);

	public static void register() {
		Terrain.LOGGER.debug("Registering items!");
		ITEMS.register();
	}
}
