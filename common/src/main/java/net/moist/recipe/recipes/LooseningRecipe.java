package net.moist.recipe.recipes;


import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.moist.recipe.ModRecipes;

public class LooseningRecipe implements Recipe<RecipeInput> {
	private final Either<Block, TagKey<Block>> inputBlock;
	private final BlockState resultState;


	public LooseningRecipe(TagKey<Block> inputBlock, BlockState resultState) {this(Either.right(inputBlock) ,resultState);}
	public LooseningRecipe(Block inputBlock, BlockState resultState) {this(Either.left(inputBlock) ,resultState);}
	public LooseningRecipe(Either<Block, TagKey<Block>> inputBlock, BlockState resultState) {
		this.inputBlock = inputBlock;
		this.resultState = resultState;
	}

	public boolean matches(Level level, BlockPos pos) {
		return this.inputBlock.map(
			block -> level.getBlockState(pos).is(block),
			tag -> level.getBlockState(pos).is(tag)
		);}

	public boolean matches(RecipeInput recipeInput, Level level) {return false;}
	public ItemStack assemble(RecipeInput recipeInput, HolderLookup.Provider provider) {return ItemStack.EMPTY;}
	public boolean canCraftInDimensions(int i, int j) {return false;}
	public ItemStack getResultItem(HolderLookup.Provider provider) {return null;}


	public RecipeSerializer<?> getSerializer() {return ModRecipes.LOOSEN_SERIALIZER.get();}
	public RecipeType<?> getType() {return ModRecipes.WORLD_LOOSEN.get();}

	public Either<Block, TagKey<Block>> getInputBlock() { return this.inputBlock; }
	public BlockState getResultState() { return this.resultState; }
}
