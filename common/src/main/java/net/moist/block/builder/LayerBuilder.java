package net.moist.block.builder;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.moist.block.content.FallingLayer;
import net.moist.block.content.FloatingLayer;
import net.moist.block.content.SpreadingLayer;

public abstract class LayerBuilder {
	Boolean can_be_overgrown;
	String name;
	BlockBehaviour.Properties properties;
	RegistrySupplier<CreativeModeTab> tab;

	public LayerBuilder() {
		this.can_be_overgrown = false;
		this.name = null;
		this.properties = null;
		this.tab = null;
	}

	public static LayerBuilder falling() {return new Falling();}
	public static LayerBuilder floating() {return new Floating();}
	public static LayerBuilder spreading() {return new Spreading();}

	 public String name() {return this.name;}
	public RegistrySupplier<CreativeModeTab> tab() {return this.tab;}

	public LayerBuilder canOvergrow(boolean bool) {this.can_be_overgrown = bool; return this;}
	public LayerBuilder overgrowable() {this.can_be_overgrown = true; return this;}
	public LayerBuilder named(String name) {this.name = name; return this;}
	public LayerBuilder withTab(RegistrySupplier<CreativeModeTab> tab) {this.tab = tab; return this;}
	public LayerBuilder basedOn(Block block) {return this.withProperties(BlockBehaviour.Properties.ofFullCopy(block).dynamicShape());}
	public LayerBuilder withProperties(BlockBehaviour.Properties properties) {this.properties = properties;return this;}

	public Block build() {return new Block(properties);}
	public RegistrySupplier<Block> buildAndRegister(DeferredRegister<Block> blockDeferredRegister) {return blockDeferredRegister.register(name, this::build);}


	public static class Falling extends LayerBuilder {
		public Falling() {
			super();
		}
		@Override public FallingLayer build() {return new FallingLayer(properties, can_be_overgrown);}
		@Override public RegistrySupplier<Block> buildAndRegister(DeferredRegister<Block> blockDeferredRegister) {return blockDeferredRegister.register(name, this::build);}
	}

	public static class Floating extends LayerBuilder {
		public Floating() {
			super();
		}

		@Override public FloatingLayer build() {return new FloatingLayer(properties, can_be_overgrown);}
		public RegistrySupplier<Block> buildAndRegister(DeferredRegister<Block> blockDeferredRegister) {return blockDeferredRegister.register(name, this::build);}
	}

	public static class Spreading extends LayerBuilder {
		public Spreading() {
			super();
		}
		@Override public SpreadingLayer build() {return new SpreadingLayer(properties, can_be_overgrown);}
		public RegistrySupplier<Block> buildAndRegister(DeferredRegister<Block> blockDeferredRegister) {return blockDeferredRegister.register(name, this::build);}
	}
}
