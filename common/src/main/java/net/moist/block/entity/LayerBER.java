package net.moist.block.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.moist.Terrain;
import net.moist.block.content.FallingLayer;
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
		float distanceShrinkAmount = Math.clamp((float) Vec3.atCenterOf(blockEntity.getBlockPos()).distanceTo(player.getEyePosition()) / (256 * 16), 0f, 1f);
		BlockRenderDispatcher blockRenderer = minecraft.getBlockRenderer();VertexConsumer buffer = multiBufferSource.getBuffer(RenderType.solid());RandomSource random = RandomSource.create();

		// query adjacent blocks
		boolean POSX = adjacentLayered(blockEntity,level, pos, Direction.Axis.X, 1);
		boolean NEGX = adjacentLayered(blockEntity,level, pos, Direction.Axis.X, -1);
		boolean POSZ = adjacentLayered(blockEntity,level, pos, Direction.Axis.Z, 1);
		boolean NEGZ = adjacentLayered(blockEntity,level, pos, Direction.Axis.Z, -1);

		poseStack.pushPose();
		float xScalar = (POSX ? 0f : distanceShrinkAmount) + (NEGX ? 0f : distanceShrinkAmount); // determine the scale on the x
		float zScalar = (POSZ ? 0f : distanceShrinkAmount) + (NEGZ ? 0f : distanceShrinkAmount); // ditto for z

		poseStack.scale(1f - xScalar, 1f, 1f - zScalar);
		poseStack.translate(0f, 0.001f, 0f); // here shift a little bit up, because if i dont, it y-fights at the bottom.
		if (POSX && !NEGX) {poseStack.translate(xScalar, 0f, 0f);} // if z+1 is a layer of sufficient height to hide the x-fighting on the block meshes, shift to its side.
		if (POSZ && !NEGZ) {poseStack.translate(0f, 0f, zScalar);} // if z+1 is a layer of sufficient height to hide the z-fighting on the block meshes, shift to its side.
		if (!POSX && !NEGX) {poseStack.translate(xScalar/2, 0f, 0f);} // if x+1 and x-1 are both empty, center it.
		if (!POSZ && !NEGZ) {poseStack.translate(0f, 0f, zScalar/2);} // if z+1 and z-1 are both empty, center it.

		BlockState insideState = level.getBlockState(pos.above()).getBlock().defaultBlockState().setValue(BlockStateProperties.LAYERS, Math.min(blockEntity.getLayers() + snow_layers_below, 8));
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

	public boolean shouldRenderOffScreen(LayerBE blockEntity) {return true;}
	public int getViewDistance() {return 256;}
	public boolean shouldRender(LayerBE blockEntity, Vec3 vec3) {return Vec3.atCenterOf(blockEntity.getBlockPos()).closerThan(vec3, (double)getViewDistance());}

	private boolean adjacentLayered(LayerBE be, Level level, BlockPos pos, Direction.Axis axis, int num) {
		BlockEntity blockEntity = level.getBlockEntity(pos.relative(axis, num));
		return level.getBlockState(pos.relative(axis, num)).hasProperty(FallingLayer.LAYERS) && (blockEntity instanceof LayerBE ? ((LayerBE) blockEntity).getTotalLayers() : 0) >= be.getLayers();
	}
}
