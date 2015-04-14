package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.sample.Attribute;
import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.expression.Expression;
import org.dei.perla.lang.executor.expression.LogicValue;
import org.dei.perla.lang.executor.statement.WindowSize.WindowType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Guido Rota 02/03/15.
 */
public final class Select implements Clause {

    private static final WindowSize DEFAULT_UPTO = new WindowSize(1);

    private final List<Expression> fields;
    private final WindowSize upto;
    private final GroupBy group;
    private final Expression having;
    private final Object[] def;

    public Select(List<Expression> fields, WindowSize upto,
            GroupBy group, Expression having, Object[] def) {
        this.fields = Collections.unmodifiableList(fields);
        if (upto == null) {
            this.upto = DEFAULT_UPTO;
        } else {
            this.upto = upto;
        }
        this.group = group;
        this.having = having;
        this.def = def;
    }

    protected List<Expression> getFields() {
        return fields;
    }

    protected WindowSize getUpTo() {
        return upto;
    }

    protected GroupBy getGroupBy() {
        return group;
    }

    protected Expression getHaving() {
        return having;
    }

    protected Object[] getDefault() {
        return def;
    }

    @Override
    public boolean hasErrors() {
        return false;
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    public Select bind(Collection<Attribute> atts, List<Attribute> bound) {
        List<Expression> bfields = new ArrayList<>();
        fields.forEach(f -> bfields.add(f.bind(atts, bound)));

        GroupBy bgroup = null;
        if (group != null) {
            bgroup = group.bind(atts, bound);
        }

        Expression bhaving = null;
        if (having != null) {
            bhaving = having.bind(atts, bound);
        }

        return new Select(bfields, upto, bgroup, bhaving, def);
    }

    public List<Object[]> select(BufferView buffer) {
        List<Object[]> rs = new ArrayList<>();

        // UPTO CLAUSE
        int ut;
        if (upto.getType() == WindowType.TIME) {
            ut = buffer.samplesIn(upto.getDuration());
        } else {
            ut = upto.getSamples();
        }

        if (group == null) {
            selectBuffer(ut, buffer, rs);
        } else {
            // GROUP BY CLAUSE
            List<BufferView> bufs = group.createGroups(buffer);
            for (BufferView b : bufs) {
                selectBuffer(ut, b, rs);
                b.release();
            }
        }

        if (def != null && rs.isEmpty()) {
            // ON EMPTY SELECTION INSERT DEFAULT
            rs.add(def);
        }

        return rs;
    }

    private void selectBuffer(int upto, BufferView buf, List<Object[]> rs) {
        for (int i = 0; i < upto && i < buf.length(); i++) {
            Object[] cur = buf.get(i);
            // HAVING CLAUSE
            if (having != null &&
                    !LogicValue.toBoolean((LogicValue) having.run(cur, buf))) {
                continue;
            }
            // SELECTION
            Object[] out = new Object[fields.size()];
            for (int j = 0; j < fields.size(); j++) {
                out[j] = fields.get(j).run(cur, buf);
            }
            rs.add(out);
        }
    }

}
