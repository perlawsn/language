package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.utils.Check;
import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.expression.Expression;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Guido Rota 04/03/15.
 */
public final class GroupBy {

    private final Duration d;
    private final int count;
    private final List<? extends Expression> fields;

    public GroupBy(Duration d, int count) {
        this.d = d;
        this.count = count;
        fields = null;
    }

    public GroupBy(List<? extends Expression> fields) {
        this.fields = Collections.unmodifiableList(fields);
        d = null;
        count = -1;
    }

    public GroupBy(Duration d, int count, List<? extends Expression> fields) {
        this.d = d;
        this.count = count;
        this.fields = Collections.unmodifiableList(fields);
    }

    protected Duration getDuration() {
        return d;
    }

    protected int getCount() {
        return count;
    }

    protected List<? extends Expression> getFields() {
        return fields;
    }

    public GroupBy rebuild(List<Attribute> atts) {
        if (fields == null) {
            return this;
        }

        List<Expression> newFields = new ArrayList<>();
        fields.forEach(f -> newFields.add(f.bind(atts)));
        return new GroupBy(d, count, newFields);
    }

    public List<BufferView> createGroups(BufferView view) {
        List<BufferView> tsGroups = new LinkedList<>();
        if (d != null) {
            tsGroups.addAll(view.groupBy(d, count));
        } else {
            tsGroups.add(view);
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
