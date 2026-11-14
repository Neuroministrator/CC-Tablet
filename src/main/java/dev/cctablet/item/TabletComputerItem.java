package dev.cctablet.item;

import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.pocket.core.PocketBrain;
import dan200.computercraft.shared.pocket.core.PocketHolder;
import dan200.computercraft.shared.pocket.items.PocketComputerItem;
import dan200.computercraft.shared.pocket.peripherals.PocketSpeakerPeripheral;
import dev.cctablet.CCTablet;
import dev.cctablet.TabletTerminalContext;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.List;

public final class TabletComputerItem extends PocketComputerItem {
    private static final ComputerSide BUILT_IN_SPEAKER_SIDE = ComputerSide.RIGHT;
    private static final int BUILT_IN_SPEAKER_LIGHT = 0x3320FC;

    public TabletComputerItem(Properties properties) {
        super(properties, ComputerFamily.ADVANCED);
    }

    @Override
    public String getCreatorModId(ItemStack stack) {
        return CCTablet.MOD_ID;
    }

    @Override
    public PocketBrain getOrCreateBrain(PocketHolder holder, ItemStack stack) {
        var brain = super.getOrCreateBrain(holder, stack);
        TabletTerminalContext.applyDeviceTerminalSize(brain, stack);
        ensureBuiltInSpeaker(brain);
        return brain;
    }

    @Override
    public void tick(ItemStack stack, PocketHolder holder, PocketBrain brain) {
        var speaker = ensureBuiltInSpeaker(brain);

        var previousLight = brain.getLight();
        speaker.update();
        var speakerMadeSound = speaker.madeSound();
        brain.setLight(previousLight);

        super.tick(stack, holder, brain);

        if (brain.getUpgrade() == null) {
            brain.setLight(speakerMadeSound ? BUILT_IN_SPEAKER_LIGHT : -1);
        }
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
