package net.moist.block;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.moist.block.content.LayerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.moist.Terrain;
import net.moist.item.ModCreativeTabs;
import net.moist.item.ModItems;

import static net.moist.event.LoosenSoilEvent.loosenBlock;

public class ModBlocks {
	public static DeferredRegister<Block> BLOCKS = DeferredRegister.create(Terrain.MOD_ID, Registries.BLOCK);


	public static  RegistrySupplier<Block> LOOSE_DIRT = registerLayeredBlockPackable("dirt",Blocks.DIRT,Blocks.DIRT, true);
	public static  RegistrySupplier<Block> LOOSE_SAND = registerLayeredBlockPackable("sand",Blocks.SAND,Blocks.SAND);
	public static  RegistrySupplier<Block> LOOSE_RED_SAND = registerLayeredBlockPackable("red_sand",Blocks.RED_SAND,Blocks.RED_SAND);
	public static  RegistrySupplier<Block> LOOSE_GRAVEL = registerLayeredBlockPackable("gravel",Blocks.GRAVEL,Blocks.GRAVEL);

	//public static  RegistrySupplier<Block> LOOSE_RED_CONCRETE_POWDER = registerLayeredBlockWithTint("red_concrete_powder",Blocks.RED_CONCRETE_POWDER,Blocks.RED_CONCRETE);

	public static RegistrySupplier<Block> registerLayeredBlockPackable(String name, Block basedOn) {
		return registerLayeredBlockPackable(name, basedOn, Blocks.AIR, false);
	}
	public static RegistrySupplier<Block> registerLayeredBlockPackable(String name, Block basedOn, boolean becomesGrass) {
		return registerLayeredBlockPackable(name, basedOn, Blocks.AIR, becomesGrass);}
	public static RegistrySupplier<Block> registerLayeredBlockPackable(String name, Block basedOn, Block toPack) {
		return registerLayeredBlockPackable(name, basedOn, toPack, false);}
	public static RegistrySupplier<Block> registerLayeredBlockPackable(String name, Block basedOn, Block toPack, boolean becomesGrass) {
		RegistrySupplier<Block> pain = BLOCKS.register(Terrain.getID("loose_" + name), () -> new LayerBlock(BlockBehaviour.Properties.ofFullCopy(basedOn)).packsTo(toPack).overgrowable(becomesGrass));
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
		RegistrySupplier<Block> pain = BLOCKS.register(Terrain.getID("loose_" + name), () -> new LayerBlock(BlockBehaviour.Properties.ofFullCopy(basedOn)).packsTo(toPack).overgrowable(becomesGrass));
		ModItems.registerLayerItemVariants(name, pain, ModCreativeTabs.TOUGH_TERRAIN_TAB);
		return pain;
	}

	public static void register() {
		Terrain.LOGGER.info("Registering blocks!");

		BLOCKS.register();
		loosenBlock(Blocks.DIRT,LOOSE_DIRT);
	}
}
