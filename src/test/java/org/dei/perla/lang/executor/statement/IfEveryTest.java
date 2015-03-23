package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.expression.Constant;
import org.dei.perla.lang.executor.expression.Expression;
import org.dei.perla.lang.executor.expression.Field;
import org.junit.Test;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 23/03/15.
 */
public class IfEveryTest {

    private static final Attribute intAtt =
            Attribute.create("integer", DataType.INTEGER);
    private static final Attribute floatAtt =
            Attribute.create("float", DataType.FLOAT);
    private static final Attribute stringAtt =
            Attribute.create("string", DataType.STRING);
    private static final Attribute boolAtt =
            Attribute.create("boolean", DataType.BOOLEAN);

    private static final List<Attribute> atts;
    static {
        atts = new ArrayList<>();
        atts.add(intAtt);
        atts.add(floatAtt);
        atts.add(stringAtt);
        atts.add(boolAtt);
    }

    private static final Expression fInt = new Field("integer");
    private static final Expression fFloat = new Field("float");
    private static final Expression fString = new Field("string");
    private static final Expression fBool = new Field("boolean");

    @Test
    public void testEvery() {
        Every e;
        Period p;
        ClauseWrapper<Every> cw;
        Expression cInt = Constant.create(5, DataType.INTEGER);
        Expression cFloat = Constant.create(3.4f, DataType.FLOAT);
        Expression cString = Constant.create("test", DataType.STRING);

        cw = Every.create(Constant.TRUE, cInt, ChronoUnit.DAYS);
        assertFalse(cw.hasError());
        e = cw.getClause();
        assertFalse(e.hasErrors());
        assertTrue(e.isComplete());
        p = e.run(null);
        assertThat(p.getUnit(), equalTo(ChronoUnit.DAYS));
        assertThat(p.getValue(), equalTo(5));

        cw = Every.create(Constant.TRUE, cFloat, ChronoUnit.DAYS);
        assertFalse(cw.hasError());
        e = cw.getClause();
        assertFalse(e.hasErrors());
        assertTrue(e.isComplete());
        p = e.run(null);
        assertThat(p.getUnit(), equalTo(ChronoUnit.DAYS));
        assertThat(p.getValue(), equalTo(3));

        cw = Every.create(Constant.TRUE, cString, ChronoUnit.MONTHS);
        assertTrue(cw.hasError());
        e = cw.getClause();
        assertTrue(e.hasErrors());
    }

    @Test
    public void testEveryBind() {
        ClauseWrapper<Every> cw;
        Every e;

        cw = Every.create(Constant.TRUE, fInt, ChronoUnit.SECONDS);
        assertFalse(cw.hasError());
        e = cw.getClause();
        assertFalse(e.hasErrors());
        assertFalse(e.isComplete());
        e = e.bind(atts);
        assertFalse(e.hasErrors());
        assertTrue(e.isComplete());

        cw = Every.create(Constant.TRUE, fFloat, ChronoUnit.SECONDS);
        assertFalse(cw.hasError());
        e = cw.getClause();
        assertFalse(e.hasErrors());
        assertFalse(e.isComplete());
        e = e.bind(atts);
        assertFalse(e.hasErrors());
        assertTrue(e.isComplete());

        cw = Every.create(Constant.TRUE, fString, ChronoUnit.SECONDS);
        assertFalse(cw.hasError());
        e = cw.getClause();
        assertFalse(e.hasErrors());
        assertFalse(e.isComplete());
        e = e.bind(atts);
        assertTrue(e.hasErrors());
        assertTrue(e.isComplete());
    }

}
