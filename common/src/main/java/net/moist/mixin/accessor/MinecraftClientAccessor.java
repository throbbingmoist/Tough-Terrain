package net.moist.mixin.accessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftClientAccessor {
	@Accessor("blockRenderer")
	BlockRenderDispatcher getBlockRenderer();
}
