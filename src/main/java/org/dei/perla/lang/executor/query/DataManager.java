package org.dei.perla.lang.executor.query;

import org.dei.perla.core.utils.Check;
import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.expression.Expression;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Guido Rota 02/03/15.
 */
public class DataManager {

    public final List<Expression> select;
    public final int uptoSamples;
    public final Duration uptoDuration;
    public final List<Expression> group;
    public final Expression having;
    public final Object[] def;

    public DataManager(List<Expression> select, int uptoSamples,
            Duration uptoDuration, List<Expression> group,
            Expression having, Object[] def) {
        this.select = select;
        this.uptoSamples = uptoSamples;
        this.uptoDuration = uptoDuration;
        this.group = group;
        this.having = having;
        this.def = def;
    }

    public void select(BufferView buffer, QueryHandler handler) {
        int upto = 0;
        // UPTO CLAUSE
        if (uptoSamples != -1) {
            upto = uptoSamples;
        } else {
            upto = buffer.recordsIn(uptoDuration);
        }

        boolean generated = false;
        if (Check.nullOrEmpty(group)) {
            generated = selectBuffer(upto, buffer, handler);
        } else {
            // GROUP BY CLAUSE
            List<BufferView> groups = splitBuffer(buffer);
            for (BufferView b : groups) {
                generated |= selectBuffer(upto, b, handler);
                b.release();
            }
        }

        if (def != null && !generated) {
            // ON EMPTY SELECTION INSERT DEFAULT
            handler.newRecord(def);
        }
    }

    private boolean selectBuffer(int upto, BufferView buf, QueryHandler hand) {
        boolean generated = false;
        for (int i = 0; i < upto && i < buf.length(); i++) {
            Object[] cur = buf.get(i);
            // HAVING CLAUSE
            if (having != null && !(Boolean) having.compute(cur, buf)) {
                continue;
            }
            // SELECTION
            Object[] out = new Object[select.size()];
            for (int j = 0; j < select.size(); j++) {
                out[j] = select.get(j).compute(cur, buf);
            }
            hand.newRecord(out);
            generated = true;
        }
        return generated;
    }

    private List<BufferView> splitBuffer(BufferView buffer) {
        //TODO: split the buffer as for group by conditions
        throw new RuntimeException("unimplemented");
    }

}
