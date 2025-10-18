package net.moist.item;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.moist.Terrain;

public class ModCreativeTabs {
	private static final DeferredRegister<CreativeModeTab> TABS =
		DeferredRegister.create(Terrain.MOD_ID, Registries.CREATIVE_MODE_TAB);

	public static RegistrySupplier<CreativeModeTab> TOUGH_TERRAIN_TAB;

	public static void initTabs(){
		Terrain.LOGGER.info("Registering tabs!");
		TOUGH_TERRAIN_TAB = TABS.register("ground",
			() -> CreativeTabRegistry.create(Component.translatable("category.tough_terrain"),
				() -> new ItemStack(Items.IRON_SHOVEL)
			));
		TABS.register();
	}
}
