package net.moist.block.builder;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ConcreteBuilder {
	Boolean can_be_overgrown;
	String name;
	BlockBehaviour.Properties properties;
	RegistrySupplier<CreativeModeTab> tab;

	public ConcreteBuilder() {
		this.can_be_overgrown = false;
		this.name = null;
		this.properties = null;
		this.tab = null;
	}
}
