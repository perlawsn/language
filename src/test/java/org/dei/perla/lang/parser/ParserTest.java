package org.dei.perla.lang.parser;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.expression.AggregateOperation;
import org.dei.perla.lang.executor.expression.ComparisonOperation;
import org.dei.perla.lang.executor.expression.Expression;
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
    public void testLogicValue() throws Exception {
        Parser p = new Parser(new StringReader("true"));
        LogicValue l = p.LogicValue();
        assertThat(l, equalTo(LogicValue.TRUE));

        p = new Parser(new StringReader("false"));
        l = p.LogicValue();
        assertThat(l, equalTo(LogicValue.FALSE));

        p.ReInit(new StringReader("unknown"));
        l = p.LogicValue();
        assertThat(l, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testConstantBoolean() throws Exception {
        Parser p = new Parser(new StringReader("true"));
        boolean b = p.ConstantBoolean();
        assertTrue(b);

        p = new Parser(new StringReader("false"));
        b = p.ConstantBoolean();
        assertFalse(b);
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
        ConstantNode cn;

        p = new Parser(new StringReader("null"));
        e = p.Constant();
        assertTrue(n instanceof NullNode);

        p.ReInit(new StringReader("false"));
        n = p.Constant();
        assertTrue(n instanceof ConstantNode);
        cn = (ConstantNode) n;
        assertThat(cn.getType(), equalTo(DataType.BOOLEAN));
        assertTrue(cn.getValue() instanceof Boolean);
        assertThat(cn.getValue(), equalTo(false));

        p.ReInit(new StringReader("true"));
        n = p.Constant();
        assertTrue(n instanceof ConstantNode);
        cn = (ConstantNode) n;
        assertThat(cn.getType(), equalTo(DataType.BOOLEAN));
        assertTrue(cn.getValue() instanceof Boolean);
        assertThat(cn.getValue(), equalTo(true));

        p.ReInit(new StringReader("'test_string'"));
        n = p.Constant();
        assertTrue(n instanceof ConstantNode);
        cn = (ConstantNode) n;
        assertThat(cn.getType(), equalTo(DataType.STRING));
        assertTrue(cn.getValue() instanceof String);
        assertThat(cn.getValue(), equalTo("test_string"));

        p.ReInit(new StringReader("12"));
        n = p.Constant();
        assertTrue(n instanceof ConstantNode);
        cn = (ConstantNode) n;
        assertThat(cn.getType(), equalTo(DataType.INTEGER));
        assertTrue(cn.getValue() instanceof Integer);
        assertThat(cn.getValue(), equalTo(12));

        p.ReInit(new StringReader("1.0"));
        n = p.Constant();
        assertTrue(n instanceof ConstantNode);
        cn = (ConstantNode) n;
        assertThat(cn.getType(), equalTo(DataType.FLOAT));
        assertTrue(cn.getValue() instanceof Float);
        assertThat(cn.getValue(), equalTo(1.0f));
    }

    @Test
    public void testComparisonOperator() throws Exception {
        Parser p;
        ComparisonOperation op;

        p = new Parser(new StringReader(">"));
        op = p.ComparisonOperator();
        assertThat(op, equalTo(ComparisonOperation.GT));

        p.ReInit(new StringReader(">="));
        op = p.ComparisonOperator();
        assertThat(op, equalTo(ComparisonOperation.GE));

        p.ReInit(new StringReader("<"));
        op = p.ComparisonOperator();
        assertThat(op, equalTo(ComparisonOperation.LT));

        p.ReInit(new StringReader("<="));
        op = p.ComparisonOperator();
        assertThat(op, equalTo(ComparisonOperation.LE));

        p.ReInit(new StringReader("="));
        op = p.ComparisonOperator();
        assertThat(op, equalTo(ComparisonOperation.EQ));

        p.ReInit(new StringReader("!="));
        op = p.ComparisonOperator();
        assertThat(op, equalTo(ComparisonOperation.NE));

        p.ReInit(new StringReader("<>"));
        op = p.ComparisonOperator();
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
        op = p.AggregationOperator();
        assertThat(op, equalTo(AggregateOperation.MIN));

        p.ReInit(new StringReader("max"));
        op = p.AggregationOperator();
        assertThat(op, equalTo(AggregateOperation.MAX));

        p.ReInit(new StringReader("sum"));
        op = p.AggregationOperator();
        assertThat(op, equalTo(AggregateOperation.SUM));

        p.ReInit(new StringReader("avg"));
        op = p.AggregationOperator();
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
        Node n;
        Errors err = new Errors();

        p = new Parser(new StringReader("temperature"));
        n = p.PrimaryExpression(false, err);
        assertTrue(n instanceof FieldNode);
        assertThat(((FieldNode) n).getId(), equalTo("temperature"));

        p.ReInit(new StringReader("pressure"));
        n = p.PrimaryExpression(true, err);
        assertTrue(n instanceof FieldNode);
        assertThat(((FieldNode) n).getId(), equalTo("pressure"));
        // TODO: continue with the remaining expressions
    }

    @Test
    public void testAggregateCount() throws Exception {
        Parser p;
        Node n;
        AggregateNode an;
        Errors err = new Errors();

        p = new Parser(new StringReader("count(*, 10 samples)"));
        n = p.Aggregate(err);
        assertTrue(n instanceof AggregateNode);
        an = (AggregateNode) n;
        assertThat(an.getAggregation(), equalTo(AggregateOperation.COUNT));
        assertThat(an.getOperand(), nullValue());
        assertThat(an.getWindowSize(), equalTo(new WindowSize(10)));
        assertThat(an.getFilter(), nullValue());

        p = new Parser(new StringReader("count(*, 10 seconds, true)"));
        n = p.Aggregate(err);
        assertTrue(n instanceof AggregateNode);
        an = (AggregateNode) n;
        assertThat(an.getAggregation(), equalTo(AggregateOperation.COUNT));
        assertThat(an.getOperand(), nullValue());
        assertThat(an.getWindowSize(),
                equalTo(new WindowSize(Duration.ofSeconds(10))));
    }

}
