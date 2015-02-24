package org.dei.perla.lang.query;

import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.expression.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Guido Rota 24/02/15.
 */
public class QueryBuilder {

    private final List<Attribute> atts;

    private final List<Expression> select = new ArrayList<>();

    public QueryBuilder(List<Attribute> atts) {
        this.atts = atts;
    }

    public QueryBuilder select(Expression e) {
        if (!atts.containsAll(e.attributes())) {
            // TODO: change the exception
            throw new IllegalArgumentException();
        }
        select.add(e);
        return this;
    }

    public Query create() {
        throw new RuntimeException("unimplemented");
    }

}
