package net.moist.event;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.moist.block.ModBlocks;
import net.moist.block.content.FallingLayer;
import net.moist.item.TerrainTags;
import net.moist.recipe.recipes.LooseningRecipe;
import net.moist.recipe.ModRecipes;
import net.moist.recipe.recipes.TransformRecipe;
import net.moist.block.record.ConcreteHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class ToughTerrainEvents {


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
			if (!player.isHolding(ItemPredicate.Builder.item().of(TerrainTags.NON_DISTURBING_SHOVELS).build())) {getAdjacent(blockPos).forEach((key, targetPos) -> {
					if (level.getBlockState(blockPos).is(ModBlocks.LOOSENS_SURROUNDINGS)) {
						Optional<LooseningRecipe> recipeOptional = matchLoosenRecipe(level, targetPos);
						recipeOptional.ifPresent(recipe -> level.setBlock(targetPos, recipe.getResultState(), 11));
					}
				});
			}
			return EventResult.pass();
		});




		InteractionEvent.RIGHT_CLICK_BLOCK.register(((player, hand, pos, face) -> { AtomicReference<@Nullable Boolean> shouldInterrupt = new AtomicReference<>();
			if (!player.level().isClientSide()) {
				Level level = player.level();
				ServerLevel serverLevel = player.getServer().getLevel(level.dimension());
				Optional<TransformRecipe> recipeOptional = matchTransformRecipe(level, pos, player.getItemInHand(hand));

				recipeOptional.ifPresent(recipe -> {
					BlockState initialState = level.getBlockState(pos);
					if (!initialState.hasProperty(FallingLayer.LAYERS) || initialState.getValue(FallingLayer.LAYERS) >= 8) {

						ItemStack handItem = player.getItemInHand(hand);
						if(!player.hasInfiniteMaterials()) {
							if (handItem.is(Items.POTION)) {player.setItemInHand(hand, ItemUtils.createFilledResult(handItem, player, new ItemStack(Items.GLASS_BOTTLE)));}
							else if (handItem.getItem().hasCraftingRemainingItem()) {player.setItemInHand(hand, ItemUtils.createFilledResult(handItem, player, new ItemStack(handItem.getItem().getCraftingRemainingItem())));}
							handItem.finishUsingItem(level, player);
						}


						if (recipe.hasSoundLocation()) {
							level.playSound(null, pos, recipe.getSoundEvent(), SoundSource.BLOCKS, 1.0f, 0.8f);
						}

						if (recipe.getParticleDataOptional().isPresent()) {
							ParticleType<?> particleType = BuiltInRegistries.PARTICLE_TYPE.get(recipe.getParticleDataOptional().get().location());
							double x = pos.getX() + 0.5; double y = pos.getY() + 0.5; double z =  pos.getZ() + 0.5;float distance = 0.27f;float speed = 0.1f;

							if (particleType == ParticleTypes.ITEM) {serverLevel.sendParticles(new ItemParticleOption((ParticleType<ItemParticleOption>) particleType, handItem), x, y, z, recipe.getParticleData().count(), distance, distance, distance, speed);}
							else if (particleType == ParticleTypes.BLOCK) {serverLevel.sendParticles(new BlockParticleOption((ParticleType<BlockParticleOption>) particleType, recipe.outputBlockState()), x, y, z, recipe.getParticleData().count(), distance, distance, distance, speed);}
							else if (particleType == ParticleTypes.DUST) {serverLevel.sendParticles(new DustParticleOptions(ConcreteHelper.getVectorColor(recipe.outputBlock().defaultMapColor()), 1.0f),  x, y, z, recipe.getParticleData().count(), distance, distance, distance, speed);}
							else if (particleType instanceof ParticleOptions particleOptions) {serverLevel.sendParticles(particleOptions, x, y, z, recipe.getParticleData().count(), distance, distance, distance, speed);}
						}
						recipe.getResultState().ifLeft(block -> level.setBlock(pos, block.defaultBlockState(), 11)).ifRight(blockState -> level.setBlock(pos, blockState, 11));
						recipe.getResultState().ifLeft(block -> player.awardStat(Stats.ITEM_CRAFTED.get(block.asItem()))).ifRight(blockState -> player.awardStat(Stats.ITEM_CRAFTED.get(blockState.getBlock().asItem())));

						shouldInterrupt.set(true);
					}
				});
			}
			return shouldInterrupt.get() != null ? EventResult.interrupt(shouldInterrupt.get()) : EventResult.pass();
		}));
	}
}
