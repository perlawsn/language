package org.dei.perla.lang.parser.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.expression.Expression;
import org.dei.perla.lang.executor.expression.Field;
import org.dei.perla.lang.executor.expression.GroupTS;
import org.dei.perla.lang.executor.expression.Null;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

/**
 * @author Guido Rota 06/03/15.
 */
public class MiscTest {

    private static final Attribute tsAtt =
            Attribute.create("timestamp", DataType.TIMESTAMP);
    private static final Attribute intAtt =
            Attribute.create("integer", DataType.INTEGER);
    private static final Attribute floatAtt =
            Attribute.create("float", DataType.FLOAT);

    private static final List<Attribute> atts;
    static {
        Attribute[] aa = new Attribute[]{
                tsAtt,
                intAtt,
                floatAtt
        };
        atts = Collections.unmodifiableList(Arrays.asList(aa));
    }

    @Test
    public void testGroupTSNode() {
        GroupTSNode n = new GroupTSNode();
        assertThat(n.getType(), equalTo(DataType.TIMESTAMP));

        Expression e = n.build(atts);
        assertTrue(e instanceof GroupTS);
        assertThat(((GroupTS) e).getIndex(), equalTo(0));

        e = n.build(Collections.emptyList());
        assertTrue(e instanceof Null);
    }

    @Test
    public void testFieldNode() {
        FieldNode n = new FieldNode("integer");
        assertThat(n.getType(), nullValue());

        Expression e = n.build(atts);
        assertTrue(e instanceof Field);
        assertThat(((Field) e).getIndex(), equalTo(1));

        n = new FieldNode("float");
        assertThat(n.getType(), nullValue());

        e = n.build(atts);
        assertTrue(e instanceof Field);
        assertThat(((Field) e).getIndex(), equalTo(2));
    }

}
