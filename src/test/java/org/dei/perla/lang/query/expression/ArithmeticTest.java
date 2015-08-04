package org.dei.perla.lang.query.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.query.BindingException;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Guido Rota 02/03/15.
 */
public class ArithmeticTest {

    @Test
    public void additionIntegerTest() {
        Expression e1 = Constant.create(1, DataType.INTEGER);
        Expression e2 = Constant.create(23, DataType.INTEGER);

        Expression e = new Arithmetic(ArithmeticOperation.ADDITION,
                e1, e2, DataType.INTEGER);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(e.run(null, null), equalTo(1 + 23));
    }

    @Test
    public void additionFloatTest() {
        Expression e1 = Constant.create(1.5f, DataType.FLOAT);
        Expression e2 = Constant.create(4.4f, DataType.FLOAT);

        Expression e = new Arithmetic(ArithmeticOperation.ADDITION,
                e1, e2, DataType.FLOAT);
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertThat(e.run(null, null), equalTo(4.4f + 1.5f));
    }

    @Test
    public void additionNullTest() {
        Expression c = Constant.create(43, DataType.INTEGER);

        Expression e = new Arithmetic(ArithmeticOperation.ADDITION,
                c, Constant.NULL, DataType.INTEGER);
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = new Arithmetic(ArithmeticOperation.ADDITION,
                Constant.NULL, c, DataType.INTEGER);
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = new Arithmetic(ArithmeticOperation.ADDITION,
                Constant.NULL, Constant.NULL, DataType.INTEGER);
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void subtractionIntegerTest() {
        Expression e1 = Constant.create(1, DataType.INTEGER);
        Expression e2 = Constant.create(23, DataType.INTEGER);

        Expression e = new Arithmetic(ArithmeticOperation.SUBTRACTION,
                e1, e2, DataType.INTEGER);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(e.run(null, null), equalTo(1 - 23));
    }

    @Test
    public void subtractionFloatTest() {
        Expression e1 = Constant.create(1.5f, DataType.FLOAT);
        Expression e2 = Constant.create(4.4f, DataType.FLOAT);

        Expression e = new Arithmetic(ArithmeticOperation.SUBTRACTION,
                e1, e2, DataType.FLOAT);
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertThat(e.run(null, null), equalTo(4.4f - 1.5f));
    }

    @Test
    public void subtractionNullTest() {
        Expression c = Constant.create(43, DataType.INTEGER);

        Expression e = new Arithmetic(ArithmeticOperation.SUBTRACTION,
                c, Constant.NULL, DataType.INTEGER);
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = new Arithmetic(ArithmeticOperation.SUBTRACTION,
                Constant.NULL, c, DataType.INTEGER);
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = new Arithmetic(ArithmeticOperation.SUBTRACTION,
                Constant.NULL, Constant.NULL, DataType.INTEGER);
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void productIntegerTest() {
        Expression e1 = Constant.create(2, DataType.INTEGER);
        Expression e2 = Constant.create(23, DataType.INTEGER);

        Expression e = new Arithmetic(ArithmeticOperation.PRODUCT,
                e1, e2, DataType.INTEGER);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(e.run(null, null), equalTo(2 * 23));
    }

    @Test
    public void productFloatTest() {
        Expression e1 = Constant.create(1.5f, DataType.FLOAT);
        Expression e2 = Constant.create(4.4f, DataType.FLOAT);

        Expression e = new Arithmetic(ArithmeticOperation.PRODUCT,
                e1, e2, DataType.FLOAT);
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertThat(e.run(null, null), equalTo(4.4f * 1.5f));
    }

    @Test
    public void productNullTest() {
        Expression c = Constant.create(43, DataType.INTEGER);

        Expression e = new Arithmetic(ArithmeticOperation.PRODUCT,
                c, Constant.NULL, DataType.INTEGER);
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = new Arithmetic(ArithmeticOperation.PRODUCT,
                Constant.NULL, c, DataType.INTEGER);
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = new Arithmetic(ArithmeticOperation.PRODUCT,
                Constant.NULL, Constant.NULL, DataType.INTEGER);
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void divisionIntegerTest() {
        Expression e1 = Constant.create(1, DataType.INTEGER);
        Expression e2 = Constant.create(23, DataType.INTEGER);

        Expression e = new Arithmetic(ArithmeticOperation.DIVISION,
                e1, e2, DataType.INTEGER);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(e.run(null, null), equalTo(1 / 23));
    }

    @Test
    public void divisionFloatTest() {
        Expression e1 = Constant.create(1.5f, DataType.FLOAT);
        Expression e2 = Constant.create(4.4f, DataType.FLOAT);

        Expression e = new Arithmetic(ArithmeticOperation.DIVISION,
                e1, e2, DataType.FLOAT);
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertThat(e.run(null, null), equalTo(4.4f / 1.5f));
    }

    @Test
    public void divisionNullTest() {
        Expression c = Constant.create(43, DataType.INTEGER);

        Expression e = new Arithmetic(ArithmeticOperation.DIVISION,
                c, Constant.NULL, DataType.INTEGER);
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = new Arithmetic(ArithmeticOperation.DIVISION,
                Constant.NULL, c, DataType.INTEGER);
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = new Arithmetic(ArithmeticOperation.DIVISION,
                Constant.NULL, Constant.NULL, DataType.INTEGER);
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void moduloTest() {
        Expression e1 = Constant.create(1, DataType.INTEGER);
        Expression e2 = Constant.create(23, DataType.INTEGER);

        Expression e = new Arithmetic(ArithmeticOperation.MODULO,
                e1, e2, DataType.INTEGER);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(e.run(null, null), equalTo(1 % 23));
    }

    @Test
    public void moduloNullTest() {
        Expression c = Constant.create(43, DataType.INTEGER);

        Expression e = new Arithmetic(ArithmeticOperation.MODULO,
                c, Constant.NULL, DataType.INTEGER);
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = new Arithmetic(ArithmeticOperation.MODULO,
                Constant.NULL, c, DataType.INTEGER);
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = new Arithmetic(ArithmeticOperation.MODULO,
                Constant.NULL, Constant.NULL, DataType.INTEGER);
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void inverseTest() {
        Expression op = Constant.create(1, DataType.INTEGER);
        Expression e = new Inverse(op, DataType.INTEGER);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(e.run(null, null), equalTo(-1));

        op = Constant.create(1.4f, DataType.FLOAT);
        e = new Inverse(op, DataType.FLOAT);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(e.run(null, null), equalTo(-1.4f));
    }

    @Test
    public void inverseNullTest() {
        Expression e = new Inverse(Constant.NULL, DataType.INTEGER);
        Object res = e.run(null, null);
        assertThat(res, nullValue());
    }

}
