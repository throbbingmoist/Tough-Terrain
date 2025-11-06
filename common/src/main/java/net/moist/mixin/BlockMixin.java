package net.moist.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.moist.block.content.FallingLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockMixin {
	@Inject(method = "shouldRenderFace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/BlockGetter;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"), cancellable = true)
	private static void tough_terrain$shouldRenderFace(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction, BlockPos neighborPos, CallbackInfoReturnable<Boolean> cir) {
		if (blockState.is(Blocks.SNOW)) {
			BlockState stateBelowSnow = blockGetter.getBlockState(blockPos.below());
			if (stateBelowSnow.hasProperty(FallingLayer.LAYERS)) {
				int totalHeight = blockState.getValue(BlockStateProperties.LAYERS) + stateBelowSnow.getValue(FallingLayer.LAYERS);
				VoxelShape fullFace = Block.box(0, stateBelowSnow.getValue(FallingLayer.LAYERS), 0, 16, totalHeight, 16);

				//if (Shapes.joinIsNotEmpty(fullFace)) {
					cir.setReturnValue(true);
					cir.cancel();
				//}
				// If the intersection is not empty, the original method runs (it will return true/skip rendering correctly)
			}
		}
	}
}
