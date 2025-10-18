package net.moist.fabric.datagen;

import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.moist.Terrain;
import net.moist.block.ModBlocks;
import net.moist.item.ModItems;

import java.util.concurrent.CompletableFuture;

public class RecipeProvider extends FabricRecipeProvider {
	public RecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	public void buildRecipes(RecipeOutput exporter) {
		//ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.LOOSE_DIRT.get(), 1);
		createLayerRecipes(exporter, ModBlocks.LOOSE_DIRT);
		createLayerRecipes(exporter, ModBlocks.LOOSE_SAND);
		createLayerRecipes(exporter, ModBlocks.LOOSE_RED_SAND);
		createLayerRecipes(exporter, ModBlocks.LOOSE_GRAVEL);

		SimpleCookingRecipeBuilder
			.smelting(Ingredient.of(ModItems.ITEMS.getRegistrar().get(Terrain.getID("loose_sand_block")), ModItems.ITEMS.getRegistrar().get(Terrain.getID("loose_red_sand_block"))), RecipeCategory.BUILDING_BLOCKS, Blocks.GLASS, 0.1f, 200)
			.unlockedBy("any", RecipeProvider.has(ModBlocks.LOOSE_SAND.get()))
			.save(exporter, Terrain.getID("smelt_loose_sand_block"));
	}
//
	public void createLayerRecipes(RecipeOutput output, RegistrySupplier<Block> block) {

		String id = block.get().asItem().getDescriptionId().replace("block." + Terrain.MOD_ID + ".", "");

		Terrain.LOGGER.info(id);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModItems.ITEMS.getRegistrar().get(Terrain.getID(id+"_block")), 1)
			.requires(ModItems.ITEMS.getRegistrar().get(Terrain.getID(id+"_slab")), 2)
			.unlockedBy("any", RecipeProvider.has(block.get()))
			.save(output, Terrain.getID("craft_"+id+"_block"));
		ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModItems.ITEMS.getRegistrar().get(Terrain.getID(id+"_slab")), 1)
			.requires(block.get(), 4)
			.unlockedBy("any", RecipeProvider.has(block.get()))
			.save(output, Terrain.getID("craft_"+id+"_slab"));

	}
}
