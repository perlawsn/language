package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.record.Record;
import org.dei.perla.lang.executor.ArrayBuffer;
import org.dei.perla.lang.executor.Buffer;
import org.dei.perla.lang.executor.BufferView;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Guido Rota 23/02/15.
 */
public class ComparisonTest {

    private static Attribute integerAtt =
            Attribute.create("integer", DataType.INTEGER);
    private static Attribute stringAtt =
            Attribute.create("string", DataType.STRING);
    private static Attribute floatAtt =
            Attribute.create("float", DataType.FLOAT);

    private static BufferView view;

    private static final Expression tsExpr = new Field(0, DataType.TIMESTAMP);
    private static final Expression intExpr = new Field(1, DataType.INTEGER);
    private static final Expression stringExpr = new Field(2, DataType.STRING);
    private static final Expression floatExpr = new Field(3, DataType.FLOAT);

    private static final Expression trueExpr = new Constant(true, DataType.BOOLEAN);
    private static final Expression falseExpr = new Constant(false, DataType.BOOLEAN);

    private static final Expression t1 =
            new Constant(Instant.parse("2015-02-23T15:07:45.000Z"), DataType.TIMESTAMP);
    private static final Expression t2 =
            new Constant(Instant.parse("2015-02-23T15:08:45.000Z"), DataType.TIMESTAMP);

    @BeforeClass
    public static void setupBuffer() {
        Attribute[] as = new Attribute[]{
                Attribute.TIMESTAMP,
                integerAtt,
                stringAtt,
                floatAtt
        };
        List<Attribute> atts = Arrays.asList(as);

        Buffer b = new ArrayBuffer(atts, 512);
        b.add(new Record(atts, new Object[]{Instant.now(), 0, "0", 0.0f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 1, "1", 1.1f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 2, "2", 2.2f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 3, "3", 3.3f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 4, "4", 4.4f}));

        view = b.unmodifiableView();
    }

    @Test
    public void equalTest() {
        Expression eq = new Equal(intExpr, new Constant(4, DataType.INTEGER));
        assertThat(eq.getType(), equalTo(DataType.BOOLEAN));
        Object res = eq.compute(view.get(0), view);
        assertThat(res, equalTo(true));
        res = eq.compute(view.get(1), view);
        assertThat(res, equalTo(false));

        eq = new Equal(floatExpr, new Constant(4.4f, DataType.FLOAT));
        res = eq.compute(view.get(0), view);
        assertThat(res, equalTo(true));
        res = eq.compute(view.get(1), view);
        assertThat(res, equalTo(false));

        eq = new Equal(stringExpr, new Constant("4", DataType.STRING));
        res = eq.compute(view.get(0), view);
        assertThat(res, equalTo(true));
        res = eq.compute(view.get(1), view);
        assertThat(res, equalTo(false));

        res = new Equal(trueExpr, trueExpr).compute(null, null);
        assertThat(res, equalTo(true));
        res = new Equal(trueExpr, falseExpr).compute(null, null);
        assertThat(res, equalTo(false));

        res = new Equal(t1, t1).compute(null, null);
        assertThat(res, equalTo(true));
        res = new Equal(t1, t2).compute(null, null);
        assertThat(res, equalTo(false));
    }

    @Test
    public void notEqualTest() {
        Expression eq = new NotEqual(intExpr, new Constant(4, DataType.INTEGER));
        assertThat(eq.getType(), equalTo(DataType.BOOLEAN));
        Object res = eq.compute(view.get(0), view);
        assertThat(res, equalTo(false));
        res = eq.compute(view.get(1), view);
        assertThat(res, equalTo(true));

        eq = new NotEqual(floatExpr, new Constant(4.4f, DataType.FLOAT));
        res = eq.compute(view.get(0), view);
        assertThat(res, equalTo(false));
        res = eq.compute(view.get(1), view);
        assertThat(res, equalTo(true));

        eq = new NotEqual(stringExpr, new Constant("4", DataType.STRING));
        res = eq.compute(view.get(0), view);
        assertThat(res, equalTo(false));
        res = eq.compute(view.get(1), view);
        assertThat(res, equalTo(true));

        res = new NotEqual(trueExpr, trueExpr).compute(null, null);
        assertThat(res, equalTo(false));
        res = new NotEqual(trueExpr, falseExpr).compute(null, null);
        assertThat(res, equalTo(true));

        res = new NotEqual(t1, t1).compute(null, null);
        assertThat(res, equalTo(false));
        res = new NotEqual(t1, t2).compute(null, null);
        assertThat(res, equalTo(true));
    }

