package net.moist.recipe;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface RecipeBlockInput {
	Block getBlock();
	BlockState getBlockState();
	//BlockState getBlockState(int layers);
}
