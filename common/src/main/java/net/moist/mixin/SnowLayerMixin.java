package net.moist.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.moist.block.content.FallingLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SnowLayerBlock.class)
public class SnowLayerMixin {
	@Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
	private void tough_terrain$getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext, CallbackInfoReturnable<VoxelShape> cir) {
		BlockState belowState = blockGetter.getBlockState(blockPos.below());
		if (belowState.hasProperty(FallingLayer.LAYERS)) {
			int thisHeight = blockState.getValue(BlockStateProperties.LAYERS);
			int belowHeight = belowState.getValue(FallingLayer.LAYERS);
			double top = belowHeight + thisHeight;
			cir.setReturnValue(Block.box(
				0.0, Math.max(-16,-16+ (belowHeight*2)), 0.0,
				16.0, Math.max(-16,-16+(top*2)), 16.0));
			cir.cancel();
		}
	}
	@Inject(method = "getVisualShape", at = @At("HEAD"), cancellable = true)
	private void tough_terrain$getVisShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext, CallbackInfoReturnable<VoxelShape> cir) {
		BlockState belowState = blockGetter.getBlockState(blockPos.below());
		if (belowState.hasProperty(FallingLayer.LAYERS)) {
			int thisHeight = blockState.getValue(BlockStateProperties.LAYERS);
			int belowHeight = belowState.getValue(FallingLayer.LAYERS);
			double top = belowHeight + thisHeight;
			cir.setReturnValue(Block.box(
				0.0, Math.max(-16,-16+ (belowHeight*2)), 0.0,
				16.0, Math.max(-16,-16+(top*2)), 16.0));
			cir.cancel();
		}
	}

	@Inject(method = "getShadeBrightness", at = @At("HEAD"), cancellable = true)
	protected void tough_terrain$getShadeBrightness(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CallbackInfoReturnable<Float> cir) {
		cir.setReturnValue((Integer)blockState.getValue(SnowLayerBlock.LAYERS) == 8 && !blockGetter.getBlockState(blockPos.below()).hasProperty(FallingLayer.LAYERS) ? 0.2F : 1.0F);
		cir.cancel();
	}

	@Inject(method = "getBlockSupportShape", at = @At("HEAD"), cancellable = true)
	private void tough_terrain$getBSShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CallbackInfoReturnable<VoxelShape> cir) {
		BlockState belowState = blockGetter.getBlockState(blockPos.below());
		if (belowState.hasProperty(FallingLayer.LAYERS)) {
			int thisHeight = blockState.getValue(BlockStateProperties.LAYERS);
			int belowHeight = belowState.getValue(FallingLayer.LAYERS);
			double top = belowHeight + thisHeight;
			cir.setReturnValue(Block.box(
				0.0, Math.max(-16,-16+ (belowHeight*2)), 0.0,
				16.0, Math.max(-16,-16+(top*2)), 16.0));
			cir.cancel();
		}
	}
	@Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
	private void tough_terrain$getColShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext, CallbackInfoReturnable<VoxelShape> cir) {
		BlockState belowState = blockGetter.getBlockState(blockPos.below());
		if (belowState.hasProperty(FallingLayer.LAYERS)) {
			int thisHeight = blockState.getValue(BlockStateProperties.LAYERS);
			int belowHeight = belowState.getValue(FallingLayer.LAYERS);
			double top = belowHeight + thisHeight;
			cir.setReturnValue(Block.box(
				0.0, Math.max(-16,-16+ (belowHeight*2)), 0.0,
				16.0, Math.max(-16,-16+(top*2))-2, 16.0));
			cir.cancel();
		}
	}

	@Inject(method = "canSurvive", at = @At("HEAD"), cancellable = true)
	private void tough_terrain$canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
		if (levelReader.getBlockState(blockPos.below()).is(Blocks.SNOW)) {
			if (levelReader.getBlockState(blockPos.below(2)).hasProperty(FallingLayer.LAYERS) && levelReader.getBlockState(blockPos.below(2)).getValue(FallingLayer.LAYERS) < 8) {
				cir.setReturnValue(false);
				cir.cancel();
			}
		}
	}
}
