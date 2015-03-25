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
import static org.junit.Assert.*;

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

    private static final List<Attribute> atts;
    private static final Expression intField;
    private static final Expression floatField;
    static {
        atts = Arrays.asList(new Attribute[] {
                Attribute.TIMESTAMP,
                intAtt,
                stringAtt,
                floatAtt
        });

        intField = new Field(intAtt.getId());
        floatField = new Field(floatAtt.getId());
    }

    @Test
    public void additionIntegerTest() {
        Expression e1 = Constant.create(1, DataType.INTEGER);
        Expression e = Arithmetic.createAddition(e1, intField);
        e = e.bind(atts);

        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object[] record = new Object[]{4};
        assertThat(e.run(record, null), equalTo(1 + 4));
        record = new Object[]{3};
        assertThat(e.run(record, null), equalTo(1 + 3));
    }

    @Test
    public void additionFloatTest() {
        Expression e1 = Constant.create(1.5f, DataType.FLOAT);
        Expression e = Arithmetic.createAddition(e1, floatField);
        e = e.bind(atts);

        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        Object[] record = new Object[]{4.4f};
        assertThat(e.run(record, null), equalTo(4.4f + 1.5f));
        record = new Object[]{3.3f};
        assertThat(e.run(record, null), equalTo(3.3f + 1.5f));
    }

    @Test
    public void additionMixedTest() {
        Expression e1 = Constant.create(1.5f, DataType.FLOAT);
        Expression e2 = Constant.create(5, DataType.INTEGER);

        Expression e = Arithmetic.createAddition(e1, e2);
        assertFalse(e.hasErrors());
        assertThat(e.run(null, null), equalTo(1.5f + 5f));

        e = Arithmetic.createAddition(e2, e1);
        assertFalse(e.hasErrors());
        assertThat(e.run(null, null), equalTo(1.5f + 5f));
    }

    @Test
    public void additionNullTest() {
        Expression c = Constant.create(43, DataType.INTEGER);
        Expression nul = Constant.NULL_INTEGER;

        Expression e = Arithmetic.createAddition(c, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createAddition(nul, c);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createAddition(nul, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void additionErrorTest() {
        Expression err = new ErrorExpression("test");
        Expression c = Constant.create(85, DataType.INTEGER);

        Expression e = Arithmetic.createAddition(err, c);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Arithmetic.createAddition(c, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Arithmetic.createAddition(err, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

    @Test
    public void additionBindTest() {
        Expression c1 = Constant.create(1.5f, DataType.FLOAT);
        Field f1 = new Field("integer");
        Field f2 = new Field("float");

        Expression e = Arithmetic.createAddition(c1, f1);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        List<Attribute> as = e.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Arithmetic.createAddition(f1, c1);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        as = e.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Arithmetic.createAddition(f1, f2);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        as = e.getAttributes();
        assertThat(as.size(), equalTo(2));
        assertTrue(as.contains(intAtt));
        assertTrue(as.contains(floatAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

    @Test
    public void subtractionIntegerTest() {
        Expression e1 = Constant.create(1, DataType.INTEGER);
        Expression e = Arithmetic.createSubtraction(e1, intField);
        e = e.bind(atts);

        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object[] record = new Object[]{4};
        assertThat(e.run(record, null), equalTo(1 - 4));
        record = new Object[]{3};
        assertThat(e.run(record, null), equalTo(1 - 3));
    }

    @Test
    public void subtractionFloatTest() {
        Expression e1 = Constant.create(1.5f, DataType.FLOAT);
        Expression e = Arithmetic.createSubtraction(e1, floatField);
        e = e.bind(atts);

        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        Object[] record = new Object[]{4.4f};
        assertThat(e.run(record, null), equalTo(1.5f - 4.4f));
        record = new Object[]{3.3f};
        assertThat(e.run(record, null), equalTo(1.5f - 3.3f));
    }

    @Test
    public void subtractionMixedTest() {
        Expression e1 = Constant.create(1.5f, DataType.FLOAT);
        Expression e2 = Constant.create(5, DataType.INTEGER);

        Expression e = Arithmetic.createSubtraction(e1, e2);
        assertThat(e.run(null, null), equalTo(1.5f - 5f));

        e = Arithmetic.createSubtraction(e2, e1);
        assertThat(e.run(null, null), equalTo(5f - 1.5f));
    }

    @Test
    public void subtractionNullTest() {
        Expression c = Constant.create(43, DataType.INTEGER);
        Expression nul = Constant.NULL_INTEGER;

        Expression e = Arithmetic.createSubtraction(c, nul);
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createSubtraction(nul, c);
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createSubtraction(nul, nul);
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void subtractionErrorTest() {
        Expression err = new ErrorExpression("test");
        Expression c = Constant.create(85, DataType.INTEGER);

        Expression e = Arithmetic.createSubtraction(err, c);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Arithmetic.createSubtraction(c, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Arithmetic.createSubtraction(err, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

    @Test
    public void subtractionBindTest() {
        Expression c1 = Constant.create(1.5f, DataType.FLOAT);
        Field f1 = new Field("integer");
        Field f2 = new Field("float");

        Expression e = Arithmetic.createSubtraction(c1, f1);
        assertFalse(e.isComplete());
        e = e.bind(atts);
        List<Attribute> as = e.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(intAtt));
        assertTrue(e.isComplete());

        e = Arithmetic.createSubtraction(f1, c1);
        assertFalse(e.isComplete());
        e = e.bind(atts);
        as = e.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(intAtt));
        assertTrue(e.isComplete());

        e = Arithmetic.createSubtraction(f1, f2);
        assertFalse(e.isComplete());
        e = e.bind(atts);
        as = e.getAttributes();
        assertThat(as.size(), equalTo(2));
        assertTrue(as.contains(intAtt));
        assertTrue(as.contains(floatAtt));
        assertTrue(e.isComplete());
    }

    @Test
    public void productIntegerTest() {
        Expression e1 = Constant.create(1, DataType.INTEGER);
        Expression e = Arithmetic.createProduct(e1, intField);
        e = e.bind(atts);

        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object[] record = new Object[]{4};
        assertThat(e.run(record, null), equalTo(1 * 4));
        record = new Object[]{3};
        assertThat(e.run(record, null), equalTo(1 * 3));
    }

    @Test
    public void productFloatTest() {
        Expression e1 = Constant.create(1.5f, DataType.FLOAT);
        Expression e = Arithmetic.createProduct(e1, floatField);
        e = e.bind(atts);

        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        Object[] record = new Object[]{4.4f};
        assertThat(e.run(record, null), equalTo(1.5f * 4.4f));
        record = new Object[]{3.3f};
        assertThat(e.run(record, null), equalTo(1.5f * 3.3f));
    }

    @Test
    public void productMixedTest() {
        Expression e1 = Constant.create(1.5f, DataType.FLOAT);
        Expression e2 = Constant.create(5, DataType.INTEGER);

        Expression e = Arithmetic.createProduct(e1, e2);
        assertThat(e.run(null, null), equalTo(1.5f * 5f));

        e = Arithmetic.createProduct(e2, e1);
        assertThat(e.run(null, null), equalTo(5f * 1.5f));
    }

    @Test
    public void productNullTest() {
        Expression c = Constant.create(43, DataType.INTEGER);
        Expression nul = Constant.NULL_INTEGER;

        Expression e = Arithmetic.createProduct(c, nul);
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createProduct(nul, c);
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createProduct(nul, nul);
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void productErrorTest() {
        Expression err = new ErrorExpression("test");
        Expression c = Constant.create(85, DataType.INTEGER);

        Expression e = Arithmetic.createProduct(err, c);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Arithmetic.createProduct(c, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Arithmetic.createProduct(err, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

    @Test
    public void productBindTest() {
        Expression c1 = Constant.create(1.5f, DataType.FLOAT);
        Field f1 = new Field("integer");
        Field f2 = new Field("float");

        Expression e = Arithmetic.createProduct(c1, f1);
        assertFalse(e.isComplete());
        e = e.bind(atts);
        List<Attribute> as = e.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(intAtt));
        assertTrue(e.isComplete());

        e = Arithmetic.createProduct(f1, c1);
        assertFalse(e.isComplete());
        e = e.bind(atts);
        as = e.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(intAtt));
        assertTrue(e.isComplete());

        e = Arithmetic.createProduct(f1, f2);
        assertFalse(e.isComplete());
        e = e.bind(atts);
        as = e.getAttributes();
        assertThat(as.size(), equalTo(2));
        assertTrue(as.contains(intAtt));
        assertTrue(as.contains(floatAtt));
        assertTrue(e.isComplete());
    }

    @Test
    public void divisionIntegerTest() {
        Expression e1 = Constant.create(1, DataType.INTEGER);
        Expression e = Arithmetic.createDivision(e1, intField);
        e = e.bind(atts);

        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object[] record = new Object[]{4};
        assertThat(e.run(record, null), equalTo(1 / 4));
        record = new Object[]{3};
        assertThat(e.run(record, null), equalTo(1 / 3));
    }

    @Test
    public void divisionFloatTest() {
        Expression e1 = Constant.create(1.5f, DataType.FLOAT);
        Expression e = Arithmetic.createDivision(e1, floatField);
        e = e.bind(atts);

        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        Object[] record = new Object[]{4.4f};
        assertThat(e.run(record, null), equalTo(1.5f / 4.4f));
        record = new Object[]{3.3f};
        assertThat(e.run(record, null), equalTo(1.5f / 3.3f));
    }

    @Test
    public void divisionMixedTest() {
        Expression e1 = Constant.create(1.5f, DataType.FLOAT);
        Expression e2 = Constant.create(5, DataType.INTEGER);

        Expression e = Arithmetic.createDivision(e1, e2);
        assertThat(e.run(null, null), equalTo(1.5f / 5f));

        e = Arithmetic.createDivision(e2, e1);
        assertThat(e.run(null, null), equalTo(5f / 1.5f));
    }

    @Test
    public void divisionNullTest() {
        Expression c = Constant.create(43, DataType.INTEGER);
        Expression nul = Constant.NULL_INTEGER;

        Expression e = Arithmetic.createDivision(c, nul);
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createDivision(nul, c);
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createDivision(nul, nul);
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void divisionErrorTest() {
        Expression err = new ErrorExpression("test");
        Expression c = Constant.create(85, DataType.INTEGER);

        Expression e = Arithmetic.createDivision(err, c);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Arithmetic.createDivision(c, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Arithmetic.createDivision(err, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

    @Test
    public void divisionBindTest() {
        Expression c1 = Constant.create(1.5f, DataType.FLOAT);
        Field f1 = new Field("integer");
        Field f2 = new Field("float");

        Expression e = Arithmetic.createDivision(c1, f1);
        assertFalse(e.isComplete());
        e = e.bind(atts);
        List<Attribute> as = e.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(intAtt));
        assertTrue(e.isComplete());

        e = Arithmetic.createDivision(f1, c1);
        assertFalse(e.isComplete());
        e = e.bind(atts);
        as = e.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(intAtt));
        assertTrue(e.isComplete());

        e = Arithmetic.createDivision(f1, f2);
        assertFalse(e.isComplete());
        e = e.bind(atts);
        as = e.getAttributes();
        assertThat(as.size(), equalTo(2));
        assertTrue(as.contains(intAtt));
        assertTrue(as.contains(floatAtt));
        assertTrue(e.isComplete());
    }

    @Test
    public void moduloTest() {
        Expression e1 = Constant.create(1, DataType.INTEGER);
        Expression e = Arithmetic.createModulo(e1, intField);
        e = e.bind(atts);

        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object[] record = new Object[]{4};
        assertThat(e.run(record, null), equalTo(1 % 4));
        record = new Object[]{3};
        assertThat(e.run(record, null), equalTo(1 % 3));
    }

    @Test
    public void moduloNullTest() {
        Expression c = Constant.create(43, DataType.INTEGER);
        Expression nul = Constant.NULL_INTEGER;

        Expression e = Arithmetic.createModulo(c, nul);
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createModulo(nul, c);
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createModulo(nul, nul);
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void moduloErrorTest() {
        Expression err = new ErrorExpression("test");
        Expression c = Constant.create(85, DataType.INTEGER);

        Expression e = Arithmetic.createModulo(err, c);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Arithmetic.createModulo(c, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Arithmetic.createModulo(err, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

    @Test
    public void moduloBindTest() {
        Expression c1 = Constant.create(1, DataType.INTEGER);
        Field f1 = new Field("integer");

        Expression e = Arithmetic.createModulo(c1, f1);
        assertFalse(e.isComplete());
        e = e.bind(atts);
        List<Attribute> as = e.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(intAtt));
        assertTrue(e.isComplete());

        e = Arithmetic.createModulo(f1, c1);
        assertFalse(e.isComplete());
        e = e.bind(atts);
        as = e.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(intAtt));
        assertTrue(e.isComplete());

        e = Arithmetic.createModulo(f1, f1);
        assertFalse(e.isComplete());
        e = e.bind(atts);
        as = e.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(intAtt));
        assertTrue(e.isComplete());
    }

    @Test
    public void inverseTest() {
        Expression e1 = Constant.create(1, DataType.INTEGER);

        Expression e = Arithmetic.createInverse(e1);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(e.run(null, null), equalTo(-1));

        e = Arithmetic.createInverse(floatField);
        e = e.bind(atts);
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        Object[] record = new Object[]{5.4f};
        assertThat(e.run(record, null), equalTo(-(Float) record[0]));
        record = new Object[]{3.2f};
        assertThat(e.run(record, null), equalTo(-(Float) record[0]));
    }

    @Test
    public void inverseBindTest() {
        Expression f = new Field("integer");

        Expression e = Arithmetic.createInverse(f);
        assertFalse(e.isComplete());
        e = e.bind(atts);
        List<Attribute> atts = e.getAttributes();
        assertThat(atts.size(), equalTo(1));
        assertTrue(atts.contains(intAtt));
        assertTrue(e.isComplete());
    }

    @Test
    public void inverseNullTest() {
        Expression e = Arithmetic.createInverse(Constant.NULL_INTEGER);
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void inverseErrorTest() {
        Expression err = new ErrorExpression("test");

        Expression e = Arithmetic.createInverse(err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

}
