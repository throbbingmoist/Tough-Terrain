package net.moist.event;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.moist.Terrain;
import net.moist.block.ModBlocks;
import net.moist.block.content.FallingLayer;
import net.moist.recipe.LooseningRecipe;
import net.moist.recipe.ModRecipes;
import net.moist.recipe.TransformRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class ToughTerrainEvents {
	public static final TagKey<Item> SHOVELS_THAT_PACK = TagKey.create(Registries.ITEM, Terrain.getID("packing_shovels"));


	public static ConcurrentHashMap<Integer, BlockPos> getAdjacent(BlockPos pos) {
		ConcurrentHashMap<Integer, BlockPos> positions = new ConcurrentHashMap<>();

		positions.put(1, pos.above());
		positions.put(2, pos.below());
		positions.put(3, pos.east());
		positions.put(4, pos.north());
		positions.put(5, pos.south());
		positions.put(6, pos.west());

		return positions;
	}

	public static Optional<LooseningRecipe> matchLoosenRecipe(Level level, BlockPos pos) {
		if (level.isClientSide()) {return Optional.empty();}
		return level.getRecipeManager().getAllRecipesFor(ModRecipes.WORLD_LOOSEN.get()).stream().map(RecipeHolder::value).filter(recipe -> recipe.matches(level, pos)).findFirst();
	}
	public static Optional<TransformRecipe> matchTransformRecipe(Level level, BlockPos pos, ItemStack itemInHand) {
		if (level.isClientSide()) {return Optional.empty();}
		return level.getRecipeManager().getAllRecipesFor(ModRecipes.WORLD_TRANSFORM.get()).stream().map(RecipeHolder::value).filter(recipe -> recipe.matches(itemInHand, level, pos)).findFirst();
	}

	public static void subscribe() {
		BlockEvent.BREAK.register((level, blockPos, state, player, xp) -> {
			if (!player.isHolding(ItemPredicate.Builder.item().of(SHOVELS_THAT_PACK).build())) {getAdjacent(blockPos).forEach((key, targetPos) -> {
					if (level.getBlockState(blockPos).is(ModBlocks.LOOSENS_SURROUNDINGS)) {
						Optional<LooseningRecipe> recipeOptional = matchLoosenRecipe(level, targetPos);
						recipeOptional.ifPresent(recipe -> level.setBlock(targetPos, recipe.getResultState(), 11));
					}
				});
			}
			return EventResult.pass();
		});

		InteractionEvent.RIGHT_CLICK_BLOCK.register(((player, hand, pos, face) -> {
			AtomicReference<@Nullable Boolean> shouldInterrupt = new AtomicReference<>();
			Optional<TransformRecipe> recipeOptional = matchTransformRecipe(player.level(), pos, player.getItemInHand(hand));
			recipeOptional.ifPresent(recipe -> {
				BlockState initialState = player.level().getBlockState(pos);
				if (!initialState.hasProperty(FallingLayer.LAYERS) || initialState.getValue(FallingLayer.LAYERS) >= 8) {
					ItemStack handItem = player.getItemInHand(hand).consumeAndReturn(1, player);
					if (recipe.hasSoundLocation()) { player.level().playSound(null, pos, recipe.getSoundEvent(), SoundSource.BLOCKS, 1.0f, 0.8f); }
					if (recipe.getParticleDataOptional().isPresent()) {
						ServerLevel serverLevel = player.getServer().getLevel(player.level().dimension());
						ParticleType<?> particleType = BuiltInRegistries.PARTICLE_TYPE.get(recipe.getParticleDataOptional().get().location());

						if (particleType == ParticleTypes.ITEM) {
							serverLevel.sendParticles(new ItemParticleOption((ParticleType<ItemParticleOption>) particleType, handItem), pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, recipe.getParticleData().count(), 0.28, 0.28, 0.28, 0.05);
						} else if (particleType == ParticleTypes.BLOCK) {
							BlockState finalState = recipe.getResultState().map(Block::defaultBlockState, state -> state);
							serverLevel.sendParticles(new BlockParticleOption((ParticleType<BlockParticleOption>) particleType, finalState), pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, recipe.getParticleData().count(), 0.28, 0.28, 0.28, 0.05);
						} else if (particleType instanceof ParticleOptions particleOptions) {
							serverLevel.sendParticles(particleOptions, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, recipe.getParticleData().count(), 0.25, 0.25, 0.25, 0.05);
						}
					}
					recipe.getResultState()
						.ifLeft(block -> player.level().setBlock(pos, block.defaultBlockState(), 11))
						.ifRight(blockState -> player.level().setBlock(pos, blockState, 11));
					recipe.getResultState()
						.ifLeft(block -> player.awardStat(Stats.ITEM_CRAFTED.get(block.asItem())) )
						.ifRight(blockState -> player.awardStat(Stats.ITEM_CRAFTED.get(blockState.getBlock().asItem())) );

					shouldInterrupt.set(true);
				}});
			return shouldInterrupt.get() != null ? EventResult.interrupt(shouldInterrupt.get()) : EventResult.pass();
		}));

	}
}
