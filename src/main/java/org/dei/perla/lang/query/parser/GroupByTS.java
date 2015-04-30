package org.dei.perla.lang.query.parser;

import java.time.Duration;

/**
 * @author Guido Rota 13/03/15.
 */
public final class GroupByTS {

    private final Duration d;
    private final int count;

    public GroupByTS(Duration d, int count) {
        this.d = d;
        this.count = count;
    }

    public Duration getDuration() {
        return d;
    }

    public int getCount() {
        return count;
    }

}
