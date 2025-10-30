package net.moist.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TransformSerializer implements RecipeSerializer<TransformRecipe> {
	public static final Codec<Either<Block, TagKey<Block>>> INPUT_CODEC = Codec.either(
		BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").codec(),
		TagKey.codec(Registries.BLOCK).fieldOf("tag").codec());

	public static final Codec<Either<Block, BlockState>> RESULT_CODEC = Codec.either(
		BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").codec(),
		BlockState.CODEC.fieldOf("state").codec());

	public static final MapCodec<TransformRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		INPUT_CODEC.fieldOf("input").forGetter(TransformRecipe::getInputBlock),
		Ingredient.CODEC.fieldOf("transformer").forGetter(TransformRecipe::getTransformItem),
		ResourceLocation.CODEC.optionalFieldOf("sound").forGetter(TransformRecipe::getSoundLocation),
		BlockState.CODEC.fieldOf("result").forGetter(TransformRecipe::getResultState)
		//RESULT_CODEC.fieldOf("result").forGetter(TransformRecipe::getResultState)
	).apply(instance, TransformRecipe::new));


	public MapCodec<TransformRecipe> codec() {
		return CODEC;
	}
	public StreamCodec<RegistryFriendlyByteBuf, TransformRecipe> streamCodec() {
		return StreamCodec.of((buf, recipe) -> buf.writeJsonWithCodec(CODEC.codec(), recipe), buf -> buf.readJsonWithCodec(CODEC.codec()));
	}
}
