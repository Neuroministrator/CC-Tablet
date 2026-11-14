package dev.cctablet.upgrade;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.AbstractPocketUpgrade;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dev.cctablet.peripheral.UniversalModemPeripheral;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class UniversalModemUpgrade extends AbstractPocketUpgrade {
    public UniversalModemUpgrade(ResourceLocation id, ItemStack craftingItem) {
        super(id, craftingItem);
    }

    @Override
    public IPeripheral createPeripheral(IPocketAccess access) {
        return new UniversalModemPeripheral(
            access::getLevel,
            access::getPosition,
            access.getUpgradeNBTData(),
            access::updateUpgradeNBTData
        );
    }
}
