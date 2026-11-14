package dev.cctablet.registry;

import dev.cctablet.CCTablet;
import dev.cctablet.block.UniversalModemBlock;
import dev.cctablet.block.HddCaseBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
        DeferredRegister.create(ForgeRegistries.BLOCKS, CCTablet.MOD_ID);

    public static final RegistryObject<UniversalModemBlock> UNIVERSAL_MODEM = BLOCKS.register(
        "universal_modem",
        () -> new UniversalModemBlock(BlockBehaviour.Properties.of().strength(2.0f)
            .requiresCorrectToolForDrops().noOcclusion())
    );

    public static final RegistryObject<HddCaseBlock> HDD_CASE = BLOCKS.register(
        "hdd_case",
        () -> new HddCaseBlock(BlockBehaviour.Properties.of().strength(3.0f).requiresCorrectToolForDrops())
    );

    private ModBlocks() {}
}
