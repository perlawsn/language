package org.dei.perla.lang.parser;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.parser.ast.*;
import org.dei.perla.lang.parser.ast.NodeSpecificationsAST.NodeSpecificationsType;
import org.dei.perla.lang.query.expression.*;
import org.dei.perla.lang.query.statement.RatePolicy;
import org.dei.perla.lang.query.statement.RefreshType;
import org.dei.perla.lang.query.statement.WindowSize;
import org.dei.perla.lang.query.statement.WindowSize.WindowType;
import org.junit.Test;

import java.io.StringReader;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

/**
 * @author Guido Rota 30/07/15.
 */
public class ParserASTTest {

    private static ParserAST getParser(String s) {
        return new ParserAST(new StringReader(s));
    }

    ///////////////////////////////////////////////
    // MISC
    ///////////////////////////////////////////////

    @Test
    public void testSign() throws Exception {
        ParserAST p = getParser("+");
        Sign s = p.Sign();
        assertThat(s, equalTo(Sign.PLUS));

        p = getParser("-");
        s = p.Sign();
        assertThat(s, equalTo(Sign.MINUS));
    }

    @Test
    public void testConstantBoolean() throws Exception {
        ParserAST p = getParser("TRUE");
        LogicValue v = p.ConstantBoolean();
        assertThat(v, equalTo(LogicValue.TRUE));

        p = getParser("true");
        v = p.ConstantBoolean();
        assertThat(v, equalTo(LogicValue.TRUE));

        p = getParser("false");
        v = p.ConstantBoolean();
        assertThat(v, equalTo(LogicValue.FALSE));

        p = getParser("FALSE");
        v = p.ConstantBoolean();
        assertThat(v, equalTo(LogicValue.FALSE));
    }

    @Test(expected = ParseException.class)
    public void testUnknown() throws Exception {
        ParserAST p = getParser("UNKNOWN");
        p.ConstantBoolean();
    }

    @Test
    public void testConstantFloat() throws Exception {
        ParserAST p = getParser("3.14");
        float f = p.ConstantFloat();
        assertThat(f, equalTo(3.14f));

        p = getParser("0.0");
        f = p.ConstantFloat();
        assertThat(f, equalTo(0f));
    }

    @Test
    public void testConstantInteger() throws Exception {
        ParserAST p = getParser("3");
        int i = p.ConstantInteger();
        assertThat(i, equalTo(3));

        p = getParser("0");
        i = p.ConstantInteger();
        assertThat(i, equalTo(0));
    }

    @Test
    public void testComparisonOperation() throws Exception {
        ParserAST p = getParser("=");
        ComparisonOperation c = p.ComparisonOperation();
        assertThat(c, equalTo(ComparisonOperation.EQ));

        p = getParser("!=");
        c = p.ComparisonOperation();
        assertThat(c, equalTo(ComparisonOperation.NE));

        p = getParser("<>");
        c = p.ComparisonOperation();
        assertThat(c, equalTo(ComparisonOperation.NE));

        p = getParser(">");
        c = p.ComparisonOperation();
        assertThat(c, equalTo(ComparisonOperation.GT));

        p = getParser(">=");
        c = p.ComparisonOperation();
        assertThat(c, equalTo(ComparisonOperation.GE));

        p = getParser("<");
        c = p.ComparisonOperation();
        assertThat(c, equalTo(ComparisonOperation.LT));

        p = getParser("<=");
        c = p.ComparisonOperation();
        assertThat(c, equalTo(ComparisonOperation.LE));
    }

    @Test
    public void testDataType() throws Exception {
        ParserAST p = getParser("integer");
        DataType t = p.Type();
        assertThat(t, equalTo(DataType.INTEGER));

        p = getParser("float");
        t = p.Type();
        assertThat(t, equalTo(DataType.FLOAT));

        p = getParser("boolean");
        t = p.Type();
        assertThat(t, equalTo(DataType.BOOLEAN));

        p = getParser("string");
        t = p.Type();
        assertThat(t, equalTo(DataType.STRING));

        p = getParser("timestamp");
        t = p.Type();
        assertThat(t, equalTo(DataType.TIMESTAMP));

        p = getParser("id");
        t = p.Type();
        assertThat(t, equalTo(DataType.ID));

        p = getParser("any");
        t = p.Type();
        assertThat(t, equalTo(DataType.ANY));
    }

