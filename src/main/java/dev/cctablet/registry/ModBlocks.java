package dev.cctablet.registry;

import dev.cctablet.CCTablet;
import dev.cctablet.block.UniversalModemBlock;
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

    private ModBlocks() {}
}
