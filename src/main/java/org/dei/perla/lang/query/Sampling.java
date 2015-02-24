package org.dei.perla.lang.query;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.expression.Expression;
import org.dei.perla.lang.expression.Field;

import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * @author Guido Rota 24/02/15.
 */
public class Sampling {

    public static final Attribute GROUP_TS =
            Attribute.create("GROUP_TS", DataType.TIMESTAMP);

    //private final List<Expression> select;
    private int uptoSamples = 1;
    private Duration uptoDuration = null;
    //private final Set<Field> groupBy;
    private Duration tsGroupDuration;
    private int tsGroups;
    //private final Expression having;

    protected Sampling() {

    }

}
