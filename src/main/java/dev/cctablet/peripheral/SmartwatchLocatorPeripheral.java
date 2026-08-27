package dev.cctablet.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.pocket.core.PocketBrain;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

// this is the built in locator
public final class SmartwatchLocatorPeripheral implements IPeripheral {
    private final PocketBrain brain;

    public SmartwatchLocatorPeripheral(PocketBrain brain) {
        this.brain = brain;
    }

    @Override
    public String getType() {
        return "locator";
    }

    // this gets the exact position
    @LuaFunction(mainThread = true)
    public final Object[] getPosition() {
        var pos = position();
        return new Object[]{pos[0], pos[1], pos[2]};
    }

    // this gets the block position
    @LuaFunction(mainThread = true)
    public final Object[] getBlockPosition() {
        var entity = brain.getEntity();
        if (entity != null) {
            var block = entity.blockPosition();
            return new Object[]{block.getX(), block.getY(), block.getZ()};
        }

        var pos = brain.getPosition();
        return new Object[]{
            (int) Math.floor(pos.x),
            (int) Math.floor(pos.y),
            (int) Math.floor(pos.z)
        };
    }

    @LuaFunction
    public final String getDimension() {
        return brain.getLevel().dimension().location().toString();
    }

    @LuaFunction(mainThread = true)
    public final String getFacing() {
        var entity = brain.getEntity();
        return entity instanceof LivingEntity living ? living.getDirection().getName() : "unknown";
    }

    // this gets the rotation
    @LuaFunction(mainThread = true)
    public final Object[] getRotation() {
        var entity = brain.getEntity();
        return entity == null
            ? new Object[]{0.0D, 0.0D}
            : new Object[]{(double) entity.getYRot(), (double) entity.getXRot()};
    }

    // this gets all locator data
    @LuaFunction(mainThread = true)
    public final Map<String, Object> locate() {
        var entity = brain.getEntity();
        var xyz = position(entity);

        int blockX;
        int blockY;
        int blockZ;
        if (entity != null) {
            var block = entity.blockPosition();
            blockX = block.getX();
            blockY = block.getY();
            blockZ = block.getZ();
        } else {
            blockX = (int) Math.floor(xyz[0]);
            blockY = (int) Math.floor(xyz[1]);
            blockZ = (int) Math.floor(xyz[2]);
        }

        var result = new LinkedHashMap<String, Object>();
        result.put("x", xyz[0]);
        result.put("y", xyz[1]);
        result.put("z", xyz[2]);
        result.put("blockX", blockX);
        result.put("blockY", blockY);
        result.put("blockZ", blockZ);
        result.put("dimension", brain.getLevel().dimension().location().toString());
        result.put("facing", entity instanceof LivingEntity living ? living.getDirection().getName() : "unknown");
        result.put("yaw", entity == null ? 0.0D : (double) entity.getYRot());
        result.put("pitch", entity == null ? 0.0D : (double) entity.getXRot());
        return result;
    }

    private double[] position() {
        return position(brain.getEntity());
    }

    private double[] position(@Nullable Entity entity) {
        if (entity != null) return new double[]{entity.getX(), entity.getY(), entity.getZ()};

        // this uses the cached position
        var pos = brain.getPosition();
        return new double[]{pos.x, pos.y, pos.z};
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other instanceof SmartwatchLocatorPeripheral locator && locator.brain == brain;
    }
}
