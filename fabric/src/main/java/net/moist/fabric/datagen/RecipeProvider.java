package net.moist.fabric.datagen;

import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.*;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.moist.Terrain;
import net.moist.block.ModBlocks;
import net.moist.block.content.FallingLayer;
import net.moist.item.ModItems;
import net.moist.recipe.LooseningRecipe;
import net.moist.recipe.TransformRecipe;
import org.jetbrains.annotations.Nullable;

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

		createLooseningRecipe(exporter, "loosen_dirt", Blocks.DIRT, ModBlocks.LOOSE_DIRT.get());

		// Datagen Testing recipes.
		//createTransformRecipe(exporter, "bottle_convert_dirt", ModBlocks.LOOSE_DIRT.get(), Items.POTION, SoundEvents.BOTTLE_EMPTY, Blocks.DIRT);
		//createTransformRecipe(exporter, "clay_convert_dirt", ModBlocks.LOOSE_DIRT.get(), Items.CLAY_BALL, SoundEvents.SLIME_ATTACK, Blocks.DIRT);
	}


	public void createLooseningRecipe(RecipeOutput output, String id, Block input, Block result) {
		BlockState finalResult = result.defaultBlockState().hasProperty(FallingLayer.LAYERS) ? result.defaultBlockState().setValue(FallingLayer.LAYERS, FallingLayer.MAX_LAYERS) : result.defaultBlockState();
		output.accept(Terrain.getID(id), new LooseningRecipe(input, finalResult), null);
	}
	public void createTransformRecipe(RecipeOutput output, String id, Block input, Item transformItem, Block result) {createTransformRecipe(output, id, input, Ingredient.of(transformItem), null, result);}
	public void createTransformRecipe(RecipeOutput output, String id, Block input, Item transformItem, SoundEvent event, Block result) {createTransformRecipe(output, id, input, Ingredient.of(transformItem), event, result);}
	public void createTransformRecipe(RecipeOutput output, String id, Block input, Ingredient transformItem, @Nullable SoundEvent event, Block result) {
		BlockState finalResult = result.defaultBlockState().hasProperty(FallingLayer.LAYERS) ? result.defaultBlockState().setValue(FallingLayer.LAYERS, FallingLayer.MAX_LAYERS) : result.defaultBlockState();
		output.accept(Terrain.getID(id), new TransformRecipe(input, transformItem, event, finalResult), null);
	}

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
