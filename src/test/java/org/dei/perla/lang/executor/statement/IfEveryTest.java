package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.expression.Comparison;
import org.dei.perla.lang.executor.expression.Constant;
import org.dei.perla.lang.executor.expression.Expression;
import org.dei.perla.lang.executor.expression.Field;
import org.junit.Test;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

    private static final Object[][] records;
    static {
        records = new Object[4][];
        for (int i = 0; i < records.length; i++) {
            records[i] = new Object[1];
            records[i][0] = i;
        }
    }

    private static final Expression fInt = new Field("integer");
    private static final Expression fFloat = new Field("float");
    private static final Expression fString = new Field("string");
    private static final Expression fBool = new Field("boolean");

    @Test
    public void testSingle() {
        IfEvery e;
        Period p;
        ClauseWrapper<IfEvery> cw;
        Expression cInt = Constant.create(5, DataType.INTEGER);
        Expression cFloat = Constant.create(3.4f, DataType.FLOAT);
        Expression cString = Constant.create("test", DataType.STRING);

        cw = IfEvery.create(Constant.TRUE, cInt, ChronoUnit.DAYS);
        assertFalse(cw.hasError());
        e = cw.getClause();
        assertFalse(e.hasErrors());
        assertTrue(e.isComplete());
        p = e.run(null);
        assertThat(p.getValue(), equalTo(5));

        cw = IfEvery.create(Constant.TRUE, cFloat, ChronoUnit.DAYS);
        assertFalse(cw.hasError());
        e = cw.getClause();
        assertFalse(e.hasErrors());
        assertTrue(e.isComplete());
        p = e.run(null);
        assertThat(p.getValue(), equalTo(3));

        cw = IfEvery.create(Constant.TRUE, cString, ChronoUnit.MONTHS);
        assertTrue(cw.hasError());
        e = cw.getClause();
        assertTrue(e.hasErrors());
    }

    @Test
    public void testBind() {
        ClauseWrapper<IfEvery> cw;
        IfEvery e;

        cw = IfEvery.create(Constant.TRUE, fInt, ChronoUnit.SECONDS);
        assertFalse(cw.hasError());
        e = cw.getClause();
        assertFalse(e.hasErrors());
        assertFalse(e.isComplete());
        e = e.bind(atts);
        assertFalse(e.hasErrors());
        assertTrue(e.isComplete());

        cw = IfEvery.create(Constant.TRUE, fFloat, ChronoUnit.SECONDS);
        assertFalse(cw.hasError());
        e = cw.getClause();
        assertFalse(e.hasErrors());
        assertFalse(e.isComplete());
        e = e.bind(atts);
        assertFalse(e.hasErrors());
        assertTrue(e.isComplete());

        cw = IfEvery.create(fBool, fFloat, ChronoUnit.SECONDS);
        assertFalse(cw.hasError());
        e = cw.getClause();
        assertFalse(e.hasErrors());
        assertFalse(e.isComplete());
        e = e.bind(atts);
        assertFalse(e.hasErrors());
        assertTrue(e.isComplete());

        cw = IfEvery.create(Constant.TRUE, fString, ChronoUnit.SECONDS);
        assertFalse(cw.hasError());
        e = cw.getClause();
        assertFalse(e.hasErrors());
        assertFalse(e.isComplete());
        e = e.bind(atts);
        assertTrue(e.hasErrors());
        assertTrue(e.isComplete());
    }

    @Test
    public void testMultiple() {
        Period p;
        IfEvery ife;
        IfEvery last;
        ClauseWrapper<IfEvery> cw;
        Expression cond;
        Expression field = new Field("integer");
        Expression c0 = Constant.create(0, DataType.INTEGER);
        Expression c1 = Constant.create(1, DataType.INTEGER);
        Expression c2 = Constant.create(2, DataType.INTEGER);

        cond = Comparison.createEQ(field, c0);
        cw = IfEvery.create(cond, field, ChronoUnit.SECONDS);
        assertFalse(cw.hasError());
        ife = last = cw.getClause();
        cond = Comparison.createEQ(field, c1);
        cw = IfEvery.create(last, cond, field, ChronoUnit.SECONDS);
        assertFalse(cw.hasError());
        last = cw.getClause();
        cond = Comparison.createEQ(field, c2);
        cw = IfEvery.create(last, cond, field, ChronoUnit.SECONDS);
        assertFalse(cw.hasError());
        last = cw.getClause();
        cw = IfEvery.create(last, Constant.TRUE, field, ChronoUnit.SECONDS);
        assertFalse(cw.hasError());
        last = cw.getClause();

        assertFalse(ife.hasErrors());
        assertFalse(ife.isComplete());
        ife = ife.bind(atts);
        assertFalse(ife.hasErrors());
        assertTrue(ife.isComplete());

        p = ife.run(records[0]);
        assertThat(p.getValue(), equalTo(0));
        p = ife.run(records[1]);
        assertThat(p.getValue(), equalTo(1));
        p = ife.run(records[2]);
        assertThat(p.getValue(), equalTo(2));
        p = ife.run(records[3]);
        assertThat(p.getValue(), equalTo(3));
    }

}
