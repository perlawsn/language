package org.dei.perla.lang.executor.query;

import org.dei.perla.lang.executor.BufferView;

import java.time.Duration;

/**
 * @author Guido Rota 04/03/15.
 */
public final class UpTo {

    private final int samples;
    private final Duration d;

    public UpTo() {
        samples = 1;
        d = null;
    }

    public UpTo(int samples) {
        this.samples = samples;
        d = null;
    }

    public UpTo(Duration d) {
        this.d = d;
        this.samples = -1;
    }

    public int getSamples(BufferView view) {
        if (samples > 0) {
            return samples;
        } else {
            return view.recordsIn(d);
        }
    }

}
