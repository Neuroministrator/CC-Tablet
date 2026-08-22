package dev.cctablet.item;

import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.pocket.core.PocketBrain;
import dan200.computercraft.shared.pocket.core.PocketHolder;
import dan200.computercraft.shared.pocket.items.PocketComputerItem;
import dan200.computercraft.shared.pocket.peripherals.PocketSpeakerPeripheral;
import dev.cctablet.TabletTerminalContext;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * wider advanced pocket computer with a built in speaker
 * speaker is on right and the normal upgrade stays on back
 */
public final class TabletComputerItem extends PocketComputerItem {
    private static final ComputerSide BUILT_IN_SPEAKER_SIDE = ComputerSide.RIGHT;

    public TabletComputerItem(Properties properties) {
        super(properties, ComputerFamily.ADVANCED);
    }

    @Override
    public PocketBrain getOrCreateBrain(PocketHolder holder, ItemStack stack) {
        var brain = TabletTerminalContext.creatingTablet(() -> super.getOrCreateBrain(holder, stack));
        ensureBuiltInSpeaker(brain);
        return brain;
    }

    @Override
    public void tick(ItemStack stack, PocketHolder holder, PocketBrain brain) {
        // tick the speaker first so the back upgrade gets the last chance to set the light
        ensureBuiltInSpeaker(brain).update();
        super.tick(stack, holder, brain);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, world, list, flag);
        list.add(Component.translatable("tooltip.cctablet.built_in_speaker").withStyle(ChatFormatting.DARK_GRAY));
    }

    private static PocketSpeakerPeripheral ensureBuiltInSpeaker(PocketBrain brain) {
        var current = brain.computer().getPeripheral(BUILT_IN_SPEAKER_SIDE);
        if (current instanceof PocketSpeakerPeripheral speaker) return speaker;

        var speaker = new PocketSpeakerPeripheral(brain);
        brain.computer().setPeripheral(BUILT_IN_SPEAKER_SIDE, speaker);
        return speaker;
    }
}
