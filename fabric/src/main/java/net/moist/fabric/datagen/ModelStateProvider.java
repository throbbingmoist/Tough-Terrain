package net.moist.fabric.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
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
import net.moist.block.content.FallingLayer;
import net.moist.block.content.FloatingLayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModelStateProvider extends FabricModelProvider {
	public ModelStateProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
		generateLayers(blockStateModelGenerator, ModBlocks.LOOSE_DIRT.getPlacedLayer(), Blocks.DIRT, true);
		generateLayers(blockStateModelGenerator, ModBlocks.LOOSE_SAND.getPlacedLayer(), Blocks.SAND, true);
		generateLayers(blockStateModelGenerator, ModBlocks.LOOSE_RED_SAND.getPlacedLayer(), Blocks.RED_SAND, true);
		generateLayers(blockStateModelGenerator, ModBlocks.LOOSE_GRAVEL.getPlacedLayer(), Blocks.GRAVEL, true);

		//generateLayers(blockStateModelGenerator, ModBlocks.LOOSE_RED_CONCRETE_POWDER.get(), Blocks.RED_CONCRETE_POWDER, false, GenericStuff.POWDER);
		generateGrassLayers(blockStateModelGenerator, ModBlocks.GRASS_LAYER.getPlacedLayer(), Blocks.DIRT, Blocks.GRASS_BLOCK, null);
		generateGrassLikeLayers(blockStateModelGenerator, ModBlocks.MYCELIUM_LAYER.getPlacedLayer(), Blocks.DIRT, Blocks.MYCELIUM, null);

	}

	public void generateLayers(BlockModelGenerators blockModelGenerators, Block layeredBlock, Block textureBlock, boolean shouldTint) {generateLayers( blockModelGenerators,  layeredBlock,  textureBlock,  shouldTint, null);}
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
	public void generateGrassLayers(BlockModelGenerators blockModelGenerators, Block layeredBlock, Block primaryBlock, Block secondaryBlock, @Nullable String genericItemName) {
		String blockName = layeredBlock.asItem().getDescriptionId().replace("block." + Terrain.MOD_ID + ".", "");
		ModelTemplates.CUBE_BOTTOM_TOP.create(layeredBlock, getGrassLikeTextureMapping(primaryBlock, secondaryBlock), blockModelGenerators.modelOutput);
		ModelTemplates.SLAB_BOTTOM.create(Terrain.getID("block/"+blockName+"_slab"), getGrassLikeTextureMapping(primaryBlock, secondaryBlock), blockModelGenerators.modelOutput);
		for (int i = 1; i <= 8; ++i) {
			generateGrassLayerModel(blockModelGenerators, primaryBlock, secondaryBlock, blockName, i);
			generateSnowyGrassLikeLayerModel(blockModelGenerators, primaryBlock, secondaryBlock, Blocks.SNOW, blockName+"_snow", i);
		}
		generateGrassLayeredBlockstates(blockModelGenerators, layeredBlock, blockName);
		generateNonSpriteLayerItemModels(blockModelGenerators, blockName);
	}
	public void generateGrassLikeLayers(BlockModelGenerators blockModelGenerators, Block layeredBlock, Block primaryBlock, Block secondaryBlock, @Nullable String genericItemName) {
		String blockName = layeredBlock.asItem().getDescriptionId().replace("block." + Terrain.MOD_ID + ".", "");
		ModelTemplates.CUBE_BOTTOM_TOP.create(layeredBlock, getGrassLikeTextureMapping(primaryBlock, secondaryBlock), blockModelGenerators.modelOutput);
		ModelTemplates.SLAB_BOTTOM.create(Terrain.getID("block/"+blockName+"_slab"), getGrassLikeTextureMapping(primaryBlock, secondaryBlock), blockModelGenerators.modelOutput);
		for (int i = 1; i <= 8; ++i) {
			generateGrassLikeLayerModel(blockModelGenerators, primaryBlock, secondaryBlock, blockName, i);
		}
		generateLayeredBlockstates(blockModelGenerators, layeredBlock, blockName);
		generateNonSpriteLayerItemModels(blockModelGenerators, blockName, "_2");
	}

	private static @NotNull TextureMapping getGrassLikeTextureMapping(Block primaryBlock, Block secondaryBlock) {
		ResourceLocation primaryID = ModBlocks.BLOCKS.getRegistrar().getId(primaryBlock);
		ResourceLocation secondaryID = ModBlocks.BLOCKS.getRegistrar().getId(secondaryBlock);
		return new TextureMapping().put(TextureSlot.PARTICLE,primaryID)
			.put(TextureSlot.UP, secondaryID.withPrefix("block/").withSuffix("_top"))
			.put(TextureSlot.TOP, secondaryID.withPrefix("block/").withSuffix("_top"))
			.put(TextureSlot.SIDE, secondaryID.withPrefix("block/").withSuffix("_side"))
			.put(TextureSlot.BOTTOM, primaryID.withPrefix("block/").withSuffix("_block"))
			.put(TextureSlot.DOWN, primaryID.withPrefix("block/").withSuffix("_block"))
			;
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

		JsonArray fromArray = new JsonArray();   fromArray.add(-0.0f); fromArray.add(-0.0f); fromArray.add(-0.0f);element.add("from", fromArray);
		JsonArray toArray = new JsonArray();     toArray.add(16.0f); toArray.add(level*2f); toArray.add(16.0f);element.add("to", toArray);


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

	private static void generateGrassLayerModel(BlockModelGenerators blockModelGenerators, Block bottomTextureBlock, Block mainTextureBlock, String blockName, int level) {
		ResourceLocation generatedModel = ResourceLocation.fromNamespaceAndPath(Terrain.MOD_ID, "block/" + blockName + "_height" + level);

		JsonObject model = new JsonObject();

		JsonObject textures = new JsonObject();
		textures.addProperty("particle", TextureMapping.getBlockTexture(bottomTextureBlock).toString());
		textures.addProperty("bottom", TextureMapping.getBlockTexture(bottomTextureBlock).toString());
		textures.addProperty("side", TextureMapping.getBlockTexture(mainTextureBlock, "_side").toString());
		textures.addProperty("overlay", TextureMapping.getBlockTexture(mainTextureBlock, "_side_overlay").toString());
		textures.addProperty("top", TextureMapping.getBlockTexture(mainTextureBlock, "_top").toString());
		JsonArray elements = new JsonArray();
		JsonObject block = new JsonObject();

		JsonArray fromArray = new JsonArray();   fromArray.add(-0.0f); fromArray.add(-0.0f); fromArray.add(- 0.0f);   block.add("from", fromArray);
		JsonArray toArray = new JsonArray();     toArray.add(16.0f); toArray.add(level*2f); toArray.add(16.0f);   block.add("to", toArray);

		JsonArray uvs = new JsonArray();
		uvs.add(0);uvs.add(0);
		uvs.add(16);uvs.add(level*2);

		JsonObject faceData_Bottom = new JsonObject(); faceData_Bottom.addProperty("texture", "#bottom");
		JsonObject faceData_Side = new JsonObject(); faceData_Side.addProperty("texture", "#side"); faceData_Side.add("uv", uvs);
		JsonObject faceData_Top = new JsonObject(); faceData_Top.addProperty("texture", "#top"); faceData_Top.addProperty("tintindex", 0);
		JsonObject faceData_SideOverlay = new JsonObject();
		faceData_SideOverlay.addProperty("texture", "#overlay"); faceData_SideOverlay.addProperty("tintindex", 0);
		faceData_SideOverlay.add("uv", uvs);


		JsonObject block_faces = new JsonObject();
		block_faces.add("up", faceData_Top);
		block_faces.add("down", faceData_Bottom);
		block_faces.add("north", faceData_Side);
		block_faces.add("south", faceData_Side);
		block_faces.add("east", faceData_Side);
		block_faces.add("west", faceData_Side);
		block.add("faces", block_faces);


		JsonObject overlay = new JsonObject();
		overlay.add("from", fromArray);
		overlay.add("to", toArray);



		JsonObject overlay_faces = new JsonObject();
		JsonObject faceData_northOverlayer = faceData_SideOverlay.deepCopy();
		//faceData_northOverlayer.addProperty("cullface", "north");
		faceData_northOverlayer.add("uv", uvs);
		JsonObject faceData_southOverlayer = faceData_SideOverlay.deepCopy();
		//faceData_southOverlayer.addProperty("cullface", "south");
		faceData_southOverlayer.add("uv", uvs);
		JsonObject faceData_westOverlayer = faceData_SideOverlay.deepCopy();
		//faceData_westOverlayer.addProperty("cullface", "west");
		faceData_westOverlayer.add("uv", uvs);
		JsonObject faceData_eastOverlayer = faceData_SideOverlay.deepCopy();
		//faceData_eastOverlayer.addProperty("cullface", "east");
		faceData_eastOverlayer.add("uv", uvs);



		overlay_faces.add("north", faceData_northOverlayer);
		overlay_faces.add("south", faceData_southOverlayer);
		overlay_faces.add("east", faceData_eastOverlayer);
		overlay_faces.add("west", faceData_westOverlayer);
		overlay.add("faces", overlay_faces);

		elements.add(block);
		elements.add(overlay);
		model.add("textures", textures);
		//model.addProperty("loader", "architectury:composite");
		model.add("elements", elements);
		model.addProperty("render_type", "minecraft:cutout");
		if (level == 1) {model.addProperty("parent", "block/thin_block");} else {model.addProperty("parent", "block/block");}

		blockModelGenerators.modelOutput.accept(generatedModel, () -> model);
	}

	private static void generateGrassLikeLayerModel(BlockModelGenerators blockModelGenerators, Block baseTextureBlock, Block mainTextureBlock, String blockName, int level) {
		ResourceLocation generatedModel = ResourceLocation.fromNamespaceAndPath(Terrain.MOD_ID, "block/" + blockName + "_height" + level);

		JsonObject model = new JsonObject();

		JsonObject textures = new JsonObject();
		textures.addProperty("particle", TextureMapping.getBlockTexture(baseTextureBlock).toString());
		textures.addProperty("bottom", TextureMapping.getBlockTexture(baseTextureBlock).toString());
		textures.addProperty("side", TextureMapping.getBlockTexture(mainTextureBlock, "_side").toString());
		textures.addProperty("top", TextureMapping.getBlockTexture(mainTextureBlock, "_top").toString());

		model.add("textures", textures);
		model.addProperty("render_type", "minecraft:cutout");
		JsonArray elements = new JsonArray();

		JsonObject block = new JsonObject();

		JsonArray fromArray = new JsonArray();   fromArray.add(-0.0f); fromArray.add(-0.0f); fromArray.add(- 0.0f);   block.add("from", fromArray);
		JsonArray toArray = new JsonArray();     toArray.add(16.0f); toArray.add(level*2f); toArray.add(16.0f);   block.add("to", toArray);

		JsonArray uvs = new JsonArray();
		uvs.add(0);uvs.add(0);
		uvs.add(16);uvs.add(level*2);

		JsonObject faceData_Bottom = new JsonObject();faceData_Bottom.addProperty("texture", "#bottom");
		JsonObject faceData_Side = new JsonObject();faceData_Side.addProperty("texture", "#side"); faceData_Side.add("uv", uvs);
		JsonObject faceData_Top = new JsonObject();faceData_Top.addProperty("texture", "#top");faceData_Top.addProperty("tintindex", 0);

		JsonObject block_faces = new JsonObject();
		block_faces.add("up", faceData_Top);
		block_faces.add("down", faceData_Bottom);
		block_faces.add("north", faceData_Side);
		block_faces.add("south", faceData_Side);
		block_faces.add("east", faceData_Side);
		block_faces.add("west", faceData_Side);
		block.add("faces", block_faces);
		elements.add(block);

		model.add("elements", elements);
		if (level == 1) {model.addProperty("parent", "block/thin_block");} else {model.addProperty("parent", "block/block");}

		blockModelGenerators.modelOutput.accept(generatedModel, () -> model);
	}

	private static void generateSnowyGrassLikeLayerModel(BlockModelGenerators blockModelGenerators, Block baseTextureBlock, Block mainTextureBlock, Block topTextureBlock, String blockName, int level) {
		ResourceLocation generatedModel = ResourceLocation.fromNamespaceAndPath(Terrain.MOD_ID, "block/" + blockName + "_height" + level);

		JsonObject model = new JsonObject();

		JsonObject textures = new JsonObject();
		textures.addProperty("particle", TextureMapping.getBlockTexture(baseTextureBlock).toString());
		textures.addProperty("bottom", TextureMapping.getBlockTexture(baseTextureBlock).toString());
		textures.addProperty("side", TextureMapping.getBlockTexture(mainTextureBlock, "_snow").toString());
		textures.addProperty("top", TextureMapping.getBlockTexture(topTextureBlock).toString());

		model.add("textures", textures);
		model.addProperty("render_type", "minecraft:cutout");
		JsonArray elements = new JsonArray();

		JsonObject block = new JsonObject();

		JsonArray fromArray = new JsonArray();   fromArray.add(-0.0f); fromArray.add(-0.0f); fromArray.add(-0.0f);   block.add("from", fromArray);
		JsonArray toArray = new JsonArray();     toArray.add(16.0f); toArray.add(level*2f); toArray.add(16.0f);   block.add("to", toArray);

		JsonArray uvs = new JsonArray();
		uvs.add(0);uvs.add(0);
		uvs.add(16);uvs.add(level*2);

		JsonObject faceData_Bottom = new JsonObject();faceData_Bottom.addProperty("texture", "#bottom");
		JsonObject faceData_Side = new JsonObject();faceData_Side.addProperty("texture", "#side"); faceData_Side.add("uv", uvs);
		JsonObject faceData_Top = new JsonObject();faceData_Top.addProperty("texture", "#top");//faceData_Top.addProperty("tintindex", 0); Don't tint. It's only snow rn.

		JsonObject block_faces = new JsonObject();
		block_faces.add("up", faceData_Top);
		block_faces.add("down", faceData_Bottom);
		block_faces.add("north", faceData_Side);
		block_faces.add("south", faceData_Side);
		block_faces.add("east", faceData_Side);
		block_faces.add("west", faceData_Side);
		block.add("faces", block_faces);
		elements.add(block);

		model.add("elements", elements);
		if (level == 1) {model.addProperty("parent", "block/thin_block");} else {model.addProperty("parent", "block/block");}

		blockModelGenerators.modelOutput.accept(generatedModel, () -> model);
	}

	private static void generateLayeredBlockstates(BlockModelGenerators blockModelGenerators, Block layeredBlock, String blockName) {
		blockModelGenerators.blockStateOutput.accept( MultiVariantGenerator.multiVariant(layeredBlock).with(PropertyDispatch.property(FallingLayer.LAYERS).generate((integer) -> {
			ResourceLocation modelLocation;
			if (integer > 8) {modelLocation = ResourceLocation.fromNamespaceAndPath(Terrain.MOD_ID, "block/" + blockName);}
			else {modelLocation = ResourceLocation.fromNamespaceAndPath(Terrain.MOD_ID, "block/" + blockName + "_height" + integer);}
			return Variant.variant().with(VariantProperties.MODEL, modelLocation);
		})));
	}
	private static void generateGrassLayeredBlockstates(BlockModelGenerators blockModelGenerators, Block layeredBlock, String blockName) {
		blockModelGenerators.blockStateOutput.accept( MultiVariantGenerator.multiVariant(layeredBlock).with(PropertyDispatch.properties(FallingLayer.LAYERS, BlockStateProperties.SNOWY).generate((integer, bool) -> {
			ResourceLocation modelLocation;
			String SnowSuffix = bool ? "_snow" : "";
			if (integer > 8) {modelLocation = ResourceLocation.fromNamespaceAndPath(Terrain.MOD_ID, "block/" + blockName+SnowSuffix);}
			else {modelLocation = ResourceLocation.fromNamespaceAndPath(Terrain.MOD_ID, "block/" +blockName+SnowSuffix+ "_height" + integer);}
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
	private static void generateNonSpriteLayerItemModels(BlockModelGenerators blockModelGenerators, String blockName) {
		generateNonSpriteLayerItemModels(blockModelGenerators, blockName, null);
	}
	private static void generateNonSpriteLayerItemModels(BlockModelGenerators blockModelGenerators, String blockName, @Nullable String suffix) {
		blockModelGenerators.modelOutput.accept(
			ResourceLocation.fromNamespaceAndPath(Terrain.MOD_ID, "item/" + blockName +"_block"), () -> {
				JsonObject model = new JsonObject();
				model.addProperty("parent", Terrain.MOD_ID+":block/"+ blockName + "_height8");
				return model;
			});
		blockModelGenerators.modelOutput.accept(
			ResourceLocation.fromNamespaceAndPath(Terrain.MOD_ID, "item/" + blockName +"_slab"), () -> {
				JsonObject model = new JsonObject();
				model.addProperty("parent", Terrain.MOD_ID+":block/"+ blockName + "_height4");
				return model;
			});
		blockModelGenerators.modelOutput.accept(
			ResourceLocation.fromNamespaceAndPath(Terrain.MOD_ID, "item/" + blockName), () -> {
				JsonObject model = new JsonObject();
				model.addProperty("parent", Terrain.MOD_ID+":block/"+ blockName + "_height1");
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
