package net.moist.item.content;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.moist.Terrain;
import net.moist.block.content.FallingLayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LayerItem extends BlockItem {
	private final int layerAmount;
	private final String item_suffix;

	protected static final VoxelShape[] LAYER_SHAPES = new VoxelShape[]{
		Shapes.empty(),
		Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
		Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
		Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
		Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
		Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
		Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D),
		Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D),
		Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
	};

	public LayerItem(Block block, Properties properties, int layerAmount) { this(block, properties, layerAmount, null); }
	public LayerItem(Block block, Properties properties, int layerAmount, @Nullable String itemSuffix) {
		super(block, properties);
		this.layerAmount = layerAmount;
		this.item_suffix = itemSuffix != null ? itemSuffix : "";
	}

	private static boolean isSnowySetting(BlockState blockState) {return blockState.is(BlockTags.SNOW);}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		ItemStack itemstack = context.getItemInHand();
		Player player = context.getPlayer();

		BlockState existingState = level.getBlockState(pos);
		BlockPos placementPos = pos;
		BlockState placementState = level.getBlockState(placementPos);

		BlockPlaceContext blockPlaceContext = new BlockPlaceContext(context);
		if (!placementState.canBeReplaced(blockPlaceContext)) {
			placementPos = pos.relative(context.getClickedFace());
			placementState = level.getBlockState(placementPos);
		}

		if (player != null && context.getItemInHand().getItem() instanceof LayerItem) {
			LayerItem layerItem = ((LayerItem) context.getItemInHand().getItem());
			AABB BlockBoundingBox = LAYER_SHAPES[layerItem.layerAmount].bounds().move(placementPos).move(.0, placementState.getOptionalValue(FallingLayer.LAYERS).orElse(0)/8.0, .0);
			AABB PlayerBoundingBox = player.getBoundingBox();

			//System.out.println("BLock's bounding box ("+BlockBoundingBox+") and Player's bounding box("+PlayerBoundingBox+") "+(BlockBoundingBox.intersects(PlayerBoundingBox) ? "intersect" : "do not intersect"));
			if (BlockBoundingBox.intersects(PlayerBoundingBox)) return InteractionResult.FAIL;
		}

		//this is intended for handling stacking up with the item.
		if (context.getClickedFace().equals(Direction.UP) && existingState.hasProperty(FallingLayer.LAYERS) && existingState.getBlock() == this.getBlock()) {
			int currentLayers = existingState.getValue(FallingLayer.LAYERS);
			if (currentLayers < FallingLayer.MAX_LAYERS) {
				if (!level.isClientSide) {
					int layers = currentLayers + this.getLayerAmount();
					if (layers > FallingLayer.MAX_LAYERS) {
						if (level.getBlockState(pos.above()).canBeReplaced() && !(level.getBlockState(pos.above()).is(Blocks.SNOW))) {
							if (existingState.hasBlockEntity()) {
								((EntityBlock) existingState.getBlock()).newBlockEntity(pos, existingState);
								((EntityBlock) existingState.getBlock()).newBlockEntity(pos.above(), existingState);
							}
							level.setBlockAndUpdate(pos, existingState.setValue(FallingLayer.LAYERS, FallingLayer.MAX_LAYERS).setValue(BlockStateProperties.WATERLOGGED, level.getFluidState(pos).is(Fluids.WATER)).trySetValue(BlockStateProperties.SNOWY, isSnowySetting(level.getBlockState(pos.above()))));
							level.setBlockAndUpdate(pos.above(), existingState.setValue(FallingLayer.LAYERS, layers - FallingLayer.MAX_LAYERS).setValue(BlockStateProperties.WATERLOGGED, level.getFluidState(pos.above()).is(Fluids.WATER)).trySetValue(BlockStateProperties.SNOWY, isSnowySetting(level.getBlockState(pos.above()))));
						} else {
							return InteractionResult.FAIL;
						}
					} else {
						if (existingState.hasBlockEntity())
							((EntityBlock) existingState.getBlock()).newBlockEntity(pos, existingState);
						level.setBlockAndUpdate(pos, existingState.setValue(FallingLayer.LAYERS, layers).setValue(BlockStateProperties.WATERLOGGED, level.getFluidState(pos).is(Fluids.WATER)).trySetValue(BlockStateProperties.SNOWY, isSnowySetting(level.getBlockState(pos.above()))));
					}
					level.playSound(null, pos, existingState.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
					if ((context.getPlayer() != null) && (!context.getPlayer().isCreative())) {
						itemstack.shrink(1);
					}
				}
				return InteractionResult.sidedSuccess(level.isClientSide);
			}
		}

		//this is intended for when we arent clicking on the item.
		if (placementState.canBeReplaced(blockPlaceContext)) {
			if (!level.isClientSide) {
				int currentLayers = placementState.getOptionalValue(FallingLayer.LAYERS).orElse(0);
				if (currentLayers < FallingLayer.MAX_LAYERS) {
					int layers = currentLayers + this.getLayerAmount();
					placementState = this.getBlock().defaultBlockState().setValue(FallingLayer.LAYERS, Math.min(layers, FallingLayer.MAX_LAYERS)).setValue(BlockStateProperties.WATERLOGGED,level.getFluidState(placementPos).is(Fluids.WATER)).trySetValue(BlockStateProperties.SNOWY, isSnowySetting(level.getBlockState(placementPos.above())));
					if (layers > FallingLayer.MAX_LAYERS) {
						if (level.getBlockState(placementPos.above()).canBeReplaced()) {
							if (placementState.hasBlockEntity()) ((EntityBlock) placementState.getBlock()).newBlockEntity(placementPos, placementState);
							level.setBlockAndUpdate(placementPos, placementState.setValue(FallingLayer.LAYERS, FallingLayer.MAX_LAYERS).setValue(BlockStateProperties.WATERLOGGED,level.getFluidState(placementPos).is(Fluids.WATER)) );
							if (placementState.hasBlockEntity()) ((EntityBlock) placementState.getBlock()).newBlockEntity(placementPos.above(), placementState);
							level.setBlockAndUpdate(placementPos.above(), placementState.setValue(FallingLayer.LAYERS, layers - FallingLayer.MAX_LAYERS).setValue(BlockStateProperties.WATERLOGGED,level.getFluidState(placementPos.above()).is(Fluids.WATER)) );
						} else {
							return InteractionResult.FAIL;
						}
					} else {
						if (placementState.hasBlockEntity()) ((EntityBlock) placementState.getBlock()).newBlockEntity(placementPos, placementState);
						level.setBlockAndUpdate(placementPos, placementState.setValue(FallingLayer.LAYERS, layers).setValue(BlockStateProperties.WATERLOGGED,level.getFluidState(placementPos).is(Fluids.WATER)));
					}
					level.playSound(null, placementPos, placementState.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
					if ((context.getPlayer() != null) && (!context.getPlayer().isCreative())) {
						itemstack.shrink(1);
					}
					return InteractionResult.sidedSuccess(level.isClientSide);
				}
			}
			return InteractionResult.sidedSuccess(level.isClientSide);
		}return super.useOn(context);
	}

	@Override public @NotNull Component getName(ItemStack itemStack) {return Component.translatable(this.getDescriptionId(itemStack)+item_suffix);}
	public int getLayerAmount() {return layerAmount;}
}