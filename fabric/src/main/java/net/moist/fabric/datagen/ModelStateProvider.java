package net.moist.fabric.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.moist.Terrain;
import net.moist.block.ModBlocks;
import net.moist.block.content.LayerBlock;
import net.moist.util.GenericStuff;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.data.models.BlockModelGenerators.createSimpleBlock;

public class ModelStateProvider extends FabricModelProvider {
	public ModelStateProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
		generateLayers(blockStateModelGenerator, ModBlocks.LOOSE_DIRT.get(), Blocks.DIRT, true);
		generateLayers(blockStateModelGenerator, ModBlocks.LOOSE_SAND.get(), Blocks.SAND, true);
		generateLayers(blockStateModelGenerator, ModBlocks.LOOSE_RED_SAND.get(), Blocks.RED_SAND, true);
		generateLayers(blockStateModelGenerator, ModBlocks.LOOSE_GRAVEL.get(), Blocks.GRAVEL, true);

		//generateLayers(blockStateModelGenerator, ModBlocks.LOOSE_RED_CONCRETE_POWDER.get(), Blocks.RED_CONCRETE_POWDER, false, GenericStuff.POWDER);

	}

	public void generateLayers(BlockModelGenerators blockModelGenerators, Block layeredBlock, Block textureBlock, boolean shouldTint, @Nullable String genericItemName) {
		String blockName = layeredBlock.asItem().getDescriptionId().replace("block." + Terrain.MOD_ID + ".", "");
		ModelTemplates.CUBE_ALL.create(layeredBlock, TextureMapping.cube(textureBlock), blockModelGenerators.modelOutput);
		ModelTemplates.SLAB_BOTTOM.create(Terrain.getID("block/"+blockName+"_slab"), TextureMapping.cube(textureBlock), blockModelGenerators.modelOutput);
		for (int i = 1; i <= 8; ++i) {
			generateLayerModel(blockModelGenerators, textureBlock, blockName, i, shouldTint);
		}
		generateLayeredBlockstates(blockModelGenerators, layeredBlock, blockName);
		if (genericItemName != null) {
			generateGenericItems(blockModelGenerators, blockName, genericItemName);

		} else {
			generateLayerItemModels(blockModelGenerators, blockName);
		}
	}
	public void generateLayers(BlockModelGenerators blockModelGenerators, Block layeredBlock, Block textureBlock, boolean shouldTint) {
		generateLayers( blockModelGenerators,  layeredBlock,  textureBlock,  shouldTint, null);
	}

	private static void generateLayerModel(BlockModelGenerators blockModelGenerators, Block textureBlock, String blockName, int level, boolean shouldTint) {
		ResourceLocation generatedModel = ResourceLocation.fromNamespaceAndPath(Terrain.MOD_ID, "block/" + blockName + "_height" + level);

		JsonObject model = new JsonObject();

		JsonObject textures = new JsonObject();
		textures.addProperty("texture", TextureMapping.getBlockTexture(textureBlock).toString());
		textures.addProperty("particle", TextureMapping.getBlockTexture(textureBlock).toString());
		model.add("textures", textures);

		JsonArray elements = new JsonArray();
		JsonObject element = new JsonObject();

		JsonArray fromArray = new JsonArray();
		fromArray.add(0);
		fromArray.add(0);
		fromArray.add(0);
		element.add("from", fromArray);

		JsonArray toArray = new JsonArray();
		toArray.add(16);
		toArray.add(level*2);
		toArray.add(16);
		element.add("to", toArray);

		JsonObject faces = new JsonObject();

		JsonObject faceData = new JsonObject();
		faceData.addProperty("texture", "#texture");
		if (shouldTint) {faceData.addProperty("tintindex", 0);}

		faces.add("up", faceData);
		faces.add("down", faceData);
		faces.add("north", faceData);
		faces.add("south", faceData);
		faces.add("east", faceData);
		faces.add("west", faceData);

		element.add("faces", faces);
		elements.add(element);
		model.add("elements", elements);

		blockModelGenerators.modelOutput.accept(generatedModel, () -> model);
	}

	private static void generateLayeredBlockstates(BlockModelGenerators blockModelGenerators, Block layeredBlock, String blockName) {
		blockModelGenerators.blockStateOutput.accept( MultiVariantGenerator.multiVariant(layeredBlock).with(PropertyDispatch.property(LayerBlock.LAYERS).generate((integer) -> {
			ResourceLocation modelLocation;
			if (integer > 8) {modelLocation = ResourceLocation.fromNamespaceAndPath(Terrain.MOD_ID, "block/" + blockName);}
			else {modelLocation = ResourceLocation.fromNamespaceAndPath(Terrain.MOD_ID, "block/" + blockName + "_height" + integer);}
			return Variant.variant().with(VariantProperties.MODEL, modelLocation);
		})));
	}

	private static void generateLayerItemModels(BlockModelGenerators blockModelGenerators, String blockName) {
		blockModelGenerators.modelOutput.accept(
			ResourceLocation.fromNamespaceAndPath(Terrain.MOD_ID, "item/" + blockName +"_block"), () -> {
				JsonObject model = new JsonObject();
				model.addProperty("parent", Terrain.MOD_ID + ":block/" + blockName);
				return model;
			});
		blockModelGenerators.modelOutput.accept(
			ResourceLocation.fromNamespaceAndPath(Terrain.MOD_ID, "item/" + blockName +"_slab"), () -> {
				JsonObject model = new JsonObject();
				model.addProperty("parent", Terrain.MOD_ID + ":block/" + blockName +"_slab");
				return model;
			});
		blockModelGenerators.modelOutput.accept(
			ResourceLocation.fromNamespaceAndPath(Terrain.MOD_ID, "item/" + blockName), () -> {
				JsonObject model = new JsonObject();
				model.addProperty("parent", "minecraft:item/generated");
				JsonObject textures = new JsonObject();
				textures.addProperty("layer0", Terrain.MOD_ID + ":item/" + blockName);
				model.add("textures", textures);
				return model;
			});
	}
	private static void generateGenericItems(BlockModelGenerators blockModelGenerators, String blockName, String genericName) {
		blockModelGenerators.modelOutput.accept(
			ResourceLocation.fromNamespaceAndPath(Terrain.MOD_ID, "item/" + blockName +"_block"), () -> {
				JsonObject model = new JsonObject();
				model.addProperty("parent", Terrain.MOD_ID + ":block/" + blockName);
				return model;
			});
		blockModelGenerators.modelOutput.accept(
			ResourceLocation.fromNamespaceAndPath(Terrain.MOD_ID, "item/" + blockName +"_slab"), () -> {
				JsonObject model = new JsonObject();
				model.addProperty("parent", Terrain.MOD_ID + ":block/" + blockName +"_slab");
				return model;
			});
		blockModelGenerators.modelOutput.accept(
			ResourceLocation.fromNamespaceAndPath(Terrain.MOD_ID, "item/" + blockName), () -> {
				JsonObject model = new JsonObject();
				model.addProperty("parent", "minecraft:item/generated");
				JsonObject textures = new JsonObject();
				textures.addProperty("layer0", Terrain.MOD_ID + ":item/loose_" + genericName);
				textures.addProperty("tintindex", 0);
				model.add("textures", textures);
				return model;
			});
	}


	@Override
	public void generateItemModels(ItemModelGenerators itemModelGenerator) {

	}
}
