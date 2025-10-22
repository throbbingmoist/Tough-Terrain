package net.moist.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;
import net.moist.block.ModBlocks;
import net.moist.block.content.LayerBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(SpreadingSnowyDirtBlock.class)
public abstract class SpreadingSnowyDirtBlockMixin extends SnowyDirtBlock {

	@Shadow private static boolean canBeGrass(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.above();
		BlockState blockState2 = levelReader.getBlockState(blockPos2);
		if (blockState2.is(Blocks.SNOW) && (Integer)blockState2.getValue(SnowLayerBlock.LAYERS) == 1) {
			return true;
		} else if (blockState2.getFluidState().getAmount() == 8) {
			return false;
		} else if (!(blockState2.getBlock() instanceof LayerBlock)) {
			return false;
		} else {
			int i = LightEngine.getLightBlockInto(levelReader, blockState, blockPos, blockState2, blockPos2, Direction.UP, blockState2.getLightBlock(levelReader, blockPos2));
			return i < levelReader.getMaxLightLevel();
		}
	}

	@Shadow private static boolean canPropagate(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.above();
		return canBeGrass(blockState, levelReader, blockPos) && !levelReader.getFluidState(blockPos2).is(FluidTags.WATER);
	}

	public SpreadingSnowyDirtBlockMixin() {
		super(null);
	}

	@Inject(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"), cancellable = true)
	private void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource, CallbackInfo ci) {
		if (!canBeGrass(blockState, serverLevel, blockPos)) {
			serverLevel.setBlockAndUpdate(blockPos, Blocks.DIRT.defaultBlockState());
			ci.cancel();
		} else {
			if (serverLevel.getMaxLocalRawBrightness(blockPos.above()) >= 9) {
				BlockState blockState2 = this.defaultBlockState();

				for(int i = 0; i < 4; ++i) {
					BlockPos blockPos2 = blockPos.offset(randomSource.nextInt(3) - 1, randomSource.nextInt(5) - 3, randomSource.nextInt(3) - 1);
					if (serverLevel.getBlockState(blockPos2).getBlock() instanceof LayerBlock  && ((LayerBlock) serverLevel.getBlockState(blockPos2).getBlock()).isOvergrowable() && canPropagate(blockState2, serverLevel, blockPos2)) {
						if (serverLevel.getBlockState(blockPos2).getValue(LayerBlock.LAYERS) == LayerBlock.MAX_LAYERS) {
							serverLevel.setBlockAndUpdate(blockPos2, (BlockState)blockState2.setValue(SNOWY, serverLevel.getBlockState(blockPos2.above()).is(Blocks.SNOW)));
							ci.cancel();
						} else if (serverLevel.getBlockState(blockPos2).getValue(LayerBlock.LAYERS) != LayerBlock.MAX_LAYERS) {
							if (blockState2.getBlock().equals(Blocks.GRASS_BLOCK)) {
								serverLevel.setBlockAndUpdate(blockPos2, ModBlocks.GRASS_LAYER.get().defaultBlockState().setValue(LayerBlock.LAYERS, serverLevel.getBlockState(blockPos2).getValue(LayerBlock.LAYERS)));
							}
							if (blockState2.getBlock().equals(Blocks.MYCELIUM)) {
								serverLevel.setBlockAndUpdate(blockPos2, ModBlocks.MYCELIUM_LAYER.get().defaultBlockState().setValue(LayerBlock.LAYERS, serverLevel.getBlockState(blockPos2).getValue(LayerBlock.LAYERS)));
							}
							ci.cancel();
						}
					}
//					if (serverLevel.getBlockState(blockPos2).getBlock() instanceof LayerBlock && ((LayerBlock) serverLevel.getBlockState(blockPos2).getBlock()).isOvergrowable() &&
//						serverLevel.getBlockState(blockPos2).hasProperty(LayerBlock.LAYERS) && serverLevel.getBlockState(blockPos2).getValue(LayerBlock.LAYERS) == LayerBlock.MAX_LAYERS &&
//						canPropagate(blockState2, serverLevel, blockPos2)) {
//						serverLevel.setBlockAndUpdate(blockPos2, (BlockState)blockState2.setValue(SNOWY, serverLevel.getBlockState(blockPos2.above()).is(Blocks.SNOW)));
//						ci.cancel();
//					}


				}
			}

		}
	}
}
