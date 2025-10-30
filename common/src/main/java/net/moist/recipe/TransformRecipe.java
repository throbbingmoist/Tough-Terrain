package net.moist.recipe;


import com.mojang.datafixers.util.Either;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


public class TransformRecipe implements Recipe<RecipeInput> {
	private final Either<Block, TagKey<Block>> inputBlock;
	private final Ingredient transformItem;
	private final Optional<ResourceLocation> soundLocation;
	private final Optional<ResourceLocation> particleLocation = Optional.empty();
	private final Optional<Integer> particleCount = Optional.empty();
	private final BlockState resultState;
	//private Either<Block, BlockState> resultState;

	public TransformRecipe(TagKey<Block> inputBlock, Ingredient transformItem, BlockState resultState) {
		this(Either.right(inputBlock), transformItem, Optional.empty() ,resultState);
	}
	public TransformRecipe(Block inputBlock, Ingredient transformItem, BlockState resultState) {
		this(Either.left(inputBlock), transformItem, Optional.empty() ,resultState);
	}
	public TransformRecipe(TagKey<Block> inputBlock, Ingredient transformItem, @Nullable SoundEvent soundLocation, BlockState resultState) {
		this(Either.right(inputBlock), transformItem, soundLocation != null ? soundLocation.getLocation() : null ,resultState);
	}
	public TransformRecipe(Block inputBlock, Ingredient transformItem, @Nullable SoundEvent soundLocation, BlockState resultState) {
		this(Either.left(inputBlock), transformItem, soundLocation != null ? soundLocation.getLocation() : null ,resultState);
	}
	public TransformRecipe(TagKey<Block> inputBlock, Ingredient transformItem,@Nullable  ResourceLocation soundLocation, BlockState resultState) {
		this(Either.right(inputBlock), transformItem, soundLocation,resultState);
	}
	public TransformRecipe(Block inputBlock, Ingredient transformItem, @Nullable ResourceLocation soundLocation, BlockState resultState) {
		this(Either.left(inputBlock), transformItem, soundLocation,resultState);
	}

	public TransformRecipe(Either<Block, TagKey<Block>> inputBlock, Ingredient transformItem, Optional<ResourceLocation> soundLocation, BlockState resultState) {this(inputBlock, transformItem, soundLocation.orElse(null), resultState);}

	public TransformRecipe(Builder builder) {
		this.inputBlock = builder.inputBlock;
		this.transformItem = builder.transformItem;
		this.soundLocation = Optional.ofNullable(builder.soundLocation);
		this.resultState = builder.resultState;
	}

	public TransformRecipe(Either<Block, TagKey<Block>> inputBlock, Ingredient transformItem, @Nullable ResourceLocation soundLocation, BlockState resultState) {
		this.inputBlock = inputBlock;
		this.transformItem = transformItem;
		this.soundLocation = Optional.ofNullable(soundLocation);
		this.resultState = resultState;
	}
//	public TransformRecipe(Either<Block, TagKey<Block>> inputBlock, Ingredient transformItem, @Nullable ResourceLocation soundLocation, Either<Block, BlockState>  resultState) {
//		this.inputBlock = inputBlock;
//		this.transformItem = transformItem;
//		this.soundLocation = Optional.ofNullable(soundLocation);
//		this.resultState = resultState;
//	}



	public boolean matches(ItemStack stack_in_hand, Level level, BlockPos pos) {
		return matchesItem(stack_in_hand) && matchesBlock(level, pos);
	}
	public boolean matchesBlock(Level level, BlockPos pos) {
		return this.inputBlock.map(
			block -> level.getBlockState(pos).is(block),
			tag -> level.getBlockState(pos).is(tag)
		);}
	public boolean matchesItem(ItemStack stack_in_hand) {
		if (!stack_in_hand.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY.withPotion(Potions.WATER)).is(Potions.WATER)) {return false;}
		return transformItem.test(stack_in_hand);
	}

	public boolean matches(RecipeInput recipeInput, Level level) {return false;}
	public ItemStack assemble(RecipeInput recipeInput, HolderLookup.Provider provider) {return ItemStack.EMPTY;}
	public boolean canCraftInDimensions(int i, int j) {return false;}
	public ItemStack getResultItem(HolderLookup.Provider provider) {return null;}


	public RecipeSerializer<?> getSerializer() {return ModRecipes.TRANSFORM_SERIALIZER.get();}
	public RecipeType<?> getType() {return ModRecipes.WORLD_TRANSFORM.get();}


	public Either<Block, TagKey<Block>> getInputBlock() { return inputBlock; }
	public Ingredient getTransformItem() {return transformItem;}
	public boolean hasSoundLocation() { return soundLocation.isPresent(); }

	public Optional<ResourceLocation> getSoundLocation() { return soundLocation; }
	public @Nullable SoundEvent getSoundEvent() {return soundLocation.map(BuiltInRegistries.SOUND_EVENT::get).orElse(null);}

	public BlockState getResultState() { return resultState; }
	//public Either<Block, BlockState> getResultState() { return resultState; }
	public class Builder {
		private Either<Block, TagKey<Block>> inputBlock;
		private Ingredient transformItem;
		private ResourceLocation soundLocation;
		private ResourceLocation particleLocation;
		private Integer particleCount;
		private BlockState resultState;
		//private Either<Block, BlockState> resultState;
		public Builder() {
			this.inputBlock = null;
			this.transformItem = null;
			this.soundLocation = null;
			this.particleLocation = null;
			this.particleCount = null;
			this.resultState = null;
		}

		public Builder input(TagKey<Block> tag) {
			this.inputBlock = Either.right(tag);
			return this;
		}
		public Builder input(Block block) {
			this.inputBlock = Either.left(block);
			return this;
		}
		public Builder transformItem(Ingredient transformItem) {
			this.transformItem = transformItem;
			return this;
		}
		public Builder soundLocation(ResourceLocation soundLocation) {
			this.soundLocation = soundLocation;
			return this;
		}
		public Builder particles(ResourceLocation particleLocation, Integer particleCount) {
			this.particleLocation = particleLocation;
			this.particleCount = particleCount;
			return this;
		}
		public Builder result(BlockState state) {
			this.resultState = state;
			return this;
		}
//		public Builder result(BlockState state) {
//			this.resultState = Either.left(state);
//			return this;
//		}
//		public Builder result(Block block) {
//			this.resultState = Either.right(block);
//			return this;
//		}

		public void build(ResourceLocation resourceLocation, RecipeOutput output, @Nullable AdvancementHolder advancementHolder) {
			output.accept(resourceLocation, new TransformRecipe(this), advancementHolder);
		}
		public void build(ResourceLocation resourceLocation, RecipeOutput output) {build(resourceLocation, output, null);}
	}
}
