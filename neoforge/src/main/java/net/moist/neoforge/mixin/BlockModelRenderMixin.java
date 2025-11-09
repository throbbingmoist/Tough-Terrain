package net.moist.neoforge.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.moist.block.content.FallingLayer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ModelBlockRenderer.class)
public class BlockModelRenderMixin {
	@Unique private static final ThreadLocal<BlockPos> LAST_MODIFIED_POS = ThreadLocal.withInitial(() -> null);
	@ModifyVariable(
		method = "putQuadData",
		at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;putBulkData(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/minecraft/client/renderer/block/model/BakedQuad;[FFFFF[IIZ)V"),
		argsOnly = true
	)
	private PoseStack.Pose modifyPoseStackPose(
		PoseStack.Pose pose,
		BlockAndTintGetter blockAndTintGetter,
		BlockState blockState,
		BlockPos blockPos
	) {
		if (!blockPos.equals(LAST_MODIFIED_POS.get())) {
			if (blockState.is(Blocks.SNOW)) {
				BlockState stateBelow = blockAndTintGetter.getBlockState(blockPos.below());
				//Terrain.LOGGER.info("state below snow at "+blockPos+" is "+stateBelow+" ("+blockPos.below()+")");
				if (stateBelow.hasProperty(FallingLayer.LAYERS)) {
					int baseBlockLayers = stateBelow.getValue(FallingLayer.LAYERS);
					float translationAmountY = (16.0f - (baseBlockLayers * 2.0f)) / 16.0f;
					Matrix4f currentMatrix = pose.pose();
					currentMatrix.translate(0.0f, -translationAmountY, 0.0f);
				}
			}
			LAST_MODIFIED_POS.set(blockPos.immutable());
		}
		return pose;
	}
	// OLD ATTEMPT ↑
}
