package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.BindingException;
import org.junit.Test;

import java.util.ArrayList;
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
    public void additionIntegerTest() throws BindingException {
        Errors err = new Errors();
        Expression e1 = Constant.create(1, DataType.INTEGER);

        Expression e = Arithmetic.createAddition(e1, intField, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object[] sample = new Object[]{4};
        assertThat(e.run(sample, null), equalTo(1 + 4));
        sample = new Object[]{3};
        assertThat(e.run(sample, null), equalTo(1 + 3));
    }

    @Test
    public void additionFloatTest() throws BindingException {
        Errors err = new Errors();
        Expression e1 = Constant.create(1.5f, DataType.FLOAT);

        Expression e = Arithmetic.createAddition(e1, floatField, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        Object[] sample = new Object[]{4.4f};
        assertThat(e.run(sample, null), equalTo(4.4f + 1.5f));
        sample = new Object[]{3.3f};
        assertThat(e.run(sample, null), equalTo(3.3f + 1.5f));
    }

    @Test
    public void additionMixedTest() {
        Errors err = new Errors();
        Expression e1 = Constant.create(1.5f, DataType.FLOAT);
        Expression e2 = Constant.create(5, DataType.INTEGER);

        Expression e = Arithmetic.createAddition(e1, e2, err);
        assertTrue(err.isEmpty());
        assertThat(e.run(null, null), equalTo(1.5f + 5f));

        e = Arithmetic.createAddition(e2, e1, err);
        assertTrue(err.isEmpty());
        assertThat(e.run(null, null), equalTo(1.5f + 5f));
    }

    @Test
    public void additionNullTest() {
        Errors err = new Errors();
        Expression c = Constant.create(43, DataType.INTEGER);
        Expression nul = Constant.NULL;

        Expression e = Arithmetic.createAddition(c, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createAddition(nul, c, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createAddition(nul, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void additionIncompleteTest() {
        Errors err = new Errors();
        Expression c = Constant.create(43, DataType.INTEGER);

        Expression e = Arithmetic.createAddition(c, new Field("integer"), err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createAddition(new Field("integer"), c, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void additionBindTest() throws BindingException {
        Errors err = new Errors();
        Expression c1 = Constant.create(1.5f, DataType.FLOAT);
        Field f1 = new Field("integer");
        Field f2 = new Field("float");

        Expression e = Arithmetic.createAddition(c1, f1, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Arithmetic.createAddition(f1, c1, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Arithmetic.createAddition(f1, f2, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(2));
        assertTrue(bound.contains(intAtt));
        assertTrue(bound.contains(floatAtt));
        assertTrue(e.isComplete());
    }

    @Test
    public void subtractionIntegerTest() throws BindingException {
        Errors err = new Errors();
        Expression e1 = Constant.create(1, DataType.INTEGER);

        Expression e = Arithmetic.createSubtraction(e1, intField, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object[] sample = new Object[]{4};
        assertThat(e.run(sample, null), equalTo(1 - 4));
        sample = new Object[]{3};
        assertThat(e.run(sample, null), equalTo(1 - 3));
    }

    @Test
    public void subtractionFloatTest() throws BindingException {
        Errors err = new Errors();
        Expression e1 = Constant.create(1.5f, DataType.FLOAT);

        Expression e = Arithmetic.createSubtraction(e1, floatField, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        Object[] sample = new Object[]{4.4f};
        assertThat(e.run(sample, null), equalTo(1.5f - 4.4f));
        sample = new Object[]{3.3f};
        assertThat(e.run(sample, null), equalTo(1.5f - 3.3f));
    }

    @Test
    public void subtractionMixedTest() {
        Errors err = new Errors();
        Expression e1 = Constant.create(1.5f, DataType.FLOAT);
        Expression e2 = Constant.create(5, DataType.INTEGER);

        Expression e = Arithmetic.createSubtraction(e1, e2, err);
        assertTrue(err.isEmpty());
        assertThat(e.run(null, null), equalTo(1.5f - 5f));

        e = Arithmetic.createSubtraction(e2, e1, err);
        assertTrue(err.isEmpty());
        assertThat(e.run(null, null), equalTo(5f - 1.5f));
    }

    @Test
    public void subtractionNullTest() {
        Errors err = new Errors();
        Expression c = Constant.create(43, DataType.INTEGER);
        Expression nul = Constant.NULL;

        Expression e = Arithmetic.createSubtraction(c, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createSubtraction(nul, c, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createSubtraction(nul, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void subtractionIncompleteTest() {
        Errors err = new Errors();
        Expression c = Constant.create(43, DataType.INTEGER);

        Expression e = Arithmetic.createSubtraction(c, new Field("integer"), err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createSubtraction(new Field("integer"), c, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void subtractionBindTest() throws BindingException {
        Errors err = new Errors();
        Expression c1 = Constant.create(1.5f, DataType.FLOAT);
        Field f1 = new Field("integer");
        Field f2 = new Field("float");

        Expression e = Arithmetic.createSubtraction(c1, f1, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Arithmetic.createSubtraction(f1, c1, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Arithmetic.createSubtraction(f1, f2, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(2));
        assertTrue(bound.contains(intAtt));
        assertTrue(bound.contains(floatAtt));
        assertTrue(e.isComplete());
    }

    @Test
    public void productIntegerTest() throws BindingException {
        Errors err = new Errors();
        Expression e1 = Constant.create(1, DataType.INTEGER);

        Expression e = Arithmetic.createProduct(e1, intField, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object[] sample = new Object[]{4};
        assertThat(e.run(sample, null), equalTo(1 * 4));
        sample = new Object[]{3};
        assertThat(e.run(sample, null), equalTo(1 * 3));
    }

    @Test
    public void productFloatTest() throws BindingException {
        Errors err = new Errors();
        Expression e1 = Constant.create(1.5f, DataType.FLOAT);

        Expression e = Arithmetic.createProduct(e1, floatField, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        Object[] sample = new Object[]{4.4f};
        assertThat(e.run(sample, null), equalTo(1.5f * 4.4f));
        sample = new Object[]{3.3f};
        assertThat(e.run(sample, null), equalTo(1.5f * 3.3f));
    }

    @Test
    public void productMixedTest() {
        Errors err = new Errors();
        Expression e1 = Constant.create(1.5f, DataType.FLOAT);
        Expression e2 = Constant.create(5, DataType.INTEGER);

        Expression e = Arithmetic.createProduct(e1, e2, err);
        assertTrue(err.isEmpty());
        assertThat(e.run(null, null), equalTo(1.5f * 5f));

        e = Arithmetic.createProduct(e2, e1, err);
        assertTrue(err.isEmpty());
        assertThat(e.run(null, null), equalTo(5f * 1.5f));
    }

    @Test
    public void productIncompleteTest() {
        Errors err = new Errors();
        Expression c = Constant.create(43, DataType.INTEGER);

        Expression e = Arithmetic.createProduct(c, new Field("integer"), err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createProduct(new Field("integer"), c, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void productNullTest() {
        Errors err = new Errors();
        Expression c = Constant.create(43, DataType.INTEGER);
        Expression nul = Constant.NULL;

        Expression e = Arithmetic.createProduct(c, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createProduct(nul, c, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createProduct(nul, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void productBindTest() throws BindingException {
        Errors err = new Errors();
        Expression c1 = Constant.create(1.5f, DataType.FLOAT);
        Field f1 = new Field("integer");
        Field f2 = new Field("float");

        Expression e = Arithmetic.createProduct(c1, f1, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Arithmetic.createProduct(f1, c1, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Arithmetic.createProduct(f1, f2, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(2));
        assertTrue(bound.contains(intAtt));
        assertTrue(bound.contains(floatAtt));
        assertTrue(e.isComplete());
    }

    @Test
    public void divisionIntegerTest() throws BindingException {
        Errors err = new Errors();
        Expression e1 = Constant.create(1, DataType.INTEGER);

        Expression e = Arithmetic.createDivision(e1, intField, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object[] sample = new Object[]{4};
        assertThat(e.run(sample, null), equalTo(1 / 4));
        sample = new Object[]{3};
        assertThat(e.run(sample, null), equalTo(1 / 3));
    }

    @Test
    public void divisionFloatTest() throws BindingException {
        Errors err = new Errors();
        Expression e1 = Constant.create(1.5f, DataType.FLOAT);

        Expression e = Arithmetic.createDivision(e1, floatField, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        Object[] sample = new Object[]{4.4f};
        assertThat(e.run(sample, null), equalTo(1.5f / 4.4f));
        sample = new Object[]{3.3f};
        assertThat(e.run(sample, null), equalTo(1.5f / 3.3f));
    }

    @Test
    public void divisionMixedTest() {
        Errors err = new Errors();
        Expression e1 = Constant.create(1.5f, DataType.FLOAT);
        Expression e2 = Constant.create(5, DataType.INTEGER);

        Expression e = Arithmetic.createDivision(e1, e2, err);
        assertTrue(err.isEmpty());
        assertThat(e.run(null, null), equalTo(1.5f / 5f));

        e = Arithmetic.createDivision(e2, e1, err);
        assertTrue(err.isEmpty());
        assertThat(e.run(null, null), equalTo(5f / 1.5f));
    }

    @Test
    public void divisionIncompleteTest() {
        Errors err = new Errors();
        Expression c = Constant.create(43, DataType.INTEGER);

        Expression e = Arithmetic.createDivision(c, new Field("integer"), err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createDivision(new Field("integer"), c, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void divisionNullTest() {
        Errors err = new Errors();
        Expression c = Constant.create(43, DataType.INTEGER);
        Expression nul = Constant.NULL;

        Expression e = Arithmetic.createDivision(c, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createDivision(nul, c, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createDivision(nul, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void divisionBindTest() throws BindingException {
        Errors err = new Errors();
        Expression c1 = Constant.create(1.5f, DataType.FLOAT);
        Field f1 = new Field("integer");
        Field f2 = new Field("float");

        Expression e = Arithmetic.createDivision(c1, f1, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Arithmetic.createDivision(f1, c1, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Arithmetic.createDivision(f1, f2, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(2));
        assertTrue(bound.contains(intAtt));
        assertTrue(bound.contains(floatAtt));
        assertTrue(e.isComplete());
    }

    @Test
    public void moduloTest() throws BindingException {
        Errors err = new Errors();
        Expression e1 = Constant.create(1, DataType.INTEGER);

        Expression e = Arithmetic.createModulo(e1, intField, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object[] sample = new Object[]{4};
        assertThat(e.run(sample, null), equalTo(1 % 4));
        sample = new Object[]{3};
        assertThat(e.run(sample, null), equalTo(1 % 3));
    }

    @Test
    public void moduloIncompleteTest() throws BindingException {
        Errors err = new Errors();
        Expression c = Constant.create(43, DataType.INTEGER);

        Expression e = Arithmetic.createModulo(c, new Field("integer"), err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createModulo(new Field("integer"), c, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void moduloNullTest() {
        Errors err = new Errors();
        Expression c = Constant.create(43, DataType.INTEGER);
        Expression nul = Constant.NULL;

        Expression e = Arithmetic.createModulo(c, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createModulo(nul, c, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Arithmetic.createModulo(nul, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void moduloBindTest() throws BindingException {
        Errors err = new Errors();
        Expression c1 = Constant.create(1, DataType.INTEGER);
        Field f1 = new Field("integer");

        Expression e = Arithmetic.createModulo(c1, f1, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Arithmetic.createModulo(f1, c1, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Arithmetic.createModulo(f1, f1, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
    }

    @Test
    public void inverseTest() throws BindingException {
        Errors err = new Errors();
        Expression e1 = Constant.create(1, DataType.INTEGER);

        Expression e = Arithmetic.createInverse(e1, err);
        assertTrue(err.isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(e.run(null, null), equalTo(-1));

        e = Arithmetic.createInverse(floatField, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>());
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        Object[] sample = new Object[]{5.4f};
        assertThat(e.run(sample, null), equalTo(-(Float) sample[0]));
        sample = new Object[]{3.2f};
        assertThat(e.run(sample, null), equalTo(-(Float) sample[0]));
    }

    @Test
    public void inverseBindTest() throws BindingException {
        Errors err = new Errors();
        Expression f = new Field("integer");

        Expression e = Arithmetic.createInverse(f, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
    }

    @Test
    public void inverseIncompleteTest() {
        Errors err = new Errors();
        Expression e = Arithmetic.createInverse(new Field("integer"), err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void inverseNullTest() {
        Errors err = new Errors();
        Expression e = Arithmetic.createInverse(Constant.NULL, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());
    }

}
