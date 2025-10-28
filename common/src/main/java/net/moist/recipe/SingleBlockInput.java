package net.moist.recipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SingleBlockInput implements RecipeBlockInput {
	private final ResourceLocation output_location;

	public SingleBlockInput(ResourceLocation output) {
		this.output_location = output;
	}

	public Block getBlock() {
		return BuiltInRegistries.BLOCK.get(output_location);
	}
	public BlockState getBlockState() {
		return this.getBlock().defaultBlockState();
	}

}
