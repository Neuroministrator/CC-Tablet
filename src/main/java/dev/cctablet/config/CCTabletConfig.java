package dev.cctablet.config;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.common.ForgeConfigSpec;

public final class CCTabletConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.IntValue HDD1_CAPACITY_MIB = capacity("hdd1CapacityMiB", 1);
    public static final ForgeConfigSpec.IntValue HDD2_CAPACITY_MIB = capacity("hdd2CapacityMiB", 2);
    public static final ForgeConfigSpec.IntValue HDD3_CAPACITY_MIB = capacity("hdd3CapacityMiB", 4);
    public static final ForgeConfigSpec.IntValue HDD4_CAPACITY_MIB = capacity("hdd4CapacityMiB", 8);

    private static final ForgeConfigSpec COMMON_SPEC = BUILDER.build();

    private CCTabletConfig() {
    }

    private static ForgeConfigSpec.IntValue capacity(String name, int defaultMiB) {
        return BUILDER.comment("Capacity for " + name.substring(0, 4).toUpperCase() + " in MiB.")
            .defineInRange(name, defaultMiB, 1, 256);
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_SPEC, "cctablet-common.toml");
    }
}