    @Test
    public void greaterTest() {
        Expression eq = new Greater(new Constant(4, DataType.INTEGER), intExpr);
        assertThat(eq.getType(), equalTo(DataType.BOOLEAN));
        Object res = eq.compute(view.get(0), view);
        assertThat(res, equalTo(false));
        res = eq.compute(view.get(1), view);
        assertThat(res, equalTo(true));
        eq = new Greater(intExpr, new Constant(4, DataType.INTEGER));
        res = eq.compute(view.get(1), view);
        assertThat(res, equalTo(false));

        eq = new Greater(new Constant(4.4f, DataType.FLOAT), floatExpr);
        res = eq.compute(view.get(0), view);
        assertThat(res, equalTo(false));
        res = eq.compute(view.get(1), view);
        assertThat(res, equalTo(true));
        eq = new Greater(floatExpr, new Constant(4.4f, DataType.FLOAT));
        res = eq.compute(view.get(1), view);
        assertThat(res, equalTo(false));

        res = new Greater(t1, t1).compute(null, null);
        assertThat(res, equalTo(false));
        res = new Greater(t1, t2).compute(null, null);
        assertThat(res, equalTo(false));
        res = new Greater(t2, t1).compute(null, null);
        assertThat(res, equalTo(true));
    }

    @Test
    public void greaterEqualTest() {
        Expression eq = new GreaterEqual(new Constant(4, DataType.INTEGER), intExpr);
        assertThat(eq.getType(), equalTo(DataType.BOOLEAN));
        Object res = eq.compute(view.get(0), view);
        assertThat(res, equalTo(true));
        res = eq.compute(view.get(1), view);
        assertThat(res, equalTo(true));
        eq = new GreaterEqual(intExpr, new Constant(4, DataType.INTEGER));
        res = eq.compute(view.get(1), view);
        assertThat(res, equalTo(false));

        eq = new GreaterEqual(new Constant(4.4f, DataType.FLOAT), floatExpr);
        res = eq.compute(view.get(0), view);
        assertThat(res, equalTo(true));
        res = eq.compute(view.get(1), view);
        assertThat(res, equalTo(true));
        eq = new GreaterEqual(floatExpr, new Constant(4.4f, DataType.FLOAT));
        res = eq.compute(view.get(1), view);
        assertThat(res, equalTo(false));

        res = new GreaterEqual(t1, t1).compute(null, null);
        assertThat(res, equalTo(true));
        res = new GreaterEqual(t1, t2).compute(null, null);
        assertThat(res, equalTo(false));
        res = new GreaterEqual(t2, t1).compute(null, null);
        assertThat(res, equalTo(true));
    }

    @Test
    public void lessTest() {
        Expression eq = new Less(new Constant(4, DataType.INTEGER), intExpr);
        assertThat(eq.getType(), equalTo(DataType.BOOLEAN));
        Object res = eq.compute(view.get(0), view);
        assertThat(res, equalTo(false));
        res = eq.compute(view.get(1), view);
        assertThat(res, equalTo(false));
        eq = new Less(intExpr, new Constant(4, DataType.INTEGER));
        res = eq.compute(view.get(1), view);
        assertThat(res, equalTo(true));

        eq = new Less(new Constant(4.4f, DataType.FLOAT), floatExpr);
        res = eq.compute(view.get(0), view);
        assertThat(res, equalTo(false));
        res = eq.compute(view.get(1), view);
        assertThat(res, equalTo(false));
        eq = new Less(floatExpr, new Constant(4.4f, DataType.FLOAT));
        res = eq.compute(view.get(1), view);
        assertThat(res, equalTo(true));

        res = new Less(t1, t1).compute(null, null);
        assertThat(res, equalTo(false));
        res = new Less(t1, t2).compute(null, null);
        assertThat(res, equalTo(true));
        res = new Less(t2, t1).compute(null, null);
        assertThat(res, equalTo(false));
    }

    @Test
    public void lessEqualTest() {
        Expression eq = new LessEqual(new Constant(4, DataType.INTEGER), intExpr);
        assertThat(eq.getType(), equalTo(DataType.BOOLEAN));
        Object res = eq.compute(view.get(0), view);
        assertThat(res, equalTo(true));
        res = eq.compute(view.get(1), view);
        assertThat(res, equalTo(false));
        eq = new LessEqual(intExpr, new Constant(4, DataType.INTEGER));
        res = eq.compute(view.get(1), view);
        assertThat(res, equalTo(true));

        eq = new LessEqual(new Constant(4.4f, DataType.FLOAT), floatExpr);
        res = eq.compute(view.get(0), view);
        assertThat(res, equalTo(true));
        res = eq.compute(view.get(1), view);
        assertThat(res, equalTo(false));
        eq = new LessEqual(floatExpr, new Constant(4.4f, DataType.FLOAT));
        res = eq.compute(view.get(1), view);
        assertThat(res, equalTo(true));

        res = new LessEqual(t1, t1).compute(null, null);
        assertThat(res, equalTo(true));
        res = new LessEqual(t1, t2).compute(null, null);
        assertThat(res, equalTo(true));
        res = new LessEqual(t2, t1).compute(null, null);
        assertThat(res, equalTo(false));
    }

}
