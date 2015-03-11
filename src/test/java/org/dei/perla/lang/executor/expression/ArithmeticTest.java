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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 02/03/15.
 */
public class ArithmeticTest {

    private static Attribute intAtt =
            Attribute.create("integer", DataType.INTEGER);
    private static Attribute stringAtt =
            Attribute.create("string", DataType.STRING);
    private static Attribute floatAtt =
            Attribute.create("float", DataType.FLOAT);

    private static List<Attribute> atts;
    private static BufferView view;

    private static Expression intField;
    private static Expression floatField;

    @BeforeClass
    public static void setupBuffer() {
        Attribute[] as = new Attribute[] {
                Attribute.TIMESTAMP,
                intAtt,
                stringAtt,
                floatAtt
        };
        atts = Arrays.asList(as);

        intField = new Field(intAtt.getId()).rebuild(atts);
        floatField = new Field(floatAtt.getId()).rebuild(atts);

        Buffer b = new ArrayBuffer(0, 512);
        b.add(new Record(atts, new Object[]{Instant.now(), 0, "0", 0.0f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 1, "1", 1.1f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 2, "2", 2.2f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 3, "3", 3.3f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 4, "4", 4.4f}));

        view = b.unmodifiableView();
    }

    @Test
    public void additionIntegerTest() {
        Constant e1 = new Constant(1, DataType.INTEGER);
        Expression e = Arithmetic.createAddition(e1, intField);

        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(e.run(view.get(0), view), equalTo(1 + 4));
        assertThat(e.run(view.get(1), view), equalTo(1 + 3));
    }

    @Test
    public void additionFloatTest() {
        Constant e1 = new Constant(1.5f, DataType.FLOAT);
        Expression e = Arithmetic.createAddition(e1, floatField);

        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertThat(e.run(view.get(0), view), equalTo(4.4f + 1.5f));
        assertThat(e.run(view.get(1), view), equalTo(3.3f + 1.5f));
    }

    @Test
    public void additionMixedTest() {
        Constant e1 = new Constant(1.5f, DataType.FLOAT);
        Constant e2 = new Constant(5, DataType.INTEGER);

        Expression e = Arithmetic.createAddition(e1, e2);
        assertThat(e.run(null, null), equalTo(1.5f + 5f));

        e = Arithmetic.createAddition(e2, e1);
        assertThat(e.run(null, null), equalTo(1.5f + 5f));
    }

