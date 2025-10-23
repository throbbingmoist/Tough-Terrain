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
import net.moist.block.content.FallingLayer;
import net.moist.block.content.SpreadingLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(SpreadingSnowyDirtBlock.class)
public abstract class SpreadingSnowyDirtBlockMixin extends SnowyDirtBlock {
	private int FORSIZE = 4; // remember to set to 4


	@Shadow private static boolean canBeGrass(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.above();
		BlockState blockState2 = levelReader.getBlockState(blockPos2);
		if (blockState2.is(Blocks.SNOW) && (Integer)blockState2.getValue(SnowLayerBlock.LAYERS) == 1) {
			return true;
		} else if (blockState2.getFluidState().getAmount() == 8) {
			return false;
		} else if (!(blockState2.getBlock() instanceof FallingLayer) && !(blockState2.getBlock() instanceof SpreadingLayer)) {
			return false;
		} else {
			int i = LightEngine.getLightBlockInto(levelReader, blockState, blockPos, blockState2, blockPos2, Direction.UP, blockState2.getLightBlock(levelReader, blockPos2));
			return i < levelReader.getMaxLightLevel();
		}
	}
	@Unique private static boolean terrain$canBeGrass(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.above();
		BlockState blockState2 = levelReader.getBlockState(blockPos2);
		if (blockState2.is(Blocks.SNOW) && (Integer)blockState2.getValue(SnowLayerBlock.LAYERS) == 1) {
			return true;
		} else if (blockState2.getFluidState().getAmount() == 8) {
			return false;
		} else if (blockState2.getBlock() instanceof SpreadingLayer) {
			return false;
		} else if (blockState2.getBlock() instanceof FallingLayer) {
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
	private void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
		if (!terrain$canBeGrass(state, level, pos)) {
			level.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
			ci.cancel();
		} else if (level.getMaxLocalRawBrightness(pos.above()) >= 9) {
			BlockState origin_state = this.defaultBlockState();
			for(int i = 0; i < FORSIZE; ++i) {
				BlockPos target_pos = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
				BlockState target_state = level.getBlockState(target_pos);

				if (origin_state.hasProperty(FallingLayer.LAYERS)) {
					if (target_state.hasProperty(FallingLayer.LAYERS) && ModBlocks.IsLayerOvergrowable(target_state) && (terrain$canBeGrass(target_state, level, target_pos) && !level.getFluidState(target_pos).is(FluidTags.WATER))) {
						int val = target_state.getValue(FallingLayer.LAYERS); if (val != 8) {
							level.setBlockAndUpdate(target_pos, origin_state.setValue(FallingLayer.LAYERS,val));
						} else {
							level.setBlockAndUpdate(target_pos, ModBlocks.GetFullBlockToGrow(origin_state));
						}
						ci.cancel();
					} else {
						if(target_state.is(Blocks.DIRT) && (terrain$canBeGrass(target_state, level, target_pos) && !level.getFluidState(target_pos).is(FluidTags.WATER))) {
							level.setBlockAndUpdate(target_pos, ModBlocks.GetFullBlockToGrow(origin_state));
							ci.cancel();
						}
					}
					ci.cancel();
				} else {
					if (target_state.hasProperty(FallingLayer.LAYERS) && ModBlocks.IsLayerOvergrowable(target_state) && (terrain$canBeGrass(target_state, level, target_pos) && !level.getFluidState(target_pos).is(FluidTags.WATER))) {
						level.setBlockAndUpdate(target_pos, ModBlocks.GetLayerBlockToGrow(origin_state, target_state.getValue(FallingLayer.LAYERS)));
						ci.cancel();
					}
					if (!terrain$canBeGrass(target_state, level, target_pos)) {ci.cancel();}
				}
			}
		}
	}
}
