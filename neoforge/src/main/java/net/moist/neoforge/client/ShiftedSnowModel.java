package net.moist.neoforge.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.models.model.DelegatedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.moist.Terrain;
import net.moist.block.content.FallingLayer;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

import java.awt.image.renderable.RenderContext;
import java.util.List;
import java.util.function.Supplier;

public class ShiftedSnowModel extends BakedModelWrapper<BakedModel> {
	private BakedModel originalModel;

	public ShiftedSnowModel(BakedModel originalModel) {
		super(originalModel);
		this.originalModel = originalModel;
	}

	private boolean shouldDownShift(BlockGetter world, BlockPos pos) {
		BlockState belowState = world.getBlockState(pos.below());
		return belowState.hasProperty(FallingLayer.LAYERS);
	}
	private float getDownShift(BlockGetter world, BlockPos pos) {
		BlockState belowState = world.getBlockState(pos.below());
		if (shouldDownShift(world, pos)) {
			return 16.0f - belowState.getValue(FallingLayer.LAYERS) * 2.0f;
		}
		return 0.0f;
	}

	@Override
	public @NotNull ModelData getModelData(BlockAndTintGetter level, BlockPos pos, @NotNull BlockState state, ModelData modelData) {

		boolean needsShift = shouldDownShift(level, pos);
		float downShift = getDownShift(level, pos);
		return ModelData.builder()
			.with(ModelShiftHelper.NEEDS_SHIFT, needsShift)
			.with(ModelShiftHelper.SHIFT_AMOUNT, downShift)
			.build();
	}

	@Override
	public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, ModelData modelData, @Nullable RenderType renderType) {

		Boolean shouldShift = modelData.get(ModelShiftHelper.NEEDS_SHIFT);
		Float downShift = modelData.get(ModelShiftHelper.SHIFT_AMOUNT);

		if (shouldShift != null && shouldShift && downShift != null && !downShift.equals(0.0f)) {
			List<BakedQuad> originalQuads = originalModel.getQuads(state, side, rand, modelData, renderType);
			return ModelShiftHelper.shiftQuads(originalQuads, -downShift/16f);
		}
		return originalModel.getQuads(state, side, rand, modelData, renderType);
	}
}
