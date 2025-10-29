package net.moist.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class WorldRecipeSerializer implements RecipeSerializer<LooseningRecipe> {
	public static final MapCodec<LooseningRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ResourceLocation.CODEC.fieldOf("input").forGetter(ins -> ins.getInputBlock().arch$registryName()),
		BlockState.CODEC.fieldOf("result").forGetter(LooseningRecipe::getResultState)
	).apply(instance, (inputBlock, resultState) -> new LooseningRecipe(BuiltInRegistries.BLOCK.get(inputBlock), resultState)));

	@Override
	public MapCodec<LooseningRecipe> codec() {
		return CODEC;
	}

	@Override
	public StreamCodec<RegistryFriendlyByteBuf, LooseningRecipe> streamCodec() {
		return StreamCodec.of(this::toNetwork, this::fromNetwork);
	}

	private LooseningRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
		return new LooseningRecipe(
			BuiltInRegistries.BLOCK.get(buf.readResourceLocation()),
			BlockState.CODEC.decode(NbtOps.INSTANCE, buf.readNbt()).getOrThrow().getFirst()
		);
	}

	private void toNetwork(RegistryFriendlyByteBuf buf, LooseningRecipe recipe) {
		buf.writeResourceLocation(recipe.getInputBlock().arch$registryName());
		CompoundTag resultTag = (CompoundTag) BlockState.CODEC.encodeStart(
			net.minecraft.nbt.NbtOps.INSTANCE,
			recipe.getResultState()
		).result().get();
		buf.writeNbt(resultTag);

	}
}
