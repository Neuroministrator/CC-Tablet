package dev.cctablet.recipe;

import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.pocket.items.PocketComputerItem;
import dev.cctablet.registry.ModItems;
import dev.cctablet.registry.ModRecipes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public final class TabletRecipe extends CustomRecipe {
    public TabletRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer inventory, Level level) {
        boolean foundPocket = false;
        int gold = 0;

        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            var stack = inventory.getItem(slot);
            if (stack.isEmpty()) continue;

            if (isConvertiblePocket(stack)) {
                if (foundPocket) return false;
                foundPocket = true;
            } else if (stack.is(Items.GOLD_INGOT)) {
                gold++;
            } else {
                return false;
            }
        }

        return foundPocket && gold == 2;
    }

    @Override
    public ItemStack assemble(CraftingContainer inventory, RegistryAccess registryAccess) {
        ItemStack pocketStack = ItemStack.EMPTY;
        PocketComputerItem pocketItem = null;
        int gold = 0;

        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            var stack = inventory.getItem(slot);
            if (stack.isEmpty()) continue;

            if (isConvertiblePocket(stack)) {
                if (!pocketStack.isEmpty()) return ItemStack.EMPTY;
                pocketStack = stack;
                pocketItem = (PocketComputerItem) stack.getItem();
            } else if (stack.is(Items.GOLD_INGOT)) {
                gold++;
            } else {
                return ItemStack.EMPTY;
            }
        }

        if (pocketItem == null || gold != 2) return ItemStack.EMPTY;

        var result = pocketItem.changeItem(pocketStack, ModItems.TABLET.get());
        if (result.isEmpty()) return ItemStack.EMPTY;

        var sourceTag = pocketStack.getTag();
        if (sourceTag != null && sourceTag.contains(PocketComputerItem.NBT_ON)) {
            result.getOrCreateTag().putBoolean(
                PocketComputerItem.NBT_ON,
                sourceTag.getBoolean(PocketComputerItem.NBT_ON)
            );
        }

        return result;
    }

    private static boolean isConvertiblePocket(ItemStack stack) {
        return stack.is(ModRegistry.Items.POCKET_COMPUTER_ADVANCED.get());
    }

    @Override
    public boolean isSpecial() {
        return false;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY,
            Ingredient.of(ModRegistry.Items.POCKET_COMPUTER_ADVANCED.get()),
            Ingredient.of(Items.GOLD_INGOT),
            Ingredient.of(Items.GOLD_INGOT));
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 3;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return new ItemStack(ModItems.TABLET.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.TABLET.get();
    }
}
