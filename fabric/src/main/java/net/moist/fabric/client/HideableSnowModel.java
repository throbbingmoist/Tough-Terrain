package net.moist.fabric.client;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
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
import net.moist.block.entity.LayerBE;
import net.moist.block.entity.TerrainBlockEntities;
import net.moist.util.ForceRenderableModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;


public class HideableSnowModel implements BakedModel, FabricBakedModel, ForceRenderableModel {
	private final BakedModel bakedOriginal;
	private boolean shouldForceRender;
	public HideableSnowModel(BakedModel bakedOriginal) {
		this.bakedOriginal = bakedOriginal;
		this.shouldForceRender = false;
	}
	@Override public boolean isVanillaAdapter() {
		return false;
	}

	@Override public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
		if ((blockView.getBlockState(pos.below()).hasProperty(FallingLayer.LAYERS) && state.is(Blocks.SNOW)) && !this.shouldForceRender) {
		} else {
			if (this.shouldForceRender) this.shouldForceRender = false;
			bakedOriginal.emitBlockQuads(blockView, state, pos, randomSupplier, context);
		}
	}


	@Override public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {bakedOriginal.emitItemQuads(stack, randomSupplier, context);}

	@Override public @NotNull List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource randomSource) {
		return bakedOriginal.getQuads(blockState, direction, randomSource);
	}

	@Override public boolean useAmbientOcclusion() {return true;}
	@Override public boolean isGui3d() {return bakedOriginal.isGui3d();}
	@Override public boolean usesBlockLight() {return false;}
	@Override public boolean isCustomRenderer() {return bakedOriginal.isCustomRenderer();}
	@Override public @NotNull TextureAtlasSprite getParticleIcon() {return bakedOriginal.getParticleIcon();}
	@Override public @NotNull ItemTransforms getTransforms() {return bakedOriginal.getTransforms();}
	@Override public @NotNull ItemOverrides getOverrides() {return bakedOriginal.getOverrides();}

	public BakedModel shouldForceRender() {
		this.shouldForceRender = true;
		return this;
	}
}
