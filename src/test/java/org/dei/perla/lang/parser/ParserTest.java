package org.dei.perla.lang.parser;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.expression.*;
import org.dei.perla.lang.executor.statement.WindowSize;
import org.junit.Test;

import java.io.StringReader;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

/**
 * @author Guido Rota 04/03/15.
 */
public class ParserTest {

    @Test
    public void testSign() throws Exception {
        Parser p = new Parser(new StringReader("+"));
        Sign s = p.Sign();
        assertThat(s, equalTo(Sign.PLUS));

        p = new Parser(new StringReader("-"));
        s = p.Sign();
        assertThat(s, equalTo(Sign.MINUS));
    }

    @Test
    public void testConstantBoolean() throws Exception {
        Parser p = new Parser(new StringReader("true"));
        LogicValue b = p.ConstantBoolean();
        assertThat(b, equalTo(LogicValue.TRUE));

        p = new Parser(new StringReader("false"));
        b = p.ConstantBoolean();
        assertThat(b, equalTo(LogicValue.FALSE));
    }

    @Test
    public void testConstantFloat() throws Exception {
        Parser p = new Parser(new StringReader("12.0"));
        float f = p.ConstantFloat();
        assertThat(f, equalTo(12f));

        p = new Parser(new StringReader("43.9586"));
        f = p.ConstantFloat();
        assertThat(f, equalTo(43.9586f));
    }

    @Test
    public void testConstantInteger() throws Exception {
        Parser p = new Parser(new StringReader("12"));
        int i = p.ConstantInteger();
        assertThat(i, equalTo(12));

        p = new Parser(new StringReader("45"));
        i = p.ConstantInteger();
        assertThat(i, equalTo(45));

        p.ReInit(new StringReader("0x12"));
        i = p.ConstantInteger();
        assertThat(i, equalTo(18));
    }

    @Test
    public void testConstantString() throws Exception {
        Parser p = new Parser(new StringReader("\"test\""));
        String s = p.ConstantString();
        assertThat(s, equalTo("test"));

        p = new Parser(new StringReader("'test'"));
        s = p.ConstantString();
        assertThat(s, equalTo("test"));
    }

