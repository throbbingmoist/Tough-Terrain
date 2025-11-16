package net.moist.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.moist.Terrain;
import net.moist.block.content.FallingLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockMixin {
	@Shadow
	private BlockState defaultBlockState;

	@Inject(method = "shouldRenderFace", at = @At(value = "HEAD"), cancellable = true)
	private static void tough_terrain$shouldRenderFace(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction, BlockPos blockPos2, CallbackInfoReturnable<Boolean> cir) {
		if (blockState.is(Blocks.SNOW)) {
			BlockState stateBelowSnow = blockGetter.getBlockState(blockPos.below());
			if (stateBelowSnow.hasProperty(FallingLayer.LAYERS)) {
				cir.setReturnValue(true);
				cir.cancel();
			}
		}
	}
	// todo: rewrite this part for a better check possibly?
}