    @Test
    public void testTimeUnit() throws Exception {
        ParserAST p = getParser("milliseconds");
        TemporalUnit t = p.TimeUnit();
        assertThat(t, equalTo(ChronoUnit.MILLIS));

        p = getParser("ms");
        t = p.TimeUnit();
        assertThat(t, equalTo(ChronoUnit.MILLIS));

        p = getParser("seconds");
        t = p.TimeUnit();
        assertThat(t, equalTo(ChronoUnit.SECONDS));

        p = getParser("s");
        t = p.TimeUnit();
        assertThat(t, equalTo(ChronoUnit.SECONDS));

        p = getParser("minutes");
        t = p.TimeUnit();
        assertThat(t, equalTo(ChronoUnit.MINUTES));

        p = getParser("m");
        t = p.TimeUnit();
        assertThat(t, equalTo(ChronoUnit.MINUTES));

        p = getParser("hours");
        t = p.TimeUnit();
        assertThat(t, equalTo(ChronoUnit.HOURS));

        p = getParser("h");
        t = p.TimeUnit();
        assertThat(t, equalTo(ChronoUnit.HOURS));

        p = getParser("days");
        t = p.TimeUnit();
        assertThat(t, equalTo(ChronoUnit.DAYS));

        p = getParser("d");
        t = p.TimeUnit();
        assertThat(t, equalTo(ChronoUnit.DAYS));
    }

    @Test
    public void testAggregateOperation() throws Exception {
        ParserAST p = getParser("AVG");
        AggregateOperation a = p.AggregateOperation();
        assertThat(a, equalTo(AggregateOperation.AVG));

        p = getParser("MIN");
        a = p.AggregateOperation();
        assertThat(a, equalTo(AggregateOperation.MIN));

        p = getParser("MAX");
        a = p.AggregateOperation();
        assertThat(a, equalTo(AggregateOperation.MAX));

        p = getParser("SUM");
        a = p.AggregateOperation();
        assertThat(a, equalTo(AggregateOperation.SUM));
    }

    @Test
    public void testSelectionsNumber() throws Exception {
        ParserAST p = getParser("4 selections");
        int s = p.SelectionsNumber();
        assertThat(s, equalTo(4));
    }

