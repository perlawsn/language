package org.dei.perla.lang.query;

import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.expression.Expression;
import org.dei.perla.lang.expression.Field;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Guido Rota 24/02/15.
 */
public class QueryBuilder {

    private final Errors err = new Errors();

    private final List<Attribute> atts;

    private final List<Expression> select = new ArrayList<>();
    private final Set<Attribute> groupBy = new TreeSet<>();
    private boolean distinct = false;

    public QueryBuilder(List<Attribute> atts) {
        this.atts = new ArrayList<>();
        this.atts.addAll(atts);
        this.atts.add(Sampling.GROUP_TS);
    }

    public QueryBuilder select(Expression e) {
        if (!atts.containsAll(e.attributes())) {
            throw new IllegalArgumentException();
        }
        select.add(e);
        return this;
    }

    public QueryBuilder groupBy(Expression e) {
        if (!(e instanceof Field)) {
            throw new IllegalArgumentException();
        }
        return this;
    }

    public QueryBuilder groupByTimestamp(Duration d, int groups) {
        return this;
    }

    public QueryBuilder distinct(boolean value) {
        distinct = value;
        return this;
    }

    public Query create() {
        throw new RuntimeException("unimplemented");
    }

}
