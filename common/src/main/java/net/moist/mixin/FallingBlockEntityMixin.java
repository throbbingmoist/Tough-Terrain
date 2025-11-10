package net.moist.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.moist.Terrain;
import net.moist.block.content.FallingLayer;
import org.joml.Vector3f;
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

		double offset = Math.min(Math.max(entity.getDeltaMovement().y(), -entity.getDeltaMovement().y()), 1.5); Vec3 detectionCoords = entity.position().relative(Direction.DOWN, 0.5+offset);
		BlockHitResult blockHitResult = level.clip(new ClipContext(detectionCoords.add(0.5, 1.0, 0.5), detectionCoords.subtract(0.5, 0.0, 0.5), ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, this));

		BlockPos currentPos = blockHitResult.getBlockPos();
		BlockState fallingBlockState = this.getBlockState();
		BlockState existingState = level.getBlockState(currentPos);

		//Terrain.LOGGER.info("SPEED: " + entity.getDeltaMovement().y() + ", AT "+ entity.position() +" ||| RESULT:" + blockHitResult.getType().toString() + " on "+ existingState +" at " + detectionCoords + "(offset "+offset+" down) at age " + this.time);
		// use a velocity check to determine if the block is about to land
		if (entity.getDeltaMovement().y() <= 0 && !level.isClientSide && fallingBlockState.hasProperty(FallingLayer.LAYERS)) {
//			if (blockHitResult.getType() != HitResult.Type.MISS) {
//				((ServerLevel) level).sendParticles(new DustParticleOptions(new Vector3f(1f, 0f, 0f), 1.0f), detectionCoords.x, detectionCoords.y, detectionCoords.z, 1, 0.0f, 0.0f, 0.0f, 0.0f);
//			} else {
//				((ServerLevel) level).sendParticles(new DustParticleOptions(new Vector3f(0f, 1f, 0f), 1.0f), detectionCoords.x, detectionCoords.y, detectionCoords.z, 1, 0.0f, 0.0f, 0.0f, 0.0f);
//			} this is just a commented out debug string i used for visualizing clipping. make no mistake, this is being left in case i update that again.
			if (existingState.hasProperty(FallingLayer.LAYERS) && fallingBlockState.hasProperty(FallingLayer.LAYERS) && (existingState.is(fallingBlockState.getBlock()))) { // we check if the block we land on has layers, is the same as the falling block, and if we have layers as a redundancy check. just in case.
				//Terrain.LOGGER.info("Triggering Block landed in layers.");
				int currentLayers = existingState.getValue(FallingLayer.LAYERS);
				int fallingLayers = fallingBlockState.getValue(FallingLayer.LAYERS);
				int totalLayers = currentLayers + fallingLayers;

				if (totalLayers <= FallingLayer.MAX_LAYERS) { // this just straight up calculates if it's overflow time
					BlockState newState = existingState.setValue(FallingLayer.LAYERS, totalLayers);
					level.setBlock(currentPos, newState, 11);
					level.playSound(null, currentPos, this.blockState.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1f, 0.2f);
					entity.discard();
					ci.cancel();
				} else {
					int excessLayers = totalLayers - FallingLayer.MAX_LAYERS; // this is the magic part that calculates amount to spawn of the stuff above.
					BlockState newMaxState = existingState.setValue(FallingLayer.LAYERS, FallingLayer.MAX_LAYERS);
					level.setBlock(currentPos, newMaxState, 11); // it sets the block here, then, above us, we also place the excess layers
					level.playSound(null, currentPos, this.blockState.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1f, 0.2f);
					BlockState excessState = fallingBlockState.setValue(FallingLayer.LAYERS, excessLayers);
					level.setBlock(currentPos.above(), excessState, 11);
					entity.discard();
					ci.cancel();
				}
				entity.discard();
				ci.cancel();
			} else if (
				(blockHitResult.getType() != HitResult.Type.MISS) && fallingBlockState.hasProperty(FallingLayer.LAYERS) &&
				!(level.getBlockState(entity.blockPosition()).is(fallingBlockState.getBlock()) || level.getBlockState(entity.blockPosition().below(1)).is(fallingBlockState.getBlock()) || level.getBlockState(entity.blockPosition().below(2)).is(fallingBlockState.getBlock()))
			) { // make sure we hit something before running the code
				currentPos = entity.blockPosition();
				existingState = level.getBlockState(currentPos);
				//Terrain.LOGGER.info("Triggering Block Hit Result != Miss code");
				if (existingState.canBeReplaced()) { // check if the block can be placed.
					if (level.setBlock(currentPos, fallingBlockState, 11)) {
						//Terrain.LOGGER.info("Placing "+fallingBlockState+"at "+currentPos);
						level.playSound(null, currentPos, this.blockState.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1f, 0.2f);
						entity.discard();
						ci.cancel();
					} else {  // fallback.
						//Terrain.LOGGER.info("Dropping some "+fallingBlockState+"at "+currentPos);
						for (int i = 1; i <= fallingBlockState.getValue(FallingLayer.LAYERS); i++) {entity.spawnAtLocation((ServerLevel) level, this.getBlockState().getBlock());}
						entity.discard();
						ci.cancel();
					}
				} else {
					//Terrain.LOGGER.info("Cant replace"+existingState+"! dropping some "+fallingBlockState+"at "+currentPos);
					for (int i = 1; i <= fallingBlockState.getValue(FallingLayer.LAYERS); i++) {entity.spawnAtLocation((ServerLevel) level, this.getBlockState().getBlock());}
					entity.discard();
					ci.cancel();
				}
			}
		}
	}
}