    @Test
    public void testWindowSize() throws Exception {
        ParserContext ctx = new ParserContext();

        ParserAST p = getParser("3 seconds");
        WindowSizeAST w = p.WindowSize("", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(w.getType(), equalTo(WindowSize.WindowType.TIME));
        ConstantAST c = (ConstantAST) w.getDurationValue();
        assertThat(c.getValue(), equalTo(3));
        assertThat(w.getDurationUnit(), equalTo(ChronoUnit.SECONDS));

        p = getParser("one");
        w = p.WindowSize("", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(w.getType(), equalTo(WindowSize.WindowType.SAMPLE));
        c = (ConstantAST) w.getSamples();
        assertThat(c.getValue(), equalTo(1));

        p = getParser("23 samples");
        w = p.WindowSize("", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(w.getType(), equalTo(WindowSize.WindowType.SAMPLE));
        c = (ConstantAST) w.getSamples();
        assertThat(c.getValue(), equalTo(23));

        p = getParser("-15 seconds");
        p.WindowSize("", ctx);
        assertFalse(ctx.hasErrors());

        ctx = new ParserContext();
        p = getParser("-1 * 32 samples");
        p.WindowSize("", ctx);
        assertFalse(ctx.hasErrors());
    }

    @Test
    public void testIdentifier() throws Exception {
        String t1 = "test1";
        String t2 = "test2";

        ParserAST p = getParser(t1);
        String s = p.Identifier();
        assertThat(s, equalTo(t1));

        p = getParser(t2);
        s = p.Identifier();
        assertThat(s, equalTo(t2));
    }

    ///////////////////////////////////////////////
    // Expressions
    ///////////////////////////////////////////////

    @Test
    public void testBoolean() throws Exception {
        ParserContext ctx = new ParserContext();
        ParserAST p = getParser("true or false");
        BoolAST b = (BoolAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(b.getOperation(), equalTo(BoolOperation.OR));
        assertThat(b.getLeftOperand(), equalTo(ConstantAST.TRUE));
        assertThat(b.getRightOperand(), equalTo(ConstantAST.FALSE));

        p = getParser("true and false");
        b = (BoolAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(b.getOperation(), equalTo(BoolOperation.AND));
        assertThat(b.getLeftOperand(), equalTo(ConstantAST.TRUE));
        assertThat(b.getRightOperand(), equalTo(ConstantAST.FALSE));

        p = getParser("true xor false");
        b = (BoolAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(b.getOperation(), equalTo(BoolOperation.XOR));
        assertThat(b.getLeftOperand(), equalTo(ConstantAST.TRUE));
        assertThat(b.getRightOperand(), equalTo(ConstantAST.FALSE));

        p = getParser("not true");
        NotAST not = (NotAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(not.getOperand(), equalTo(ConstantAST.TRUE));
    }

    @Test
    public void testIs() throws Exception {
        ParserContext ctx = new ParserContext();
        ParserAST p = getParser("true is unknown");
        IsAST is = (IsAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(is.getOperand(), equalTo(ConstantAST.TRUE));
        assertThat(is.getLogicValue(), equalTo(LogicValue.UNKNOWN));

        p = getParser("true is not unknown");
        NotAST b = (NotAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        is = (IsAST) ((NotAST) b).getOperand();
        assertThat(is.getOperand(), equalTo(ConstantAST.TRUE));
        assertThat(is.getLogicValue(), equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testIsNull() throws Exception {
        ParserContext ctx = new ParserContext();
        ParserAST p = getParser("true is null");
        IsNullAST is = (IsNullAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(is.getOperand(), equalTo(ConstantAST.TRUE));

        p = getParser("true is not null");
        NotAST b = (NotAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        is = (IsNullAST) b.getOperand();
        assertThat(is.getOperand(), equalTo(ConstantAST.TRUE));
    }

    @Test
    public void testLike() throws Exception {
        ParserContext ctx = new ParserContext();
        ParserAST p = getParser("'test' like 'te?t'");
        LikeAST l = (LikeAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(l.getOperand(),
                equalTo(new ConstantAST("test", DataType.STRING)));
        assertThat(l.getPattern(), equalTo("te?t"));
    }

    @Test
    public void testBetween() throws Exception {
        ParserContext ctx = new ParserContext();
        ParserAST p = getParser("5 between 12 and 32");
        BetweenAST b = (BetweenAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(b.getOperand(), equalTo(
                new ConstantAST(5, DataType.INTEGER)));
        assertThat(b.getMin(), equalTo(
                new ConstantAST(12, DataType.INTEGER)));
        assertThat(b.getMax(), equalTo(
                new ConstantAST(32, DataType.INTEGER)));
    }

    @Test
    public void testComparison() throws Exception {
        ParserContext ctx = new ParserContext();
        ConstantAST op1 = new ConstantAST(5, DataType.INTEGER);
        ConstantAST op2 = new ConstantAST(12, DataType.INTEGER);

        ParserAST p = getParser("5 = 12");
        ComparisonAST c = (ComparisonAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(c.getOperation(), equalTo(ComparisonOperation.EQ));
        assertThat(c.getLeftOperand(), equalTo(op1));
        assertThat(c.getRightOperand(), equalTo(op2));

        p = getParser("5 != 12");
        c = (ComparisonAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(c.getOperation(), equalTo(ComparisonOperation.NE));
        assertThat(c.getLeftOperand(), equalTo(op1));
        assertThat(c.getRightOperand(), equalTo(op2));

        p = getParser("5 <> 12");
        c = (ComparisonAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(c.getOperation(), equalTo(ComparisonOperation.NE));
        assertThat(c.getLeftOperand(), equalTo(op1));
        assertThat(c.getRightOperand(), equalTo(op2));

        p = getParser("5 > 12");
        c = (ComparisonAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(c.getOperation(), equalTo(ComparisonOperation.GT));
        assertThat(c.getLeftOperand(), equalTo(op1));
        assertThat(c.getRightOperand(), equalTo(op2));

        p = getParser("5 >= 12");
        c = (ComparisonAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(c.getOperation(), equalTo(ComparisonOperation.GE));
        assertThat(c.getLeftOperand(), equalTo(op1));
        assertThat(c.getRightOperand(), equalTo(op2));

        p = getParser("5 < 12");
        c = (ComparisonAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(c.getOperation(), equalTo(ComparisonOperation.LT));
        assertThat(c.getLeftOperand(), equalTo(op1));
        assertThat(c.getRightOperand(), equalTo(op2));

        p = getParser("5 <= 12");
        c = (ComparisonAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(c.getOperation(), equalTo(ComparisonOperation.LE));
        assertThat(c.getLeftOperand(), equalTo(op1));
        assertThat(c.getRightOperand(), equalTo(op2));
    }

    @Test
    public void testBitwise() throws Exception {
        ParserContext ctx = new ParserContext();
        ConstantAST op1 = new ConstantAST(43, DataType.INTEGER);
        ConstantAST op2 = new ConstantAST(12, DataType.INTEGER);

        ParserAST p = getParser("43 | 12");
        BitwiseAST b = (BitwiseAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(b.getOperation(), equalTo(BitwiseOperation.OR));
        assertThat(b.getLeftOperand(), equalTo(op1));
        assertThat(b.getRightOperand(), equalTo(op2));

        p = getParser("43 & 12");
        b = (BitwiseAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(b.getOperation(), equalTo(BitwiseOperation.AND));
        assertThat(b.getLeftOperand(), equalTo(op1));
        assertThat(b.getRightOperand(), equalTo(op2));

        p = getParser("43 ^ 12");
        b = (BitwiseAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(b.getOperation(), equalTo(BitwiseOperation.XOR));
        assertThat(b.getLeftOperand(), equalTo(op1));
        assertThat(b.getRightOperand(), equalTo(op2));

        p = getParser("43 >> 12");
        b = (BitwiseAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(b.getOperation(), equalTo(BitwiseOperation.RSH));
        assertThat(b.getLeftOperand(), equalTo(op1));
        assertThat(b.getRightOperand(), equalTo(op2));

        p = getParser("43 << 12");
        b = (BitwiseAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(b.getOperation(), equalTo(BitwiseOperation.LSH));
        assertThat(b.getLeftOperand(), equalTo(op1));
        assertThat(b.getRightOperand(), equalTo(op2));

        p = getParser("~43");
        BitwiseNotAST bn = (BitwiseNotAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(bn.getOperand(), equalTo(op1));
    }

    @Test
    public void testArithmetic() throws Exception {
        ParserContext ctx = new ParserContext();
        ConstantAST op1 = new ConstantAST(43, DataType.INTEGER);
        ConstantAST op2 = new ConstantAST(12, DataType.INTEGER);

        ParserAST p = getParser("43 + 12");
        ArithmeticAST a = (ArithmeticAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(a.getOperation(), equalTo(ArithmeticOperation.ADDITION));
        assertThat(a.getLeftOperand(), equalTo(op1));
        assertThat(a.getRightOperand(), equalTo(op2));

        p = getParser("43 - 12");
        a = (ArithmeticAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(a.getOperation(), equalTo(ArithmeticOperation.SUBTRACTION));
        assertThat(a.getLeftOperand(), equalTo(op1));
        assertThat(a.getRightOperand(), equalTo(op2));

        p = getParser("43 * 12");
        a = (ArithmeticAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(a.getOperation(), equalTo(ArithmeticOperation.PRODUCT));
        assertThat(a.getLeftOperand(), equalTo(op1));
        assertThat(a.getRightOperand(), equalTo(op2));

        p = getParser("43 / 12");
        a = (ArithmeticAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(a.getOperation(), equalTo(ArithmeticOperation.DIVISION));
        assertThat(a.getLeftOperand(), equalTo(op1));
        assertThat(a.getRightOperand(), equalTo(op2));

        p = getParser("43 % 12");
        a = (ArithmeticAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(a.getOperation(), equalTo(ArithmeticOperation.MODULO));
        assertThat(a.getLeftOperand(), equalTo(op1));
        assertThat(a.getRightOperand(), equalTo(op2));

        p = getParser("-43");
        InverseAST inv = (InverseAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(inv.getOperand(), equalTo(op1));
    }

    @Test
    public void testAggregate() throws Exception {
        ParserContext ctx = new ParserContext();
        WindowSize ws = new WindowSize(Duration.ofSeconds(3));

        ParserAST p = getParser("count(*, 3 seconds, false)");
        AggregateAST a = (AggregateAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(a.getOperation(), equalTo(AggregateOperation.COUNT));
        WindowSizeAST w = a.getWindowSize();
        assertThat(w.getType(), equalTo(WindowSize.WindowType.TIME));
        assertThat(w.getDurationUnit(), equalTo(ChronoUnit.SECONDS));
        ConstantAST c = (ConstantAST) w.getDurationValue();
        assertThat(c.getValue(), equalTo(3));
        assertThat(c.getType(), equalTo(DataType.INTEGER));
        assertThat(a.getFilter(), equalTo(ConstantAST.FALSE));

        p = getParser("sum(temperature, 3 seconds, false)");
        a = (AggregateAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(a.getOperation(), equalTo(AggregateOperation.SUM));
        AttributeReferenceAST att = (AttributeReferenceAST) a.getOperand();
        assertThat(att.getId(), equalTo("temperature"));
        w = a.getWindowSize();
        assertThat(w.getType(), equalTo(WindowSize.WindowType.TIME));
        assertThat(w.getDurationUnit(), equalTo(ChronoUnit.SECONDS));
        c = (ConstantAST) w.getDurationValue();
        assertThat(c.getValue(), equalTo(3));
        assertThat(c.getType(), equalTo(DataType.INTEGER));
        assertThat(a.getFilter(), equalTo(ConstantAST.FALSE));

        p = getParser("max(temperature, 3 seconds, false)");
        a = (AggregateAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(a.getOperation(), equalTo(AggregateOperation.MAX));
        att = (AttributeReferenceAST) a.getOperand();
        assertThat(att.getId(), equalTo("temperature"));
        w = a.getWindowSize();
        assertThat(w.getType(), equalTo(WindowSize.WindowType.TIME));
        assertThat(w.getDurationUnit(), equalTo(ChronoUnit.SECONDS));
        c = (ConstantAST) w.getDurationValue();
        assertThat(c.getValue(), equalTo(3));
        assertThat(c.getType(), equalTo(DataType.INTEGER));
        assertThat(a.getFilter(), equalTo(ConstantAST.FALSE));

        p = getParser("min(temperature, 3 seconds, false)");
        a = (AggregateAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(a.getOperation(), equalTo(AggregateOperation.MIN));
        att = (AttributeReferenceAST) a.getOperand();
        assertThat(att.getId(), equalTo("temperature"));
        w = a.getWindowSize();
        assertThat(w.getType(), equalTo(WindowSize.WindowType.TIME));
        assertThat(w.getDurationUnit(), equalTo(ChronoUnit.SECONDS));
        c = (ConstantAST) w.getDurationValue();
        assertThat(c.getValue(), equalTo(3));
        assertThat(c.getType(), equalTo(DataType.INTEGER));
        assertThat(a.getFilter(), equalTo(ConstantAST.FALSE));

        p = getParser("avg(temperature, 3 seconds, false)");
        a = (AggregateAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(a.getOperation(), equalTo(AggregateOperation.AVG));
        att = (AttributeReferenceAST) a.getOperand();
        assertThat(att.getId(), equalTo("temperature"));
        w = a.getWindowSize();
        assertThat(w.getType(), equalTo(WindowSize.WindowType.TIME));
        assertThat(w.getDurationUnit(), equalTo(ChronoUnit.SECONDS));
        c = (ConstantAST) w.getDurationValue();
        assertThat(c.getValue(), equalTo(3));
        assertThat(c.getType(), equalTo(DataType.INTEGER));
        assertThat(a.getFilter(), equalTo(ConstantAST.FALSE));
    }

    @Test
    public void testConstantAST() throws Exception {
        ParserAST p = getParser("34");
        ConstantAST c = p.Constant();
        assertThat(c.getType(), equalTo(DataType.INTEGER));
        assertThat(c.getValue(), equalTo(34));

        p = getParser("3.1415");
        c = p.Constant();
        assertThat(c.getType(), equalTo(DataType.FLOAT));
        assertThat(c.getValue(), equalTo(3.1415f));

        p = getParser("'test'");
        c = p.Constant();
        assertThat(c.getType(), equalTo(DataType.STRING));
        assertThat(c.getValue(), equalTo("test"));

        p = getParser("TRUE");
        c = p.Constant();
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.TRUE));
    }

    @Test
    public void testAttributeAST() throws Exception {
        ParserContext ctx = new ParserContext();
        ParserAST p = getParser("temperature");
        assertFalse(ctx.hasErrors());
        AttributeReferenceAST a = p.AttributeReference();
        assertThat(a.getId(), equalTo("temperature"));
        assertThat(a.getType(), equalTo(DataType.ANY));

        p = getParser("temperature: float");
        a = p.AttributeReference();
        assertThat(a.getId(), equalTo("temperature"));
        assertThat(a.getType(), equalTo(DataType.FLOAT));
    }

    @Test
    public void testSimpleExpression() throws Exception {
        ParserContext ctx = new ParserContext();
        ParserAST p = getParser("temperature + pressure - 3");
        p.Expression(ExpressionType.SIMPLE, "", ctx);
        assertFalse(ctx.hasErrors());

        p = getParser("temperature + COUNT(*, 5 seconds)");
        p.Expression(ExpressionType.SIMPLE, "", ctx);
        assertTrue(ctx.hasErrors());
    }

    @Test
    public void testConstantExpression() throws Exception {
        ParserContext ctx = new ParserContext();
        ParserAST p = getParser("12 + 5");
        p.Expression(ExpressionType.CONSTANT, "", ctx);
        assertFalse(ctx.hasErrors());

        p = getParser("temperature + 5");
        p.Expression(ExpressionType.CONSTANT, "", ctx);
        assertTrue(ctx.hasErrors());

        ctx = new ParserContext();
        p = getParser("5 + count(*, 12 seconds)");
        p.Expression(ExpressionType.CONSTANT, "", ctx);
        assertTrue(ctx.hasErrors());
    }

    ///////////////////////////////////////////////
    // SAMPLING
    ///////////////////////////////////////////////

    @Test
    public void testSamplingEvent() throws Exception {
        ParserContext ctx = new ParserContext();
        ParserAST p = getParser("sampling on event fire, smoke");
        SamplingAST s = p.SamplingClause(ctx);
        assertFalse(ctx.hasErrors());
        assertTrue(s instanceof SamplingEventAST);
        SamplingEventAST se = (SamplingEventAST) s;
        assertThat(se.getEvents().size(), equalTo(2));
        assertTrue(se.getEvents().contains("fire"));
        assertTrue(se.getEvents().contains("smoke"));
    }

    @Test
    public void testIfEvery() throws Exception {
        // No conditions
        ParserContext ctx = new ParserContext();
        ParserAST p = getParser("every 10 ms");
        List<IfEveryAST> ifes = p.IfEveryClause(ctx);
        assertFalse(ctx.hasErrors());
        assertThat(ifes.size(), equalTo(1));
        IfEveryAST ife = ifes.get(0);
        assertThat(ife.getCondition(), equalTo(ConstantAST.TRUE));
        EveryAST ev = ife.getEvery();
        assertThat(ev.getUnit(), equalTo(ChronoUnit.MILLIS));
        assertThat(ev.getValue(),
                equalTo(new ConstantAST(10, DataType.INTEGER)));

        // Single condition with every
        ctx = new ParserContext();
        p = getParser("if temperature > 10 every 10 minutes else every 1 hours");
        ifes = p.IfEveryClause(ctx);
        assertFalse(ctx.hasErrors());
        assertThat(ifes.size(), equalTo(2));

        ife = ifes.get(0);
        assertTrue(ife.getCondition() instanceof ComparisonAST);
        ev = ife.getEvery();
        assertThat(ev.getUnit(), equalTo(ChronoUnit.MINUTES));
        assertThat(ev.getValue(),
                equalTo(new ConstantAST(10, DataType.INTEGER)));

        ife = ifes.get(1);
        assertThat(ife.getCondition(), equalTo(ConstantAST.TRUE));
        ev = ife.getEvery();
        assertThat(ev.getUnit(), equalTo(ChronoUnit.HOURS));
        assertThat(ev.getValue(),
                equalTo(new ConstantAST(1, DataType.INTEGER)));

        // Multiple conditions
        ctx = new ParserContext();
        p = getParser("if temperature < 10 every 10 minutes " +
                "if temperature < 20 every 20 minutes " +
                "else every 1 hours");
        ifes = p.IfEveryClause(ctx);
        assertFalse(ctx.hasErrors());
        assertThat(ifes.size(), equalTo(3));

        ife = ifes.get(0);
        assertTrue(ife.getCondition() instanceof ComparisonAST);
        ev = ife.getEvery();
        assertThat(ev.getUnit(), equalTo(ChronoUnit.MINUTES));
        assertThat(ev.getValue(),
                equalTo(new ConstantAST(10, DataType.INTEGER)));

        ife = ifes.get(1);
        assertTrue(ife.getCondition() instanceof ComparisonAST);
        ev = ife.getEvery();
        assertThat(ev.getUnit(), equalTo(ChronoUnit.MINUTES));
        assertThat(ev.getValue(),
                equalTo(new ConstantAST(20, DataType.INTEGER)));

        ife = ifes.get(2);
        assertThat(ife.getCondition(), equalTo(ConstantAST.TRUE));
        ev = ife.getEvery();
        assertThat(ev.getUnit(), equalTo(ChronoUnit.HOURS));
        assertThat(ev.getValue(),
                equalTo(new ConstantAST(1, DataType.INTEGER)));
    }

    @Test
    public void testRatePolicy() throws Exception {
        ParserContext ctx = new ParserContext();
        ParserAST p = getParser("on unsupported sample rate do not sample");
        RatePolicy r = p.RatePolicy();
        assertFalse(ctx.hasErrors());
        assertThat(r, equalTo(RatePolicy.STRICT));

        p = getParser("on unsupported sample rate adapt");
        r = p.RatePolicy();
        assertFalse(ctx.hasErrors());
        assertThat(r, equalTo(RatePolicy.ADAPTIVE));
    }

    @Test
    public void testSamplingIfEvery() throws Exception {
        // Plain sampling, no refresh
        ParserContext ctx = new ParserContext();
        ParserAST p = getParser("sampling every 10 seconds");
        SamplingAST s = p.SamplingClause(ctx);
        assertFalse(ctx.hasErrors());
        assertTrue(s instanceof SamplingIfEveryAST);
        SamplingIfEveryAST si = (SamplingIfEveryAST) s;
        assertThat(si.getRefresh(), equalTo(RefreshAST.NEVER));
        assertThat(si.getRatePolicy(), equalTo(RatePolicy.STRICT));
        List<IfEveryAST> ifes = si.getIfEvery();
        assertThat(ifes.size(), equalTo(1));

        // Plain sampling, event refresh
        ctx = new ParserContext();
        p = getParser("sampling every 10 seconds refresh on event fire");
        s = p.SamplingClause(ctx);
        assertFalse(ctx.hasErrors());
        assertTrue(s instanceof SamplingIfEveryAST);
        si = (SamplingIfEveryAST) s;
        assertThat(si.getRefresh().getType(), equalTo(RefreshType.EVENT));
        assertThat(si.getRatePolicy(), equalTo(RatePolicy.STRICT));
        ifes = si.getIfEvery();
        assertThat(ifes.size(), equalTo(1));

        // Complex sampling, time-based refresh, adaptive rate
        ctx = new ParserContext();
        p = getParser("sampling if temperature < 10 every 10 seconds " +
                "if temperature < 20 every 20 seconds " +
                "else every 1 minutes " +
                "on unsupported sample rate adapt " +
                "refresh every 20 minutes");
        s = p.SamplingClause(ctx);
        assertFalse(ctx.hasErrors());
        assertTrue(s instanceof SamplingIfEveryAST);
        si = (SamplingIfEveryAST) s;
        assertThat(si.getRefresh().getType(), equalTo(RefreshType.TIME));
        ifes = si.getIfEvery();
        assertThat(ifes.size(), equalTo(3));
        assertThat(si.getRatePolicy(), equalTo(RatePolicy.ADAPTIVE));
    }

    @Test
    public void testNodeSpecifications() throws Exception {
        // ALL
        ParserAST p = getParser("require all");
        NodeSpecificationsAST spec = p.NodeSpecifications();
        assertThat(spec, notNullValue());
        assertThat(spec.getType(), equalTo(NodeSpecificationsType.ALL));

        // SPECS
        p = getParser("require temperature, pressure:integer");
        spec = p.NodeSpecifications();
        assertThat(spec, notNullValue());
        assertThat(spec.getType(), equalTo(NodeSpecificationsType.SPECS));
        List<Attribute> specAtts = spec.getSpecifications();
        assertThat(specAtts.size(), equalTo(2));
        Attribute a = Attribute.create("temperature", DataType.ANY);
        assertTrue(specAtts.contains(a));
        a = Attribute.create("pressure", DataType.INTEGER);
        assertTrue(specAtts.contains(a));
    }

    @Test
    public void testExecuteIf() throws Exception {
        // SIMPLE CONDITION
        ParserAST p = getParser("execute if true");
        ParserContext ctx = new ParserContext();
        ExecutionConditionsAST cond = p.ExecutionConditionsClause(ctx);
        assertThat(cond, notNullValue());
        assertThat(cond.getCondition(), equalTo(ConstantAST.TRUE));
        assertThat(cond.getSpecifications(),
                equalTo(NodeSpecificationsAST.EMPTY));
        assertThat(cond.getRefresh(), equalTo(RefreshAST.NEVER));

        // FULL
        p = getParser(
                "execute if true require temp:integer refresh every 10 m");
        cond = p.ExecutionConditionsClause(ctx);
        assertThat(cond, notNullValue());
        assertThat(cond.getCondition(), equalTo(ConstantAST.TRUE));
        NodeSpecificationsAST spec = cond.getSpecifications();
        assertThat(spec.getType(), equalTo(NodeSpecificationsType.SPECS));
        List<Attribute> specAtts = spec.getSpecifications();
        assertThat(specAtts.size(), equalTo(1));
        Attribute a = Attribute.create("temp", DataType.INTEGER);
        assertTrue(specAtts.contains(a));
        RefreshAST ref = cond.getRefresh();
        assertThat(ref.getType(), equalTo(RefreshType.TIME));
        assertThat(ref.getDurationUnit(), equalTo(ChronoUnit.MINUTES));
        ConstantAST c = new ConstantAST(10, DataType.INTEGER);
        assertThat(ref.getDurationValue(), equalTo(c));
    }

    @Test
    public void testGroupBy() throws Exception {
        ParserAST p = getParser("group by temperature, pressure");
        ParserContext ctx = new ParserContext();
        GroupByAST group = p.GroupByClause();
        assertThat(group, notNullValue());
        List<String> fields = group.getFields();
        assertThat(fields.size(), equalTo(2));
        assertTrue(fields.contains("temperature"));
        assertTrue(fields.contains("pressure"));
    }

    @Test
    public void testEvery() throws Exception {
        ParserAST p = getParser("every 10 seconds");
        ParserContext ctx = new ParserContext();
        WindowSizeAST every = p.EveryClause(ctx);
        assertThat(every, notNullValue());
        assertThat(every.getType(), equalTo(WindowType.TIME));
        assertThat(every.getDurationUnit(), equalTo(ChronoUnit.SECONDS));
        ConstantAST c = (ConstantAST) every.getDurationValue();
        assertThat(c.getType(), equalTo(DataType.INTEGER));
        assertThat(c.getValue(), equalTo(10));
    }

    @Test
    public void testUpTo() throws Exception {
        ParserAST p = getParser("up to 10 samples");
        ParserContext ctx = new ParserContext();
        WindowSizeAST upto = p.UpToClause(ctx);
        assertThat(upto, notNullValue());
        assertThat(upto.getType(), equalTo(WindowType.SAMPLE));
        ConstantAST c = (ConstantAST) upto.getSamples();
        assertThat(c.getType(), equalTo(DataType.INTEGER));
        assertThat(c.getValue(), equalTo(10));
    }

    @Test
    public void testOnEmptySelection() throws Exception {
        ParserAST p = getParser("on empty selection insert default");
        OnEmptySelection oes = p.OnEmptySelectionClause();
        assertThat(oes, equalTo(OnEmptySelection.DEFAULT));

        p = getParser("on empty selection insert nothing");
        oes = p.OnEmptySelectionClause();
        assertThat(oes, equalTo(OnEmptySelection.NOTHING));
    }

    @Test
    public void testFieldSelection() throws Exception {
        ParserContext ctx = new ParserContext();

        // Plain field selection
        ParserAST p = getParser("temperature");
        FieldSelectionAST fs = p.FieldSelection(ctx);
        assertFalse(ctx.hasErrors());
        assertThat(fs, notNullValue());
        AttributeReferenceAST ref = (AttributeReferenceAST) fs.getField();
        assertThat(ref.getId(), equalTo("temperature"));
        assertThat(ref.getType(), equalTo(DataType.ANY));
        ConstantAST def = (ConstantAST) fs.getDefault();
        assertThat(def, equalTo(ConstantAST.NULL));

        // Explicit default null
        p = getParser("temperature:integer default null");
        fs = p.FieldSelection(ctx);
        assertFalse(ctx.hasErrors());
        assertThat(fs, notNullValue());
        ref = (AttributeReferenceAST) fs.getField();
        assertThat(ref.getId(), equalTo("temperature"));
        assertThat(ref.getType(), equalTo(DataType.INTEGER));
        def = (ConstantAST) fs.getDefault();
        assertThat(def, equalTo(ConstantAST.NULL));

        // Default value
        p = getParser("temperature default 5");
        fs = p.FieldSelection(ctx);
        assertFalse(ctx.hasErrors());
        assertThat(fs, notNullValue());
        ref = (AttributeReferenceAST) fs.getField();
        assertThat(ref.getId(), equalTo("temperature"));
        assertThat(ref.getType(), equalTo(DataType.ANY));
        def = (ConstantAST) fs.getDefault();
        assertThat(def.getType(), equalTo(DataType.INTEGER));
        assertThat(def.getValue(), equalTo(5));
    }

    @Test
    public void testFieldSelectionList() throws Exception {
        ParserContext ctx = new ParserContext();
        ParserAST p = getParser("temp, press:integer, hum default 10");
        List<FieldSelectionAST> fsl = p.FieldSelectionList(ctx);
        assertFalse(ctx.hasErrors());
        assertThat(fsl, notNullValue());
        assertThat(fsl.size(), equalTo(3));

        FieldSelectionAST fs = fsl.get(0);
        assertThat(fs, notNullValue());
        AttributeReferenceAST ref = (AttributeReferenceAST) fs.getField();
        assertThat(ref.getId(), equalTo("temp"));
        assertThat(ref.getType(), equalTo(DataType.ANY));
        ConstantAST def = (ConstantAST) fs.getDefault();
        assertThat(def, equalTo(ConstantAST.NULL));

        fs = fsl.get(1);
        assertThat(fs, notNullValue());
        ref = (AttributeReferenceAST) fs.getField();
        assertThat(ref.getId(), equalTo("press"));
        assertThat(ref.getType(), equalTo(DataType.INTEGER));
        def = (ConstantAST) fs.getDefault();
        assertThat(def, equalTo(ConstantAST.NULL));

        fs = fsl.get(2);
        assertThat(fs, notNullValue());
        ref = (AttributeReferenceAST) fs.getField();
        assertThat(ref.getId(), equalTo("hum"));
        assertThat(ref.getType(), equalTo(DataType.ANY));
        def = (ConstantAST) fs.getDefault();
        assertThat(def.getType(), equalTo(DataType.INTEGER));
        assertThat(def.getValue(), equalTo(10));
    }

}
