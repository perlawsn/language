package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.expression.Expression;
import org.dei.perla.lang.executor.expression.LogicValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Guido Rota 02/03/15.
 */
public final class Select {

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

    public Select rebuidl(List<Attribute> atts) {
        List<Expression> newFields = new ArrayList<>();
        fields.forEach(f -> newFields.add(f.bind(atts)));
        GroupBy newGroup = group.bind(atts);
        Expression newHaving = having.bind(atts);
        return new Select(newFields, upto, newGroup, newHaving, def);
    }

    public void select(BufferView buffer, SelectHandler handler) {
        // UPTO CLAUSE
        int ut;
        if (upto.getDuration() != null) {
            ut = buffer.recordsIn(upto.getDuration());
        } else {
            ut = upto.getSamples();
        }

        boolean generated = false;
        if (group == null) {
            generated = selectBuffer(ut, buffer, handler);
        } else {
            // GROUP BY CLAUSE
            List<BufferView> bufs = group.createGroups(buffer);
            for (BufferView b : bufs) {
                generated |= selectBuffer(ut, b, handler);
                b.release();
            }
        }

        if (def != null && !generated) {
            // ON EMPTY SELECTION INSERT DEFAULT
            handler.newRecord(def);
        }
    }

    private boolean selectBuffer(int upto, BufferView buf, SelectHandler handler) {
        boolean generated = false;
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
            handler.newRecord(out);
            generated = true;
        }
        return generated;
    }

}
