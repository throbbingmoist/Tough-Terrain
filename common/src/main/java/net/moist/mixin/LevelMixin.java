package net.moist.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.moist.Terrain;
import net.moist.block.content.FallingLayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import static net.moist.Terrain.getLookGranular;

@Mixin(LevelRenderer.class)
public class LevelMixin {

	@Shadow
	@Nullable
	private ClientLevel level;

	@Shadow
	@Final
	private Minecraft minecraft;


	@ModifyVariable(
		method = "renderLevel",
		at = @At(
			value = "INVOKE_ASSIGN",
			ordinal = 0,
			target = "Lnet/minecraft/world/phys/BlockHitResult;getBlockPos()Lnet/minecraft/core/BlockPos;"
		),
		expect = 0
	)
	public BlockPos shiftOutline(BlockPos pos) {
		if (this.level != null) {
			BlockState stateHere = this.level.getBlockState(pos);
			BlockState stateAbove = this.level.getBlockState(pos.above());

			if (stateHere.hasProperty(FallingLayer.LAYERS) && stateAbove.is(Blocks.SNOW) && (getLookGranular(this.minecraft.hitResult) >  stateHere.getOptionalValue(FallingLayer.LAYERS).orElse(0)/8f)) {
				if (stateHere.getValue(FallingLayer.LAYERS) < 8) {return pos.above();}
			}
		}
		return pos;
	}

}
