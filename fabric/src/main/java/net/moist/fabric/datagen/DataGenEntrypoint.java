package net.moist.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class DataGenEntrypoint implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(LanguageProvider::new);
		pack.addProvider(RecipeProvider::new);
		pack.addProvider(TagProviders.ModBlockTags::new);
		pack.addProvider(TagProviders.ModItemTags::new);
		pack.addProvider(BlockLootTableProvider::new);
		pack.addProvider(ModelStateProvider::new);
	}
}
