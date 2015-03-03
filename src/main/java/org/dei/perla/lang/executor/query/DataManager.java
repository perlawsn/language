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
    public final boolean distinct;
    public final int uptoSamples;
    public final Duration uptoDuration;
    public final List<Expression> group;
    public final Expression having;
    public final Object[] def;

    public DataManager(List<Expression> select, boolean distinct,
            int uptoSamples, Duration uptoDuration, List<Expression> group,
            Expression having, Object[] def) {
        this.select = select;
        this.distinct = distinct;
        this.uptoSamples = uptoSamples;
        this.uptoDuration = uptoDuration;
        this.group = group;
        this.having = having;
        this.def = def;
    }

    public void run(BufferView buffer, QueryHandler handler) {
        // GROUP BY CLAUSE
        List<BufferView> bufs = splitBuffer(buffer);

        int upto = 0;
        for (BufferView b : bufs) {
            // UPTO CLAUSE
            if (uptoSamples != -1) {
                upto = uptoSamples;
            } else {
                upto = buffer.indexOf(uptoDuration);
            }
            for (int i = 0; i < upto && i < buffer.length(); i++) {
                Object[] cur = b.get(i);
                // HAVING CLAUSE
                if (having != null && !(Boolean) having.compute(cur, b)) {
                    if (def != null) {
                        // ON EMPTY SELECTION INSERT DEFAULT
                        handler.newRecord(def);
                    }
                    continue;
                }
                // SELECTION
                Object[] out = new Object[select.size()];
                for (int j = 0; j < select.size(); j++) {
                    out[j] = select.get(j).compute(cur, b);
                }
                handler.newRecord(out);
            }
        }
    }

    private List<BufferView> splitBuffer(BufferView buffer) {
        List<BufferView> bufs = new LinkedList<>();
        if (Check.nullOrEmpty(group)) {
            bufs.add(buffer);
            return bufs;
        }

        //TODO: split the buffer as for group by conditions
        throw new RuntimeException("unimplemented");
    }

}
