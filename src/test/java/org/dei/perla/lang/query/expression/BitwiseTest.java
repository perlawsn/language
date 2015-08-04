package org.dei.perla.lang.query.expression;

import org.dei.perla.core.descriptor.DataType;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Guido Rota 02/03/15.
 */
public class BitwiseTest {

    @Test
    public void bitwiseANDTest() {
        Expression e1 = Constant.create(12, DataType.INTEGER);
        Expression e2 = Constant.create(5639, DataType.INTEGER);

        Expression e = new Bitwise(BitwiseOperation.AND, e1, e2);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object res = e.run(null, null);
        assertThat(res, equalTo(12 & 5639));
    }

    @Test
    public void bitwiseANDNullTest() {
        Expression c = Constant.create(43, DataType.INTEGER);

        Expression e = new Bitwise(BitwiseOperation.AND, c, Constant.NULL);
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = new Bitwise(BitwiseOperation.AND, Constant.NULL, c);
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = new Bitwise(BitwiseOperation.AND, Constant.NULL, Constant.NULL);
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void bitwiseORTest() {
        Expression e1 = Constant.create(12, DataType.INTEGER);
        Expression e2 = Constant.create(5639, DataType.INTEGER);

        Expression e = new Bitwise(BitwiseOperation.OR, e1, e2);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object res = e.run(null, null);
        assertThat(res, equalTo(12 & 5639));
    }

    @Test
    public void bitwiseORNullTest() {
        Expression c = Constant.create(43, DataType.INTEGER);

        Expression e = new Bitwise(BitwiseOperation.OR, c, Constant.NULL);
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = new Bitwise(BitwiseOperation.OR, Constant.NULL, c);
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = new Bitwise(BitwiseOperation.OR, Constant.NULL, Constant.NULL);
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void bitwiseXORTest() {
        Expression e1 = Constant.create(12, DataType.INTEGER);
        Expression e2 = Constant.create(5639, DataType.INTEGER);

        Expression e = new Bitwise(BitwiseOperation.XOR, e1, e2);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object res = e.run(null, null);
        assertThat(res, equalTo(12 ^ 5639));
    }

    @Test
    public void bitwiseXORNullTest() {
        Expression c = Constant.create(43, DataType.INTEGER);

        Expression e = new Bitwise(BitwiseOperation.XOR, c, Constant.NULL);
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = new Bitwise(BitwiseOperation.XOR, Constant.NULL, c);
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = new Bitwise(BitwiseOperation.XOR, Constant.NULL, Constant.NULL);
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void bitwiseNOTTest() {
        Expression e1 = Constant.create(7382, DataType.INTEGER);

        Expression e = new BitwiseNot(e1);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object res = e.run(null, null);
        assertThat(res, equalTo(~7382));
    }

    @Test
    public void bitwiseNOTNullTest() {
        Expression e = new BitwiseNot(Constant.NULL);
        Object res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void bitwiseLSHTest() {
        Expression e1 = Constant.create(7382, DataType.INTEGER);
        Expression e2 = Constant.create(8, DataType.INTEGER);

        Expression e = new Bitwise(BitwiseOperation.LSH, e1, e2);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object res = e.run(null, null);
        assertThat(res, equalTo(7382 << 8));
    }

    @Test
    public void bitwiseLSHNullTest() {
        Expression c = Constant.create(43, DataType.INTEGER);

        Expression e = new Bitwise(BitwiseOperation.LSH, c, Constant.NULL);
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = new Bitwise(BitwiseOperation.LSH, Constant.NULL, c);
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = new Bitwise(BitwiseOperation.LSH, Constant.NULL, Constant.NULL);
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void bitwiseRSHTest() {
        Expression e1 = Constant.create(7382, DataType.INTEGER);
        Expression e2 = Constant.create(8, DataType.INTEGER);

        Expression e = new Bitwise(BitwiseOperation.RSH, e1, e2);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object res = e.run(null, null);
        assertThat(res, equalTo(7382 << 8));
    }

    @Test
    public void bitwiseRSHNullTest() {
        Expression c = Constant.create(43, DataType.INTEGER);

        Expression e = new Bitwise(BitwiseOperation.RSH, c, Constant.NULL);
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = new Bitwise(BitwiseOperation.RSH, Constant.NULL, c);
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = new Bitwise(BitwiseOperation.RSH, Constant.NULL, Constant.NULL);
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

}
