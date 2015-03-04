package org.dei.perla.lang.parser;

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

}
