package net.moist.fabric.datagen;

import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
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

		TransformRecipe.builder()
			.input(ModBlocks.LOOSE_DIRT.get()).result(Blocks.DIRT).transformItem(Ingredient.of(Items.POTION))
			.sound(SoundEvents.BOTTLE_EMPTY).particles(ParticleTypes.SPLASH, 75)
			.build("transform_dirt_with_bottle", exporter);

		TransformRecipe.builder()
			.input(ModBlocks.LOOSE_SAND.get()).result(Blocks.SAND).transformItem(Ingredient.of(Items.POTION))
			.sound(SoundEvents.BOTTLE_EMPTY).particles(ParticleTypes.SPLASH, 75)
			.build("transform_sand_with_bottle", exporter);

		TransformRecipe.builder()
			.input(ModBlocks.LOOSE_RED_SAND.get()).result(Blocks.RED_SAND).transformItem(Ingredient.of(Items.POTION))
			.sound(SoundEvents.BOTTLE_EMPTY).particles(ParticleTypes.SPLASH, 75)
			.build("transform_red_sand_with_bottle", exporter);

		TransformRecipe.builder()
			.input(ModBlocks.LOOSE_GRAVEL.get()).result(Blocks.GRAVEL).transformItem(Ingredient.of(Items.POTION))
			.sound(SoundEvents.BOTTLE_EMPTY).particles(ParticleTypes.SPLASH, 75)
			.build("transform_gravel_with_bottle", exporter);
	}


	public void createLooseningRecipe(RecipeOutput output, String id, Block input, Block result) {
		BlockState finalResult = result.defaultBlockState().hasProperty(FallingLayer.LAYERS) ? result.defaultBlockState().setValue(FallingLayer.LAYERS, FallingLayer.MAX_LAYERS) : result.defaultBlockState();
		output.accept(Terrain.getID(id), new LooseningRecipe(input, finalResult), null);
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
