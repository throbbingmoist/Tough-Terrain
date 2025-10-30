package net.moist.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class LooseningSerializer implements RecipeSerializer<LooseningRecipe> {
	public static final Codec<Either<Block, TagKey<Block>>> INPUT_CODEC = Codec.either(
		BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").codec(),
		TagKey.codec(Registries.BLOCK).fieldOf("tag").codec());

	public static final MapCodec<LooseningRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		INPUT_CODEC.fieldOf("input").forGetter(LooseningRecipe::getInputBlock),
		BlockState.CODEC.fieldOf("result").forGetter(LooseningRecipe::getResultState)
	).apply(instance, LooseningRecipe::new));


	public MapCodec<LooseningRecipe> codec() {
		return CODEC;
	}
	public StreamCodec<RegistryFriendlyByteBuf, LooseningRecipe> streamCodec() {
		return StreamCodec.of((buf, recipe) -> buf.writeJsonWithCodec(CODEC.codec(), recipe), buf -> buf.readJsonWithCodec(CODEC.codec()));
	}
}
