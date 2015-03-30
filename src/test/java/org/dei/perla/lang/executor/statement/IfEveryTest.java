package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.expression.Comparison;
import org.dei.perla.lang.executor.expression.Constant;
import org.dei.perla.lang.executor.expression.Expression;
import org.dei.perla.lang.executor.expression.Field;
import org.junit.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

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
        Duration d;
        Expression cInt = Constant.create(5, DataType.INTEGER);
        Expression cFloat = Constant.create(3.4f, DataType.FLOAT);
        Expression cString = Constant.create("test", DataType.STRING);

        e = IfEvery.create(Constant.TRUE, cInt, ChronoUnit.DAYS);
        assertFalse(e.hasErrors());
        assertTrue(e.isComplete());
        d = e.run(null);
        assertThat(d, equalTo(Duration.ofDays(5)));

        e = IfEvery.create(Constant.TRUE, cFloat, ChronoUnit.DAYS);
        assertFalse(e.hasErrors());
        assertTrue(e.isComplete());
        d = e.run(null);
        assertThat(d, equalTo(Duration.ofDays(3)));

        e = IfEvery.create(Constant.TRUE, cString, ChronoUnit.MONTHS);
        assertTrue(e.hasErrors());
    }

    @Test
    public void testBind() {
        IfEvery e;

        e = IfEvery.create(Constant.TRUE, fInt, ChronoUnit.SECONDS);
        assertFalse(e.hasErrors());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound);
        assertFalse(e.hasErrors());
        assertTrue(e.isComplete());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));

        e = IfEvery.create(Constant.TRUE, fFloat, ChronoUnit.SECONDS);
        assertFalse(e.hasErrors());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound);
        assertFalse(e.hasErrors());
        assertTrue(e.isComplete());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(floatAtt));

        e = IfEvery.create(fBool, fFloat, ChronoUnit.SECONDS);
        assertFalse(e.hasErrors());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound);
        assertFalse(e.hasErrors());
        assertTrue(e.isComplete());
        assertThat(bound.size(), equalTo(2));
        assertTrue(bound.contains(floatAtt));
        assertTrue(bound.contains(boolAtt));

        e = IfEvery.create(Constant.TRUE, fString, ChronoUnit.SECONDS);
        assertFalse(e.hasErrors());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound);
        assertTrue(e.hasErrors());
        assertTrue(e.isComplete());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(stringAtt));
    }

    @Test
    public void testMultiple() {
        Duration d;
        IfEvery ife;
        IfEvery last;
        Expression cond;
        Expression field = new Field("integer");
        Expression c0 = Constant.create(0, DataType.INTEGER);
        Expression c1 = Constant.create(1, DataType.INTEGER);
        Expression c2 = Constant.create(2, DataType.INTEGER);

        cond = Comparison.createEQ(field, c0);
        ife = last = IfEvery.create(cond, field, ChronoUnit.SECONDS);
        cond = Comparison.createEQ(field, c1);
        last = IfEvery.create(last, cond, field, ChronoUnit.SECONDS);
        cond = Comparison.createEQ(field, c2);
        last = IfEvery.create(last, cond, field, ChronoUnit.SECONDS);
        last = IfEvery.create(last, Constant.TRUE, field, ChronoUnit.SECONDS);

        assertFalse(ife.hasErrors());
        assertFalse(ife.isComplete());
        ife = ife.bind(atts, new ArrayList<>());
        assertFalse(ife.hasErrors());
        assertTrue(ife.isComplete());

        d = ife.run(records[0]);
        assertThat(d, equalTo(Duration.ofSeconds(0)));
        d = ife.run(records[1]);
        assertThat(d, equalTo(Duration.ofSeconds(1)));
        d = ife.run(records[2]);
        assertThat(d, equalTo(Duration.ofSeconds(2)));
        d = ife.run(records[3]);
        assertThat(d, equalTo(Duration.ofSeconds(3)));
    }

}
