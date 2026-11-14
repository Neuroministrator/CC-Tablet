package dev.cctablet.registry;

import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser;
import dev.cctablet.CCTablet;
import dev.cctablet.upgrade.UniversalModemUpgrade;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModUpgrades {
    public static final DeferredRegister<PocketUpgradeSerialiser<?>> POCKET_UPGRADES =
        DeferredRegister.create(PocketUpgradeSerialiser.registryId(), CCTablet.MOD_ID);

    public static final RegistryObject<PocketUpgradeSerialiser<UniversalModemUpgrade>> UNIVERSAL_MODEM =
        POCKET_UPGRADES.register("universal_modem", () -> PocketUpgradeSerialiser.simpleWithCustomItem(
            UniversalModemUpgrade::new));

    private ModUpgrades() {}
}
