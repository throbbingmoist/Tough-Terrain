package net.moist.block.entity;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.moist.Terrain;
import net.moist.block.ModBlocks;

import java.util.function.Supplier;

public class TerrainBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
		DeferredRegister.create(Terrain.MOD_ID, Registries.BLOCK_ENTITY_TYPE);


	public static final Supplier<BlockEntityType<LayerBE>> LAYER_BE =
		BLOCK_ENTITY_TYPES.register("layer_block_entity",
			() -> BlockEntityType.Builder.of(
				LayerBE::new,
				ModBlocks.LOOSE_DIRT.placedLayer().get(),
				ModBlocks.LOOSE_SAND.placedLayer().get(),
				ModBlocks.LOOSE_RED_SAND.placedLayer().get(),
				ModBlocks.LOOSE_GRAVEL.placedLayer().get(),
				ModBlocks.GRASS_LAYER.placedLayer().get(),
				ModBlocks.MYCELIUM_LAYER.placedLayer().get()
			).build(null)
		);
	public static void register() {
		BLOCK_ENTITY_TYPES.register();

	}
}
