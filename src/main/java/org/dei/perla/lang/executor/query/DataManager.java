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
    public final List<Expression> having;
    public final Object[] def;

    public DataManager(List<Expression> select, boolean distinct,
            int uptoSamples, Duration uptoDuration, List<Expression> group,
            List<Expression> having, Object[] def) {
        this.select = select;
        this.distinct = distinct;
        this.uptoSamples = uptoSamples;
        this.uptoDuration = uptoDuration;
        this.group = group;
        this.having = having;
        this.def = def;
    }

    public void run(BufferView buffer) {
        List<BufferView> bufs = splitBuffer(buffer);

        int upto = 0;
        for (BufferView b : bufs) {
            if (uptoSamples != -1) {
                upto = uptoSamples;
            } else {
                throw new RuntimeException("unimplemented");
            }

            for (int i = 0; i < upto; i++) {
                Object[] r = new Object[select.size()];
                for (int j = 0; j < select.size(); j++) {
                    r[j] = select.get(j).compute(b.get(i), b);
                }
                //TODO: deliver the record
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
