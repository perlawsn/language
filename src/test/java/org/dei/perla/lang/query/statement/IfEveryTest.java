package org.dei.perla.lang.query.statement;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.query.expression.Constant;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.expression.Field;
import org.junit.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
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

    private static final Object[][] samples;
    static {
        samples = new Object[4][];
        for (int i = 0; i < samples.length; i++) {
            samples[i] = new Object[1];
            samples[i][0] = i;
        }
    }

    private static final Expression fInt =
            new Field("integer", DataType.INTEGER, 0);
    private static final Expression fFloat =
            new Field("float", DataType.FLOAT, 1);
    private static final Expression fString =
            new Field("string", DataType.STRING, 2);
    private static final Expression fBool =
            new Field("boolean", DataType.BOOLEAN, 3);

    @Test
    public void testSingle() {
        Duration d;
        Errors err = new Errors();
        Expression cInt = Constant.create(5, DataType.INTEGER);
        Expression cFloat = Constant.create(3.4f, DataType.FLOAT);
        Expression cString = Constant.create("test", DataType.STRING);

        IfEvery e = IfEvery.create(Constant.TRUE, cInt, ChronoUnit.DAYS, err);
        assertTrue(err.isEmpty());
        assertTrue(err.isEmpty());
        d = e.run(null);
        assertThat(d, equalTo(Duration.ofDays(5)));

        e = IfEvery.create(Constant.TRUE, cFloat, ChronoUnit.DAYS, err);
        assertTrue(err.isEmpty());
        assertTrue(err.isEmpty());
        d = e.run(null);
        assertThat(d, equalTo(Duration.ofDays(3)));
    }
/*
    @Test
    public void testMultiple() {
        Duration d;
        IfEvery ife;
        IfEvery last;
        Errors err = new Errors();
        Expression cond;
        Expression c0 = Constant.create(0, DataType.INTEGER);
        Expression c1 = Constant.create(1, DataType.INTEGER);
        Expression c2 = Constant.create(2, DataType.INTEGER);

        cond = new Comparison(ComparisonOperation.EQ, fInt, c0);
        ife = last = IfEvery.create(cond, fInt, ChronoUnit.SECONDS, err);
        cond = Comparison.createEQ(field, c1, err);
        last = IfEvery.create(last, cond, fInt, ChronoUnit.SECONDS, err);
        cond = Comparison.createEQ(field, c2, err);
        last = IfEvery.create(last, cond, fInt, ChronoUnit.SECONDS, err);
        last = IfEvery.create(last, Constant.TRUE, field,
                ChronoUnit.SECONDS, err);

        assertTrue(err.isEmpty());
        assertFalse(ife.isComplete());
        ife = ife.bind(atts, new ArrayList<>(), err);
        assertTrue(err.isEmpty());
        assertTrue(err.isEmpty());
        assertTrue(ife.isComplete());

        d = ife.run(samples[0]);
        assertThat(d, equalTo(Duration.ofSeconds(0)));
        d = ife.run(samples[1]);
        assertThat(d, equalTo(Duration.ofSeconds(1)));
        d = ife.run(samples[2]);
        assertThat(d, equalTo(Duration.ofSeconds(2)));
        d = ife.run(samples[3]);
        assertThat(d, equalTo(Duration.ofSeconds(3)));
    }
*/
}
