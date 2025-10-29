package net.moist.block;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.state.BlockState;
import net.moist.block.content.FallingLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.moist.Terrain;
import net.moist.block.content.SpreadingLayer;
import net.moist.item.ModCreativeTabs;
import net.moist.item.ModItems;

public class ModBlocks {
	public static DeferredRegister<Block> BLOCKS = DeferredRegister.create(Terrain.MOD_ID, Registries.BLOCK);

	public static final TagKey<Block> LOOSENS_SURROUNDINGS = TagKey.create(Registries.BLOCK, Terrain.getID("loosens_surroundings"));

	public static  RegistrySupplier<Block> LOOSE_DIRT = registerLayeredBlockPackable("dirt",Blocks.DIRT,Blocks.DIRT, true);
	public static  RegistrySupplier<Block> LOOSE_SAND = registerLayeredBlockPackable("sand",Blocks.SAND,Blocks.SAND);
	public static  RegistrySupplier<Block> LOOSE_RED_SAND = registerLayeredBlockPackable("red_sand",Blocks.RED_SAND,Blocks.RED_SAND);
	public static  RegistrySupplier<Block> LOOSE_GRAVEL = registerLayeredBlockPackable("gravel",Blocks.GRAVEL,Blocks.GRAVEL);

	public static  RegistrySupplier<Block> GRASS_LAYER = registerLayeredGrass("grass_layer",Blocks.GRASS_BLOCK, true);
	public static  RegistrySupplier<Block> MYCELIUM_LAYER = registerLayeredGrass("mycelium_layer",Blocks.MYCELIUM);

	public static RegistrySupplier<Block> registerLayeredBlockPackable(String name, Block basedOn) {
		return registerLayeredBlockPackable(name, basedOn, Blocks.AIR, false);
	}
	public static RegistrySupplier<Block> registerLayeredBlockPackable(String name, Block basedOn, boolean becomesGrass) {
		return registerLayeredBlockPackable(name, basedOn, Blocks.AIR, becomesGrass);}
	public static RegistrySupplier<Block> registerLayeredBlockPackable(String name, Block basedOn, Block toPack) {
		return registerLayeredBlockPackable(name, basedOn, toPack, false);}
	public static RegistrySupplier<Block> registerLayeredBlockPackable(String name, Block basedOn, Block toPack, boolean becomesGrass) {
		RegistrySupplier<Block> pain = BLOCKS.register(Terrain.getID("loose_" + name), () -> new FallingLayer(BlockBehaviour.Properties.ofFullCopy(basedOn)).packsTo(toPack).overgrowable(becomesGrass));
		ModItems.registerLayerItemVariants(name, pain, ModCreativeTabs.TOUGH_TERRAIN_TAB);
		return pain;
	}

	public static RegistrySupplier<Block> registerLayeredBlockWithTint(String name, Block basedOn) {
		return registerLayeredBlockPackable(name, basedOn, Blocks.AIR, false);
	}
	public static RegistrySupplier<Block> registerLayeredBlockWithTint(String name, Block basedOn, boolean becomesGrass) {
		return registerLayeredBlockPackable(name, basedOn, Blocks.AIR, becomesGrass);}
	public static RegistrySupplier<Block> registerLayeredBlockWithTint(String name, Block basedOn, Block toPack) {
		return registerLayeredBlockPackable(name, basedOn, toPack, false);}
	public static RegistrySupplier<Block> registerLayeredBlockWithTint(String name, Block basedOn, Block toPack, boolean becomesGrass) {
		RegistrySupplier<Block> pain = BLOCKS.register(Terrain.getID("loose_" + name), () -> new FallingLayer(BlockBehaviour.Properties.ofFullCopy(basedOn)).packsTo(toPack).overgrowable(becomesGrass));
		ModItems.registerLayerItemVariants(name, pain, ModCreativeTabs.TOUGH_TERRAIN_TAB);
		return pain;
	}

	public static RegistrySupplier<Block> registerConcretePowder(String name, Block basedOn, Block toPack) {
		RegistrySupplier<Block> pain = BLOCKS.register(Terrain.getID("loose_" + name), () -> new FallingLayer(BlockBehaviour.Properties.ofFullCopy(basedOn)).packsTo(toPack));
		ModItems.registerLayerItemVariants(name, pain, ModCreativeTabs.TOUGH_TERRAIN_TAB);
		return pain;
	}

	public static RegistrySupplier<Block> registerLayeredGrass(String name, Block basedOn) {
		return registerLayeredGrass(name, basedOn, false);
	}
	public static RegistrySupplier<Block> registerLayeredGrass(String name, Block basedOn, boolean ShouldTint) {
		RegistrySupplier<Block> pain = BLOCKS.register(Terrain.getID(name), () -> new SpreadingLayer(BlockBehaviour.Properties.ofFullCopy(basedOn)));
		if (ShouldTint) {
			ModItems.registerTintedGrassLayerItemVariants(name, pain, ModCreativeTabs.TOUGH_TERRAIN_TAB);
		} else {
			ModItems.registerGrassLayerItemVariants(name, pain, ModCreativeTabs.TOUGH_TERRAIN_TAB);
		}

		return pain;
	}
	public static RegistrySupplier<Block> registerLayeredGrassNoItems(String name, Block basedOn) {
		RegistrySupplier<Block> pain = BLOCKS.register(Terrain.getID(name), () -> new SpreadingLayer(BlockBehaviour.Properties.ofFullCopy(basedOn)));
		return pain;
	}

	public static boolean IsLayerOvergrowable(BlockState state) {
		return IsLayerOvergrowable(state.getBlock());
	}
	public static boolean IsLayerOvergrowable(Block state) {
		if (state instanceof FallingLayer) { return ((FallingLayer) state).isOvergrowable();}
		if (state instanceof SpreadingLayer) { return ((SpreadingLayer) state).isOvergrowable();}
		return false;
	}

	public static BlockState GetFullBlockToGrow(BlockState state) {
		if (state.is(ModBlocks.GRASS_LAYER)) { return Blocks.GRASS_BLOCK.defaultBlockState(); }
		if (state.is(ModBlocks.MYCELIUM_LAYER)) { return Blocks.MYCELIUM.defaultBlockState(); }
		return null;
	}
	public static BlockState GetLayerBlockToGrow(BlockState state, int i) {
		if (state.is(Blocks.GRASS_BLOCK)) { return GRASS_LAYER.get().defaultBlockState().setValue(FallingLayer.LAYERS, i); }
		if (state.is(Blocks.MYCELIUM)) { return MYCELIUM_LAYER.get().defaultBlockState().setValue(FallingLayer.LAYERS, i); }
		return null;
	}

	public static void register() {
		Terrain.LOGGER.debug("Registering blocks!");

		BLOCKS.register();
	}



}