    @Test
    public void testConstant() throws Exception {
        Parser p;
        Expression e;

        p = new Parser(new StringReader("null"));
        e = p.Constant();
        assertTrue(e instanceof Null);

        p.ReInit(new StringReader("false"));
        e = p.Constant();
        assertTrue(e instanceof Constant);
        Constant c = (Constant) e;
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.FALSE));

        p.ReInit(new StringReader("true"));
        e = p.Constant();
        assertTrue(e instanceof Constant);
        c = (Constant) e;
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.TRUE));

        p.ReInit(new StringReader("'test_string'"));
        e = p.Constant();
        assertTrue(e instanceof Constant);
        c = (Constant) e;
        assertThat(c.getType(), equalTo(DataType.STRING));
        assertTrue(c.getValue() instanceof String);
        assertThat(c.getValue(), equalTo("test_string"));

        p.ReInit(new StringReader("12"));
        e = p.Constant();
        assertTrue(e instanceof Constant);
        c = (Constant) e;
        assertThat(c.getType(), equalTo(DataType.INTEGER));
        assertTrue(c.getValue() instanceof Integer);
        assertThat(c.getValue(), equalTo(12));

        p.ReInit(new StringReader("1.0"));
        e = p.Constant();
        assertTrue(e instanceof Constant);
        c = (Constant) e;
        assertThat(c.getType(), equalTo(DataType.FLOAT));
        assertTrue(c.getValue() instanceof Float);
        assertThat(c.getValue(), equalTo(1.0f));
    }

    @Test
    public void testComparisonOperator() throws Exception {
        Parser p;
        ComparisonOperation op;

        p = new Parser(new StringReader(">"));
        op = p.ComparisonOperation();
        assertThat(op, equalTo(ComparisonOperation.GT));

        p.ReInit(new StringReader(">="));
        op = p.ComparisonOperation();
        assertThat(op, equalTo(ComparisonOperation.GE));

        p.ReInit(new StringReader("<"));
        op = p.ComparisonOperation();
        assertThat(op, equalTo(ComparisonOperation.LT));

        p.ReInit(new StringReader("<="));
        op = p.ComparisonOperation();
        assertThat(op, equalTo(ComparisonOperation.LE));

        p.ReInit(new StringReader("="));
        op = p.ComparisonOperation();
        assertThat(op, equalTo(ComparisonOperation.EQ));

        p.ReInit(new StringReader("!="));
        op = p.ComparisonOperation();
        assertThat(op, equalTo(ComparisonOperation.NE));

        p.ReInit(new StringReader("<>"));
        op = p.ComparisonOperation();
        assertThat(op, equalTo(ComparisonOperation.NE));
    }

    @Test
    public void testFieldType() throws Exception {
        Parser p;
        DataType d;

        p = new Parser(new StringReader("id"));
        d = p.FieldType();
        assertThat(d, equalTo(DataType.ID));

        p.ReInit(new StringReader("timestamp"));
        d = p.FieldType();
        assertThat(d, equalTo(DataType.TIMESTAMP));

        p.ReInit(new StringReader("boolean"));
        d = p.FieldType();
        assertThat(d, equalTo(DataType.BOOLEAN));

        p.ReInit(new StringReader("integer"));
        d = p.FieldType();
        assertThat(d, equalTo(DataType.INTEGER));

        p.ReInit(new StringReader("float"));
        d = p.FieldType();
        assertThat(d, equalTo(DataType.FLOAT));

        p.ReInit(new StringReader("string"));
        d = p.FieldType();
        assertThat(d, equalTo(DataType.STRING));
    }

    @Test
    public void testTimeUnit() throws Exception {
        Parser p;
        TemporalUnit t;

        p = new Parser(new StringReader("seconds"));
        t = p.TimeUnit();
        assertThat(t, equalTo(ChronoUnit.SECONDS));
        p.ReInit(new StringReader("s"));
        t = p.TimeUnit();
        assertThat(t, equalTo(ChronoUnit.SECONDS));

        p.ReInit(new StringReader("minutes"));
        t = p.TimeUnit();
        assertThat(t, equalTo(ChronoUnit.MINUTES));
        p.ReInit(new StringReader("m"));
        t = p.TimeUnit();
        assertThat(t, equalTo(ChronoUnit.MINUTES));

        p.ReInit(new StringReader("hours"));
        t = p.TimeUnit();
        assertThat(t, equalTo(ChronoUnit.HOURS));
        p.ReInit(new StringReader("h"));
        t = p.TimeUnit();
        assertThat(t, equalTo(ChronoUnit.HOURS));

        p.ReInit(new StringReader("milliseconds"));
        t = p.TimeUnit();
        assertThat(t, equalTo(ChronoUnit.MILLIS));
        p.ReInit(new StringReader("ms"));
        t = p.TimeUnit();
        assertThat(t, equalTo(ChronoUnit.MILLIS));

        p.ReInit(new StringReader("days"));
        t = p.TimeUnit();
        assertThat(t, equalTo(ChronoUnit.DAYS));
        p.ReInit(new StringReader("d"));
        t = p.TimeUnit();
        assertThat(t, equalTo(ChronoUnit.DAYS));
    }

    @Test
    public void testAggregationOperation() throws Exception {
        Parser p;
        AggregateOperation op;

        p = new Parser(new StringReader("min"));
        op = p.AggregateOperation();
        assertThat(op, equalTo(AggregateOperation.MIN));

        p.ReInit(new StringReader("max"));
        op = p.AggregateOperation();
        assertThat(op, equalTo(AggregateOperation.MAX));

        p.ReInit(new StringReader("sum"));
        op = p.AggregateOperation();
        assertThat(op, equalTo(AggregateOperation.SUM));

        p.ReInit(new StringReader("avg"));
        op = p.AggregateOperation();
        assertThat(op, equalTo(AggregateOperation.AVG));
    }

    @Test
    public void testDuration() throws Exception {
        Parser p;
        Duration d;

        p = new Parser(new StringReader("10 seconds"));
        d = p.Duration();
        assertThat(d, equalTo(Duration.ofSeconds(10)));

        p.ReInit(new StringReader("10 s"));
        d = p.Duration();
        assertThat(d, equalTo(Duration.ofSeconds(10)));

        p.ReInit(new StringReader("24 h"));
        d = p.Duration();
        assertThat(d, equalTo(Duration.ofHours(24)));
    }

    @Test(expected = ParseException.class)
    public void testNegativeDuration() throws Exception {
        Parser p;

        p = new Parser(new StringReader("-24 hours"));
        p.Duration();
    }

    @Test
    public void testSamplesNumber() throws Exception {
        Parser p;
        int s;

        p = new Parser(new StringReader("32 samples"));
        s = p.SamplesNumber();
        assertThat(s, equalTo(32));

        p.ReInit(new StringReader("2 samples"));
        s = p.SamplesNumber();
        assertThat(s, equalTo(2));

        p.ReInit(new StringReader("one"));
        s = p.SamplesNumber();
        assertThat(s, equalTo(1));
    }

    @Test(expected = ParseException.class)
    public void testNegativeSamplesNumber() throws Exception {
        Parser p;

        p = new Parser(new StringReader("-12 samples"));
        p.SamplesNumber();
    }

    @Test
    public void testSelectionsNumber() throws Exception {
        Parser p;
        int s;
        Errors err = new Errors();

        p = new Parser(new StringReader("12 selections"));
        s = p.SelectionsNumber(err);
        assertThat(s, equalTo(12));
        assertTrue(err.isEmpty());

        p.ReInit(new StringReader("1 selections"));
        s = p.SelectionsNumber(err);
        assertThat(s, equalTo(1));
        assertTrue(err.isEmpty());
    }

    @Test(expected = ParseException.class)
    public void testNegativeSelectionsNumber() throws Exception {
        Parser p;
        Errors err = new Errors();

        p = new Parser(new StringReader("-1 selections"));
        p.SelectionsNumber(err);
    }

    @Test
    public void testZeroSelectionsNumber() throws Exception {
        Parser p;
        Errors err = new Errors();

        p = new Parser(new StringReader("0 selections"));
        p.SelectionsNumber(err);
        assertFalse(err.isEmpty());
    }

    @Test
    public void testWindowSize() throws Exception {
        Parser p;
        WindowSize ws;
        Errors err = new Errors();

        p = new Parser(new StringReader("10 seconds"));
        ws = p.WindowSize(err);
        assertTrue(err.isEmpty());
        assertThat(ws.getDuration(), equalTo(Duration.ofSeconds(10)));

        p.ReInit(new StringReader("2 samples"));
        ws = p.WindowSize(err);
        assertTrue(err.isEmpty());
        assertThat(ws.getSamples(), equalTo(2));

        p.ReInit(new StringReader("one"));
        ws = p.WindowSize(err);
        assertTrue(err.isEmpty());
        assertThat(ws.getSamples(), equalTo(1));
    }

    @Test
    public void testZeroWindowSize() throws Exception {
        Parser p;
        Errors err = new Errors();

        p = new Parser(new StringReader("0 days"));
        p.WindowSize(err);
        assertFalse(err.isEmpty());

        err = new Errors();
        p.ReInit(new StringReader("0 samples"));
        p.WindowSize(err);
        assertFalse(err.isEmpty());
    }

    @Test
    public void testIdentifier() throws Exception {
        Parser p;
        String id;

        p = new Parser(new StringReader("temperature"));
        id = p.Identifier();
        assertThat(id, equalTo("temperature"));

        p.ReInit(new StringReader("pressure"));
        id = p.Identifier();
        assertThat(id, equalTo("pressure"));
    }

    @Test(expected = ParseException.class)
    public void testKeywordIdentifier() throws Exception {
        Parser p;

        p = new Parser(new StringReader("select"));
        p.Identifier();
    }

    @Test
    public void testPrimaryExpression() throws Exception {
        Parser p;
        Expression e;
        Errors err = new Errors();

        p = new Parser(new StringReader("temperature"));
        e = p.PrimaryExpression(false, err);
        assertTrue(e instanceof Field);
        assertThat(((Field) e).getId(), equalTo("temperature"));

        p.ReInit(new StringReader("pressure"));
        e = p.PrimaryExpression(true, err);
        assertTrue(e instanceof Field);
        assertThat(((Field) e).getId(), equalTo("pressure"));
        // TODO: continue with the remaining expressions
    }

    @Test
    public void testAggregateCount() throws Exception {
        Parser p;
        Expression e;
        Aggregate agg;
        Errors err = new Errors();

        p = new Parser(new StringReader("count(*, 10 samples)"));
        e = p.Aggregate(err);
        assertTrue(e instanceof CountAggregate);
        agg = (Aggregate) e;
        assertThat(agg.getOperand(), nullValue());
        assertThat(agg.getWindowSize(), equalTo(new WindowSize(10)));
        assertThat(agg.getFilter(), nullValue());

        p.ReInit(new StringReader("count(*, 10 seconds, true)"));
        e = p.Aggregate(err);
        assertTrue(e instanceof CountAggregate);
        agg = (Aggregate) e;
        assertThat(agg.getOperand(), nullValue());
        assertThat(agg.getWindowSize(),
                equalTo(new WindowSize(Duration.ofSeconds(10))));
    }

    @Test
    public void testArithmeticFactor() throws Exception {
        Parser p;
        Expression e;
        Errors err = new Errors();

        p = new Parser(new StringReader("-10"));
        e = p.ArithmeticFactor(false, err);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Constant);
        assertThat(((Constant) e).getValue(), equalTo(-10));

        p.ReInit(new StringReader("-temperature"));
        e = p.ArithmeticFactor(false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Inverse);
    }

    @Test
    public void testArithmeticTerm() throws Exception {
        Parser p;
        Expression e;
        Errors err = new Errors();

        p = new Parser(new StringReader("pressure"));
        e = p.ArithmeticTerm(false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Field);
        assertThat(((Field) e).getId(), equalTo("pressure"));

        p.ReInit(new StringReader("10 * -32"));
        e = p.ArithmeticTerm(false, err);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Constant);
        assertThat(((Constant) e).getValue(), equalTo(-320));

        p.ReInit(new StringReader("100 / 10"));
        e = p.ArithmeticTerm(false, err);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Constant);
        assertThat(((Constant) e).getValue(), equalTo(10));

        p.ReInit(new StringReader("temperature * 10"));
        e = p.ArithmeticTerm(false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Arithmetic);
        assertThat(((Arithmetic) e).getOperation(),
                equalTo(ArithmeticOperation.PRODUCT));

        p.ReInit(new StringReader("23 / pressure"));
        e = p.ArithmeticTerm(false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Arithmetic);
        assertThat(((Arithmetic) e).getOperation(),
                equalTo(ArithmeticOperation.DIVISION));

        p.ReInit(new StringReader("23 % pressure"));
        e = p.ArithmeticTerm(false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Arithmetic);
        assertThat(((Arithmetic) e).getOperation(),
                equalTo(ArithmeticOperation.MODULO));

        p.ReInit(new StringReader("23 * pressure / 25"));
        e = p.ArithmeticTerm(false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Arithmetic);
        assertThat(((Arithmetic) e).getOperation(),
                equalTo(ArithmeticOperation.DIVISION));
    }

    @Test
    public void testArithmeticExpression() throws Exception {
        Parser p;
        Expression e;
        Errors err = new Errors();

        p = new Parser(new StringReader("pressure"));
        e = p.ArithmeticExpression(false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Field);
        assertThat(((Field) e).getId(), equalTo("pressure"));

        p.ReInit(new StringReader("10 + -32"));
        e = p.ArithmeticExpression(false, err);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Constant);
        assertThat(((Constant) e).getValue(), equalTo(-22));

        p.ReInit(new StringReader("100 - 10"));
        e = p.ArithmeticExpression(false, err);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Constant);
        assertThat(((Constant) e).getValue(), equalTo(90));

        p.ReInit(new StringReader("temperature + 10"));
        e = p.ArithmeticExpression(false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Arithmetic);
        assertThat(((Arithmetic) e).getOperation(),
                equalTo(ArithmeticOperation.ADDITION));

        p.ReInit(new StringReader("23 - pressure"));
        e = p.ArithmeticExpression(false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Arithmetic);
        assertThat(((Arithmetic) e).getOperation(),
                equalTo(ArithmeticOperation.SUBTRACTION));

        p.ReInit(new StringReader("11 - 23 + pressure / 25"));
        e = p.ArithmeticExpression(false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Arithmetic);
        assertThat(((Arithmetic) e).getOperation(),
                equalTo(ArithmeticOperation.ADDITION));
    }

    @Test
    public void testBitwiseShift() throws Exception {
        Parser p;
        Expression e;
        Errors err = new Errors();

        p = new Parser(new StringReader("pressure"));
        e = p.BitwiseShift(false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Field);
        assertThat(((Field) e).getId(), equalTo("pressure"));

        p.ReInit(new StringReader("10 << 32"));
        e = p.BitwiseShift(false, err);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Constant);
        assertThat(((Constant) e).getValue(), equalTo(10 << 32));

        p.ReInit(new StringReader("100 >> 10"));
        e = p.BitwiseShift(false, err);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Constant);
        assertThat(((Constant) e).getValue(), equalTo(100 >> 10));

        p.ReInit(new StringReader("temperature << 10"));
        e = p.BitwiseShift(false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Bitwise);
        assertThat(((Bitwise) e).getOperation(),
                equalTo(BitwiseOperation.LSH));

        p.ReInit(new StringReader("23 >> pressure"));
        e = p.BitwiseShift(false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Bitwise);
        assertThat(((Bitwise) e).getOperation(),
                equalTo(BitwiseOperation.RSH));

        p.ReInit(new StringReader("11 << pressure / 25"));
        e = p.BitwiseShift(false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Bitwise);
        assertThat(((Bitwise) e).getOperation(),
                equalTo(BitwiseOperation.LSH));
    }

    @Test
    public void testBitwiseFactor() throws Exception {
        Parser p;
        Expression e;
        Errors err = new Errors();

        p = new Parser(new StringReader("pressure"));
        e = p.BitwiseFactor(false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Field);
        assertThat(((Field) e).getId(), equalTo("pressure"));

        p.ReInit(new StringReader("10 ^ 32"));
        e = p.BitwiseFactor(false, err);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Constant);
        assertThat(((Constant) e).getValue(), equalTo(10 ^ 32));

        p.ReInit(new StringReader("temperature ^ 10"));
        e = p.BitwiseFactor(false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Bitwise);
        assertThat(((Bitwise) e).getOperation(),
                equalTo(BitwiseOperation.XOR));

        p.ReInit(new StringReader("11 ^ pressure - 25"));
        e = p.BitwiseFactor(false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Bitwise);
        assertThat(((Bitwise) e).getOperation(),
                equalTo(BitwiseOperation.XOR));
    }

    @Test
    public void testBitwiseTerm() throws Exception {
        Parser p;
        Expression e;
        Errors err = new Errors();

        p = new Parser(new StringReader("light"));
        e = p.BitwiseTerm(false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Field);
        assertThat(((Field) e).getId(), equalTo("light"));

        p.ReInit(new StringReader("10 & 32"));
        e = p.BitwiseTerm(false, err);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Constant);
        assertThat(((Constant) e).getValue(), equalTo(10 & 32));

        p.ReInit(new StringReader("temperature & 10"));
        e = p.BitwiseTerm(false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Bitwise);
        assertThat(((Bitwise) e).getOperation(),
                equalTo(BitwiseOperation.AND));

        p.ReInit(new StringReader("23 & pressure * 74"));
        e = p.BitwiseTerm(false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Bitwise);
        assertThat(((Bitwise) e).getOperation(),
                equalTo(BitwiseOperation.AND));
    }

    @Test
    public void testBitwiseExpression() throws Exception {
        Parser p;
        Expression e;
        Errors err = new Errors();

        p = new Parser(new StringReader("sound_level"));
        e = p.BitwiseExpression(false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Field);
        assertThat(((Field) e).getId(), equalTo("sound_level"));

        p.ReInit(new StringReader("10 | 32"));
        e = p.BitwiseExpression(false, err);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Constant);
        assertThat(((Constant) e).getValue(), equalTo(10 | 32));

        p.ReInit(new StringReader("temperature | 10"));
        e = p.BitwiseExpression(false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Bitwise);
        assertThat(((Bitwise) e).getOperation(),
                equalTo(BitwiseOperation.OR));

        p.ReInit(new StringReader("23 | pressure & 74"));
        e = p.BitwiseExpression(false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Bitwise);
        assertThat(((Bitwise) e).getOperation(),
                equalTo(BitwiseOperation.OR));
    }

    @Test
    public void testComparisonExpression() throws Exception {
        Parser p;
        Expression e;
        Errors err = new Errors();

        p = new Parser(new StringReader("pressure"));
        e = p.Comparison(false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Field);
        assertThat(((Field) e).getId(), equalTo("pressure"));

        p.ReInit(new StringReader("10 < 32"));
        e = p.Comparison(false, err);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Constant);
        assertThat(((Constant) e).getValue(), equalTo(true));

        p.ReInit(new StringReader("1 > 10"));
        e = p.Comparison(false, err);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Constant);
        assertThat(((Constant) e).getValue(), equalTo(false));

        p.ReInit(new StringReader("temperature < 10"));
        e = p.Comparison(false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Comparison);
        assertThat(((Comparison) e).getOperation(),
                equalTo(ComparisonOperation.LT));

        p.ReInit(new StringReader("23 <= pressure"));
        e = p.Comparison(false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Comparison);
        assertThat(((Comparison) e).getOperation(),
                equalTo(ComparisonOperation.LE));

        p.ReInit(new StringReader("23 > pressure"));
        e = p.Comparison(false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Comparison);
        assertThat(((Comparison) e).getOperation(),
                equalTo(ComparisonOperation.GT));

        p.ReInit(new StringReader("23 >= pressure"));
        e = p.Comparison(false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Comparison);
        assertThat(((Comparison) e).getOperation(),
                equalTo(ComparisonOperation.GE));

        p.ReInit(new StringReader("23 = pressure"));
        e = p.Comparison(false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Comparison);
        assertThat(((Comparison) e).getOperation(),
                equalTo(ComparisonOperation.EQ));

        p.ReInit(new StringReader("23 != pressure"));
        e = p.Comparison(false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Comparison);
        assertThat(((Comparison) e).getOperation(),
                equalTo(ComparisonOperation.NE));

        p.ReInit(new StringReader("23 <> pressure"));
        e = p.Comparison(false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(err.isEmpty());
        assertTrue(e instanceof Comparison);
        assertThat(((Comparison) e).getOperation(),
                equalTo(ComparisonOperation.NE));
    }

    @Test
    public void testBetween() throws Exception {
        Parser p;
        Expression e;
        Errors err = new Errors();
        Constant c = new Constant(12, DataType.INTEGER);
        Field f = new Field("test");

        p = new Parser(new StringReader("between 0 and 43"));
        e = p.Between(c, false, err);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e instanceof Constant);
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.TRUE));

        p.ReInit(new StringReader("between -12 and 45"));
        e = p.Between(f, false, err);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e instanceof Between);
    }

}
