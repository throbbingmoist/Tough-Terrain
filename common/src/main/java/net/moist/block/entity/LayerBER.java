package net.moist.block.entity;

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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.moist.HideableSnowModelHelper;
import net.moist.Terrain;
import net.moist.block.content.FallingLayer;

public class LayerBER implements BlockEntityRenderer<LayerBE> {
	@Override
	public void render(LayerBE blockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
		Minecraft minecraft = Minecraft.getInstance();
		Level level = blockEntity.getLevel();
		Player player = minecraft.player;
		if (level == null || player == null) return;

		BlockState state = blockEntity.getBlockState(); BlockPos pos = blockEntity.getBlockPos(); BlockState aboveState = level.getBlockState(pos.above());
		int snow_layers_below = Math.clamp(blockEntity.getAboveLayers(), 0, 8 - blockEntity.getLayers()); int snow_layers_above = blockEntity.getAboveLayers() - snow_layers_below;

		if (!aboveState.is(Blocks.SNOW)) return;//if (blockEntity.getLevel().getGameTime() % 20 == 0) Terrain.LOGGER.info("lower:"+snow_layers_below+", upper:"+snow_layers_above);
		BlockRenderDispatcher blockRenderer = minecraft.getBlockRenderer();VertexConsumer buffer = multiBufferSource.getBuffer(RenderType.solid());RandomSource random = RandomSource.create();

		//System.out.println(distanceMult); … yeah, using a distance multiplier probably isn't best practice… but if it works, it works, and this is way less effort than messing with vanilla's rendering code.

		float distanceMult = Math.clamp(1f - (float) Vec3.atCenterOf(blockEntity.getBlockPos()).distanceTo(player.getEyePosition()) / (256 * 12), 0f, 1f);

		poseStack.pushPose();
		poseStack.translate((1.0f-distanceMult)/2, 0.01f, (1.0f-distanceMult)/2);
		poseStack.scale(distanceMult, 1, distanceMult);

		if (snow_layers_above == 0) {
			BlockState insideState = level.getBlockState(pos.above()).getBlock().defaultBlockState().setValue(BlockStateProperties.LAYERS, blockEntity.getLayers()+snow_layers_below);//if (blockEntity.getLevel().getGameTime() % 20 == 0) Terrain.LOGGER.info(insideState.toString());
			BakedModel insideModel = blockRenderer.getBlockModel(insideState);
			blockRenderer.getModelRenderer().tesselateBlock(level, insideModel, insideState, pos, poseStack, buffer, false, random, state.getSeed(pos), OverlayTexture.NO_OVERLAY);
		} else {
			BlockState insideState = level.getBlockState(pos.above()).getBlock().defaultBlockState().setValue(BlockStateProperties.LAYERS, 8);//if (blockEntity.getLevel().getGameTime() % 20 == 0) Terrain.LOGGER.info(insideState.toString());
			BakedModel insideModel = blockRenderer.getBlockModel(insideState);
			blockRenderer.getModelRenderer().tesselateBlock(level, insideModel, insideState, pos, poseStack, buffer, false, random, state.getSeed(pos), OverlayTexture.NO_OVERLAY);
		}
//		poseStack.translate(-distanceMult, -0.01f, -distanceMult);
//		poseStack.scale(1f, 1f, 1f);

		poseStack.popPose();


		if (snow_layers_above > 0) {BlockState coverState = level.getBlockState(pos.above()).getBlock().defaultBlockState().setValue(BlockStateProperties.LAYERS, snow_layers_above);
			poseStack.pushPose();
			BakedModel coverModel = (BakedModel) HideableSnowModelHelper.shouldForceRender(blockRenderer.getBlockModel(coverState));//if (blockEntity.getLevel().getGameTime() % 20 == 0) Terrain.LOGGER.info(coverState.toString());
			poseStack.translate(0.0f, 1.0f, 0.0f);
			blockRenderer.getModelRenderer().tesselateBlock(level, coverModel, coverState, pos.above(), poseStack, buffer, false, random, state.getSeed(pos.above()), OverlayTexture.NO_OVERLAY);
			poseStack.popPose();
		}
	}

	public boolean shouldRenderOffScreen(LayerBE blockEntity) {
		return false;
	}

	public int getViewDistance() {
		return 256;
	}

	public boolean shouldRender(LayerBE blockEntity, Vec3 vec3) {
		return Vec3.atCenterOf(blockEntity.getBlockPos()).closerThan(vec3, (double)getViewDistance());
	}
}
