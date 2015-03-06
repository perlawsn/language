package org.dei.perla.lang.parser.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.expression.Expression;
import org.dei.perla.lang.executor.expression.GroupTS;

import java.util.List;

/**
 * @author Guido Rota 06/03/15.
 */
public class GroupTSNode implements Node {

    @Override
    public DataType getType() {
        return DataType.TIMESTAMP;
    }

    @Override
    public Expression build(List<Attribute> atts) {
        int i = attributeIndex("timestamp", atts);
        if (i < 0) {
            return NULL_EXPRESSION;
        }
        return new GroupTS(i);
    }

}
