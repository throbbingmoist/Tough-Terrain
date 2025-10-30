package net.moist.recipe;


import com.mojang.datafixers.util.Either;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.moist.Terrain;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


public class TransformRecipe implements Recipe<RecipeInput> {

	public record ParticleData(ResourceLocation location, Integer count) {}
	public ParticleData EMPTY = new ParticleData(null, null);

	private final Either<Block, TagKey<Block>> inputBlock;
	private final Ingredient transformItem;
	private final Optional<ResourceLocation> soundLocation;
	private final Optional<ParticleData> particleData;
	private final Either<Block, BlockState> resultState;


	public TransformRecipe(Either<Block, TagKey<Block>> inputBlock, Ingredient transformItem, Optional<ResourceLocation> soundLocation, Either<Block, BlockState>  resultState) {
		this(inputBlock, transformItem, soundLocation.orElse(null), null, null, resultState);
	}
	public TransformRecipe(Either<Block, TagKey<Block>> inputBlock, Ingredient transformItem, Optional<ResourceLocation> soundLocation, Optional<ParticleData> particleData, Either<Block, BlockState>  resultState) {
		this(inputBlock, transformItem, soundLocation.orElse(null), particleData.map(ParticleData::location).orElse(null), particleData.map(ParticleData::count).orElse(null), resultState);
	}
	public TransformRecipe(Either<Block, TagKey<Block>> inputBlock, Ingredient transformItem, Optional<ResourceLocation> soundLocation, @Nullable ResourceLocation particleLocation, @Nullable Integer particleCount, Either<Block, BlockState>  resultState) {
		this(inputBlock, transformItem, soundLocation.orElse(null), particleLocation, particleCount, resultState);
	}

	public TransformRecipe(Builder builder) {
		ParticleData data = builder.particleLocation != null && builder.particleCount != null ? new ParticleData(builder.particleLocation, builder.particleCount) : null;
		this.inputBlock = builder.inputBlock;
		this.transformItem = builder.transformItem;
		this.soundLocation = Optional.ofNullable(builder.soundLocation);
		this.particleData = Optional.ofNullable(data);
		this.resultState = builder.resultState;
	}

	public TransformRecipe(Either<Block, TagKey<Block>> inputBlock, Ingredient transformItem, @Nullable ResourceLocation soundLocation, @Nullable ResourceLocation particleLocation, @Nullable Integer particleCount, Either<Block, BlockState>  resultState) {
		ParticleData data = particleLocation != null && particleCount != null ? new ParticleData(particleLocation, particleCount) : null;

		this.inputBlock = inputBlock;
		this.transformItem = transformItem;
		this.soundLocation = Optional.ofNullable(soundLocation);
		this.particleData = Optional.ofNullable(data);
		this.resultState = resultState;
	}



	public boolean matches(ItemStack stack_in_hand, Level level, BlockPos pos) {
		return matchesItem(stack_in_hand) && matchesBlock(level, pos);
	}
	public boolean matchesBlock(Level level, BlockPos pos) {return this.inputBlock.map(block -> level.getBlockState(pos).is(block), tag -> level.getBlockState(pos).is(tag));}
	public boolean matchesItem(ItemStack stack_in_hand) {
		if (! ( stack_in_hand.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY.withPotion(Potions.WATER)).is(Potions.WATER) ) ) {return false;}
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

	public Either<Block, BlockState> getResultState() { return resultState; }

	public ParticleData getParticleData() {return particleData.orElse(null);}
	public Optional<ParticleData> getParticleDataOptional() {return particleData;}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private Either<Block, TagKey<Block>> inputBlock;
		private Ingredient transformItem;
		private ResourceLocation soundLocation;
		private ResourceLocation particleLocation;
		private Integer particleCount;
		private Either<Block, BlockState> resultState;
		public Builder() {
			this.inputBlock = null;
			this.transformItem = null;
			this.soundLocation = null;
			this.particleLocation = null;
			this.particleCount = null;
			this.resultState = null;
		}

		public Builder input(TagKey<Block> tag) {this.inputBlock = Either.right(tag);return this;}
		public Builder input(Block block) {this.inputBlock = Either.left(block);return this;}
		public Builder transformItem(Ingredient transformItem) {this.transformItem = transformItem;return this;}
		public Builder sound(SoundEvent soundEvent) {return soundLocation(soundEvent.getLocation());}
		public Builder soundLocation(ResourceLocation soundLocation) {this.soundLocation = soundLocation;return this;}
		public Builder particles(ResourceLocation particleLocation, Integer particleCount) {this.particleLocation = particleLocation;this.particleCount = particleCount;return this;}
		public Builder particles(ParticleType<?> particleLocation, Integer particleCount) {particles(BuiltInRegistries.PARTICLE_TYPE.getKey(particleLocation), particleCount);return this;}
		public Builder result(Block block) {this.resultState = Either.left(block);return this;}
		public Builder result(BlockState state) {this.resultState = Either.right(state);return this;}

		public void build(ResourceLocation resourceLocation, RecipeOutput output, @Nullable AdvancementHolder advancementHolder) {
			output.accept(resourceLocation, new TransformRecipe(this), advancementHolder);
		}
		public void build(String resourceLocation, RecipeOutput output) {build(Terrain.getID(resourceLocation), output, null);}
		public void build(ResourceLocation resourceLocation, RecipeOutput output) {build(resourceLocation, output, null);}
	}
}
