package net.moist.recipe;


import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class LooseningRecipe implements Recipe<RecipeInput> {
	private final Block inputBlock;
	private final BlockState resultState;


	public LooseningRecipe(Block inputBlock, BlockState resultState) {
		this.inputBlock = inputBlock;
		this.resultState = resultState;
	}

	public boolean matches(Level level, BlockPos pos) {return level.getBlockState(pos).is(this.inputBlock);}

	public boolean matches(RecipeInput recipeInput, Level level) {return false;}
	public ItemStack assemble(RecipeInput recipeInput, HolderLookup.Provider provider) {return ItemStack.EMPTY;}
	public boolean canCraftInDimensions(int i, int j) {return false;}
	public ItemStack getResultItem(HolderLookup.Provider provider) {return null;}


	public RecipeSerializer<?> getSerializer() {return RecipeTypes.LOOSEN_SERIALIZER.get();}
	public RecipeType<?> getType() {return RecipeTypes.WORLD_LOOSEN.get();}

	public Block getInputBlock() { return this.inputBlock; }
	public BlockState getResultState() { return this.resultState; }
}
