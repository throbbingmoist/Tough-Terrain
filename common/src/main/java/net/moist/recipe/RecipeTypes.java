package net.moist.recipe;

import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.moist.Terrain;

import static net.minecraft.world.item.crafting.RecipeType.register;

public class RecipeTypes {
	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZER = DeferredRegister.create(Terrain.MOD_ID, Registries.RECIPE_SERIALIZER);
	public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Terrain.MOD_ID, Registries.RECIPE_TYPE);

	public static final RecipeType<WorldRecipe> WORLD_TRANSFORM = register("world_transform");

	public static void setup() {

	}
}

