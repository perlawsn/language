package org.dei.perla.lang.executor.query;

import org.dei.perla.core.utils.Check;
import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.expression.Field;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Guido Rota 04/03/15.
 */
public final class GroupBy {

    private final Duration d;
    private final int count;
    private final List<Field> fields;

    public GroupBy(Duration d, int count) {
        this.d = d;
        this.count = count;
        fields = null;
    }

    public GroupBy(List<Field> fields) {
        this.fields = fields;
        d = null;
        count = -1;
    }

    public GroupBy(Duration d, int count, List<Field> fields) {
        this.d = d;
        this.count = count;
        this.fields = fields;
    }

    public List<BufferView> createGroups(BufferView buffer) {
        List<BufferView> tsGroups = new LinkedList<>();
        if (d != null) {
            tsGroups.addAll(buffer.groupBy(d, count));
        } else {
            tsGroups.add(buffer);
        }
        if (Check.nullOrEmpty(fields)) {
            return tsGroups;
        }

        // Further grouping if both timestamp and classic group by are specified
        // in the query.
        List<BufferView> bufs = new LinkedList<>();
        for (BufferView tgb : tsGroups) {
            bufs.addAll(tgb.groupBy(fields));
        }
        return bufs;
    }

}
