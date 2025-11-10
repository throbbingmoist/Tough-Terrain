package net.moist.fabric.client;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.moist.block.content.FallingLayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.List;
import java.util.function.Supplier;

public class ShiftedSnowModel implements BakedModel {
	private final BakedModel bakedOriginal;

	public ShiftedSnowModel(BakedModel bakedOriginal) {
		this.bakedOriginal = bakedOriginal;
	}

	@Override public boolean isVanillaAdapter() { return false; }

	@Override
	public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
		QuadEmitter emitter = context.getEmitter();
		RandomSource random = randomSupplier.get();

		boolean needsShift = shouldDownShift(blockView, pos);

		if (needsShift && state.is(Blocks.SNOW)) {
			// Create a translation matrix for the shift

			RenderContext.QuadTransform shiftTransform = quad -> {
				for (int i = 0; i < 4; i++) {
					float x = quad.x(i);
					float y = quad.y(i);
					float z = quad.z(i);
					quad.pos(i, x, y - getDownShift(blockView, pos)/16f, z);
				}
				return true;
			};
			context.pushTransform(shiftTransform);

			if (bakedOriginal instanceof FabricBakedModel fabricModel) {
				fabricModel.emitBlockQuads(blockView, state, pos, randomSupplier, context);
			} else {
				for (Direction direction : Direction.values()) {
					for (BakedQuad quad : bakedOriginal.getQuads(state, direction, random)) {
						emitter.copyFrom((QuadView) quad);
						emitter.emit();
					}
				}
			}
			if (needsShift) {
				context.popTransform();
			}
		} else {
			bakedOriginal.emitBlockQuads(blockView, state, pos, randomSupplier, context);
		}
	}

	@Override
	public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
		bakedOriginal.emitItemQuads(stack, randomSupplier, context);
	}


	@Override public @NotNull List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource randomSource) {
		return bakedOriginal.getQuads(blockState, direction, randomSource);
	}

	@Override public boolean useAmbientOcclusion() {return bakedOriginal.useAmbientOcclusion();}
	@Override public boolean isGui3d() {return bakedOriginal.isGui3d();}
	@Override public boolean usesBlockLight() {return bakedOriginal.usesBlockLight();}
	@Override public boolean isCustomRenderer() {return bakedOriginal.isCustomRenderer();}
	@Override public TextureAtlasSprite getParticleIcon() {return bakedOriginal.getParticleIcon();}
	@Override public ItemTransforms getTransforms() {return bakedOriginal.getTransforms();}
	@Override public ItemOverrides getOverrides() {return bakedOriginal.getOverrides();}

	private boolean shouldDownShift(BlockAndTintGetter world, BlockPos pos) {
		BlockState belowState = world.getBlockState(pos.below());
		return belowState.hasProperty(FallingLayer.LAYERS);
	}
	private float getDownShift(BlockAndTintGetter world, BlockPos pos) {
		BlockState belowState = world.getBlockState(pos.below());
		if (shouldDownShift(world, pos)) {
			return 16.0f - belowState.getValue(FallingLayer.LAYERS) * 2.0f;
		}
		return 0.0f;
	}
}
