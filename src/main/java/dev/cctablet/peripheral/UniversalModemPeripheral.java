package dev.cctablet.peripheral;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.network.Packet;
import dan200.computercraft.api.network.PacketNetwork;
import dan200.computercraft.api.network.PacketReceiver;
import dan200.computercraft.api.network.PacketSender;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.config.Config;
import dan200.computercraft.shared.peripheral.modem.ModemState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;

/** A wireless modem with configurable range and optional cross-dimension networking. */
public final class UniversalModemPeripheral implements IPeripheral, PacketSender, PacketReceiver {
    /** Value used to select Ender-style networking. */
    public static final String ENDER = "ender";

    private enum TransmissionType { NORMAL, ENDER }

    private final Supplier<ServerLevel> level;
    private final Supplier<Vec3> position;
    private final CompoundTag persistentSettings;
    private final Runnable persistSettings;
    private final ModemState state = new ModemState();
    private final Set<IComputerAccess> computers =
        Collections.newSetFromMap(new ConcurrentHashMap<>());
    private volatile double range = Config.modemRange;
    private volatile boolean ignoreEnder;
    private volatile TransmissionType lastTransmission = TransmissionType.NORMAL;
    private volatile PacketNetwork network;

    public UniversalModemPeripheral(Supplier<ServerLevel> level, Supplier<Vec3> position) {
        this(level, position, new CompoundTag(), () -> {});
    }

    public UniversalModemPeripheral(
        Supplier<ServerLevel> level, Supplier<Vec3> position, CompoundTag persistentSettings, Runnable persistSettings
    ) {
        this.level = level;
        this.position = position;
        this.persistentSettings = persistentSettings;
        this.persistSettings = persistSettings;
        loadSettings(persistentSettings);
    }

    @Override
    public String getType() {
        return "universal_modem";
    }

    @Override
    public Set<String> getAdditionalTypes() {
        return Set.of("modem");
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other == this;
    }

    @Override
    public synchronized void attach(IComputerAccess computer) {
        computers.add(computer);
        network = getNetwork();
        network.addReceiver(this);
    }

    @Override
    public synchronized void detach(IComputerAccess computer) {
        computers.remove(computer);
        if (computers.isEmpty()) {
            var current = network;
            network = null;
            if (current != null) current.removeReceiver(this);
        }
    }

    public synchronized void removed() {
        var current = network;
        network = null;
        if (current != null) current.removeReceiver(this);
        computers.clear();
    }

    public boolean pollChanged() {
        return state.pollChanged();
    }

    public boolean isOpen() {
        return state.isOpen();
    }

    public void saveSettings(CompoundTag tag) {
        tag.putBoolean("ender_range", isEnderRange());
        if (!isEnderRange()) tag.putDouble("range", range);
        tag.putBoolean("ignore_ender", ignoreEnder);
    }

    public void loadSettings(CompoundTag tag) {
        if (tag.getBoolean("ender_range")) {
            range = Double.POSITIVE_INFINITY;
        } else if (tag.contains("range")) {
            var loadedRange = tag.getDouble("range");
            if (Double.isFinite(loadedRange) && loadedRange > 0.0) range = loadedRange;
        }
        ignoreEnder = tag.getBoolean("ignore_ender");
    }

    private void persistSettings() {
        saveSettings(persistentSettings);
        persistSettings.run();
    }

    @Override
    public ServerLevel getLevel() {
        return level.get();
    }

    @Override
    public Vec3 getPosition() {
        return position.get();
    }

    @Override
    @LuaFunction
    public double getRange() {
        return range;
    }

    @Override
    public boolean isInterdimensional() {
        return range == Double.POSITIVE_INFINITY;
    }

    @Override
    public void receiveSameDimension(Packet packet, double distance) {
        receive(packet, distance, false);
    }

    @Override
    public void receiveDifferentDimension(Packet packet) {
        receive(packet, 0.0, true);
    }

    @LuaFunction
    public final void open(int channel) throws LuaException {
        validateChannel(channel);
        state.open(channel);
    }

