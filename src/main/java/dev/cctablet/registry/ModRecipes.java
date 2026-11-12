package dev.cctablet.registry;

import dev.cctablet.CCTablet;
import dev.cctablet.recipe.TabletRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
        DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CCTablet.MOD_ID);

    public static final RegistryObject<SimpleCraftingRecipeSerializer<TabletRecipe>> TABLET = RECIPE_SERIALIZERS.register(
        "tablet",
        () -> new SimpleCraftingRecipeSerializer<>(TabletRecipe::new)
    );

    private ModRecipes() {
    }
}
