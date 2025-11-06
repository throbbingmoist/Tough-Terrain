package net.moist.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.moist.block.content.FallingLayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.concurrent.atomic.AtomicInteger;

@Mixin(LevelRenderer.class)
public class LevelMixin {

	@Shadow
	@Nullable
	private ClientLevel level;
	@Unique private static AtomicInteger tough_terrain$lastKnownLayerHeight = new AtomicInteger(0);
	@Unique private static BlockPos tough_terrain$blockPos;

	@ModifyVariable(
		method = "renderLevel",
		at = @At(
			value = "INVOKE_ASSIGN",
			target = "Lnet/minecraft/core/BlockPos;of(J)Lnet/minecraft/core/BlockPos;"
		),
		expect = 0
	)
	public BlockPos getShift(BlockPos blockPos) {
		if (this.level != null) {
			tough_terrain$lastKnownLayerHeight.set(
				this.level.getBlockState(blockPos.below()).getOptionalValue(FallingLayer.LAYERS).orElse(0)
			);
		}
		tough_terrain$blockPos = blockPos;
		return blockPos;
	}

	@ModifyVariable(
		method = "renderLevel",
		at = @At(
			value = "INVOKE_ASSIGN",
			ordinal = 1,
			target = "Lcom/mojang/blaze3d/vertex/PoseStack;last()Lcom/mojang/blaze3d/vertex/PoseStack$Pose;"
		),
		expect = 0
	)
	public PoseStack.Pose shiftPose(PoseStack.Pose pose2) {
		if (this.level != null) {
			if (this.level.getBlockState(tough_terrain$blockPos).is(Blocks.SNOW) && this.level.getBlockState(tough_terrain$blockPos.below()).hasProperty(FallingLayer.LAYERS)) {
				pose2.pose().translate(0.0f, -1.0f + ( (tough_terrain$lastKnownLayerHeight.get() * 2f) / 16f), 0.0f);
			}
		}
		return pose2;
	}

}
