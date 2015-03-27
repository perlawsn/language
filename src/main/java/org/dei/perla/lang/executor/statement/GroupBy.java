package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.utils.Check;
import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.expression.Expression;

import java.time.Duration;
import java.util.*;

/**
 * @author Guido Rota 04/03/15.
 */
public final class GroupBy implements Clause {

    private final Duration d;
    private final int count;
    private final List<? extends Expression> groups;

    public GroupBy(Duration d, int count) {
        this.d = d;
        this.count = count;
        groups = null;
    }

    public GroupBy(List<? extends Expression> groups) {
        this.groups = Collections.unmodifiableList(groups);
        d = null;
        count = -1;
    }

    public GroupBy(Duration d, int count, List<? extends Expression> groups) {
        this.d = d;
        this.count = count;
        this.groups = Collections.unmodifiableList(groups);
    }

    protected Duration getDuration() {
        return d;
    }

    protected int getCount() {
        return count;
    }

    protected List<? extends Expression> getGroups() {
        return groups;
    }

    @Override
    public boolean hasErrors() {
        return false;
    }

    @Override
    public boolean isComplete() {
        if (groups == null) {
            return true;
        }

        for (Expression e : groups) {
            if (!e.isComplete()) {
                return false;
            }
        }
        return true;
    }

    public GroupBy bind(Collection<Attribute> atts, List<Attribute> bound) {
        if (groups == null) {
            return new GroupBy(d, count);
        }
        List<Expression> bgroups = new ArrayList<>();
        groups.forEach(f -> bgroups.add(f.bind(atts, bound)));
        return new GroupBy(d, count, bgroups);
    }

    public List<BufferView> createGroups(BufferView view) {
        List<BufferView> tsGroups = new LinkedList<>();
        if (d != null) {
            tsGroups.addAll(view.groupBy(d, count));
        } else {
            tsGroups.add(view);
        }
        if (Check.nullOrEmpty(groups)) {
            return tsGroups;
        }

        // Further grouping if both timestamp and classic group by are specified
        // in the query.
        List<BufferView> bufs = new LinkedList<>();
        for (BufferView tgb : tsGroups) {
            bufs.addAll(tgb.groupBy(groups));
        }
        return bufs;
    }

}
