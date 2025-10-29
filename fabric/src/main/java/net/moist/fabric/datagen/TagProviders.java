package net.moist.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.moist.Terrain;
import net.moist.block.ModBlocks;

import java.util.concurrent.CompletableFuture;
public class TagProviders {
	public static class ModBlockTags extends FabricTagProvider.BlockTagProvider {

		public ModBlockTags(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
			super(output, registriesFuture);
		}

		@Override
		protected void addTags(HolderLookup.Provider wrapperLookup) {
			this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(ModBlocks.LOOSE_DIRT.getKey(),ModBlocks.LOOSE_RED_SAND.getKey(),ModBlocks.LOOSE_SAND.getKey(),ModBlocks.LOOSE_GRAVEL.getKey());
			this.tag(BlockTags.SMELTS_TO_GLASS).addOptional(Terrain.getID("loose_sand_block"));

			FabricTagProvider<Block>.FabricTagBuilder loosens_surroundings = getOrCreateTagBuilder(ModBlocks.LOOSENS_SURROUNDINGS);
			loosens_surroundings.add(Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.PODZOL, Blocks.MYCELIUM, Blocks.COARSE_DIRT, Blocks.ROOTED_DIRT, Blocks.DIRT_PATH);
		}
	}

	public static class ModItemTags extends  FabricTagProvider.ItemTagProvider {
		public static final boolean shouldLoosenForWood = false;
		public static final  boolean shouldLoosenForStone = false;

		public ModItemTags(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
			super(output, completableFuture);
		}

		@Override
		protected void addTags(HolderLookup.Provider wrapperLookup) {
			FabricTagProvider<Item>.FabricTagBuilder tag = getOrCreateTagBuilder(TagKey.create(Registries.ITEM, Terrain.getID("packing_shovels")));


			if (shouldLoosenForWood) {tag.add(Items.WOODEN_SHOVEL);}
			if (shouldLoosenForStone) {tag.add(Items.STONE_SHOVEL);}

			tag.add(Items.IRON_SHOVEL, Items.GOLDEN_SHOVEL, Items.DIAMOND_SHOVEL, Items.NETHERITE_SHOVEL);
			tag.addOptional(ResourceLocation.withDefaultNamespace("copper_shovel"));
		}
	}
}
