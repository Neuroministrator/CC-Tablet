package dev.cctablet;

import java.util.function.Supplier;

/**
 * tracks tablet computer creation so normal pockets keep their normal size
 * the thread local is removed again as soon as creation finishes
 */
public final class TabletTerminalContext {
    public static final int WIDTH = 40;
    public static final int HEIGHT = 20;

    private static final ThreadLocal<Integer> DEPTH = new ThreadLocal<>();

    private TabletTerminalContext() {
    }

    public static boolean isCreatingTablet() {
        var depth = DEPTH.get();
        return depth != null && depth > 0;
    }

    public static <T> T creatingTablet(Supplier<T> action) {
        var current = DEPTH.get();
        int previous = current == null ? 0 : current;
        DEPTH.set(previous + 1);

        try {
            return action.get();
        } finally {
            if (previous == 0) DEPTH.remove();
            else DEPTH.set(previous);
        }
    }
}
