package org.dei.perla.lang.query.parser;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.registry.DataTemplate;
import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.query.expression.*;
import org.dei.perla.lang.query.statement.*;
import org.dei.perla.lang.query.statement.Refresh.RefreshType;
import org.dei.perla.lang.query.statement.WindowSize.WindowType;
import org.junit.Test;

import java.io.StringReader;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * @author Guido Rota 04/03/15.
 */
public class ParserTest {

    private static final Attribute powerAtt =
            Attribute.create("power", DataType.INTEGER);
    private static final Attribute alertAtt =
            Attribute.create("alert", DataType.BOOLEAN);
    private static final Attribute lowPowerAtt =
            Attribute.create("low_power", DataType.BOOLEAN);

    private static final List<Attribute> atts;
    static {
        atts = Arrays.asList(new Attribute[]{
                powerAtt,
                alertAtt,
                lowPowerAtt
        });
    }

    private static final Object[][] samples;
    static {
        samples = new Object[4][];
        samples[0] = new Object[1];
        samples[0][0] = 94;
        samples[1] = new Object[1];
        samples[1][0] = 75;
        samples[2] = new Object[1];
        samples[2][0] =  50;
        samples[3] = new Object[1];
        samples[3][0] = 30;
    }

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

        p = new Parser(new StringReader("false"));
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
    public void testType() throws Exception {
        Parser p;
        DataType d;

        p = new Parser(new StringReader("id"));
        d = p.Type();
        assertThat(d, equalTo(DataType.ID));

        p.ReInit(new StringReader("timestamp"));
        d = p.Type();
        assertThat(d, equalTo(DataType.TIMESTAMP));

        p.ReInit(new StringReader("boolean"));
        d = p.Type();
        assertThat(d, equalTo(DataType.BOOLEAN));

        p.ReInit(new StringReader("integer"));
        d = p.Type();
        assertThat(d, equalTo(DataType.INTEGER));

        p.ReInit(new StringReader("float"));
        d = p.Type();
        assertThat(d, equalTo(DataType.FLOAT));

        p.ReInit(new StringReader("string"));
        d = p.Type();
        assertThat(d, equalTo(DataType.STRING));
    }

    @Test
    public void testTypeClass() throws Exception {
        Parser p;
        TypeClass c;

        p = new Parser(new StringReader("id"));
        c = p.TypeClass();
        assertThat(c, equalTo(TypeClass.ID));

        p.ReInit(new StringReader("timestamp"));
        c = p.TypeClass();
        assertThat(c, equalTo(TypeClass.TIMESTAMP));

        p.ReInit(new StringReader("boolean"));
        c = p.TypeClass();
        assertThat(c, equalTo(TypeClass.BOOLEAN));

        p.ReInit(new StringReader("integer"));
        c = p.TypeClass();
        assertThat(c, equalTo(TypeClass.INTEGER));

        p.ReInit(new StringReader("float"));
        c = p.TypeClass();
        assertThat(c, equalTo(TypeClass.FLOAT));

        p.ReInit(new StringReader("string"));
        c = p.TypeClass();
        assertThat(c, equalTo(TypeClass.STRING));
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
        assertThat(ws.getType(), equalTo(WindowType.TIME));
        assertThat(ws.getDuration(), equalTo(Duration.ofSeconds(10)));

        p.ReInit(new StringReader("2 samples"));
        ws = p.WindowSize(err);
        assertTrue(err.isEmpty());
        assertThat(ws.getType(), equalTo(WindowType.SAMPLE));
        assertThat(ws.getSamples(), equalTo(2));

        p.ReInit(new StringReader("one"));
        ws = p.WindowSize(err);
        assertTrue(err.isEmpty());
        assertThat(ws.getType(), equalTo(WindowType.SAMPLE));
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
        Set<String> ids = new TreeSet<>();

        p = new Parser(new StringReader("temperature"));
        e = p.PrimaryExpression(ExpressionType.SIMPLE, err, ids);
        assertTrue(e instanceof Field);
        assertThat(((Field) e).getId(), equalTo("temperature"));

        p.ReInit(new StringReader("pressure"));
        e = p.PrimaryExpression(ExpressionType.AGGREGATE, err, ids);
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
        Set<String> ids = new TreeSet<>();

        p = new Parser(new StringReader("count(*, 10 samples)"));
        e = p.Aggregate(err, ids);
        assertTrue(e instanceof CountAggregate);
        agg = (Aggregate) e;
        assertThat(agg.getOperand(), equalTo(Constant.NULL));
        assertThat(agg.getWindowSize(), equalTo(new WindowSize(10)));
        assertThat(agg.getFilter(), equalTo(Constant.TRUE));

        p.ReInit(new StringReader("count(*, 10 seconds, true)"));
        e = p.Aggregate(err, ids);
        assertTrue(e instanceof CountAggregate);
        agg = (Aggregate) e;
        assertThat(agg.getOperand(), equalTo(Constant.NULL));
        assertThat(agg.getWindowSize(),
                equalTo(new WindowSize(Duration.ofSeconds(10))));
    }

