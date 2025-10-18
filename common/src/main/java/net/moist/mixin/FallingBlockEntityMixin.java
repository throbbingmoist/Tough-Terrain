package net.moist.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.moist.block.content.LayerBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity {
	@Shadow public abstract BlockState getBlockState();

	@Shadow private BlockState blockState;

	public FallingBlockEntityMixin() {
		super(null, null); // Dummy constructor for mixin
	}

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void tick(CallbackInfo ci) {
		FallingBlockEntity entity = (FallingBlockEntity) (Object) this;
		Level level = entity.level();

		BlockPos currentPos = entity.blockPosition();
		BlockState existingState = level.getBlockState(currentPos);
		BlockState fallingBlockState = this.getBlockState();

		// Use a velocity check to determine if the block is about to land
		if (entity.getDeltaMovement().y() < 0 && !level.isClientSide) {
			// Check if the block at the position below is our stacking block
			if (existingState.getBlock() instanceof LayerBlock && fallingBlockState.getBlock() instanceof LayerBlock && existingState.is(fallingBlockState.getBlock())) {
				int currentLayers = existingState.getValue(LayerBlock.LAYERS);
				int fallingLayers = fallingBlockState.getValue(LayerBlock.LAYERS);
				int totalLayers = currentLayers + fallingLayers;

				if (totalLayers <= LayerBlock.MAX_LAYERS) {
					BlockState newState = existingState.setValue(LayerBlock.LAYERS, totalLayers);
					level.setBlock(currentPos, newState, 11);
					level.playSound(null, currentPos, this.blockState.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1f, 0.2f);
					entity.discard();
					ci.cancel();
				} else {
					int excessLayers = totalLayers - LayerBlock.MAX_LAYERS;
					BlockState newMaxState = existingState.setValue(LayerBlock.LAYERS, LayerBlock.MAX_LAYERS);
					level.setBlock(currentPos, newMaxState, 11);
					level.playSound(null, currentPos, this.blockState.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1f, 0.2f);

					BlockState excessState = fallingBlockState.setValue(LayerBlock.LAYERS, excessLayers);
					FallingBlockEntity.fall(level, currentPos.above(), excessState);

					entity.discard();
					ci.cancel();
				}
			} else if (existingState.getBlock() instanceof LayerBlock && fallingBlockState.getBlock() instanceof LayerBlock) {
				if (existingState.canBeReplaced()) {
					if (level.setBlock(currentPos, fallingBlockState, 11)) {
						level.playSound(null, currentPos, this.blockState.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1f, 0.2f);
						entity.discard();
						ci.cancel();
					} else {
						for (int i = 1; i <= fallingBlockState.getValue(LayerBlock.LAYERS); i++) {
							entity.spawnAtLocation(this.getBlockState().getBlock());
						}
						entity.discard();
						ci.cancel();
					}
				} else {
					for (int i = 1; i <= fallingBlockState.getValue(LayerBlock.LAYERS); i++) {
						entity.spawnAtLocation(this.getBlockState().getBlock());
					}
					entity.discard();
					ci.cancel();
				}
			}
		}
	}
}
