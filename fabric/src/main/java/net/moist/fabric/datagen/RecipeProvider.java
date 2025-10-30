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

		// Datagen Testing recipes.
//		TransformRecipe.builder()
//			.input(ModBlocks.LOOSE_DIRT.get())
//			.transformItem(Ingredient.of(Items.CLAY_BALL))
//			.sound(SoundEvents.SLIME_ATTACK)
//			.particles(ParticleTypes.ITEM, 25)
//			.result(Blocks.DIRT)
//			.build("test_recipe", exporter);
//		TransformRecipe.builder()
//			.input(ModBlocks.LOOSE_SAND.get())
//			.transformItem(Ingredient.of(Items.RED_DYE))
//			.sound(SoundEvents.SLIME_ATTACK)
//			.particles(ParticleTypes.BLOCK, 25)
//			.result(ModBlocks.LOOSE_RED_SAND.get().defaultBlockState().setValue(FallingLayer.LAYERS, FallingLayer.MAX_LAYERS))
//			.build("test_recipe_state", exporter);
//		TransformRecipe.builder()
//			.input(ModBlocks.LOOSE_SAND.get())
//			.transformItem(Ingredient.of(Items.CLAY_BALL))
//			.sound(SoundEvents.SLIME_ATTACK)
//			.particles(ParticleTypes.BLOCK, 25)
//			.result(Blocks.SAND)
//			.build("test_recipe_block", exporter);
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
