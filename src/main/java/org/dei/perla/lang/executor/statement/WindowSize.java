package org.dei.perla.lang.executor.statement;

import java.time.Duration;

/**
 * @author Guido Rota 04/03/15.
 */
public final class WindowSize {

    public static final WindowSize ZERO = new WindowSize(0);
    public static final WindowSize ONE = new WindowSize(1);

    private final WindowType type;
    private final int samples;
    private final Duration d;

    public WindowSize(int samples) {
        this.samples = samples;
        d = null;
        type = WindowType.SAMPLE;
    }

    public WindowSize(Duration d) {
        this.d = d;
        samples = -1;
        type = WindowType.TIME;
    }

    public WindowType getType() {
        return type;
    }

    public int getSamples() {
        if (type != WindowType.SAMPLE) {
            throw new RuntimeException(
                    "Cannot access samples in time-based WindowSize object");
        }
        return samples;
    }

    public Duration getDuration() {
        if (type != WindowType.TIME) {
            throw new RuntimeException(
                    "Cannot access samples in sample WindowSize object");
        }
        return d;
    }

    public boolean isZero() {
        if (d != null) {
            return d.isZero();
        } else {
            return samples == 0;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof WindowSize)) {
            return false;
        }

        WindowSize o = (WindowSize) other;
        if (o.type != type) {
            return false;
        }

        switch (type) {
            case TIME:
                return d.equals(o.d);
            case SAMPLE:
                return samples == o.samples;
            default:
                throw new RuntimeException("Unexpected WindowSize type " + type);
        }
    }

    @Override
    public String toString() {
        switch(type) {
            case TIME:
                return d.toString();
            case SAMPLE:
                return samples + " SAMPLES";
            default:
                throw new RuntimeException("Unexpected WindowSize type " + type);
        }
    }


    /**
     * @author Guido Rota 30/03/2014
     */
    public static enum WindowType {

        TIME,
        SAMPLE

    }

}
