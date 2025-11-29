package net.moist.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.moist.block.content.FallingLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SnowLayerBlock.class)
public class SnowLayerMixin {

	@ModifyVariable(at = @At("HEAD"), method = "<init>", argsOnly = true)
	private static BlockBehaviour.Properties SnowLayerBlock(BlockBehaviour.Properties properties) {
		return properties.dynamicShape();
	}

	@WrapMethod(method = "getShape")
	private VoxelShape tough_terrain$getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext, Operation<VoxelShape> original) {
		BlockState belowState = blockGetter.getBlockState(blockPos.below());
		int thisHeight = blockState.getValue(BlockStateProperties.LAYERS);
		if (belowState.hasProperty(FallingLayer.LAYERS)) {
			int belowHeight = belowState.getValue(FallingLayer.LAYERS);
			double top = belowHeight + thisHeight;
			return Block.box(
				0.0, Math.max(-16,-16+ (belowHeight*2)), 0.0,
				16.0, Math.max(-16,-16+(top*2)), 16.0);
		}
		return Block.box(0.0, 0.0, 0.0, 16.0, (thisHeight*2), 16.0);
	}
	@WrapMethod(method = "getCollisionShape")
	private VoxelShape tough_terrain$getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext, Operation<VoxelShape> original) {
		BlockState belowState = blockGetter.getBlockState(blockPos.below());
		int thisHeight = blockState.getValue(BlockStateProperties.LAYERS);
		if (belowState.hasProperty(FallingLayer.LAYERS)) {
			int belowHeight = belowState.getValue(FallingLayer.LAYERS);
			double top = belowHeight + thisHeight;
			return Block.box(
				0.0, Math.max(-16,-16+ (belowHeight*2)), 0.0,
				16.0, Math.max(-16,-16+(top*2)) - 2.0, 16.0);
		}
		return Block.box(0.0, 0.0, 0.0, 16.0, (thisHeight*2) - 2.0, 16.0);
	}
	@WrapMethod(method = "getVisualShape")
	private VoxelShape tough_terrain$getVisShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext, Operation<VoxelShape> original) {
		BlockState belowState = blockGetter.getBlockState(blockPos.below());
		int thisHeight = blockState.getValue(BlockStateProperties.LAYERS);
		if (belowState.hasProperty(FallingLayer.LAYERS)) {
			int belowHeight = belowState.getValue(FallingLayer.LAYERS);
			double top = belowHeight + thisHeight;
			return Block.box(
				0.0, Math.max(-16,-16+ (belowHeight*2)), 0.0,
				16.0, Math.max(-16,-16+(top*2)), 16.0);
		}
		return Block.box(0.0, 0.0, 0.0, 16.0, (thisHeight*2), 16.0);
	}
	@Inject(method = "getShadeBrightness", at = @At("HEAD"), cancellable = true)
	protected void tough_terrain$getShadeBrightness(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CallbackInfoReturnable<Float> cir) {
		cir.setReturnValue( (blockState.getValue(SnowLayerBlock.LAYERS) == 8 && !blockGetter.getBlockState(blockPos.below()).hasProperty(FallingLayer.LAYERS)) ? 0.2F : 1F);
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
