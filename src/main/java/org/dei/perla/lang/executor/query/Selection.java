package org.dei.perla.lang.executor.query;

import org.dei.perla.core.utils.Check;
import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.QueryHandler;
import org.dei.perla.lang.executor.expression.Expression;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Guido Rota 02/03/15.
 */
public final class Selection {

    private static final UpTo DEFAULT_UPTO = new UpTo();

    private final List<Expression> select;
    private final UpTo upto;
    private final GroupBy group;
    private final Expression having;
    private final Object[] def;

    public Selection(List<Expression> select, UpTo upto,
            GroupBy group, Expression having, Object[] def) {
        this.select = select;
        if (upto == null) {
            this.upto = DEFAULT_UPTO;
        } else {
            this.upto = upto;
        }
        this.group = group;
        this.having = having;
        this.def = def;
    }

    public void select(BufferView buffer, SelectHandler handler) {
        // UPTO CLAUSE
        int ut = upto.getSamples(buffer);

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
            if (having != null && !(Boolean) having.run(cur, buf)) {
                continue;
            }
            // SELECTION
            Object[] out = new Object[select.size()];
            for (int j = 0; j < select.size(); j++) {
                out[j] = select.get(j).run(cur, buf);
            }
            handler.newRecord(out);
            generated = true;
        }
        return generated;
    }

}
