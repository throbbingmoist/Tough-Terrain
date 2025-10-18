package net.moist.event;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.moist.Terrain;
import net.moist.block.content.LayerBlock;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
public class LoosenSoilEvent {
	private static final ConcurrentHashMap<Block, RegistrySupplier<Block>> BLOCKS_TO_LOOSEN = new ConcurrentHashMap<>();
	public static final TagKey<Item> SHOVELS_THAT_PACK = TagKey.create(Registries.ITEM, Terrain.getID("packing_shovels"));

	public static void loosenBlock(RegistrySupplier<Block> block_broken, RegistrySupplier<Block> loosening_result) {
		BLOCKS_TO_LOOSEN.put(block_broken.get(), loosening_result);
	}
	public static void loosenBlock(Block block_broken, RegistrySupplier<Block> loosening_result) {
		BLOCKS_TO_LOOSEN.put(block_broken, loosening_result);
	}

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

	public static void subscribe() {
		Terrain.LOGGER.info("Making Event!");
		BlockEvent.BREAK.register((level, pos, state, player, xp) -> {

			if (!player.isHolding(ItemPredicate.Builder.item().of(SHOVELS_THAT_PACK).build())) {
				getAdjacent(pos).forEach((key, blockPos) -> {
					if (((BLOCKS_TO_LOOSEN.containsKey(level.getBlockState(pos).getBlock())) || ( (level.getBlockState(pos).getBlock().equals(Blocks.GRASS_BLOCK)) || (level.getBlockState(pos).getBlock().equals(Blocks.MYCELIUM)) || (level.getBlockState(pos).getBlock().equals(Blocks.PODZOL)))) && (BLOCKS_TO_LOOSEN.containsKey(level.getBlockState(blockPos).getBlock()))) {
						if (BLOCKS_TO_LOOSEN.get(level.getBlockState(blockPos).getBlock()).get().defaultBlockState().hasProperty(LayerBlock.LAYERS)) {
							level.setBlock(blockPos, BLOCKS_TO_LOOSEN.get(level.getBlockState(blockPos).getBlock()).get().defaultBlockState().setValue(LayerBlock.LAYERS, 8), 11);
						} else {
							level.setBlock(blockPos, BLOCKS_TO_LOOSEN.get(level.getBlockState(blockPos).getBlock()).get().defaultBlockState(), 11);
						}
					}
				});
			}
			return EventResult.pass();
		});
		Terrain.LOGGER.info("Finished making Event!");
	}
}
