package dev.cctablet.item;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.filesystem.Mount;
import dan200.computercraft.api.filesystem.WritableMount;
import dev.cctablet.CCTablet;
import dev.cctablet.config.CCTabletConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.fml.loading.FMLPaths;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.IntSupplier;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class HardDriveItem extends Item {
    private static final String DISK_ID_TAG = "cctablet_hdd_id";
    private static final String CAPACITY_TAG = "cctablet_hdd_capacity";
    private static final String HDD5_PROGRAMS_PATH = "cctablet/hdd5/programs";
    private static final long BYTES_PER_MIB = 1024L * 1024L;
    private static final ConcurrentHashMap<Path, WritableMount> MOUNTS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Path, ConfigDirectoryMount> READ_ONLY_MOUNTS = new ConcurrentHashMap<>();

    private final String tier;
    private final IntSupplier capacityMiB;
    private final boolean sharedReadOnly;

    public HardDriveItem(String tier, IntSupplier capacityMiB, boolean sharedReadOnly, Properties properties) {
        super(properties.stacksTo(1));
        this.tier = tier;
        this.capacityMiB = capacityMiB;
        this.sharedReadOnly = sharedReadOnly;
    }

    public @Nullable Mount createMount(ItemStack stack, ServerLevel level) {
        if (sharedReadOnly) {
            var programs = FMLPaths.CONFIGDIR.get().resolve(HDD5_PROGRAMS_PATH);
            try {
                Files.createDirectories(programs);
                var root = programs.toAbsolutePath().normalize();
                return READ_ONLY_MOUNTS.computeIfAbsent(root, ConfigDirectoryMount::new);
            } catch (IOException e) {
                CCTablet.LOGGER.error("Could not open the HDD5 program folder at {}", programs, e);
                return null;
            }
        }

        var server = level.getServer();
        var tag = stack.getOrCreateTag();
        if (!tag.hasUUID(DISK_ID_TAG)) tag.putUUID(DISK_ID_TAG, UUID.randomUUID());
        if (!tag.contains(CAPACITY_TAG)) tag.putLong(CAPACITY_TAG, getConfiguredCapacityBytes());

        var diskId = tag.getUUID(DISK_ID_TAG);
        var worldRoot = server.getWorldPath(LevelResource.ROOT).toAbsolutePath().normalize();
        var relativePath = "cctablet/hdds/" + diskId;
        var diskPath = worldRoot.resolve(relativePath).normalize();
        if (!diskPath.startsWith(worldRoot)) {
            CCTablet.LOGGER.error("Refusing to create HDD storage outside the world save directory");
            return null;
        }

        try {
            if (!Files.exists(diskPath)) {
                Files.createDirectories(diskPath);
            }
        } catch (IOException e) {
            CCTablet.LOGGER.error("Could not prepare storage for {} ({})", tier, diskId, e);
            return null;
        }

        return MOUNTS.computeIfAbsent(diskPath, path ->
            ComputerCraftAPI.createSaveDirMount(server, relativePath, tag.getLong(CAPACITY_TAG))
        );
    }

    public @Nullable String getLabel(ItemStack stack) {
        return stack.hasCustomHoverName() ? stack.getHoverName().getString() : null;
    }

    public boolean setLabel(ItemStack stack, @Nullable String label) {
        if (label == null || label.isBlank()) {
            stack.resetHoverName();
        } else {
            stack.setHoverName(Component.literal(label));
        }
        return true;
    }

    public String getDriveName(ItemStack stack) {
        var label = getLabel(stack);
        return label == null || label.isBlank() ? tier : label;
    }

    public boolean isReadOnly() {
        return sharedReadOnly;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> lines, TooltipFlag flag) {
        if (sharedReadOnly) {
            lines.add(Component.translatable("tooltip.cctablet.hdd_read_only"));
        } else {
            lines.add(Component.translatable("tooltip.cctablet.hdd_capacity", getCapacityMiB(stack)));
        }
    }

    public long getCapacityBytes(ItemStack stack) {
        if (capacityMiB == null) return 0L;
        var tag = stack.getTag();
        return tag != null && tag.contains(CAPACITY_TAG)
            ? tag.getLong(CAPACITY_TAG)
            : getConfiguredCapacityBytes();
    }

    public int getCapacityMiB(ItemStack stack) {
        return (int) Math.max(1L, getCapacityBytes(stack) / BYTES_PER_MIB);
    }

    private long getConfiguredCapacityBytes() {
        return capacityMiB.getAsInt() * BYTES_PER_MIB;
    }

    public static void prepareHdd5ProgramFolder() {
        var root = FMLPaths.CONFIGDIR.get().resolve("cctablet/hdd5");
        try {
            Files.createDirectories(root.resolve("programs"));
            var readme = root.resolve("README.txt");
            if (!Files.exists(readme)) {
                Files.writeString(readme,
                    "Files in the programs folder are shared by every HDD5 and mounted read-only.\n"
                        + "Add a file here to make it appear on every HDD5. Remove it to remove it from every HDD5.\n");
            }

            seedDefaultTestProgram(root);
        } catch (IOException e) {
            CCTablet.LOGGER.error("Could not create the HDD5 program template folder at {}", root, e);
        }
    }

    private static void seedDefaultTestProgram(Path root) throws IOException {
        var marker = root.resolve(".test_program_seeded");
        if (Files.exists(marker)) return;

        var program = root.resolve("programs/cctablet_test.lua");
        if (!Files.exists(program)) {
            try (var input = HardDriveItem.class.getResourceAsStream("/hdd5/cctablet_test.lua")) {
                if (input == null) {
                    CCTablet.LOGGER.warn("Bundled HDD5 test program resource was not found");
                    return;
                }
                Files.copy(input, program);
            }
        }
        Files.writeString(marker, "seeded\n");
    }

}