    @Test
    public void additionNullTest() {
        Expression c = new Constant(43, DataType.INTEGER);

        Expression e = Arithmetic.createAddition(c, Null.INSTANCE);
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createAddition(Null.INSTANCE, c);
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createAddition(Null.INSTANCE, Null.INSTANCE);
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void additionRebuildTest() {
        Constant c1 = new Constant(1.5f, DataType.FLOAT);
        Field f1 = new Field("integer");
        Field f2 = new Field("float");

        Expression e = Arithmetic.createAddition(c1, f1);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Arithmetic.createAddition(f1, c1);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Arithmetic.createAddition(f1, f2);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
    }

    @Test
    public void subtractionIntegerTest() {
        Constant e1 = new Constant(1, DataType.INTEGER);
        Expression e = Arithmetic.createSubtraction(e1, intField);

        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(e.run(view.get(0), view), equalTo(1 - 4));
        assertThat(e.run(view.get(1), view), equalTo(1 - 3));
    }

    @Test
    public void subtractionFloatTest() {
        Constant e1 = new Constant(1.5f, DataType.FLOAT);
        Expression e = Arithmetic.createSubtraction(e1, floatField);

        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertThat(e.run(view.get(0), view), equalTo(1.5f - 4.4f));
        assertThat(e.run(view.get(1), view), equalTo(1.5f - 3.3f));
    }

    @Test
    public void subtractionMixedTest() {
        Constant e1 = new Constant(1.5f, DataType.FLOAT);
        Constant e2 = new Constant(5, DataType.INTEGER);

        Expression e = Arithmetic.createSubtraction(e1, e2);
        assertThat(e.run(null, null), equalTo(1.5f - 5f));

        e = Arithmetic.createSubtraction(e2, e1);
        assertThat(e.run(null, null), equalTo(5f - 1.5f));
    }

    @Test
    public void subtractionNullTest() {
        Expression c = new Constant(43, DataType.INTEGER);

        Expression e = Arithmetic.createSubtraction(c, Null.INSTANCE);
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createSubtraction(Null.INSTANCE, c);
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createSubtraction(Null.INSTANCE, Null.INSTANCE);
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void subtractionRebuildTest() {
        Constant c1 = new Constant(1.5f, DataType.FLOAT);
        Field f1 = new Field("integer");
        Field f2 = new Field("float");

        Expression e = Arithmetic.createSubtraction(c1, f1);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Arithmetic.createSubtraction(f1, c1);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Arithmetic.createSubtraction(f1, f2);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
    }

    @Test
    public void productIntegerTest() {
        Constant e1 = new Constant(1, DataType.INTEGER);
        Expression e = Arithmetic.createProduct(e1, intField);

        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(e.run(view.get(0), view), equalTo(1 * 4));
        assertThat(e.run(view.get(1), view), equalTo(1 * 3));
    }

    @Test
    public void productFloatTest() {
        Constant e1 = new Constant(1.5f, DataType.FLOAT);
        Expression e = Arithmetic.createProduct(e1, floatField);

        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertThat(e.run(view.get(0), view), equalTo(1.5f * 4.4f));
        assertThat(e.run(view.get(1), view), equalTo(1.5f * 3.3f));
    }

    @Test
    public void productMixedTest() {
        Constant e1 = new Constant(1.5f, DataType.FLOAT);
        Constant e2 = new Constant(5, DataType.INTEGER);

        Expression e = Arithmetic.createProduct(e1, e2);
        assertThat(e.run(null, null), equalTo(1.5f * 5f));

        e = Arithmetic.createProduct(e2, e1);
        assertThat(e.run(null, null), equalTo(5f * 1.5f));
    }

    @Test
    public void productNullTest() {
        Expression c = new Constant(43, DataType.INTEGER);

        Expression e = Arithmetic.createProduct(c, Null.INSTANCE);
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createProduct(Null.INSTANCE, c);
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createProduct(Null.INSTANCE, Null.INSTANCE);
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void productRebuildTest() {
        Constant c1 = new Constant(1.5f, DataType.FLOAT);
        Field f1 = new Field("integer");
        Field f2 = new Field("float");

        Expression e = Arithmetic.createProduct(c1, f1);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Arithmetic.createProduct(f1, c1);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Arithmetic.createProduct(f1, f2);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
    }

    @Test
    public void divisionIntegerTest() {
        Constant e1 = new Constant(1, DataType.INTEGER);
        Expression e = Arithmetic.createDivision(e1, intField);

        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(e.run(view.get(0), view), equalTo(1 / 4));
        assertThat(e.run(view.get(1), view), equalTo(1 / 3));
    }

    @Test
    public void divisionFloatTest() {
        Constant e1 = new Constant(1.5f, DataType.FLOAT);
        Expression e = Arithmetic.createDivision(e1, floatField);

        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertThat(e.run(view.get(0), view), equalTo(1.5f / 4.4f));
        assertThat(e.run(view.get(1), view), equalTo(1.5f / 3.3f));
    }

    @Test
    public void divisionMixedTest() {
        Constant e1 = new Constant(1.5f, DataType.FLOAT);
        Constant e2 = new Constant(5, DataType.INTEGER);

        Expression e = Arithmetic.createDivision(e1, e2);
        assertThat(e.run(null, null), equalTo(1.5f / 5f));

        e = Arithmetic.createDivision(e2, e1);
        assertThat(e.run(null, null), equalTo(5f / 1.5f));
    }

    @Test
    public void divisionNullTest() {
        Expression c = new Constant(43, DataType.INTEGER);

        Expression e = Arithmetic.createDivision(c, Null.INSTANCE);
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createDivision(Null.INSTANCE, c);
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createDivision(Null.INSTANCE, Null.INSTANCE);
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void divisionRebuildTest() {
        Constant c1 = new Constant(1.5f, DataType.FLOAT);
        Field f1 = new Field("integer");
        Field f2 = new Field("float");

        Expression e = Arithmetic.createDivision(c1, f1);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Arithmetic.createDivision(f1, c1);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Arithmetic.createDivision(f1, f2);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
    }

    @Test
    public void moduloTest() {
        Constant e1 = new Constant(1, DataType.INTEGER);
        Expression e = Arithmetic.createModulo(e1, intField);

        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(e.run(view.get(0), view), equalTo(1 % 4));
        assertThat(e.run(view.get(1), view), equalTo(1 % 3));
    }

    @Test
    public void moduloNullTest() {
        Expression c = new Constant(43, DataType.INTEGER);

        Expression e = Arithmetic.createModulo(c, Null.INSTANCE);
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createModulo(Null.INSTANCE, c);
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createModulo(Null.INSTANCE, Null.INSTANCE);
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void moduloRebuildTest() {
        Constant c1 = new Constant(1, DataType.INTEGER);
        Field f1 = new Field("integer");

        Expression e = Arithmetic.createModulo(c1, f1);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Arithmetic.createModulo(f1, c1);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Arithmetic.createModulo(f1, f1);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
    }

    @Test
    public void inverseTest() {
        Constant e1 = new Constant(1, DataType.INTEGER);

        Expression e = Arithmetic.createInverse(e1);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(e.run(view.get(0), view), equalTo(-1));

        e = Arithmetic.createInverse(floatField);
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertThat(e.run(view.get(0), view),
                equalTo(-(Float) view.get(0)[3]));
        assertThat(e.run(view.get(1), view),
                equalTo(-(Float) view.get(1)[3]));
    }

    @Test
    public void inverseRebuildTest() {
        Expression f = new Field("integer");

        Expression e = Arithmetic.createInverse(f);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
    }

    @Test
    public void inverseNullTest() {
        Expression e = Arithmetic.createInverse(Null.INSTANCE);
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());
    }

}