    @LuaFunction
    public final boolean isOpen(int channel) throws LuaException {
        validateChannel(channel);
        return state.isOpen(channel);
    }

    @LuaFunction
    public final void close(int channel) throws LuaException {
        validateChannel(channel);
        state.close(channel);
    }

    @LuaFunction
    public final void closeAll() {
        state.closeAll();
    }

    @LuaFunction
    public final void transmit(int channel, int replyChannel, Object message) throws LuaException {
        validateChannel(channel);
        validateChannel(replyChannel);

        var current = network;
        if (current == null) return;

        lastTransmission = isEnderRange() ? TransmissionType.ENDER : TransmissionType.NORMAL;
        var packet = new Packet(channel, replyChannel, message, this);
        if (lastTransmission == TransmissionType.ENDER) {
            current.transmitInterdimensional(packet);
        } else {
            current.transmitSameDimension(packet, range);
        }
    }

    @LuaFunction
    public final boolean isWireless() {
        return true;
    }

    @LuaFunction
    public final void setRange(Object value) throws LuaException {
        if (value instanceof String string && ENDER.equalsIgnoreCase(string)) {
            range = Double.POSITIVE_INFINITY;
            persistSettings();
            return;
        }
        if (!(value instanceof Number number)) {
            throw new LuaException("Expected a positive number or modem.ENDER");
        }
        var newRange = number.doubleValue();
        if (!Double.isFinite(newRange) || newRange <= 0.0) {
            throw new LuaException("Expected a positive number or modem.ENDER");
        }
        range = newRange;
        persistSettings();
    }

    @LuaFunction
    public final void ignore(Object value) throws LuaException {
        requireEnder(value);
        ignoreEnder = true;
        persistSettings();
    }

    @LuaFunction
    public final void allow(Object value) throws LuaException {
        requireEnder(value);
        ignoreEnder = false;
        persistSettings();
    }

    @LuaFunction
    public final boolean isIgnored(Object value) throws LuaException {
        requireEnder(value);
        return ignoreEnder;
    }

    @LuaFunction(value = "getEnder")
    public final String getEnder() {
        return ENDER;
    }

    @Override
    public String getSenderID() {
        return "unknown";
    }

    private PacketNetwork getNetwork() {
        return ComputerCraftAPI.getWirelessNetwork(level.get().getServer());
    }

    private boolean isEnderRange() {
        return range == Double.POSITIVE_INFINITY;
    }

    public boolean isEnderMode() {
        return isEnderRange();
    }

    private void receive(Packet packet, double distance, boolean differentDimension) {
        if (packet.sender() == this || !state.isOpen(packet.channel())) return;
        if (ignoreEnder && isEnderTransmission(packet.sender())) return;

        for (var computer : computers) {
            var event = differentDimension
                ? new Object[]{computer.getAttachmentName(), packet.channel(), packet.replyChannel(), packet.payload()}
                : new Object[]{computer.getAttachmentName(), packet.channel(), packet.replyChannel(), packet.payload(), distance};
            computer.queueEvent("modem_message", event);
        }
    }

    private static void validateChannel(int channel) throws LuaException {
        if (channel < 0 || channel > 65535) {
            throw new LuaException("Expected number in range 0-65535");
        }
    }

    private static void requireEnder(Object value) throws LuaException {
        if (!(value instanceof String string) || !ENDER.equalsIgnoreCase(string)) {
            throw new LuaException("Expected modem.ENDER");
        }
    }

    private static boolean isEnderTransmission(PacketSender sender) {
        if (sender instanceof UniversalModemPeripheral modem) {
            return modem.lastTransmission == TransmissionType.ENDER;
        }

        // Check for an optional method without depending on a specific modem addon.
        try {
            var method = sender.getClass().getMethod("isInterdimensional");
            return method.getReturnType() == boolean.class && Boolean.TRUE.equals(method.invoke(sender));
        } catch (ReflectiveOperationException | SecurityException ignored) {
            // Unknown sender types are treated as normal transmissions.
            return false;
        }
    }
}
