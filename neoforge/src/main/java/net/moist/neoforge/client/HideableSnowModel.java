package net.moist.neoforge.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.moist.block.content.FallingLayer;
import net.moist.util.ForceRenderableModel;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HideableSnowModel extends BakedModelWrapper<BakedModel> implements ForceRenderableModel {
	private BakedModel originalModel;
	private boolean shouldForceRender;
	public static final ModelProperty<Boolean> SHOULD_HIDE = new ModelProperty<>();

	public HideableSnowModel(BakedModel originalModel) {super(originalModel);
		this.originalModel = originalModel;
		this.shouldForceRender = false;
	}

	@Override public @NotNull ModelData getModelData(BlockAndTintGetter level, BlockPos pos, @NotNull BlockState state, ModelData modelData) {
		boolean shouldHide = level.getBlockState(pos.below()).hasProperty(FallingLayer.LAYERS);
		return ModelData.builder()
			.with(SHOULD_HIDE, shouldHide)
			.build();
	}

	@Override
	public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, ModelData modelData, @Nullable RenderType renderType) {
		if (state.is(Blocks.SNOW)) {
			if (Boolean.TRUE.equals(modelData.get(SHOULD_HIDE)) && !this.shouldForceRender) {
				return List.of();
			}
		}
		if (this.shouldForceRender) this.shouldForceRender = false;
		return originalModel.getQuads(state, side, rand, modelData, renderType);
	}

	public BakedModel shouldForceRender() {
		this.shouldForceRender = true;
		return this;
	}
}
