package net.moist.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.HitResult;
import net.moist.block.content.FallingLayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static net.moist.Terrain.getLookGranular;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

	@Shadow @Nullable public LocalPlayer player;
	@Shadow @Nullable public ClientLevel level;

	@Shadow
	@Nullable
	public HitResult hitResult;

	@ModifyVariable(method = "continueAttack", at= @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/phys/BlockHitResult;getBlockPos()Lnet/minecraft/core/BlockPos;"))
	public BlockPos tough_terrain$continueAttack(BlockPos value) {
		if (this.player != null && this.level != null) {
			BlockState stateHere = this.level.getBlockState(value);
			BlockState stateAbove = this.level.getBlockState(value.above());
			if (stateHere.hasProperty(FallingLayer.LAYERS) && stateAbove.is(Blocks.SNOW)) {
				if (stateHere.getValue(FallingLayer.LAYERS) < 8  && (getLookGranular(this.hitResult) >= stateHere.getOptionalValue(FallingLayer.LAYERS).orElse(0)/8f)) {return value.above();}
			}}
		return value;
	}
	@ModifyVariable(method = "startAttack", at= @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/phys/BlockHitResult;getBlockPos()Lnet/minecraft/core/BlockPos;"))
	public BlockPos tough_terrain$startAttack(BlockPos value) {
		if (this.player != null && this.level != null) {
			BlockState stateHere = this.level.getBlockState(value);
			BlockState stateAbove = this.level.getBlockState(value.above());
			if (stateHere.hasProperty(FallingLayer.LAYERS) && stateAbove.is(Blocks.SNOW)) {
				if (stateHere.getValue(FallingLayer.LAYERS) < 8 && (getLookGranular(this.hitResult) >= stateHere.getOptionalValue(FallingLayer.LAYERS).orElse(0)/8f)) {return value.above();}
			}}
		return value;
	}
}
