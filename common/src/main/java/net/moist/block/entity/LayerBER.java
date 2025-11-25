package net.moist.block.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.moist.util.ForceRenderableModel;

public class LayerBER implements BlockEntityRenderer<LayerBE> {
	@Override
	public void render(LayerBE blockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
		Minecraft minecraft = Minecraft.getInstance();
		Level level = blockEntity.getLevel();
		Player player = minecraft.player;
		if (level == null || player == null) return;
		BlockState state = blockEntity.getBlockState(); BlockPos pos = blockEntity.getBlockPos(); BlockState aboveState = level.getBlockState(pos.above());
		if (!aboveState.is(Blocks.SNOW)) return;

		int snow_layers_below = Math.clamp(blockEntity.getAboveLayers(), 0, 8 - blockEntity.getLayers());
		int snow_layers_above = blockEntity.getAboveLayers() - snow_layers_below;
		float distanceMult = Math.clamp(1f - (float) Vec3.atCenterOf(blockEntity.getBlockPos()).distanceTo(player.getEyePosition()) / (256 * 16), 0f, 1f);
		float shift = Math.clamp((float) Vec3.atCenterOf(blockEntity.getBlockPos()).distanceTo(player.getEyePosition()) / (256 * 16), 0f, 1f);


		BlockRenderDispatcher blockRenderer = minecraft.getBlockRenderer();VertexConsumer buffer = multiBufferSource.getBuffer(RenderType.solid());RandomSource random = RandomSource.create();


		poseStack.pushPose();poseStack.scale(distanceMult, 1, distanceMult);poseStack.translate(shift/2, 0f, shift/2);

		BlockState insideState = level.getBlockState(pos.above()).getBlock().defaultBlockState().setValue(BlockStateProperties.LAYERS, Math.min(blockEntity.getLayers() + snow_layers_below, 8));//if (blockEntity.getLevel().getGameTime() % 20 == 0) Terrain.LOGGER.info(insideState.toString());;
		Vec3 vec3 = insideState.getOffset(level, pos.above());
		poseStack.translate(vec3.x, vec3.y, vec3.z);
		BakedModel insideModel = blockRenderer.getBlockModel(insideState);
		if (blockEntity.getLayers() != 8) blockRenderer.getModelRenderer().tesselateBlock(level, insideModel, insideState, pos, poseStack, buffer, false, random, state.getSeed(pos), OverlayTexture.NO_OVERLAY);
		poseStack.popPose();
		if (snow_layers_above > 0) {
			poseStack.pushPose();poseStack.translate(0.0f, 1.0f, 0.0f);poseStack.scale(1f, 1f, 1f);
			BlockState coverState = level.getBlockState(pos.above()).getBlock().defaultBlockState().setValue(BlockStateProperties.LAYERS, snow_layers_above);
			BakedModel coverModel = ((ForceRenderableModel) blockRenderer.getBlockModel(coverState)).shouldForceRender();
			blockRenderer.getModelRenderer().tesselateBlock(level, coverModel, coverState, pos.above(), poseStack, buffer, false, random, state.getSeed(pos.above()), OverlayTexture.NO_OVERLAY);
			poseStack.popPose();
		}
	}

	public boolean shouldRenderOffScreen(LayerBE blockEntity) {
		return true;
	}

	public int getViewDistance() {
		return 256;
	}

	public boolean shouldRender(LayerBE blockEntity, Vec3 vec3) {
		return Vec3.atCenterOf(blockEntity.getBlockPos()).closerThan(vec3, (double)getViewDistance());
	}
}
