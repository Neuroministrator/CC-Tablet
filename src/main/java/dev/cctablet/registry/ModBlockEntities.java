package dev.cctablet.registry;

import dev.cctablet.CCTablet;
import dev.cctablet.block.UniversalModemBlockEntity;
import dev.cctablet.block.HddCaseBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CCTablet.MOD_ID);

    public static final RegistryObject<BlockEntityType<UniversalModemBlockEntity>> UNIVERSAL_MODEM =
        BLOCK_ENTITIES.register("universal_modem", () -> BlockEntityType.Builder.of(
            UniversalModemBlockEntity::new, ModBlocks.UNIVERSAL_MODEM.get()).build(null));

    public static final RegistryObject<BlockEntityType<HddCaseBlockEntity>> HDD_CASE =
        BLOCK_ENTITIES.register("hdd_case", () -> BlockEntityType.Builder.of(
            HddCaseBlockEntity::new, ModBlocks.HDD_CASE.get()).build(null));

    private ModBlockEntities() {}
}
