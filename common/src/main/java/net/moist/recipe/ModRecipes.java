package net.moist.recipe;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.moist.Terrain;
import net.moist.recipe.recipes.LooseningRecipe;
import net.moist.recipe.recipes.TransformRecipe;
import net.moist.recipe.serializers.LooseningSerializer;
import net.moist.recipe.serializers.TransformSerializer;

public class ModRecipes {
	public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Terrain.MOD_ID, Registries.RECIPE_TYPE);
	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Terrain.MOD_ID, Registries.RECIPE_SERIALIZER);

	public static final RegistrySupplier<RecipeType<LooseningRecipe>> WORLD_LOOSEN = TYPES.register("world_loosen", () -> new RecipeType<>() {});
	public static final RegistrySupplier<RecipeSerializer<LooseningRecipe>> LOOSEN_SERIALIZER = SERIALIZERS.register("world_loosen", LooseningSerializer::new);

	public static final RegistrySupplier<RecipeType<TransformRecipe>> WORLD_TRANSFORM = TYPES.register("world_transform", () -> new RecipeType<>() {});
	public static final RegistrySupplier<RecipeSerializer<TransformRecipe>> TRANSFORM_SERIALIZER = SERIALIZERS.register("world_transform", TransformSerializer::new);

	public static void setup() {
		TYPES.register();
		SERIALIZERS.register();
	}
}

