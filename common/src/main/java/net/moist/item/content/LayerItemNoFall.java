package net.moist.item.content;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.moist.Terrain;
import net.moist.block.content.LayerBlockNoFall;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LayerItemNoFall extends BlockItem {
	private final int layerAmount;
	private final String item_suffix;


	public LayerItemNoFall(Block block, Properties properties, int layerAmount, @Nullable String itemSuffix) {
		super(block, properties);

		this.layerAmount = layerAmount;
		this.item_suffix = itemSuffix != null ? itemSuffix : "";
	}

	public LayerItemNoFall(Block block, Properties properties, int layerAmount) {
		this(block, properties, layerAmount, null);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		ItemStack itemstack = context.getItemInHand();

		Terrain.LOGGER.debug(context.getItemInHand().getItem().toString());
		BlockState existingState = level.getBlockState(pos);
			if (existingState.getBlock() instanceof LayerBlockNoFall && existingState.getBlock() == this.getBlock()) {
				int currentLayers = existingState.getValue(LayerBlockNoFall.LAYERS);
				if (currentLayers < LayerBlockNoFall.MAX_LAYERS) {
					if (!level.isClientSide) {
						int layers = currentLayers + this.getLayerAmount();
						if (layers > LayerBlockNoFall.MAX_LAYERS) {
							if (level.getBlockState(pos.above()).canBeReplaced()) {
								level.setBlock(pos, existingState.setValue(LayerBlockNoFall.LAYERS, LayerBlockNoFall.MAX_LAYERS).setValue(BlockStateProperties.WATERLOGGED,level.getFluidState(pos).is(Fluids.WATER)), 11);
								level.setBlock(pos.above(), existingState.setValue(LayerBlockNoFall.LAYERS, layers - LayerBlockNoFall.MAX_LAYERS).setValue(BlockStateProperties.WATERLOGGED,level.getFluidState(pos.above()).is(Fluids.WATER)), 11);

							} else {
								return InteractionResult.FAIL;
							}
						} else {
							level.setBlock(pos, existingState.setValue(LayerBlockNoFall.LAYERS, layers).setValue(BlockStateProperties.WATERLOGGED,level.getFluidState(pos).is(Fluids.WATER)), 11);
						}
						level.playSound(null, pos, existingState.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
						if ((context.getPlayer() != null) && (!context.getPlayer().isCreative())) {
							itemstack.shrink(1);
						}
					}
					return InteractionResult.sidedSuccess(level.isClientSide);
				}
			}

		BlockPos placementPos = pos;
		BlockState placementState = level.getBlockState(placementPos);

		// Use a BlockPlaceContext to get the correct replaceable check
		BlockPlaceContext blockPlaceContext = new BlockPlaceContext(context);

		if (!placementState.canBeReplaced(blockPlaceContext)) {
			placementPos = pos.relative(context.getClickedFace());
			placementState = level.getBlockState(placementPos);
		}

		if (placementState.canBeReplaced(blockPlaceContext)) {
			int layers = this.getLayerAmount();
			BlockState newState = this.getBlock().defaultBlockState().setValue(LayerBlockNoFall.LAYERS, Math.min(layers, LayerBlockNoFall.MAX_LAYERS)).setValue(BlockStateProperties.WATERLOGGED,level.getFluidState(placementPos).is(Fluids.WATER));

			if (!level.isClientSide) {
				level.setBlock(placementPos, newState, 11);
				level.playSound(null, placementPos, newState.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
				if (context.getPlayer() != null && !context.getPlayer().isCreative()) {
					itemstack.shrink(1);
				}
			}
			return InteractionResult.sidedSuccess(level.isClientSide);
		}

		return super.useOn(context);
	}



	@Override
	public @NotNull Component getName(ItemStack itemStack) {
		return Component.translatable(this.getDescriptionId(itemStack)+item_suffix);
	}

	public int getLayerAmount() {
		return layerAmount;
	}
}