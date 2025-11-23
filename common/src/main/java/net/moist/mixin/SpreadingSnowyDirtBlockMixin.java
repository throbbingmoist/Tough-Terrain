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

	@Shadow private static boolean canBeGrass(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
		return true;
	}
//	@Unique private static boolean tough_terrain$canBeGrassDebug(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
//		BlockPos blockPos2 = blockPos.above();
//		BlockState blockState2 = levelReader.getBlockState(blockPos2);
//		if (blockState2.is(Blocks.SNOW) && blockState2.getValue(SnowLayerBlock.LAYERS) == 1) {
//			System.out.println("true at"+blockPos+" due to snow layers only being one");
//			return true;
//		} else if (blockState2.getFluidState().getAmount() == 8) {
//			System.out.println("false at"+blockPos+" due to fluid");
//			return false;
//		} else if (blockState2.hasProperty(FallingLayer.LAYERS) && blockState2.getValue(FallingLayer.LAYERS) > 1) {
//			System.out.println("false at"+blockPos+" due to layers and exceeding");
//			return false;
//		} else {
//			int i = LightEngine.getLightBlockInto(levelReader, blockState, blockPos, blockState2, blockPos2, Direction.UP, blockState2.getLightBlock(levelReader, blockPos2));
//			System.out.println((i < levelReader.getMaxLightLevel())+" at"+blockPos+" due to light ("+i+","+levelReader.getMaxLightLevel()+")");
//			return i < levelReader.getMaxLightLevel();
//		}
//	}

	@Unique private static boolean tough_terrain$canBeGrass(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.above();
		BlockState blockState2 = levelReader.getBlockState(blockPos2);
		if (blockState2.is(Blocks.SNOW) && blockState2.getValue(SnowLayerBlock.LAYERS) == 1) {
			return true;
		} else if (blockState2.getFluidState().getAmount() == 8) {
			return false;
		} else if (blockState2.hasProperty(FallingLayer.LAYERS) && blockState2.getValue(FallingLayer.LAYERS) > 1) {
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


	@Inject(method = "randomTick", at = @At(value = "HEAD"), cancellable = true)
	private void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
		if (!tough_terrain$canBeGrass(state, level, pos)) {
			if (state.hasProperty(FallingLayer.LAYERS)) {
				level.setBlockAndUpdate(pos, ModBlocks.LOOSE_DIRT.getPlacedLayer().defaultBlockState().setValue(FallingLayer.LAYERS, state.getValue(FallingLayer.LAYERS)));
				ci.cancel();
			} else {
				level.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
				ci.cancel();
			}
		} else if (level.getMaxLocalRawBrightness(pos.above()) >= 9) {
			BlockState origin_state = this.defaultBlockState();
			for(int i = 0; i < 4; ++i) {
				BlockPos target_pos = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
				BlockState target_state = level.getBlockState(target_pos);

				if (origin_state.hasProperty(FallingLayer.LAYERS)) {
					if (target_state.hasProperty(FallingLayer.LAYERS) && ModBlocks.IsLayerOvergrowable(target_state) && (tough_terrain$canBeGrass(target_state, level, target_pos) && !level.getFluidState(target_pos).is(FluidTags.WATER))) {
						int val = target_state.getValue(FallingLayer.LAYERS); if (val != 8) {
							level.setBlockAndUpdate(target_pos, origin_state.setValue(FallingLayer.LAYERS,val));
						} else {
							level.setBlockAndUpdate(target_pos, ModBlocks.GetFullBlockToGrow(origin_state));
						}
						ci.cancel();
					} else {
						if(target_state.is(Blocks.DIRT) && (tough_terrain$canBeGrass(target_state, level, target_pos) && !level.getFluidState(target_pos).is(FluidTags.WATER))) {
							level.setBlockAndUpdate(target_pos, ModBlocks.GetFullBlockToGrow(origin_state));
							ci.cancel();
						}
					}
					ci.cancel();
				} else {
					if (target_state.hasProperty(FallingLayer.LAYERS) && ModBlocks.IsLayerOvergrowable(target_state) && (tough_terrain$canBeGrass(target_state, level, target_pos) && !level.getFluidState(target_pos).is(FluidTags.WATER))) {
						level.setBlockAndUpdate(target_pos, ModBlocks.GetLayerBlockToGrow(origin_state, target_state.getValue(FallingLayer.LAYERS)));
						ci.cancel();
					}
					if (!tough_terrain$canBeGrass(target_state, level, target_pos)) {ci.cancel();}
				}
			}
		}
	}
}
