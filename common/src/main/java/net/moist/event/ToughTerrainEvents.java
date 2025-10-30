package net.moist.event;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
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
				BlockState initial = player.level().getBlockState(pos);
				if (!initial.hasProperty(FallingLayer.LAYERS) || initial.getValue(FallingLayer.LAYERS) >= 8) {
					player.getItemInHand(hand).consumeAndReturn(1, player);
					if (recipe.hasSoundLocation()) { player.level().playSound(null, pos, recipe.getSoundEvent(), SoundSource.BLOCKS, 1.0f, 0.8f); }
					player.level().setBlock(pos, recipe.getResultState(), 11);
					shouldInterrupt.set(true);
				}});
			return shouldInterrupt.get() != null ? EventResult.interrupt(shouldInterrupt.get()) : EventResult.pass();
		}));

	}
}
