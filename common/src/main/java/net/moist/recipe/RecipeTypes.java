package net.moist.recipe;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.moist.Terrain;

public class RecipeTypes {
	public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Terrain.MOD_ID, Registries.RECIPE_TYPE);
	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Terrain.MOD_ID, Registries.RECIPE_SERIALIZER);

	public static final RegistrySupplier<RecipeType<LooseningRecipe>> WORLD_LOOSEN = TYPES.register("world_loosen", () -> new RecipeType<>() {});
	public static final RegistrySupplier<RecipeSerializer<LooseningRecipe>> LOOSEN_SERIALIZER = SERIALIZERS.register("world_loosen", WorldRecipeSerializer::new);

	public static void setup() {
		TYPES.register();
		SERIALIZERS.register();
	}
}

