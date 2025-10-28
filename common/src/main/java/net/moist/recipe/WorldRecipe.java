package net.moist.recipe;


import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class WorldRecipe extends CustomRecipe {
	public WorldRecipe() {
		super(CraftingBookCategory.MISC);
	}


	@Override
	public boolean matches(CraftingInput recipeInput, Level level) {
		return false;
	}

	@Override
	public ItemStack assemble(CraftingInput recipeInput, HolderLookup.Provider provider) {
		return null;
	}

	@Override
	public boolean canCraftInDimensions(int i, int j) {
		return true;
	}

	@Override
	public RecipeType<?> getType() {
		return RecipeTypes.WORLD_TRANSFORM;
	}

	public static class Serializer implements RecipeSerializer<WorldRecipe> {
		public static final MapCodec<WorldRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(Codec.STRING.optionalFieldOf("group", "").forGetter((arg) -> arg).apply(instance, WorldRecipe::new)));
		public static final StreamCodec<RegistryFriendlyByteBuf, WorldRecipe> STREAM_CODEC = StreamCodec.of(WorldRecipe.Serializer::toNetwork, WorldRecipe.Serializer::fromNetwork);

		public MapCodec<WorldRecipe> codec() {
			return CODEC;
		}

		public StreamCodec<RegistryFriendlyByteBuf, WorldRecipe> streamCodec() {
			return STREAM_CODEC;
		}

		private static WorldRecipe fromNetwork(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
			return new WorldRecipe();
		}

		private static void toNetwork(RegistryFriendlyByteBuf registryFriendlyByteBuf, WorldRecipe shapedRecipe) {
		}
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return new WorldRecipe.Serializer();
	}
}
