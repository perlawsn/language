package org.dei.perla.lang.query.expression;

import org.dei.perla.core.descriptor.DataType;
import org.junit.Test;

import java.time.Instant;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author Guido Rota 23/02/15.
 */
public class ComparisonTest {

    private static final Expression trueExpr = Constant.TRUE;
    private static final Expression falseExpr = Constant.FALSE;
    private static final Expression nullExpr = Constant.NULL;

    private static final Expression t1 =
            Constant.create(Instant.parse("2015-02-23T15:07:45.000Z"), DataType.TIMESTAMP);
    private static final Expression t2 =
            Constant.create(Instant.parse("2015-02-23T15:08:45.000Z"), DataType.TIMESTAMP);

    @Test
    public void testEQ() {
        // INTEGER
        Expression c1 = Constant.create(3, DataType.INTEGER);
        Expression c2 = Constant.create(4, DataType.INTEGER);

        Expression e = new Comparison(ComparisonOperation.EQ, c1, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = new Comparison(ComparisonOperation.EQ, c1, c2);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        // FLOAT
        c1 = Constant.create(4.3f, DataType.FLOAT);
        c2 = Constant.create(5.8f, DataType.FLOAT);

        e = new Comparison(ComparisonOperation.EQ, c1, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = new Comparison(ComparisonOperation.EQ, c1, c2);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        // STRING
        c1 = Constant.create("test", DataType.STRING);
        c2 = Constant.create("tset", DataType.STRING);

        e = new Comparison(ComparisonOperation.EQ, c1, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = new Comparison(ComparisonOperation.EQ, c1, c2);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        // TIMESTAMP
        e = new Comparison(ComparisonOperation.EQ, t1, t1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = new Comparison(ComparisonOperation.EQ, t1, t2);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));
    }

    @Test
    public void testEQNull() {
        Expression c = Constant.create(4, DataType.INTEGER);

        Expression e = new Comparison(ComparisonOperation.EQ, c, nullExpr);
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = new Comparison(ComparisonOperation.EQ, nullExpr, c);
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = new Comparison(ComparisonOperation.EQ, nullExpr, nullExpr);
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testNE() {
        // INTEGER
        Expression c1 = Constant.create(3, DataType.INTEGER);
        Expression c2 = Constant.create(4, DataType.INTEGER);

        Expression e = new Comparison(ComparisonOperation.NE, c1, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = new Comparison(ComparisonOperation.NE, c1, c2);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        // FLOAT
        c1 = Constant.create(4.3f, DataType.FLOAT);
        c2 = Constant.create(5.8f, DataType.FLOAT);

        e = new Comparison(ComparisonOperation.NE, c1, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = new Comparison(ComparisonOperation.NE, c1, c2);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        // STRING
        c1 = Constant.create("test", DataType.STRING);
        c2 = Constant.create("tset", DataType.STRING);

        e = new Comparison(ComparisonOperation.NE, c1, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = new Comparison(ComparisonOperation.NE, c1, c2);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        // TIMESTAMP
        e = new Comparison(ComparisonOperation.NE, t1, t1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = new Comparison(ComparisonOperation.NE, t1, t2);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));
    }

    @Test
    public void testNENull() {
        Expression c = Constant.create(4, DataType.INTEGER);

        Expression e = new Comparison(ComparisonOperation.NE, c, nullExpr);
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = new Comparison(ComparisonOperation.NE, nullExpr, c);
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = new Comparison(ComparisonOperation.NE, nullExpr, nullExpr);
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testGT() {
        // INTEGER
        Expression c1 = Constant.create(3, DataType.INTEGER);
        Expression c2 = Constant.create(4, DataType.INTEGER);

        Expression e = new Comparison(ComparisonOperation.GT, c1, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = new Comparison(ComparisonOperation.GT, c1, c2);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = new Comparison(ComparisonOperation.GT, c2, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        // FLOAT
        c1 = Constant.create(4.3f, DataType.FLOAT);
        c2 = Constant.create(5.8f, DataType.FLOAT);

        e = new Comparison(ComparisonOperation.GT, c1, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = new Comparison(ComparisonOperation.GT, c1, c2);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = new Comparison(ComparisonOperation.GT, c2, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        // STRING
        c1 = Constant.create("test", DataType.STRING);
        c2 = Constant.create("tset", DataType.STRING);

        e = new Comparison(ComparisonOperation.GT, c1, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = new Comparison(ComparisonOperation.GT, c1, c2);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = new Comparison(ComparisonOperation.GT, c2, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        // TIMESTAMP
        e = new Comparison(ComparisonOperation.GT, t1, t1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = new Comparison(ComparisonOperation.GT, t1, t2);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = new Comparison(ComparisonOperation.GT, c2, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));
    }

    @Test
    public void testGTNull() {
        Expression c = Constant.create(4, DataType.INTEGER);

        Expression e = new Comparison(ComparisonOperation.GT, c, nullExpr);
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = new Comparison(ComparisonOperation.GT, nullExpr, c);
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = new Comparison(ComparisonOperation.GT, nullExpr, nullExpr);
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testGE() {
        // INTEGER
        Expression c1 = Constant.create(3, DataType.INTEGER);
        Expression c2 = Constant.create(4, DataType.INTEGER);

        Expression e = new Comparison(ComparisonOperation.GE, c1, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = new Comparison(ComparisonOperation.GE, c1, c2);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = new Comparison(ComparisonOperation.GE, c2, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        // FLOAT
        c1 = Constant.create(4.3f, DataType.FLOAT);
        c2 = Constant.create(5.8f, DataType.FLOAT);

        e = new Comparison(ComparisonOperation.GE, c1, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = new Comparison(ComparisonOperation.GE, c1, c2);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = new Comparison(ComparisonOperation.GE, c2, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        // STRING
        c1 = Constant.create("test", DataType.STRING);
        c2 = Constant.create("tset", DataType.STRING);

        e = new Comparison(ComparisonOperation.GE, c1, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = new Comparison(ComparisonOperation.GE, c1, c2);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = new Comparison(ComparisonOperation.GE, c2, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        // TIMESTAMP
        e = new Comparison(ComparisonOperation.GE, t1, t1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = new Comparison(ComparisonOperation.GE, t1, t2);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = new Comparison(ComparisonOperation.GE, c2, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));
    }

    @Test
    public void testGENull() {
        Expression c = Constant.create(4, DataType.INTEGER);

        Expression e = new Comparison(ComparisonOperation.GE, c, nullExpr);
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = new Comparison(ComparisonOperation.GE, nullExpr, c);
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = new Comparison(ComparisonOperation.GE, nullExpr, nullExpr);
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testLT() {
        // INTEGER
        Expression c1 = Constant.create(3, DataType.INTEGER);
        Expression c2 = Constant.create(4, DataType.INTEGER);

        Expression e = new Comparison(ComparisonOperation.LT, c1, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = new Comparison(ComparisonOperation.LT, c1, c2);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = new Comparison(ComparisonOperation.LT, c2, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        // FLOAT
        c1 = Constant.create(4.3f, DataType.FLOAT);
        c2 = Constant.create(5.8f, DataType.FLOAT);

        e = new Comparison(ComparisonOperation.LT, c1, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = new Comparison(ComparisonOperation.LT, c1, c2);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = new Comparison(ComparisonOperation.LT, c2, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        // STRING
        c1 = Constant.create("test", DataType.STRING);
        c2 = Constant.create("tset", DataType.STRING);

        e = new Comparison(ComparisonOperation.LT, c1, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = new Comparison(ComparisonOperation.LT, c1, c2);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = new Comparison(ComparisonOperation.LT, c2, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        // TIMESTAMP
        e = new Comparison(ComparisonOperation.LT, t1, t1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = new Comparison(ComparisonOperation.LT, t1, t2);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = new Comparison(ComparisonOperation.LT, c2, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));
    }

    @Test
    public void testLTNull() {
        Expression c = Constant.create(4, DataType.INTEGER);

        Expression e = new Comparison(ComparisonOperation.LT, c, nullExpr);
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = new Comparison(ComparisonOperation.LT, nullExpr, c);
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = new Comparison(ComparisonOperation.LT, nullExpr, nullExpr);
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testLE() {
        // INTEGER
        Expression c1 = Constant.create(3, DataType.INTEGER);
        Expression c2 = Constant.create(4, DataType.INTEGER);

        Expression e = new Comparison(ComparisonOperation.LE, c1, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = new Comparison(ComparisonOperation.LE, c1, c2);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = new Comparison(ComparisonOperation.LE, c2, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        // FLOAT
        c1 = Constant.create(4.3f, DataType.FLOAT);
        c2 = Constant.create(5.8f, DataType.FLOAT);

        e = new Comparison(ComparisonOperation.LE, c1, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = new Comparison(ComparisonOperation.LE, c1, c2);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = new Comparison(ComparisonOperation.LE, c2, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        // STRING
        c1 = Constant.create("test", DataType.STRING);
        c2 = Constant.create("tset", DataType.STRING);

        e = new Comparison(ComparisonOperation.LE, c1, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = new Comparison(ComparisonOperation.LE, c1, c2);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = new Comparison(ComparisonOperation.LE, c2, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        // TIMESTAMP
        e = new Comparison(ComparisonOperation.LE, t1, t1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = new Comparison(ComparisonOperation.LE, t1, t2);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = new Comparison(ComparisonOperation.LE, c2, c1);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));
    }

    @Test
    public void testLENull() {
        Expression c = Constant.create(4, DataType.INTEGER);

        Expression e = new Comparison(ComparisonOperation.LE, c, nullExpr);
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = new Comparison(ComparisonOperation.LE, nullExpr, c);
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = new Comparison(ComparisonOperation.LE, nullExpr, nullExpr);
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testBetween() {
        Expression min = Constant.create(-0.5f, DataType.FLOAT);
        Expression max = Constant.create(456f, DataType.FLOAT);
        Expression middle = Constant.create(23f, DataType.FLOAT);

        Expression e = new Between(middle, min, max);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.TRUE));

        e = new Between(min, min, max);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.TRUE));

        e = new Between(max, min, max);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.TRUE));

        e = new Between(min, middle, max);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));

        e = new Between(max, min, middle);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));
    }

    @Test
    public void testBetweenNull() {
        Expression c1 = Constant.create(12, DataType.INTEGER);
        Expression c2 = Constant.create(25, DataType.INTEGER);

        Expression e = new Between(nullExpr, c1, c2);
        assertThat(e.run(null, null), equalTo(LogicValue.UNKNOWN));

        e = new Between(c1, nullExpr, c2);
        assertThat(e.run(null, null), equalTo(LogicValue.UNKNOWN));

        e = new Between(c1, c2, nullExpr);
        assertThat(e.run(null, null), equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testIs() {
        Expression e = new Is(Constant.TRUE, LogicValue.TRUE);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.TRUE));

        e = new Is(Constant.TRUE, LogicValue.FALSE);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));

        e = new Is(Constant.TRUE, LogicValue.UNKNOWN);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));

        e = new Is(Constant.FALSE, LogicValue.TRUE);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));

        e = new Is(Constant.FALSE, LogicValue.FALSE);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.TRUE));

        e = new Is(Constant.FALSE, LogicValue.UNKNOWN);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));

        e = new Is(Constant.UNKNOWN, LogicValue.TRUE);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));

        e = new Is(Constant.UNKNOWN, LogicValue.FALSE);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));

        e = new Is(Constant.UNKNOWN, LogicValue.UNKNOWN);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.TRUE));

        e = new Is(Constant.NULL, LogicValue.TRUE);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.UNKNOWN));

        e = new Is(Constant.NULL, LogicValue.FALSE);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.UNKNOWN));

        e = new Is(Constant.NULL, LogicValue.UNKNOWN);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testLike() {
        Expression c = Constant.create("test", DataType.STRING);
        Expression e = new Like(c, "_est");
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.TRUE));

        c = Constant.create("interest", DataType.STRING);
        e = new Like(c, "inte[s-y]est");
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));
    }

    @Test
    public void testNullLike() {
        Expression e = new Like(nullExpr, "test");
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.UNKNOWN));
    }

}
