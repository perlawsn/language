package org.dei.perla.lang.parser;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.lang.parser.ast.*;
import org.dei.perla.lang.query.expression.*;
import org.dei.perla.lang.query.statement.WindowSize;
import org.junit.Test;

import java.io.StringReader;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

/**
 * @author Guido Rota 30/07/15.
 */
public class ParserASTTEst {

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
    }

    @Test
    public void testTypeClass() throws Exception {
        ParserAST p = getParser("integer");
        TypeClass t = p.TypeClass();
        assertThat(t, equalTo(TypeClass.INTEGER));

        p = getParser("float");
        t = p.TypeClass();
        assertThat(t, equalTo(TypeClass.FLOAT));

        p = getParser("boolean");
        t = p.TypeClass();
        assertThat(t, equalTo(TypeClass.BOOLEAN));

        p = getParser("string");
        t = p.TypeClass();
        assertThat(t, equalTo(TypeClass.STRING));

        p = getParser("timestamp");
        t = p.TypeClass();
        assertThat(t, equalTo(TypeClass.TIMESTAMP));

        p = getParser("id");
        t = p.TypeClass();
        assertThat(t, equalTo(TypeClass.ID));

        p = getParser("any");
        t = p.TypeClass();
        assertThat(t, equalTo(TypeClass.ANY));
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
    public void testDuration() throws Exception {
        ParserAST p = getParser("3 seconds");
        Duration d = p.Duration();
        assertThat(d, equalTo(Duration.ofSeconds(3)));

        p = getParser("3 s");
        d = p.Duration();
        assertThat(d, equalTo(Duration.ofSeconds(3)));

        p = getParser("45 days");
        d = p.Duration();
        assertThat(d, equalTo(Duration.ofDays(45)));
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
        DurationWindowAST dw = (DurationWindowAST) p.WindowSize("", ctx);
        assertFalse(ctx.hasErrors());
        ConstantAST c = (ConstantAST) dw.getValue();
        assertThat(c.getValue(), equalTo(3));
        assertThat(dw.getUnit(), equalTo(ChronoUnit.SECONDS));

        p = getParser("23 samples");
        SampleWindowAST sw = (SampleWindowAST) p.WindowSize("", ctx);
        assertFalse(ctx.hasErrors());
        c = (ConstantAST) sw.getSamples();
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
                equalTo(new ConstantAST("test", TypeClass.STRING)));
        assertThat(l.getPattern(), equalTo("te?t"));
    }

    @Test
    public void testBetween() throws Exception {
        ParserContext ctx = new ParserContext();
        ParserAST p = getParser("5 between 12 and 32");
        BetweenAST b = (BetweenAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(b.getOperand(), equalTo(
                new ConstantAST(5, TypeClass.INTEGER)));
        assertThat(b.getMin(), equalTo(
                new ConstantAST(12, TypeClass.INTEGER)));
        assertThat(b.getMax(), equalTo(
                new ConstantAST(32, TypeClass.INTEGER)));
    }

    @Test
    public void testComparison() throws Exception {
        ParserContext ctx = new ParserContext();
        ConstantAST op1 = new ConstantAST(5, TypeClass.INTEGER);
        ConstantAST op2 = new ConstantAST(12, TypeClass.INTEGER);

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
        ConstantAST op1 = new ConstantAST(43, TypeClass.INTEGER);
        ConstantAST op2 = new ConstantAST(12, TypeClass.INTEGER);

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
        ConstantAST op1 = new ConstantAST(43, TypeClass.INTEGER);
        ConstantAST op2 = new ConstantAST(12, TypeClass.INTEGER);

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
        DurationWindowAST dw = (DurationWindowAST) a.getWindowSize();
        assertThat(dw.getUnit(), equalTo(ChronoUnit.SECONDS));
        ConstantAST c = (ConstantAST) dw.getValue();
        assertThat(c.getValue(), equalTo(3));
        assertThat(c.getTypeClass(), equalTo(TypeClass.INTEGER));
        assertThat(a.getFilter(), equalTo(ConstantAST.FALSE));

        p = getParser("sum(temperature, 3 seconds, false)");
        a = (AggregateAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(a.getOperation(), equalTo(AggregateOperation.SUM));
        AttributeReferenceAST att = (AttributeReferenceAST) a.getOperand();
        assertThat(att.getId(), equalTo("temperature"));
        dw = (DurationWindowAST) a.getWindowSize();
        assertThat(dw.getUnit(), equalTo(ChronoUnit.SECONDS));
        c = (ConstantAST) dw.getValue();
        assertThat(c.getValue(), equalTo(3));
        assertThat(c.getTypeClass(), equalTo(TypeClass.INTEGER));
        assertThat(a.getFilter(), equalTo(ConstantAST.FALSE));

        p = getParser("max(temperature, 3 seconds, false)");
        a = (AggregateAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(a.getOperation(), equalTo(AggregateOperation.MAX));
        att = (AttributeReferenceAST) a.getOperand();
        assertThat(att.getId(), equalTo("temperature"));
        dw = (DurationWindowAST) a.getWindowSize();
        assertThat(dw.getUnit(), equalTo(ChronoUnit.SECONDS));
        c = (ConstantAST) dw.getValue();
        assertThat(c.getValue(), equalTo(3));
        assertThat(c.getTypeClass(), equalTo(TypeClass.INTEGER));
        assertThat(a.getFilter(), equalTo(ConstantAST.FALSE));

        p = getParser("min(temperature, 3 seconds, false)");
        a = (AggregateAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(a.getOperation(), equalTo(AggregateOperation.MIN));
        att = (AttributeReferenceAST) a.getOperand();
        assertThat(att.getId(), equalTo("temperature"));
        dw = (DurationWindowAST) a.getWindowSize();
        assertThat(dw.getUnit(), equalTo(ChronoUnit.SECONDS));
        c = (ConstantAST) dw.getValue();
        assertThat(c.getValue(), equalTo(3));
        assertThat(c.getTypeClass(), equalTo(TypeClass.INTEGER));
        assertThat(a.getFilter(), equalTo(ConstantAST.FALSE));

        p = getParser("avg(temperature, 3 seconds, false)");
        a = (AggregateAST) p.Expression(ExpressionType.AGGREGATE, "", ctx);
        assertFalse(ctx.hasErrors());
        assertThat(a.getOperation(), equalTo(AggregateOperation.AVG));
        att = (AttributeReferenceAST) a.getOperand();
        assertThat(att.getId(), equalTo("temperature"));
        dw = (DurationWindowAST) a.getWindowSize();
        assertThat(dw.getUnit(), equalTo(ChronoUnit.SECONDS));
        c = (ConstantAST) dw.getValue();
        assertThat(c.getValue(), equalTo(3));
        assertThat(c.getTypeClass(), equalTo(TypeClass.INTEGER));
        assertThat(a.getFilter(), equalTo(ConstantAST.FALSE));
    }

    @Test
    public void testConstantAST() throws Exception {
        ParserAST p = getParser("34");
        ConstantAST c = p.Constant();
        assertThat(c.getTypeClass(), equalTo(TypeClass.INTEGER));
        assertThat(c.getValue(), equalTo(34));

        p = getParser("3.1415");
        c = p.Constant();
        assertThat(c.getTypeClass(), equalTo(TypeClass.FLOAT));
        assertThat(c.getValue(), equalTo(3.1415f));

        p = getParser("'test'");
        c = p.Constant();
        assertThat(c.getTypeClass(), equalTo(TypeClass.STRING));
        assertThat(c.getValue(), equalTo("test"));

        p = getParser("TRUE");
        c = p.Constant();
        assertThat(c.getTypeClass(), equalTo(TypeClass.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.TRUE));
    }

    @Test
    public void testAttributeAST() throws Exception {
        ParserContext ctx = new ParserContext();
        ParserAST p = getParser("temperature");
        assertFalse(ctx.hasErrors());
        AttributeReferenceAST a = p.AttributeReference();
        assertThat(a.getId(), equalTo("temperature"));
        assertThat(a.getTypeClass(), equalTo(TypeClass.ANY));

        p = getParser("temperature: float");
        a = p.AttributeReference();
        assertThat(a.getId(), equalTo("temperature"));
        assertThat(a.getTypeClass(), equalTo(TypeClass.FLOAT));
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

}
