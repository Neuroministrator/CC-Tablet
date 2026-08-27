package dev.cctablet.item;

import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.inventory.ComputerMenuWithoutInventory;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.platform.PlatformHelper;
import dan200.computercraft.shared.pocket.core.PocketBrain;
import dan200.computercraft.shared.pocket.core.PocketHolder;
import dan200.computercraft.shared.pocket.items.PocketComputerItem;
import dan200.computercraft.shared.pocket.peripherals.PocketSpeakerPeripheral;
import dev.cctablet.curios.SmartwatchCurios;
import dev.cctablet.peripheral.SmartwatchLocatorPeripheral;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

// this is the smartwatch item
public final class SmartwatchComputerItem extends PocketComputerItem implements ICurioItem {
    private static final ComputerSide LOCATOR_SIDE = ComputerSide.LEFT;
    private static final ComputerSide SPEAKER_SIDE = ComputerSide.RIGHT;
    private static final int SPEAKER_LIGHT = 0x3320FC;

    public SmartwatchComputerItem(Properties properties) {
        super(properties, ComputerFamily.ADVANCED);
    }

    @Override
    public PocketBrain getOrCreateBrain(PocketHolder holder, ItemStack stack) {
        var brain = super.getOrCreateBrain(holder, stack);
        ensureBuiltIns(brain);
        return brain;
    }

    @Override
    public void tick(ItemStack stack, PocketHolder holder, PocketBrain brain) {
        ensureLocator(brain);
        var speaker = ensureSpeaker(brain);

        // this keeps the upgrade light
        var previousLight = brain.getLight();
        speaker.update();
        var speakerMadeSound = speaker.madeSound();
        brain.setLight(previousLight);

        super.tick(stack, holder, brain);

        if (brain.getUpgrade() == null) {
            brain.setLight(speakerMadeSound ? SPEAKER_LIGHT : -1);
        }
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!SmartwatchCurios.SLOT_ID.equals(slotContext.identifier())) return;
        if (!(slotContext.entity() instanceof ServerPlayer player)) return;

        var holder = SmartwatchCurios.holder(player);
        var brain = getOrCreateBrain(holder, stack);
        brain.computer().keepAlive();
        tick(stack, holder, brain);
    }

    // this opens the watch
    public void openFromWrist(ServerPlayer player, ItemStack stack) {
        var holder = SmartwatchCurios.holder(player);
        var brain = getOrCreateBrain(holder, stack);
        var computer = brain.computer();
        computer.turnOn();

        // this opens the computer menu
        PlatformHelper.get().openMenu(
            player,
            stack.getHoverName(),
            (id, inventory, entity) -> new ComputerMenuWithoutInventory(
                ModRegistry.Menus.COMPUTER.get(),
                id,
                inventory,
                p -> holder.isValid(computer),
                computer
            ),
            new ComputerContainerData(computer, stack)
        );
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, world, list, flag);
        list.add(Component.translatable("tooltip.cctablet.built_in_locator").withStyle(ChatFormatting.DARK_GRAY));
        list.add(Component.translatable("tooltip.cctablet.built_in_speaker").withStyle(ChatFormatting.DARK_GRAY));
    }

    private static void ensureBuiltIns(PocketBrain brain) {
        ensureLocator(brain);
        ensureSpeaker(brain);
    }

    private static SmartwatchLocatorPeripheral ensureLocator(PocketBrain brain) {
        var current = brain.computer().getPeripheral(LOCATOR_SIDE);
        if (current instanceof SmartwatchLocatorPeripheral locator) return locator;

        var locator = new SmartwatchLocatorPeripheral(brain);
        brain.computer().setPeripheral(LOCATOR_SIDE, locator);
        return locator;
    }

    private static PocketSpeakerPeripheral ensureSpeaker(PocketBrain brain) {
        var current = brain.computer().getPeripheral(SPEAKER_SIDE);
        if (current instanceof PocketSpeakerPeripheral speaker) return speaker;

        var speaker = new PocketSpeakerPeripheral(brain);
        brain.computer().setPeripheral(SPEAKER_SIDE, speaker);
        return speaker;
    }
}
