package net.moist.fabric.datagen;

import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;
import net.moist.block.ModBlocks;
import net.moist.item.ModCreativeTabs;

import java.util.concurrent.CompletableFuture;

public class LanguageProvider extends FabricLanguageProvider {
	protected LanguageProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(dataOutput, "en_us", registryLookup);
	}

	@Override
	public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
		translateLayers(translationBuilder,ModBlocks.LOOSE_DIRT.placedLayer(), "Loose Dirt");
		translateLayers(translationBuilder,ModBlocks.LOOSE_SAND.placedLayer(), "Loose Sand");
		translateLayers(translationBuilder,ModBlocks.LOOSE_RED_SAND.placedLayer(), "Loose Red Sand");
		translateLayers(translationBuilder,ModBlocks.LOOSE_GRAVEL.placedLayer(), "Loose Gravel");

		translateLayers(translationBuilder,ModBlocks.GRASS_LAYER.placedLayer(), "Grass Layer");
		translateLayers(translationBuilder,ModBlocks.MYCELIUM_LAYER.placedLayer(), "Mycelium Layer");

		translationBuilder.add(ModCreativeTabs.TOUGH_TERRAIN_TAB.getKey(), "Tough Terrain");
	}

	public void translateLayers(TranslationBuilder translationBuilder, RegistrySupplier<Block> block, String blockName) {
		translationBuilder.add(block.get().asItem().getDescriptionId(), blockName);
		translationBuilder.add(block.get().asItem().getDescriptionId(block.get().asItem().getDefaultInstance())+"_block", blockName+" Block");
		translationBuilder.add(block.get().asItem().getDescriptionId(block.get().asItem().getDefaultInstance())+"_slab", blockName+" Slab");

	}
}