    @Test
    public void testArithmeticFactor() throws Exception {
        Parser p;
        Expression e;
        Errors err = new Errors();
        Set<String> ids = new TreeSet<>();

        p = new Parser(new StringReader("-10"));
        e = p.ArithmeticFactor(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertTrue(e instanceof Constant);
        assertThat(((Constant) e).getValue(), equalTo(-10));

        p.ReInit(new StringReader("-temperature"));
        e = p.ArithmeticFactor(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Inverse);
    }

    @Test
    public void testArithmeticTerm() throws Exception {
        Parser p;
        Expression e;
        Errors err = new Errors();
        Set<String> ids = new TreeSet<>();

        p = new Parser(new StringReader("pressure"));
        e = p.ArithmeticTerm(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Field);
        assertThat(((Field) e).getId(), equalTo("pressure"));

        p.ReInit(new StringReader("10 * -32"));
        e = p.ArithmeticTerm(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertTrue(e instanceof Constant);
        assertThat(((Constant) e).getValue(), equalTo(-320));

        p.ReInit(new StringReader("100 / 10"));
        e = p.ArithmeticTerm(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertTrue(e instanceof Constant);
        assertThat(((Constant) e).getValue(), equalTo(10));

        p.ReInit(new StringReader("temperature * 10"));
        e = p.ArithmeticTerm(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Arithmetic);
        assertThat(((Arithmetic) e).getOperation(),
                equalTo(ArithmeticOperation.PRODUCT));

        p.ReInit(new StringReader("23 / pressure"));
        e = p.ArithmeticTerm(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Arithmetic);
        assertThat(((Arithmetic) e).getOperation(),
                equalTo(ArithmeticOperation.DIVISION));

        p.ReInit(new StringReader("23 % pressure"));
        e = p.ArithmeticTerm(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Arithmetic);
        assertThat(((Arithmetic) e).getOperation(),
                equalTo(ArithmeticOperation.MODULO));

        p.ReInit(new StringReader("23 * pressure / 25"));
        e = p.ArithmeticTerm(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Arithmetic);
        assertThat(((Arithmetic) e).getOperation(),
                equalTo(ArithmeticOperation.DIVISION));
    }

    @Test
    public void testArithmeticExpression() throws Exception {
        Parser p;
        Expression e;
        Errors err = new Errors();
        Set<String> ids = new TreeSet<>();

        p = new Parser(new StringReader("pressure"));
        e = p.ArithmeticExpression(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Field);
        assertThat(((Field) e).getId(), equalTo("pressure"));

        p.ReInit(new StringReader("10 + -32"));
        e = p.ArithmeticExpression(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertTrue(e instanceof Constant);
        assertThat(((Constant) e).getValue(), equalTo(-22));

        p.ReInit(new StringReader("100 - 10"));
        e = p.ArithmeticExpression(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertTrue(e instanceof Constant);
        assertThat(((Constant) e).getValue(), equalTo(90));

        p.ReInit(new StringReader("temperature + 10"));
        e = p.ArithmeticExpression(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Arithmetic);
        assertThat(((Arithmetic) e).getOperation(),
                equalTo(ArithmeticOperation.ADDITION));

        p.ReInit(new StringReader("23 - pressure"));
        e = p.ArithmeticExpression(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Arithmetic);
        assertThat(((Arithmetic) e).getOperation(),
                equalTo(ArithmeticOperation.SUBTRACTION));

        p.ReInit(new StringReader("11 - 23 + pressure / 25"));
        e = p.ArithmeticExpression(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Arithmetic);
        assertThat(((Arithmetic) e).getOperation(),
                equalTo(ArithmeticOperation.ADDITION));
    }

    @Test
    public void testBitwiseShift() throws Exception {
        Parser p;
        Expression e;
        Errors err = new Errors();
        Set<String> ids = new TreeSet<>();

        p = new Parser(new StringReader("pressure"));
        e = p.BitwiseShift(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Field);
        assertThat(((Field) e).getId(), equalTo("pressure"));

        p.ReInit(new StringReader("10 << 32"));
        e = p.BitwiseShift(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertTrue(e instanceof Constant);
        assertThat(((Constant) e).getValue(), equalTo(10 << 32));

        p.ReInit(new StringReader("100 >> 10"));
        e = p.BitwiseShift(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertTrue(e instanceof Constant);
        assertThat(((Constant) e).getValue(), equalTo(100 >> 10));

        p.ReInit(new StringReader("temperature << 10"));
        e = p.BitwiseShift(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Bitwise);
        assertThat(((Bitwise) e).getOperation(),
                equalTo(BitwiseOperation.LSH));

        p.ReInit(new StringReader("23 >> pressure"));
        e = p.BitwiseShift(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Bitwise);
        assertThat(((Bitwise) e).getOperation(),
                equalTo(BitwiseOperation.RSH));

        p.ReInit(new StringReader("11 << pressure / 25"));
        e = p.BitwiseShift(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Bitwise);
        assertThat(((Bitwise) e).getOperation(),
                equalTo(BitwiseOperation.LSH));
    }

    @Test
    public void testBitwiseFactor() throws Exception {
        Parser p;
        Expression e;
        Errors err = new Errors();
        Set<String> ids = new TreeSet<>();

        p = new Parser(new StringReader("pressure"));
        e = p.BitwiseFactor(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Field);
        assertThat(((Field) e).getId(), equalTo("pressure"));

        p.ReInit(new StringReader("10 ^ 32"));
        e = p.BitwiseFactor(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertTrue(e instanceof Constant);
        assertThat(((Constant) e).getValue(), equalTo(10 ^ 32));

        p.ReInit(new StringReader("temperature ^ 10"));
        e = p.BitwiseFactor(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Bitwise);
        assertThat(((Bitwise) e).getOperation(),
                equalTo(BitwiseOperation.XOR));

        p.ReInit(new StringReader("11 ^ pressure - 25"));
        e = p.BitwiseFactor(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Bitwise);
        assertThat(((Bitwise) e).getOperation(),
                equalTo(BitwiseOperation.XOR));
    }

    @Test
    public void testBitwiseTerm() throws Exception {
        Parser p;
        Expression e;
        Errors err = new Errors();
        Set<String> ids = new TreeSet<>();

        p = new Parser(new StringReader("light"));
        e = p.BitwiseTerm(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Field);
        assertThat(((Field) e).getId(), equalTo("light"));

        p.ReInit(new StringReader("10 & 32"));
        e = p.BitwiseTerm(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertTrue(e instanceof Constant);
        assertThat(((Constant) e).getValue(), equalTo(10 & 32));

        p.ReInit(new StringReader("temperature & 10"));
        e = p.BitwiseTerm(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Bitwise);
        assertThat(((Bitwise) e).getOperation(),
                equalTo(BitwiseOperation.AND));

        p.ReInit(new StringReader("23 & pressure * 74"));
        e = p.BitwiseTerm(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Bitwise);
        assertThat(((Bitwise) e).getOperation(),
                equalTo(BitwiseOperation.AND));
    }

    @Test
    public void testBitwiseExpression() throws Exception {
        Parser p;
        Expression e;
        Errors err = new Errors();
        Set<String> ids = new TreeSet<>();

        p = new Parser(new StringReader("sound_level"));
        e = p.BitwiseExpression(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Field);
        assertThat(((Field) e).getId(), equalTo("sound_level"));

        p.ReInit(new StringReader("10 | 32"));
        e = p.BitwiseExpression(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertTrue(e instanceof Constant);
        assertThat(((Constant) e).getValue(), equalTo(10 | 32));

        p.ReInit(new StringReader("temperature | 10"));
        e = p.BitwiseExpression(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Bitwise);
        assertThat(((Bitwise) e).getOperation(),
                equalTo(BitwiseOperation.OR));

        p.ReInit(new StringReader("23 | pressure & 74"));
        e = p.BitwiseExpression(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Bitwise);
        assertThat(((Bitwise) e).getOperation(),
                equalTo(BitwiseOperation.OR));
    }

    @Test
    public void testComparisonExpression() throws Exception {
        Parser p;
        Expression e;
        Errors err = new Errors();
        Set<String> ids = new TreeSet<>();

        p = new Parser(new StringReader("pressure"));
        e = p.Comparison(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Field);
        assertThat(((Field) e).getId(), equalTo("pressure"));

        p.ReInit(new StringReader("10 < 32"));
        e = p.Comparison(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertTrue(e instanceof Constant);
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.TRUE));

        p.ReInit(new StringReader("1 > 10"));
        e = p.Comparison(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertTrue(e instanceof Constant);
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.FALSE));

        p.ReInit(new StringReader("temperature < 10"));
        e = p.Comparison(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Comparison);
        assertThat(((Comparison) e).getOperation(),
                equalTo(ComparisonOperation.LT));

        p.ReInit(new StringReader("23 <= pressure"));
        e = p.Comparison(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Comparison);
        assertThat(((Comparison) e).getOperation(),
                equalTo(ComparisonOperation.LE));

        p.ReInit(new StringReader("23 > pressure"));
        e = p.Comparison(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Comparison);
        assertThat(((Comparison) e).getOperation(),
                equalTo(ComparisonOperation.GT));

        p.ReInit(new StringReader("23 >= pressure"));
        e = p.Comparison(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Comparison);
        assertThat(((Comparison) e).getOperation(),
                equalTo(ComparisonOperation.GE));

        p.ReInit(new StringReader("23 = pressure"));
        e = p.Comparison(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Comparison);
        assertThat(((Comparison) e).getOperation(),
                equalTo(ComparisonOperation.EQ));

        p.ReInit(new StringReader("23 != pressure"));
        e = p.Comparison(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Comparison);
        assertThat(((Comparison) e).getOperation(),
                equalTo(ComparisonOperation.NE));

        p.ReInit(new StringReader("23 <> pressure"));
        e = p.Comparison(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Comparison);
        assertThat(((Comparison) e).getOperation(),
                equalTo(ComparisonOperation.NE));
    }

    @Test
    public void testBetween() throws Exception {
        Parser p;
        Expression e;
        Errors err = new Errors();
        Set<String> ids = new TreeSet<>();
        Expression c = Constant.create(12, DataType.INTEGER);
        Field f = new Field("test");

        p = new Parser(new StringReader("between 0 and 43"));
        e = p.Between(c, ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertTrue(e instanceof Constant);
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.TRUE));

        p.ReInit(new StringReader("between -12 and 45"));
        e = p.Between(f, ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Between);
    }

    @Test
    public void testIs() throws Exception {
        Parser p;
        Expression e;
        Errors err = new Errors();
        Field f = new Field("test");

        p = new Parser(new StringReader("is true"));
        e = p.Is(Constant.TRUE, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertTrue(e instanceof Constant);
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.TRUE));

        p.ReInit(new StringReader("is not true"));
        e = p.Is(Constant.TRUE, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertTrue(e instanceof Constant);
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.FALSE));

        p.ReInit(new StringReader("is false"));
        e = p.Is(f, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Is);
        assertThat(((Is) e).getLogicValue(), equalTo(LogicValue.FALSE));

        p.ReInit(new StringReader("is null"));
        e = p.Is(f, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof IsNull);

        p.ReInit(new StringReader("is not null"));
        e = p.Is(f, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Not);
    }

    @Test
    public void testLike() throws Exception {
        Parser p;
        Expression e;
        Errors err = new Errors();
        Expression c = Constant.create("test", DataType.STRING);
        Field f = new Field("test");

        p = new Parser(new StringReader("like \"%test\""));
        e = p.Like(c, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.run(null, null), equalTo(LogicValue.TRUE));

        p.ReInit(new StringReader("like \"asdf\""));
        e = p.Like(f, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Like);
    }

    @Test
    public void testBooleanPredicate() throws Exception {
        Parser p;
        Expression e;
        Errors err = new Errors();
        Set<String> ids = new TreeSet<>();

        p = new Parser(new StringReader("name like \"asdf\""));
        e = p.BooleanPredicate(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Like);

        p = new Parser(new StringReader("room between 0 and 23"));
        e = p.BooleanPredicate(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Between);

        p = new Parser(new StringReader("temperature is null"));
        e = p.BooleanPredicate(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof IsNull);

        p = new Parser(new StringReader("flag is unknown"));
        e = p.BooleanPredicate(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Is);
    }

    @Test
    public void testBooleanNegation() throws Exception {
        Parser p;
        Expression e;
        Errors err = new Errors();
        Set<String> ids = new TreeSet<>();

        p = new Parser(new StringReader("not flag"));
        e = p.BooleanNegation(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Not);
    }

    @Test
    public void testBooleanFactor() throws Exception {
        Parser p;
        Expression e;
        Errors err = new Errors();
        Set<String> ids = new TreeSet<>();

        p = new Parser(new StringReader("true xor flag"));
        e = p.BooleanFactor(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Bool);
        assertThat(((Bool) e).getOperation(), equalTo(BoolOperation.XOR));
    }

    @Test
    public void testBooleanTerm() throws Exception {
        Parser p;
        Expression e;
        Errors err = new Errors();
        Set<String> ids = new TreeSet<>();

        p = new Parser(new StringReader("true and flag"));
        e = p.BooleanTerm(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Bool);
        assertThat(((Bool) e).getOperation(), equalTo(BoolOperation.AND));
    }

    @Test
    public void testBooleanOR() throws Exception {
        Parser p;
        Expression e;
        Errors err = new Errors();
        Set<String> ids = new TreeSet<>();

        p = new Parser(new StringReader("true or flag"));
        e = p.Expression(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(e instanceof Bool);
        assertThat(((Bool) e).getOperation(), equalTo(BoolOperation.OR));
    }

    @Test
    public void testExpression() throws Exception {
        Parser p;
        Expression e;
        Errors err = new Errors();
        Set<String> ids = new TreeSet<>();

        p = new Parser(new StringReader("not (2 << 4 > 0) && false"));
        e = p.Expression(ExpressionType.SIMPLE, err, ids);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));
    }

    @Test
    public void testTerminateAfter() throws Exception {
        Parser p;
        WindowSize ws;
        Errors err = new Errors();

        p = new Parser(new StringReader("terminate after 23 days"));
        ws = p.TerminateAfterClause(err);
        assertTrue(err.isEmpty());
        assertThat(ws.getType(), equalTo(WindowType.TIME));
        assertThat(ws.getDuration(), equalTo(Duration.ofDays(23)));

        p.ReInit(new StringReader("terminate after 45 selections"));
        ws = p.TerminateAfterClause(err);
        assertTrue(err.isEmpty());
        assertThat(ws.getType(), equalTo(WindowType.SAMPLE));
        assertThat(ws.getSamples(), equalTo(45));
    }

    @Test
    public void testOnUnsupportedSR() throws Exception {
        Parser p;
        RatePolicy usr;
        Errors err = new Errors();

        p = new Parser(new StringReader("on unsupported sample rate slow down"));
        usr = p.OnUnsupportedSRClause();
        assertTrue(err.isEmpty());
        assertThat(usr, equalTo(RatePolicy.SLOW_DOWN));

        p.ReInit(new StringReader("on unsupported sample rate do not sample"));
        usr = p.OnUnsupportedSRClause();
        assertTrue(err.isEmpty());
        assertThat(usr, equalTo(RatePolicy.DO_NOT_SAMPLE));
    }

    @Test
    public void testEveryDuration() throws Exception {
        Parser p;
        Every e;
        Expression c;
        Errors err = new Errors();
        Set<String> ids = new TreeSet<>();

        p = new Parser(new StringReader("5 seconds"));
        e = p.EveryDuration(err, ids);
        c = e.getValue();
        assertTrue(c instanceof Constant);
        assertThat(((Constant) c).getValue(), equalTo(5));
        assertThat(e.getUnit(), equalTo(ChronoUnit.SECONDS));

        p.ReInit(new StringReader("25 days"));
        e = p.EveryDuration(err, ids);
        c = e.getValue();
        assertTrue(c instanceof Constant);
        assertThat(((Constant) c).getValue(), equalTo(25));
        assertThat(e.getUnit(), equalTo(ChronoUnit.DAYS));
    }

    @Test
    public void testRefresh() throws Exception {
        Parser p;
        Refresh r;
        List<Attribute> events;

        p = new Parser(new StringReader("refresh every 10 seconds"));
        r = p.RefreshClause();
        assertThat(r, notNullValue());
        assertThat(r.getType(), equalTo(RefreshType.TIME));
        assertThat(r.getDuration(), equalTo(Duration.ofSeconds(10)));

        p.ReInit(new StringReader("refresh never"));
        r = p.RefreshClause();
        assertThat(r, equalTo(Refresh.NEVER));

        p.ReInit(new StringReader("refresh on event alert"));
        r = p.RefreshClause();
        assertThat(r, notNullValue());
        assertThat(r.getType(), equalTo(RefreshType.EVENT));
        r = r.bind(atts);
        events = r.getEvents();
        assertThat(events.size(), equalTo(1));
        assertTrue(events.contains(alertAtt));

        p.ReInit(new StringReader("refresh on event alert, low_power"));
        r = p.RefreshClause();
        assertThat(r, notNullValue());
        assertThat(r.getType(), equalTo(RefreshType.EVENT));
        r = r.bind(atts);
        events = r.getEvents();
        assertThat(events.size(), equalTo(2));
        assertTrue(events.contains(alertAtt));
        assertTrue(events.contains(lowPowerAtt));
    }

    @Test
    public void testIfEveryClause() throws Exception {
        Parser p;
        IfEvery ife;
        Duration d;
        Errors err = new Errors();
        Set<String> ids = new TreeSet<>();

        p = new Parser(new StringReader("every 5 seconds"));
        ife = p.IfEveryClause(err, ids);
        assertTrue(err.isEmpty());
        assertTrue(ife.isComplete());
        d = ife.run(null);
        assertThat(d, equalTo(Duration.ofSeconds(5)));

        p.ReInit(new StringReader(
                "if false every 10 seconds else every 2 days"));
        ife = p.IfEveryClause(err, ids);
        assertTrue(err.isEmpty());
        assertTrue(ife.isComplete());
        d = ife.run(null);
        assertThat(d, equalTo(Duration.ofDays(2)));

        p.ReInit(new StringReader(
                "if power > 80 every 10 seconds " +
                        "if power > 60 every 40 seconds " +
                        "if power > 40 every 2 minutes " +
                        "else every 1 hours"
        ));
        ife = p.IfEveryClause(err, ids);
        assertTrue(err.isEmpty());
        assertFalse(ife.isComplete());
        ife = ife.bind(atts, new ArrayList<>(), err);
        assertTrue(ife.isComplete());
        d = ife.run(samples[0]);
        assertThat(d, equalTo(Duration.ofSeconds(10)));
        d = ife.run(samples[1]);
        assertThat(d, equalTo(Duration.ofSeconds(40)));
        d = ife.run(samples[2]);
        assertThat(d, equalTo(Duration.ofMinutes(2)));
        d = ife.run(samples[3]);
        assertThat(d, equalTo(Duration.ofHours(1)));
    }

    @Test
    public void testSamplingIfEvery() throws Exception {
        Parser p;
        Sampling s;
        IfEvery ife;
        Duration d;
        Errors err = new Errors();
        Set<String> ids = new TreeSet<>();

        p = new Parser(new StringReader("sampling every 5 seconds"));
        s = p.SamplingClause(err, ids);
        assertTrue(err.isEmpty());
        assertTrue(s.isComplete());
        assertTrue(s instanceof SamplingIfEvery);
        ife = ((SamplingIfEvery) s).getIfEvery();
        d = ife.run(null);
        assertThat(d, equalTo(Duration.ofSeconds(5)));

        p.ReInit(new StringReader("sampling " +
                "if power > 80 every 10 seconds " +
                "if power > 60 every 40 seconds " +
                "if power > 40 every 2 minutes " +
                "else every 1 hours"
        ));
        s = p.SamplingClause(err, ids);
        assertTrue(err.isEmpty());
        assertFalse(s.isComplete());
        assertTrue(s instanceof SamplingIfEvery);
        s = s.bind(atts, err);
        assertTrue(err.isEmpty());
        ife = ((SamplingIfEvery) s).getIfEvery();
        assertTrue(ife.isComplete());
        d = ife.run(samples[0]);
        assertThat(d, equalTo(Duration.ofSeconds(10)));
        d = ife.run(samples[1]);
        assertThat(d, equalTo(Duration.ofSeconds(40)));
        d = ife.run(samples[2]);
        assertThat(d, equalTo(Duration.ofMinutes(2)));
        d = ife.run(samples[3]);
        assertThat(d, equalTo(Duration.ofHours(1)));
    }

    @Test
    public void testSamplingEvent() throws Exception {
        Errors err = new Errors();
        Set<String> ids = new TreeSet<>();

        Parser p = new Parser(new StringReader(
                "sampling on event alert, low_power"));
        Sampling s = p.SamplingClause(err, ids);
        assertTrue(err.isEmpty());
        assertFalse(s.isComplete());
        assertTrue(s instanceof SamplingEvent);
        s = s.bind(atts, err);
        assertTrue(err.isEmpty());
        assertTrue(s.isComplete());
        List<Attribute> bound = ((SamplingEvent) s).getEvents();
        assertThat(bound.size(), equalTo(2));
        assertTrue(bound.contains(lowPowerAtt));
        assertTrue(bound.contains(alertAtt));
    }

    @Test
    public void testSpecification() throws Exception {
        Parser p = new Parser(new StringReader("temperature:ANY"));
        DataTemplate t = p.Specification();
        assertThat(t.getId(), equalTo("temperature"));
        assertThat(t.getTypeClass(), equalTo(TypeClass.ANY));


        p.ReInit(new StringReader("pressure:FLOAT"));
        t = p.Specification();
        assertThat(t.getId(), equalTo("pressure"));
        assertThat(t.getTypeClass(), equalTo(TypeClass.FLOAT));
    }

    @Test
    public void testSpecificationList() throws Exception {
        Parser p = new Parser(new StringReader(
                "temperature, pressure:FLOAT, room_name:STRING"));
        List<DataTemplate> specs = p.SpecificationList();
        assertThat(specs.size(), equalTo(3));
        DataTemplate t = specs.get(0);
        assertThat(t.getId(), equalTo("temperature"));
        assertThat(t.getTypeClass(), equalTo(TypeClass.ANY));
        t = specs.get(1);
        assertThat(t.getId(), equalTo("pressure"));
        assertThat(t.getTypeClass(), equalTo(TypeClass.FLOAT));
        t = specs.get(2);
        assertThat(t.getId(), equalTo("room_name"));
        assertThat(t.getTypeClass(), equalTo(TypeClass.STRING));
    }

    @Test
    public void testNodeSpecifications() throws Exception {
        Parser p = new Parser(new StringReader(
                "on nodes with temperature, pressure:FLOAT"
        ));
        List<DataTemplate> specs =
                p.NodeSpecifications(Collections.emptySet());
        assertThat(specs.size(), equalTo(2));
        DataTemplate t = specs.get(0);
        assertThat(t.getId(), equalTo("temperature"));
        assertThat(t.getTypeClass(), equalTo(TypeClass.ANY));
        t = specs.get(1);
        assertThat(t.getId(), equalTo("pressure"));
        assertThat(t.getTypeClass(), equalTo(TypeClass.FLOAT));

        Set<String> ids = new TreeSet<>();
        ids.add("light");
        ids.add("humidity");
        ids.add("elevation");
        p.ReInit(new StringReader("on nodes with all"));
        specs = p.NodeSpecifications(ids);
        assertThat(specs.size(), equalTo(3));
        assertTrue(specs.contains(DataTemplate.create("light", TypeClass.ANY)));
        assertTrue(specs.contains(
                DataTemplate.create("humidity", TypeClass.ANY)));
        assertTrue(specs.contains(
                DataTemplate.create("elevation", TypeClass.ANY)));
    }

    @Test
    public void testExecutionConditionsClause() throws Exception {
        Errors err = new Errors();
        Set<String> ids = new TreeSet<>();

        Parser p = new Parser(new StringReader(
                "execute if battery > 20"
        ));
        ExecutionConditions e = p.ExecutionConditionsClause(err, ids);
        assertTrue(err.isEmpty());
        assertTrue(e.getSpecs().isEmpty());
        assertThat(e.getRefresh(), equalTo(Refresh.NEVER));

        p.ReInit(new StringReader(
                "execute if battery > 20 " +
                        "on nodes with temperature:INTEGER " +
                        "refresh every 20 minutes"
        ));
        ids.clear();
        e = p.ExecutionConditionsClause(err, ids);
        assertTrue(err.isEmpty());
        assertThat(e.getSpecs().size(), equalTo(1));
        assertTrue(e.getSpecs().contains(
                DataTemplate.create("temperature", TypeClass.INTEGER)));
        assertThat(e.getRefresh().getType(), equalTo(RefreshType.TIME));
        assertThat(e.getRefresh().getDuration(),
                equalTo(Duration.ofMinutes(20)));

        // Test ExecutionCondition clause that only refreshes the list of
        // FPCs on which the query is running
        p.ReInit(new StringReader(
                "execute refresh every 15 days"
        ));
        ids.clear();
        e = p.ExecutionConditionsClause(err, ids);
        assertTrue(err.isEmpty());

        // Test if an error is generated when the condition always evaluates to
        // false
        err = new Errors();
        p.ReInit(new StringReader(
                "execute if 5 > 12"
        ));
        ids.clear();
        e = p.ExecutionConditionsClause(err, ids);
        assertFalse(err.isEmpty());
    }

    @Test
    public void testGroupBy() throws Exception {
        Parser p = new Parser(new StringReader(
                "group by timestamp(20 seconds, 10 groups), building, room"
        ));
        GroupBy gb = p.GroupByClause();
        assertThat(gb.getDuration(), equalTo(Duration.ofSeconds(20)));
        assertThat(gb.getCount(), equalTo(10));
        assertThat(gb.getGroups().size(), equalTo(2));
    }

    @Test
    public void testSelectionQuery() throws Exception {
        Errors err = new Errors();

        Parser p = new Parser(new StringReader(
                "every 20 minutes " +
                        "select room, avg(temperature, 1 minutes)" +
                        "group by room " +
                        "having temperature > 20 " +
                        "up to 12 samples " +
                        "sampling every 20 seconds " +
                        "execute if power > 30 " +
                        "on nodes with all " +
                        "refresh every 2 hours " +
                        "terminate after 60 days"

        ));
        SelectionQuery q = p.SelectionStatement(err);
        assertTrue(err.isEmpty());

        Select sel = q.getSelect();

        WindowSize upto = sel.getUpTo();
        assertThat(upto.getType(), equalTo(WindowType.SAMPLE));
        assertThat(upto.getSamples(), equalTo(12));

        GroupBy gb = sel.getGroupBy();
        assertThat(gb.getGroups().size(), equalTo(1));

        assertTrue(sel.getHaving() instanceof Comparison);
        assertTrue(q.getSampling() instanceof SamplingIfEvery);

        WindowSize every = q.getEvery();
        assertThat(every.getType(), equalTo(WindowType.TIME));
        assertThat(every.getDuration(), equalTo(Duration.ofMinutes(20)));

        ExecutionConditions ec = q.getExecutionConditions();
        assertTrue(ec.getCondition() instanceof Comparison);
        assertThat(ec.getRefresh().getType(), equalTo(RefreshType.TIME));
        assertThat(ec.getRefresh().getDuration(), equalTo(Duration.ofHours(2)));

        WindowSize terminate = q.getTerminate();
        assertThat(terminate.getType(), equalTo(WindowType.TIME));
        assertThat(terminate.getDuration(), equalTo(Duration.ofDays(60)));
    }

    @Test
    public void testSelectionQueryDefaults() throws Exception {
        Errors err = new Errors();

        Parser p = new Parser(new StringReader(
                "every one " +
                        "select room, temperature " +
                        "sampling every 20 minutes"
        ));
        SelectionQuery q = p.SelectionStatement(err);
        assertTrue(err.isEmpty());

        Select sel = q.getSelect();
        assertThat(sel.getUpTo(), equalTo(WindowSize.ONE));
        assertThat(sel.getHaving(), equalTo(Constant.TRUE));
        assertThat(sel.getGroupBy(), equalTo(GroupBy.NONE));
        assertThat(sel.getDefault().length, equalTo(0));

        assertThat(q.getWhere(), equalTo(Constant.TRUE));
        assertThat(q.getEvery(), equalTo(WindowSize.ONE));
        assertThat(q.getTerminate(), equalTo(WindowSize.ZERO));

        ExecutionConditions cond = q.getExecutionConditions();
        assertThat(cond.getRefresh(), equalTo(Refresh.NEVER));
        assertThat(cond.getCondition(), equalTo(Constant.TRUE));
        assertTrue(cond.getSpecs().isEmpty());
    }

}