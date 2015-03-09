package org.dei.perla.lang.executor.statement;

import java.time.Duration;

/**
 * @author Guido Rota 04/03/15.
 */
public final class WindowSize {

    private final int samples;
    private final Duration d;

    public WindowSize(int samples) {
        this.samples = samples;
        d = null;
    }

    public WindowSize(Duration d) {
        this.d = d;
        samples = -1;
    }

    public int getSamples() {
        return samples;
    }

    public Duration getDuration() {
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

}
