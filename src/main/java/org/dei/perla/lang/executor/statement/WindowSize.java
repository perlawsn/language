package org.dei.perla.lang.executor.statement;

import java.time.Duration;

/**
 * @author Guido Rota 04/03/15.
 */
public final class WindowSize {

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
    public boolean equals(Object o) {
        if (!(o instanceof WindowSize)) {
            return false;
        }

        WindowSize other = (WindowSize) o;
        if (samples != -1) {
            return samples == other.samples;
        } else {
            return other.d != null && d.equals(other.d);
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
