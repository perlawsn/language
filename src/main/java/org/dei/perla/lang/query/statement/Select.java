package org.dei.perla.lang.query.statement;

import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.buffer.BufferView;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.expression.LogicValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Guido Rota 02/03/15.
 */
public final class Select {

    private final List<Expression> fields;
    private final WindowSize upto;
    private final GroupBy group;
    private final Expression having;
    private final Object[] def;

    public Select(List<Expression> fields, WindowSize upto,
            GroupBy group, Expression having, Object[] def) {
        this.fields = Collections.unmodifiableList(fields);
        this.upto = upto;
        this.group = group;
        this.having = having;
        this.def = def;
    }

    public List<Expression> getFields() {
        return fields;
    }

    public WindowSize getUpTo() {
        return upto;
    }

    public GroupBy getGroupBy() {
        return group;
    }

    public Expression getHaving() {
        return having;
    }

    public Object[] getDefault() {
        return def;
    }

    /**
     * This method indicates if all the field references inside the data
     * management clause are bound.
     *
     * <p>It is important to note that the {@code Select} clause does not
     * neet to be complete in order to be executed.
     *
     * @return true if all the components of the data management clause are
     * complete (i.e., all field references are bound to an actual FPC
     * attribute).
     */
    public boolean isComplete() {
        for (Expression f : fields) {
            if (!f.isComplete()) {
                return false;
            }
        }

        return group.isComplete() && having.isComplete();
    }

    public Select bind(Collection<Attribute> atts,
            List<Attribute> bound, Errors err) {
        List<Expression> bfields = new ArrayList<>();
        fields.forEach(f -> bfields.add(f.bind(atts, bound, err)));

        GroupBy bgroup = null;
        if (group != null) {
            bgroup = group.bind(atts, bound, err);
        }

        Expression bhaving = having.bind(atts, bound, err);

        return new Select(bfields, upto, bgroup, bhaving, def);
    }

    public List<Object[]> select(BufferView buffer) {
        List<Object[]> rs = new ArrayList<>();

        // UPTO CLAUSE
        int ut;
        switch (upto.getType()) {
            case TIME:
                ut = buffer.samplesIn(upto.getDuration());
                break;
            case SAMPLE:
                ut = upto.getSamples();
                break;
            default:
                throw new RuntimeException(
                        "Unexpected upto WindowSize type " + upto.getType());
        }

        if (group.hasNoGroups()) {
            selectBuffer(ut, buffer, rs);
        } else {
            // GROUP BY CLAUSE
            List<BufferView> bufs = group.createGroups(buffer);
            for (BufferView b : bufs) {
                selectBuffer(ut, b, rs);
                b.release();
            }
        }

        if (def.length > 0 && rs.isEmpty()) {
            // ON EMPTY SELECTION INSERT DEFAULT
            rs.add(def);
        }

        return rs;
    }

    private void selectBuffer(int upto, BufferView buf, List<Object[]> rs) {
        for (int i = 0; i < upto && i < buf.length(); i++) {
            Object[] cur = buf.get(i);
            // HAVING CLAUSE
            LogicValue valid = (LogicValue) having.run(cur, buf);
            if (!LogicValue.toBoolean(valid)) {
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