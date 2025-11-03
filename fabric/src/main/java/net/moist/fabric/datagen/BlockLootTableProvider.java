package net.moist.fabric.datagen;

import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.moist.block.ModBlocks;
import net.moist.block.content.FallingLayer;

import java.util.concurrent.CompletableFuture;

public class BlockLootTableProvider extends FabricBlockLootTableProvider {
	protected BlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(dataOutput, registryLookup);
	}

	@Override
	public void generate() {

		createLayerDropTable(Blocks.GRASS_BLOCK, ModBlocks.LOOSE_DIRT.getPlacedLayer(), 8);
		createLayerDropTable(Blocks.MYCELIUM, ModBlocks.LOOSE_DIRT.getPlacedLayer(), 8);
		createLayerDropTable(Blocks.PODZOL, ModBlocks.LOOSE_DIRT.getPlacedLayer(), 8);

		createLayerDropTable(Blocks.DIRT_PATH, ModBlocks.LOOSE_DIRT.getPlacedLayer(), 8);
		createLayerDropTable(Blocks.COARSE_DIRT, ModBlocks.LOOSE_DIRT.getPlacedLayer(), 8);
		createLayerDropTable(Blocks.FARMLAND, ModBlocks.LOOSE_DIRT.getPlacedLayer(), 8);
		createLayerDropTable(Blocks.COARSE_DIRT, ModBlocks.LOOSE_DIRT.getPlacedLayer(), 8);
		createLayerDropTable(Blocks.ROOTED_DIRT, ModBlocks.LOOSE_DIRT.getPlacedLayer(), 8);

		createLayerDropTable(Blocks.DIRT, ModBlocks.LOOSE_DIRT.getPlacedLayer(), 8);
		createLayerDropTable(Blocks.SAND, ModBlocks.LOOSE_SAND.getPlacedLayer(), 8);
		createLayerDropTable(Blocks.RED_SAND, ModBlocks.LOOSE_RED_SAND.getPlacedLayer(), 8);
		createLayerDropTable(Blocks.GRAVEL, ModBlocks.LOOSE_GRAVEL.getPlacedLayer(), 8);

		createLayerTables(FallingLayer.LAYERS, ModBlocks.LOOSE_DIRT.placedLayer(), ModBlocks.LOOSE_SAND.placedLayer(), ModBlocks.LOOSE_RED_SAND.placedLayer(), ModBlocks.LOOSE_GRAVEL.placedLayer());
		createGrassLayerTables(FallingLayer.LAYERS, ModBlocks.GRASS_LAYER.placedLayer(), ModBlocks.MYCELIUM_LAYER.placedLayer());
		//createLayerTable(ModBlocks.LOOSE_RED_CONCRETE_POWDER, LayerBlock.LAYERS);
	}
	@SafeVarargs
	public final void createLayerTables(IntegerProperty LayerProperty, RegistrySupplier<Block>... block) {
		for (RegistrySupplier<Block> holder : block) {
			createLayerTable(LayerProperty, holder, holder.get());
		}
	}
	@SafeVarargs
	public final void createGrassLayerTables(IntegerProperty LayerProperty, RegistrySupplier<Block>... block) {
		for (RegistrySupplier<Block> holder : block) {
			createLayerTable(LayerProperty, holder, ModBlocks.LOOSE_DIRT.getPlacedLayer());
		}
	}
	public void createLayerTable(IntegerProperty LayerProperty, RegistrySupplier<Block> block) {
		createLayerTable(LayerProperty, block, block.get());
	}
	public void createLayerTable(IntegerProperty LayerProperty, RegistrySupplier<Block> block, ItemLike ToDrop) {
		LootTable.Builder builder = LootTable.lootTable();
		for (Integer layers : LayerProperty.getPossibleValues()) {
			builder.withPool(LootPool.lootPool()
				.setRolls(ConstantValue.exactly(1.0F))
				.add(LootItem.lootTableItem(ToDrop)
					.apply(SetItemCountFunction.setCount(ConstantValue.exactly(layers)))
					.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block.get())
						.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(LayerProperty, layers)))
				)
			);
		}
		add(block.get(), builder);
	}
	public void createLayerDropTable(Block block, Block layerBlock, int layersToDrop) {

		LootTable.Builder builder = LootTable.lootTable();
		builder.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1f))
			.add(LootItem.lootTableItem(block))
			.apply(SetItemCountFunction.setCount(ConstantValue.exactly(1f)))
			.when(this.hasSilkTouch())
		);
		builder.withPool(LootPool.lootPool()
			.setRolls(ConstantValue.exactly(1f))
			.add(LootItem.lootTableItem(layerBlock))
			.apply(SetItemCountFunction.setCount(ConstantValue.exactly(layersToDrop)))
			.when(this.doesNotHaveSilkTouch())
		);

		add(block, builder);
	}

	public LootTable.Builder createNumberedTable(ItemLike itemLike, float count) {
		return LootTable.lootTable().withPool((LootPool.Builder)this.applyExplosionCondition(itemLike, LootPool.lootPool().setRolls(ConstantValue.exactly(count)).add(LootItem.lootTableItem(itemLike))));
	}
}
