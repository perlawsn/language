package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.parser.AttributeOrder;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.TypeVariable;
import org.dei.perla.lang.query.expression.*;
import org.dei.perla.lang.query.statement.WindowSize;
import org.junit.Test;

import java.time.Instant;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

/**
 * @author Guido Rota 31/07/15.
 */
public class ExpressionASTTest {

    private static final MockExpressionAST boolExp =
            new MockExpressionAST(DataType.BOOLEAN);
    private static final MockExpressionAST intExp =
            new MockExpressionAST(DataType.INTEGER);
    private static final MockExpressionAST floatExp =
            new MockExpressionAST(DataType.FLOAT);
    private static final MockExpressionAST stringExp =
            new MockExpressionAST(DataType.STRING);
    private static final MockExpressionAST timestampExp =
            new MockExpressionAST(DataType.TIMESTAMP);

    @Test
    public void testConstantAST() {
        ConstantAST c = new ConstantAST(10, DataType.INTEGER);
        assertThat(c.getType(), equalTo(DataType.INTEGER));
        assertThat(c.getValue(), equalTo(10));
    }

    @Test
    public void testEvalIntConstant1() {
        ParserContext ctx = new ParserContext();
        ConstantAST c = new ConstantAST(10, DataType.INTEGER);
        int v = c.evalIntConstant(ctx);
        assertFalse(ctx.hasErrors());
        assertThat(v, equalTo(10));
    }

    @Test(expected = ClassCastException.class)
    public void testEvalIntConstant2() {
        ParserContext ctx = new ParserContext();
        ConstantAST c = new ConstantAST("test", DataType.STRING);
        int v = c.evalIntConstant(ctx);
    }

    @Test
    public void testEvalConstant1() {
        ParserContext ctx = new ParserContext();
        ConstantAST ca = new ConstantAST("test", DataType.STRING);
        Constant c = ca.evalConstant(ctx);
        assertThat(c.getType(), equalTo(DataType.STRING));
        assertThat(c.getValue(), equalTo("test"));
    }

    @Test(expected = RuntimeException.class)
    public void testEvalConstant2() {
        ParserContext ctx = new ParserContext();
        MockExpressionAST m = new MockExpressionAST(DataType.INTEGER);
        m.evalConstant(ctx);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonConcreteConstantAST() {
        ConstantAST c = new ConstantAST(10, DataType.ANY);
    }

    @Test
    public void testConstantInference() {
        ParserContext ctx = new ParserContext();

        ConstantAST c = new ConstantAST(10, DataType.INTEGER);
        TypeVariable v = new TypeVariable(DataType.INTEGER);
        boolean res = c.inferType(v, ctx);
        assertTrue(res);
        assertThat(c.getType(), equalTo(DataType.INTEGER));
        assertThat(v.getType(), equalTo(DataType.INTEGER));

        v = new TypeVariable(DataType.NUMERIC);
        res = c.inferType(v, ctx);
        assertTrue(res);
        assertThat(c.getType(), equalTo(DataType.INTEGER));
        assertThat(v.getType(), equalTo(DataType.INTEGER));

        v = new TypeVariable(DataType.ANY);
        res = c.inferType(v, ctx);
        assertTrue(res);
        assertThat(c.getType(), equalTo(DataType.INTEGER));
        assertThat(v.getType(), equalTo(DataType.INTEGER));

        v = new TypeVariable(DataType.STRING);
        res = c.inferType(v, ctx);
        assertFalse(res);
        assertThat(v.getType(), equalTo(DataType.STRING));
    }

    @Test
    public void testConstantCompile() {
        ParserContext ctx = new ParserContext();
        AttributeOrder ord = new AttributeOrder();

        ConstantAST ca = new ConstantAST(10, DataType.INTEGER);
        Constant c = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.INTEGER));
        assertThat(c.getValue(), equalTo(10));

        ca = new ConstantAST("test", DataType.STRING);
        c = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.STRING));
        assertThat(c.getValue(), equalTo("test"));

        ca = ConstantAST.NULL;
        c = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c, equalTo(Constant.NULL));
    }

    @Test(expected = IllegalStateException.class)
    public void testConstantASTSetType() {
        ConstantAST c = new ConstantAST("test", DataType.STRING);
        c.setType(new TypeVariable(DataType.ANY));
    }

    @Test
    public void testAttribute() {
        AttributeReferenceAST a = new AttributeReferenceAST("att", DataType.ANY);
        assertThat(a.getId(), equalTo("att"));
        assertThat(a.getType(), equalTo(DataType.ANY));
    }

    @Test
    public void testAttributeInference() {
        ParserContext ctx = new ParserContext();

        AttributeReferenceAST a = new AttributeReferenceAST("att", DataType.ANY);
        TypeVariable v = new TypeVariable(DataType.NUMERIC);
        boolean res = a.inferType(v, ctx);
        assertTrue(res);
        assertThat(a.getType(), equalTo(DataType.NUMERIC));

        a = new AttributeReferenceAST("att", DataType.INTEGER);
        res = a.inferType(v, ctx);
        assertTrue(res);
        assertThat(a.getType(), equalTo(DataType.INTEGER));

        assertThat(ctx.getErrorCount(), equalTo(0));
        Map<String, DataType> attTypes = ctx.getAttributeTypes();
        assertThat(attTypes.get("att"), equalTo(DataType.INTEGER));

        a = new AttributeReferenceAST("asd", DataType.NUMERIC);
        v = new TypeVariable(DataType.ANY);
        res = a.inferType(v, ctx);
        assertTrue(res);
        assertThat(v.getType(), equalTo(DataType.NUMERIC));
        assertThat(a.getType(), equalTo(DataType.NUMERIC));
    }

    @Test
    public void testAttributeCompile() {
        ParserContext ctx = new ParserContext();
        AttributeOrder ord = new AttributeOrder();

        AttributeReferenceAST aa = new AttributeReferenceAST("att1", DataType.INTEGER);
        AttributeReference a = (AttributeReference) aa.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(a.getId(), equalTo(aa.getId()));
        assertThat(a.getType(), equalTo(aa.getType()));
        assertThat(a.getIndex(), equalTo(0));
        assertThat(ord.getIndex(aa.getId()), equalTo(0));

        aa = new AttributeReferenceAST("att2", DataType.STRING);
        a = (AttributeReference) aa.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(a.getId(), equalTo(aa.getId()));
        assertThat(a.getType(), equalTo(aa.getType()));
        assertThat(a.getIndex(), equalTo(1));
        assertThat(ord.getIndex(aa.getId()), equalTo(1));

        aa = new AttributeReferenceAST("att1", DataType.INTEGER);
        a = (AttributeReference) aa.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(a.getId(), equalTo(aa.getId()));
        assertThat(a.getType(), equalTo(aa.getType()));
        assertThat(a.getIndex(), equalTo(0));

        aa = new AttributeReferenceAST("att3", DataType.ANY);
        Constant c = (Constant) aa.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(1));
        assertThat(c, equalTo(Constant.NULL));
    }

    @Test(expected = IllegalStateException.class)
    public void testAttributeReferenceSetType() {
        AttributeReferenceAST a =
                new AttributeReferenceAST("att", DataType.ANY);
        a.setType(new TypeVariable(DataType.NUMERIC));
    }

    @Test
    public void testBooleanBinary() {
        MockExpressionAST left = new MockExpressionAST(DataType.BOOLEAN);
        MockExpressionAST right = new MockExpressionAST(DataType.BOOLEAN);
        BoolAST b = new BoolAST(BoolOperation.OR, left, right);
        assertThat(b.getLeftOperand(), equalTo(left));
        assertThat(b.getRightOperand(), equalTo(right));
    }

    @Test
    public void testBooleanBinaryInference() {
        ParserContext ctx = new ParserContext();

        BoolAST b = new BoolAST(BoolOperation.AND, boolExp, boolExp);
        TypeVariable v = new TypeVariable(DataType.BOOLEAN);
        assertTrue(b.inferType(v, ctx));
        assertThat(b.getType(), equalTo(DataType.BOOLEAN));

        b = new BoolAST(BoolOperation.AND, boolExp, intExp);
        assertFalse(b.inferType(v, ctx));
        assertThat(b.getType(), equalTo(DataType.BOOLEAN));

        b = new BoolAST(BoolOperation.AND, intExp, boolExp);
        assertFalse(b.inferType(v, ctx));
        assertThat(b.getType(), equalTo(DataType.BOOLEAN));

        b = new BoolAST(BoolOperation.AND, intExp, stringExp);
        assertFalse(b.inferType(v, ctx));
        assertThat(b.getType(), equalTo(DataType.BOOLEAN));
    }

    @Test
    public void testBooleanBinaryCompile() {
        AttributeReferenceAST b1 =
                new AttributeReferenceAST("boolean", DataType.BOOLEAN);
        AttributeReferenceAST b2 =
                new AttributeReferenceAST("boolean", DataType.BOOLEAN);
        ParserContext ctx = new ParserContext();
        AttributeOrder ord = new AttributeOrder();

        BoolAST ba = new BoolAST(BoolOperation.AND, b1, b1);
        Bool b = (Bool) ba.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(b.getOperation(), equalTo(ba.getOperation()));
        assertThat(b.getType(), equalTo(DataType.BOOLEAN));

        ba = new BoolAST(BoolOperation.OR, ConstantAST.TRUE, ConstantAST.FALSE);
        Constant c = (Constant) ba.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.TRUE));

        ba = new BoolAST(BoolOperation.AND, ConstantAST.TRUE,
                ConstantAST.FALSE);
        c = (Constant) ba.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.FALSE));

        ba = new BoolAST(BoolOperation.XOR, ConstantAST.TRUE,
                ConstantAST.FALSE);
        c = (Constant) ba.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.TRUE));
    }

    @Test(expected = IllegalStateException.class)
    public void testBooleanBinaryNoType() {
        BoolAST b = new BoolAST(BoolOperation.OR, boolExp, boolExp);
        b.getType();
    }

    @Test(expected = IllegalStateException.class)
    public void testBooleanBinaryDoubleTypeSet() {
        BoolAST b = new BoolAST(BoolOperation.OR, boolExp, boolExp);
        ParserContext ctx = new ParserContext();
        TypeVariable v = new TypeVariable(DataType.ANY);
        b.inferType(v, ctx);
        b.inferType(v, ctx);
    }

    @Test
    public void testBooleanNot() {
        MockExpressionAST op = new MockExpressionAST(DataType.BOOLEAN);
        NotAST b = new NotAST(op);
        assertThat(b.getOperand(), equalTo(op));
    }

    @Test
    public void testBooleanNotInference() {
        ParserContext ctx = new ParserContext();

        NotAST n = new NotAST(boolExp);
        TypeVariable v = new TypeVariable(DataType.BOOLEAN);
        assertTrue(n.inferType(v, ctx));
        assertThat(n.getType(), equalTo(DataType.BOOLEAN));

        n = new NotAST(intExp);
        assertFalse(n.inferType(v, ctx));
        assertThat(n.getType(), equalTo(DataType.BOOLEAN));

        v = new TypeVariable(DataType.ANY);
        n = new NotAST(boolExp);
        assertTrue(n.inferType(v, ctx));
        assertThat(n.getType(), equalTo(DataType.BOOLEAN));

        v = new TypeVariable(DataType.NUMERIC);
        n = new NotAST(boolExp);
        assertFalse(n.inferType(v, ctx));
    }

    @Test
    public void testBooleanNotCompile() {
        AttributeReferenceAST b =
                new AttributeReferenceAST("boolean", DataType.BOOLEAN);
        ParserContext ctx = new ParserContext();
        AttributeOrder ord = new AttributeOrder();

        NotAST na = new NotAST(b);
        Not n = (Not) na.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(n.getType(), equalTo(DataType.BOOLEAN));

        na = new NotAST(ConstantAST.TRUE);
        assertThat(ctx.getErrorCount(), equalTo(0));
        Constant c = (Constant) na.toExpression(ctx, ord);
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.FALSE));

        na = new NotAST(ConstantAST.FALSE);
        assertThat(ctx.getErrorCount(), equalTo(0));
        c = (Constant) na.toExpression(ctx, ord);
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.TRUE));
    }

    @Test(expected = IllegalStateException.class)
    public void testBooleanNotNoType() {
        NotAST b = new NotAST(boolExp);
        b.getType();
    }

    @Test(expected = IllegalStateException.class)
    public void testBooleanNotDoubleTypeSet() {
        NotAST b = new NotAST(boolExp);
        ParserContext ctx = new ParserContext();
        TypeVariable v = new TypeVariable(DataType.ANY);
        b.inferType(v, ctx);
        b.inferType(v, ctx);
    }

    @Test
    public void testBitwiseBinary() {
        MockExpressionAST left = new MockExpressionAST(DataType.INTEGER);
        MockExpressionAST right = new MockExpressionAST(DataType.INTEGER);
        BitwiseAST b = new BitwiseAST(BitwiseOperation.OR, left, right);
        assertThat(b.getLeftOperand(), equalTo(left));
        assertThat(b.getRightOperand(), equalTo(right));
    }

    @Test
    public void testBitwiseBinaryInference() {
        ParserContext ctx = new ParserContext();

        BitwiseAST b = new BitwiseAST(BitwiseOperation.AND, intExp, intExp);
        TypeVariable v = new TypeVariable(DataType.INTEGER);
        assertTrue(b.inferType(v, ctx));
        assertThat(b.getType(), equalTo(DataType.INTEGER));

        b = new BitwiseAST(BitwiseOperation.AND, boolExp, intExp);
        assertFalse(b.inferType(v, ctx));

        b = new BitwiseAST(BitwiseOperation.AND, intExp, boolExp);
        assertFalse(b.inferType(v, ctx));

        b = new BitwiseAST(BitwiseOperation.AND, intExp, stringExp);
        assertFalse(b.inferType(v, ctx));
    }

    @Test
    public void testBitwiseBinaryCompile() {
        AttributeReferenceAST i1 =
                new AttributeReferenceAST("integer", DataType.INTEGER);
        AttributeReferenceAST i2 =
                new AttributeReferenceAST("integer", DataType.INTEGER);
        ParserContext ctx = new ParserContext();
        AttributeOrder ord = new AttributeOrder();

        BitwiseAST ba = new BitwiseAST(BitwiseOperation.OR, i1, i2);
        Bitwise b = (Bitwise) ba.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(b.getOperation(), equalTo(ba.getOperation()));
        assertThat(b.getType(), equalTo(DataType.INTEGER));

        ConstantAST c1 = new ConstantAST(32423, DataType.INTEGER);
        ConstantAST c2 = new ConstantAST(2, DataType.INTEGER);

        ba = new BitwiseAST(BitwiseOperation.OR, c1, c2);
        Constant c = (Constant) ba.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.INTEGER));
        assertThat(c.getValue(), equalTo(32423 | 2));

        ba = new BitwiseAST(BitwiseOperation.AND, c1, c2);
        c = (Constant) ba.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.INTEGER));
        assertThat(c.getValue(), equalTo(32423 & 2));

        ba = new BitwiseAST(BitwiseOperation.XOR, c1, c2);
        c = (Constant) ba.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.INTEGER));
        assertThat(c.getValue(), equalTo(32423 ^ 2));

        ba = new BitwiseAST(BitwiseOperation.LSH, c1, c2);
        c = (Constant) ba.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.INTEGER));
        assertThat(c.getValue(), equalTo(32423 << 2));

        ba = new BitwiseAST(BitwiseOperation.RSH, c1, c2);
        c = (Constant) ba.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.INTEGER));
        assertThat(c.getValue(), equalTo(32423 >> 2));
    }

    @Test(expected = IllegalStateException.class)
    public void testBitwiseBinaryNoType() {
        BitwiseAST b = new BitwiseAST(BitwiseOperation.XOR, intExp, intExp);
        b.getType();
    }

    @Test(expected = IllegalStateException.class)
    public void testBitwiseBinaryDoubleTypeSet() {
        BitwiseAST b = new BitwiseAST(BitwiseOperation.XOR, intExp, intExp);
        ParserContext ctx = new ParserContext();
        TypeVariable v = new TypeVariable(DataType.ANY);
        b.inferType(v, ctx);
        b.inferType(v, ctx);
    }

    @Test
    public void testBitwiseNot() {
        MockExpressionAST op = new MockExpressionAST(DataType.INTEGER);
        BitwiseNotAST b = new BitwiseNotAST(op);
        assertThat(b.getOperand(), equalTo(op));
    }

    @Test
    public void testBitwiseNotInference() {
        ParserContext ctx = new ParserContext();

        BitwiseNotAST b = new BitwiseNotAST(intExp);
        TypeVariable v = new TypeVariable(DataType.INTEGER);
        assertTrue(b.inferType(v, ctx));
        assertThat(b.getType(), equalTo(DataType.INTEGER));

        b = new BitwiseNotAST(stringExp);
        assertFalse(b.inferType(v, ctx));

        v = new TypeVariable(DataType.ANY);
        b = new BitwiseNotAST(intExp);
        assertTrue(b.inferType(v, ctx));
        assertThat(b.getType(), equalTo(DataType.INTEGER));

        v = new TypeVariable(DataType.NUMERIC);
        b = new BitwiseNotAST(intExp);
        assertTrue(b.inferType(v, ctx));
        assertThat(b.getType(), equalTo(DataType.INTEGER));
    }

    @Test
    public void testBitwiseNotCompile() {
        AttributeReferenceAST i1 =
                new AttributeReferenceAST("integer", DataType.INTEGER);
        ParserContext ctx = new ParserContext();
        AttributeOrder ord = new AttributeOrder();

        BitwiseNotAST ba = new BitwiseNotAST(i1);
        BitwiseNot b = (BitwiseNot) ba.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(b.getType(), equalTo(DataType.INTEGER));

        ba = new BitwiseNotAST(new ConstantAST(10, DataType.INTEGER));
        Constant c = (Constant) ba.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.INTEGER));
        assertThat(c.getValue(), equalTo(~10));
    }

    @Test(expected = IllegalStateException.class)
    public void testBitwiseNotNoType() {
        BitwiseNotAST b = new BitwiseNotAST(intExp);
        b.getType();
    }

    @Test(expected = IllegalStateException.class)
    public void testBitwiseNotDoubleTypeSet() {
        BitwiseNotAST b = new BitwiseNotAST(intExp);
        ParserContext ctx = new ParserContext();
        TypeVariable v = new TypeVariable(DataType.ANY);
        b.inferType(v, ctx);
        b.inferType(v, ctx);
    }

    @Test
    public void testIs() {
        MockExpressionAST op = new MockExpressionAST(DataType.BOOLEAN);
        IsAST is = new IsAST(op, LogicValue.UNKNOWN);
        assertThat(is.getOperand(), equalTo(op));
        assertThat(is.getLogicValue(), equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testIsInference() {
        ParserContext ctx = new ParserContext();

        IsAST is = new IsAST(boolExp, LogicValue.TRUE);
        TypeVariable v = new TypeVariable(DataType.BOOLEAN);
        assertTrue(is.inferType(v, ctx));
        assertThat(is.getType(), equalTo(DataType.BOOLEAN));

        is = new IsAST(intExp, LogicValue.TRUE);
        assertFalse(is.inferType(v, ctx));

        v = new TypeVariable(DataType.NUMERIC);
        is = new IsAST(boolExp, LogicValue.TRUE);
        assertFalse(is.inferType(v, ctx));

        v = new TypeVariable(DataType.ANY);
        is = new IsAST(boolExp, LogicValue.TRUE);
        assertTrue(is.inferType(v, ctx));
        assertThat(is.getType(), equalTo(DataType.BOOLEAN));
    }

    @Test
    public void testIsCompile() {
        AttributeReferenceAST iatt =
                new AttributeReferenceAST("integer", DataType.INTEGER);
        ParserContext ctx = new ParserContext();
        AttributeOrder ord = new AttributeOrder();

        IsAST ia = new IsAST(iatt, LogicValue.TRUE);
        Is i = (Is) ia.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(i.getType(), equalTo(DataType.BOOLEAN));
        assertThat(i.getLogicValue(), equalTo(ia.getLogicValue()));

        ia = new IsAST(ConstantAST.TRUE, LogicValue.TRUE);
        Constant c = (Constant) ia.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.TRUE));

        ia = new IsAST(ConstantAST.FALSE, LogicValue.TRUE);
        c = (Constant) ia.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.FALSE));

        ia = new IsAST(new ConstantAST(LogicValue.UNKNOWN, DataType.BOOLEAN),
                LogicValue.TRUE);
        c = (Constant) ia.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.FALSE));

        ia = new IsAST(ConstantAST.TRUE, LogicValue.FALSE);
        c = (Constant) ia.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.FALSE));

        ia = new IsAST(ConstantAST.FALSE, LogicValue.FALSE);
        c = (Constant) ia.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.TRUE));

        ia = new IsAST(new ConstantAST(LogicValue.UNKNOWN, DataType.BOOLEAN),
                LogicValue.FALSE);
        c = (Constant) ia.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.FALSE));

        ia = new IsAST(ConstantAST.TRUE, LogicValue.UNKNOWN);
        c = (Constant) ia.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.FALSE));

        ia = new IsAST(ConstantAST.FALSE, LogicValue.UNKNOWN);
        c = (Constant) ia.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.FALSE));

        ia = new IsAST(new ConstantAST(LogicValue.UNKNOWN, DataType.BOOLEAN),
                LogicValue.UNKNOWN);
        c = (Constant) ia.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.TRUE));
    }

    @Test(expected = IllegalStateException.class)
    public void testIsNoType() {
        IsAST is = new IsAST(boolExp, LogicValue.FALSE);
        is.getType();
    }

    @Test(expected = IllegalStateException.class)
    public void testIsDoubleTypeSet() {
        IsAST is = new IsAST(boolExp, LogicValue.FALSE);
        ParserContext ctx = new ParserContext();
        TypeVariable v = new TypeVariable(DataType.ANY);
        is.inferType(v, ctx);
        is.inferType(v, ctx);
    }

    @Test
    public void testIsNull() {
        MockExpressionAST op = new MockExpressionAST(DataType.TIMESTAMP);
        IsNullAST is = new IsNullAST(op);
        assertThat(is.getOperand(), equalTo(op));
    }

    @Test
    public void testIsNullInference() {
        ParserContext ctx = new ParserContext();

        IsNullAST is = new IsNullAST(intExp);
        TypeVariable v = new TypeVariable(DataType.BOOLEAN);
        assertTrue(is.inferType(v, ctx));
        assertThat(is.getType(), equalTo(DataType.BOOLEAN));
        is = new IsNullAST(floatExp);
        assertTrue(is.inferType(v, ctx));
        assertThat(is.getType(), equalTo(DataType.BOOLEAN));
        is = new IsNullAST(stringExp);
        assertTrue(is.inferType(v, ctx));
        assertThat(is.getType(), equalTo(DataType.BOOLEAN));
        is = new IsNullAST(boolExp);
        assertTrue(is.inferType(v, ctx));
        assertThat(is.getType(), equalTo(DataType.BOOLEAN));
        is = new IsNullAST(timestampExp);
        assertTrue(is.inferType(v, ctx));
        assertThat(is.getType(), equalTo(DataType.BOOLEAN));

        v = new TypeVariable(DataType.NUMERIC);
        is = new IsNullAST(intExp);
        assertFalse(is.inferType(v, ctx));
    }

    @Test
    public void testIsNullCompile() {
        AttributeReferenceAST iatt =
                new AttributeReferenceAST("integer", DataType.INTEGER);
        ParserContext ctx = new ParserContext();
        AttributeOrder ord = new AttributeOrder();

        IsNullAST ia = new IsNullAST(iatt);
        IsNull i = (IsNull) ia.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(i.getType(), equalTo(DataType.BOOLEAN));

        ia = new IsNullAST(new ConstantAST(10, DataType.INTEGER));
        Constant c = (Constant) ia.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.FALSE));

        ia = new IsNullAST(ConstantAST.NULL);
        c = (Constant) ia.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.TRUE));
    }

    @Test(expected = IllegalStateException.class)
    public void testIsNullNoType() {
        IsNullAST is = new IsNullAST(intExp);
        is.getType();
    }

    @Test(expected = IllegalStateException.class)
    public void testIsNullDoubleTypeSet() {
        IsNullAST is = new IsNullAST(intExp);
        ParserContext ctx = new ParserContext();
        TypeVariable v = new TypeVariable(DataType.ANY);
        is.inferType(v, ctx);
        is.inferType(v, ctx);
    }

    @Test
    public void testLike() {
        MockExpressionAST op = new MockExpressionAST(DataType.STRING);
        LikeAST l = new LikeAST(op, "test");
        assertThat(l.getOperand(), equalTo(op));
        assertThat(l.getPattern(), equalTo("test"));
    }

    @Test
    public void testLikeInference() {
        ParserContext ctx = new ParserContext();

        LikeAST l = new LikeAST(stringExp, "test");
        TypeVariable v = new TypeVariable(DataType.BOOLEAN);
        assertTrue(l.inferType(v, ctx));
        assertThat(l.getType(), equalTo(DataType.BOOLEAN));

        l = new LikeAST(intExp, "test");
        assertFalse(l.inferType(v, ctx));
    }

    @Test
    public void testLikeCompile() {
        AttributeReferenceAST satt =
                new AttributeReferenceAST("string", DataType.STRING);
        ParserContext ctx = new ParserContext();
        AttributeOrder ord = new AttributeOrder();

        LikeAST la = new LikeAST(satt, "test");
        Like l = (Like) la.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(l.getType(), equalTo(DataType.BOOLEAN));

        la = new LikeAST(new ConstantAST("test", DataType.STRING), "test");
        Constant c = (Constant) la.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.TRUE));

        la = new LikeAST(new ConstantAST("rest", DataType.STRING), "test");
        c = (Constant) la.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.FALSE));

        la = new LikeAST(ConstantAST.NULL, "test");
        c = (Constant) la.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.UNKNOWN));
    }

    @Test(expected = IllegalStateException.class)
    public void testLikeNoType() {
        LikeAST l = new LikeAST(stringExp, "test");
        l.getType();
    }

    @Test(expected = IllegalStateException.class)
    public void testLikeDoubleTypeSet() {
        LikeAST l = new LikeAST(stringExp, "test");
        ParserContext ctx = new ParserContext();
        TypeVariable v = new TypeVariable(DataType.ANY);
        l.inferType(v, ctx);
        l.inferType(v, ctx);
    }

    @Test
    public void testArithmetic() {
        MockExpressionAST op1 = new MockExpressionAST(DataType.INTEGER);
        MockExpressionAST op2 = new MockExpressionAST(DataType.INTEGER);
        ArithmeticAST a =
                new ArithmeticAST(ArithmeticOperation.ADDITION, op1, op2);
        assertThat(a.getOperation(), equalTo(ArithmeticOperation.ADDITION));
        assertThat(a.getLeftOperand(), equalTo(op1));
        assertThat(a.getRightOperand(), equalTo(op2));
    }

    @Test
    public void testArithmeticInference() {
        ParserContext ctx = new ParserContext();

        ArithmeticAST a = new ArithmeticAST(ArithmeticOperation.ADDITION,
                intExp, intExp);
        TypeVariable v = new TypeVariable(DataType.NUMERIC);
        assertTrue(a.inferType(v, ctx));
        assertThat(v.getType(), equalTo(DataType.INTEGER));
        assertThat(a.getType(), equalTo(DataType.INTEGER));

        v = new TypeVariable(DataType.NUMERIC);
        a = new ArithmeticAST(ArithmeticOperation.ADDITION, floatExp, floatExp);
        assertTrue(a.inferType(v, ctx));
        assertThat(v.getType(), equalTo(DataType.FLOAT));
        assertThat(a.getType(), equalTo(DataType.FLOAT));

        v = new TypeVariable(DataType.NUMERIC);
        a = new ArithmeticAST(ArithmeticOperation.ADDITION, intExp, floatExp);
        assertFalse(a.inferType(v, ctx));

        v = new TypeVariable(DataType.INTEGER);
        a = new ArithmeticAST(ArithmeticOperation.ADDITION, floatExp, floatExp);
        assertFalse(a.inferType(v, ctx));

        v = new TypeVariable(DataType.BOOLEAN);
        a = new ArithmeticAST(ArithmeticOperation.ADDITION, floatExp, floatExp);
        assertFalse(a.inferType(v, ctx));
    }

    @Test
    public void testArithmeticCompile() {
        AttributeReferenceAST i1 =
                new AttributeReferenceAST("i1", DataType.INTEGER);
        AttributeReferenceAST i2 =
                new AttributeReferenceAST("i2", DataType.INTEGER);
        ParserContext ctx = new ParserContext();
        AttributeOrder ord = new AttributeOrder();

        ArithmeticAST aa = new ArithmeticAST(ArithmeticOperation.ADDITION,
                i1, i2);
        assertTrue(aa.inferType(new TypeVariable(DataType.ANY), ctx));
        Arithmetic a = (Arithmetic) aa.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(a.getOperation(), equalTo(aa.getOperation()));
        assertThat(a.getType(), equalTo(DataType.INTEGER));

        ConstantAST c1int = new ConstantAST(10, DataType.INTEGER);
        ConstantAST c2int = new ConstantAST(34, DataType.INTEGER);
        ConstantAST c1float = new ConstantAST(3.2f, DataType.FLOAT);
        ConstantAST c2float = new ConstantAST(75f, DataType.FLOAT);

        aa = new ArithmeticAST(ArithmeticOperation.ADDITION,
                c1int, c2int);
        assertTrue(aa.inferType(new TypeVariable(DataType.ANY), ctx));
        Constant c = (Constant) aa.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.INTEGER));
        assertThat(c.getValue(), equalTo(10 + 34));

        aa = new ArithmeticAST(ArithmeticOperation.SUBTRACTION,
                c1int, c2int);
        assertTrue(aa.inferType(new TypeVariable(DataType.ANY), ctx));
        c = (Constant) aa.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.INTEGER));
        assertThat(c.getValue(), equalTo(10 - 34));

        aa = new ArithmeticAST(ArithmeticOperation.DIVISION,
                c1int, c2int);
        assertTrue(aa.inferType(new TypeVariable(DataType.ANY), ctx));
        c = (Constant) aa.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.INTEGER));
        assertThat(c.getValue(), equalTo(10 / 34));

        aa = new ArithmeticAST(ArithmeticOperation.PRODUCT,
                c1int, c2int);
        assertTrue(aa.inferType(new TypeVariable(DataType.ANY), ctx));
        c = (Constant) aa.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.INTEGER));
        assertThat(c.getValue(), equalTo(10 * 34));

        aa = new ArithmeticAST(ArithmeticOperation.MODULO,
                c1int, c2int);
        assertTrue(aa.inferType(new TypeVariable(DataType.ANY), ctx));
        c = (Constant) aa.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.INTEGER));
        assertThat(c.getValue(), equalTo(10 % 34));

        aa = new ArithmeticAST(ArithmeticOperation.ADDITION,
                c1float, c2float);
        assertTrue(aa.inferType(new TypeVariable(DataType.ANY), ctx));
        c = (Constant) aa.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.FLOAT));
        assertThat(c.getValue(), equalTo(3.2f + 75f));

        aa = new ArithmeticAST(ArithmeticOperation.SUBTRACTION,
                c1float, c2float);
        assertTrue(aa.inferType(new TypeVariable(DataType.ANY), ctx));
        c = (Constant) aa.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.FLOAT));
        assertThat(c.getValue(), equalTo(3.2f - 75f));

        aa = new ArithmeticAST(ArithmeticOperation.DIVISION,
                c1float, c2float);
        assertTrue(aa.inferType(new TypeVariable(DataType.ANY), ctx));
        c = (Constant) aa.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.FLOAT));
        assertThat(c.getValue(), equalTo(3.2f / 75f));

        aa = new ArithmeticAST(ArithmeticOperation.PRODUCT,
                c1float, c2float);
        assertTrue(aa.inferType(new TypeVariable(DataType.ANY), ctx));
        c = (Constant) aa.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.FLOAT));
        assertThat(c.getValue(), equalTo(3.2f * 75f));
    }

    @Test(expected = IllegalStateException.class)
    public void testArithmeticNoType() {
        ArithmeticAST a =
                new ArithmeticAST(ArithmeticOperation.ADDITION, intExp, intExp);
        a.getType();
    }

    @Test(expected = IllegalStateException.class)
    public void testArithmeticDoubleTypeSet() {
        ArithmeticAST a =
                new ArithmeticAST(ArithmeticOperation.ADDITION, intExp, intExp);
        ParserContext ctx = new ParserContext();
        TypeVariable v = new TypeVariable(DataType.ANY);
        a.inferType(v, ctx);
        a.inferType(v, ctx);
    }

    @Test
    public void testArithmeticInverse() {
        MockExpressionAST op = new MockExpressionAST(DataType.INTEGER);
        InverseAST a = new InverseAST(op);
        assertThat(a.getOperand(), equalTo(op));
    }

    @Test
    public void testArithmeticInverseInference() {
        ParserContext ctx = new ParserContext();

        InverseAST i = new InverseAST(intExp);
        TypeVariable v = new TypeVariable(DataType.NUMERIC);
        assertTrue(i.inferType(v, ctx));
        assertThat(v.getType(), equalTo(DataType.INTEGER));
        assertThat(i.getType(), equalTo(DataType.INTEGER));

        v = new TypeVariable(DataType.NUMERIC);
        i = new InverseAST(floatExp);
        assertTrue(i.inferType(v, ctx));
        assertThat(v.getType(), equalTo(DataType.FLOAT));
        assertThat(i.getType(), equalTo(DataType.FLOAT));

        v = new TypeVariable(DataType.INTEGER);
        i = new InverseAST(stringExp);
        assertFalse(i.inferType(v, ctx));

        v = new TypeVariable(DataType.ID);
        i = new InverseAST(floatExp);
        assertFalse(i.inferType(v, ctx));
    }

    @Test
    public void testArithmeticInverseCompile() {
        AttributeReferenceAST iatt =
                new AttributeReferenceAST("i1", DataType.INTEGER);
        ParserContext ctx = new ParserContext();
        AttributeOrder ord = new AttributeOrder();

        InverseAST ia = new InverseAST(iatt);
        assertTrue(ia.inferType(new TypeVariable(DataType.ANY), ctx));
        Inverse i = (Inverse) ia.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(i.getType(), equalTo(DataType.INTEGER));

        ia = new InverseAST(new ConstantAST(10, DataType.INTEGER));
        assertTrue(ia.inferType(new TypeVariable(DataType.ANY), ctx));
        Constant c = (Constant) ia.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.INTEGER));
        assertThat(c.getValue(), equalTo(-10));

        ia = new InverseAST(new ConstantAST(2.5f, DataType.FLOAT));
        assertTrue(ia.inferType(new TypeVariable(DataType.ANY), ctx));
        c = (Constant) ia.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.FLOAT));
        assertThat(c.getValue(), equalTo(-2.5f));
    }

    @Test(expected = IllegalStateException.class)
    public void testArithmeticInverseNoType() {
        InverseAST a = new InverseAST(intExp);
        a.getType();
    }

    @Test(expected = IllegalStateException.class)
    public void testArithmeticInverseDoubleTypeSet() {
        InverseAST a = new InverseAST(intExp);
        ParserContext ctx = new ParserContext();
        TypeVariable v = new TypeVariable(DataType.ANY);
        a.inferType(v, ctx);
        a.inferType(v, ctx);
    }

    @Test
    public void testComparison() {
        MockExpressionAST op1 = new MockExpressionAST(DataType.INTEGER);
        MockExpressionAST op2 = new MockExpressionAST(DataType.INTEGER);
        ComparisonAST c = new ComparisonAST(ComparisonOperation.EQ, op1, op2);
        assertThat(c.getOperation(), equalTo(ComparisonOperation.EQ));
        assertThat(c.getLeftOperand(), equalTo(op1));
        assertThat(c.getRightOperand(), equalTo(op2));
    }

    @Test
    public void testComparisonInference() {
        ParserContext ctx = new ParserContext();

        ComparisonAST c =
                new ComparisonAST(ComparisonOperation.LT, intExp, intExp);
        TypeVariable v = new TypeVariable(DataType.BOOLEAN);
        assertTrue(c.inferType(v, ctx));
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));

        v = new TypeVariable(DataType.ANY);
        c = new ComparisonAST(ComparisonOperation.LT, stringExp, stringExp);
        assertTrue(c.inferType(v, ctx));
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(v.getType(), equalTo(DataType.BOOLEAN));

        v = new TypeVariable(DataType.BOOLEAN);
        c = new ComparisonAST(ComparisonOperation.LT, intExp, floatExp);
        assertFalse(c.inferType(v, ctx));

        v = new TypeVariable(DataType.STRING);
        c = new ComparisonAST(ComparisonOperation.LT, intExp, intExp);
        assertFalse(c.inferType(v, ctx));
    }

    @Test
    public void testComparisonCompile() {
        AttributeReferenceAST i1 =
                new AttributeReferenceAST("i1", DataType.INTEGER);
        AttributeReferenceAST i2 =
                new AttributeReferenceAST("i2", DataType.INTEGER);
        ParserContext ctx = new ParserContext();
        AttributeOrder ord = new AttributeOrder();

        ComparisonAST ca = new ComparisonAST(ComparisonOperation.EQ,
                i1, i2);
        Comparison c = (Comparison) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getOperation(), equalTo(ca.getOperation()));
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));

        // Integer tests
        ConstantAST c1int = new ConstantAST(10, DataType.INTEGER);
        ConstantAST c2int = new ConstantAST(25, DataType.INTEGER);

        ca = new ComparisonAST(ComparisonOperation.EQ, c1int, c2int);
        Constant r = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(r.getType(), equalTo(DataType.BOOLEAN));
        assertThat(r.getValue(), equalTo(LogicValue.FALSE));

        ca = new ComparisonAST(ComparisonOperation.NE, c1int, c2int);
        r = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(r.getType(), equalTo(DataType.BOOLEAN));
        assertThat(r.getValue(), equalTo(LogicValue.TRUE));

        ca = new ComparisonAST(ComparisonOperation.GT, c1int, c2int);
        r = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(r.getType(), equalTo(DataType.BOOLEAN));
        assertThat(r.getValue(), equalTo(LogicValue.FALSE));

        ca = new ComparisonAST(ComparisonOperation.GE, c1int, c2int);
        r = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(r.getType(), equalTo(DataType.BOOLEAN));
        assertThat(r.getValue(), equalTo(LogicValue.FALSE));

        ca = new ComparisonAST(ComparisonOperation.LT, c1int, c2int);
        r = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(r.getType(), equalTo(DataType.BOOLEAN));
        assertThat(r.getValue(), equalTo(LogicValue.TRUE));

        ca = new ComparisonAST(ComparisonOperation.LE, c1int, c2int);
        r = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(r.getType(), equalTo(DataType.BOOLEAN));
        assertThat(r.getValue(), equalTo(LogicValue.TRUE));

        // Float tests
        ConstantAST c1float = new ConstantAST(1.2f, DataType.FLOAT);
        ConstantAST c2float = new ConstantAST(34f, DataType.FLOAT);

        ca = new ComparisonAST(ComparisonOperation.EQ, c1float, c2float);
        r = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(r.getType(), equalTo(DataType.BOOLEAN));
        assertThat(r.getValue(), equalTo(LogicValue.FALSE));

        ca = new ComparisonAST(ComparisonOperation.NE, c1float, c2float);
        r = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(r.getType(), equalTo(DataType.BOOLEAN));
        assertThat(r.getValue(), equalTo(LogicValue.TRUE));

        ca = new ComparisonAST(ComparisonOperation.GT, c1float, c2float);
        r = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(r.getType(), equalTo(DataType.BOOLEAN));
        assertThat(r.getValue(), equalTo(LogicValue.FALSE));

        ca = new ComparisonAST(ComparisonOperation.GE, c1float, c2float);
        r = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(r.getType(), equalTo(DataType.BOOLEAN));
        assertThat(r.getValue(), equalTo(LogicValue.FALSE));

        ca = new ComparisonAST(ComparisonOperation.LT, c1float, c2float);
        r = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(r.getType(), equalTo(DataType.BOOLEAN));
        assertThat(r.getValue(), equalTo(LogicValue.TRUE));

        ca = new ComparisonAST(ComparisonOperation.LE, c1float, c2float);
        r = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(r.getType(), equalTo(DataType.BOOLEAN));
        assertThat(r.getValue(), equalTo(LogicValue.TRUE));

        // Float tests
        ConstantAST c1string = new ConstantAST("asdf", DataType.STRING);
        ConstantAST c2string = new ConstantAST("fdsa", DataType.STRING);

        ca = new ComparisonAST(ComparisonOperation.EQ, c1string, c2string);
        r = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(r.getType(), equalTo(DataType.BOOLEAN));
        assertThat(r.getValue(), equalTo(LogicValue.FALSE));

        ca = new ComparisonAST(ComparisonOperation.NE, c1string, c2string);
        r = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(r.getType(), equalTo(DataType.BOOLEAN));
        assertThat(r.getValue(), equalTo(LogicValue.TRUE));

        ca = new ComparisonAST(ComparisonOperation.GT, c1string, c2string);
        r = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(r.getType(), equalTo(DataType.BOOLEAN));
        assertThat(r.getValue(), equalTo(LogicValue.FALSE));

        ca = new ComparisonAST(ComparisonOperation.GE, c1string, c2string);
        r = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(r.getType(), equalTo(DataType.BOOLEAN));
        assertThat(r.getValue(), equalTo(LogicValue.FALSE));

        ca = new ComparisonAST(ComparisonOperation.LT, c1string, c2string);
        r = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(r.getType(), equalTo(DataType.BOOLEAN));
        assertThat(r.getValue(), equalTo(LogicValue.TRUE));

        ca = new ComparisonAST(ComparisonOperation.LE, c1string, c2string);
        r = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(r.getType(), equalTo(DataType.BOOLEAN));
        assertThat(r.getValue(), equalTo(LogicValue.TRUE));

        // Timestamp tests
        Instant i = Instant.ofEpochSecond(1438850110);
        ConstantAST c1ts = new ConstantAST(i, DataType.TIMESTAMP);
        i = Instant.ofEpochSecond(1438850111);
        ConstantAST c2ts = new ConstantAST(i, DataType.TIMESTAMP);

        ca = new ComparisonAST(ComparisonOperation.EQ, c1ts, c2ts);
        r = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(r.getType(), equalTo(DataType.BOOLEAN));
        assertThat(r.getValue(), equalTo(LogicValue.FALSE));

        ca = new ComparisonAST(ComparisonOperation.NE, c1ts, c2ts);
        r = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(r.getType(), equalTo(DataType.BOOLEAN));
        assertThat(r.getValue(), equalTo(LogicValue.TRUE));

        ca = new ComparisonAST(ComparisonOperation.GT, c1ts, c2ts);
        r = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(r.getType(), equalTo(DataType.BOOLEAN));
        assertThat(r.getValue(), equalTo(LogicValue.FALSE));

        ca = new ComparisonAST(ComparisonOperation.GE, c1ts, c2ts);
        r = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(r.getType(), equalTo(DataType.BOOLEAN));
        assertThat(r.getValue(), equalTo(LogicValue.FALSE));

        ca = new ComparisonAST(ComparisonOperation.LT, c1ts, c2ts);
        r = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(r.getType(), equalTo(DataType.BOOLEAN));
        assertThat(r.getValue(), equalTo(LogicValue.TRUE));

        ca = new ComparisonAST(ComparisonOperation.LE, c1ts, c2ts);
        r = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(r.getType(), equalTo(DataType.BOOLEAN));
        assertThat(r.getValue(), equalTo(LogicValue.TRUE));

        // Bool tests
        ConstantAST c1bool = new ConstantAST(LogicValue.TRUE,
                DataType.BOOLEAN);
        ConstantAST c2bool = new ConstantAST(LogicValue.FALSE,
                DataType.BOOLEAN);

        ca = new ComparisonAST(ComparisonOperation.EQ, c1bool, c2bool);
        r = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(r.getType(), equalTo(DataType.BOOLEAN));
        assertThat(r.getValue(), equalTo(LogicValue.FALSE));

        ca = new ComparisonAST(ComparisonOperation.NE, c1bool, c2bool);
        r = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(r.getType(), equalTo(DataType.BOOLEAN));
        assertThat(r.getValue(), equalTo(LogicValue.TRUE));

        // ID tests
        ConstantAST c1id = new ConstantAST(312, DataType.ID);
        ConstantAST c2id = new ConstantAST(123, DataType.ID);

        ca = new ComparisonAST(ComparisonOperation.EQ, c1id, c2id);
        r = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(r.getType(), equalTo(DataType.BOOLEAN));
        assertThat(r.getValue(), equalTo(LogicValue.FALSE));

        ca = new ComparisonAST(ComparisonOperation.NE, c1id, c2id);
        r = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(r.getType(), equalTo(DataType.BOOLEAN));
        assertThat(r.getValue(), equalTo(LogicValue.TRUE));
    }

    @Test
    public void testComparisonForbiddenOperation() {
        ParserContext ctx = new ParserContext();
        AttributeOrder ord = new AttributeOrder();

        // Bool tests
        ConstantAST c1bool = new ConstantAST(LogicValue.TRUE,
                DataType.BOOLEAN);
        ConstantAST c2bool = new ConstantAST(LogicValue.FALSE,
                DataType.BOOLEAN);

        ComparisonAST ca =
                new ComparisonAST(ComparisonOperation.GT, c1bool, c2bool);
        Constant c = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(1));
        assertThat(c, equalTo(Constant.NULL));

        ctx = new ParserContext();
        ca = new ComparisonAST(ComparisonOperation.GE, c1bool, c2bool);
        c = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(1));
        assertThat(c, equalTo(Constant.NULL));

        ctx = new ParserContext();
        ca = new ComparisonAST(ComparisonOperation.LT, c1bool, c2bool);
        c = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(1));
        assertThat(c, equalTo(Constant.NULL));

        ctx = new ParserContext();
        ca = new ComparisonAST(ComparisonOperation.LE, c1bool, c2bool);
        c = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(1));
        assertThat(c, equalTo(Constant.NULL));

        // ID tests
        ConstantAST c1id = new ConstantAST(312, DataType.ID);
        ConstantAST c2id = new ConstantAST(123, DataType.ID);

        ctx = new ParserContext();
        ca = new ComparisonAST(ComparisonOperation.GT, c1id, c2id);
        c = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(1));
        assertThat(c, equalTo(Constant.NULL));

        ctx = new ParserContext();
        ca = new ComparisonAST(ComparisonOperation.GE, c1id, c2id);
        c = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(1));
        assertThat(c, equalTo(Constant.NULL));

        ctx = new ParserContext();
        ca = new ComparisonAST(ComparisonOperation.LT, c1id, c2id);
        c = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(1));
        assertThat(c, equalTo(Constant.NULL));

        ctx = new ParserContext();
        ca = new ComparisonAST(ComparisonOperation.LE, c1id, c2id);
        c = (Constant) ca.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(1));
        assertThat(c, equalTo(Constant.NULL));
    }

    @Test(expected = IllegalStateException.class)
    public void testComparisonNoType() {
        ComparisonAST c =
                new ComparisonAST(ComparisonOperation.EQ, intExp, intExp);
        c.getType();
    }

    @Test(expected = IllegalStateException.class)
    public void testComparisonDoubleTypeSet() {
        ComparisonAST c =
                new ComparisonAST(ComparisonOperation.EQ, intExp, intExp);
        ParserContext ctx = new ParserContext();
        TypeVariable v = new TypeVariable(DataType.ANY);
        c.inferType(v, ctx);
        c.inferType(v, ctx);
    }

    @Test
    public void testBetween() {
        MockExpressionAST op1 = new MockExpressionAST(DataType.INTEGER);
        MockExpressionAST op2 = new MockExpressionAST(DataType.INTEGER);
        MockExpressionAST op3 = new MockExpressionAST(DataType.INTEGER);
        BetweenAST b = new BetweenAST(op1, op2, op3);
        assertThat(b.getOperand(), equalTo(op1));
        assertThat(b.getMin(), equalTo(op2));
        assertThat(b.getMax(), equalTo(op3));
    }

    @Test
    public void testBetweenInference() {
        ParserContext ctx = new ParserContext();

        BetweenAST b = new BetweenAST(intExp, intExp, intExp);
        TypeVariable v = new TypeVariable(DataType.BOOLEAN);
        assertTrue(b.inferType(v, ctx));
        assertThat(b.getType(), equalTo(DataType.BOOLEAN));

        v = new TypeVariable(DataType.BOOLEAN);
        b = new BetweenAST(floatExp, floatExp, floatExp);
        assertTrue(b.inferType(v, ctx));
        assertThat(b.getType(), equalTo(DataType.BOOLEAN));

        v = new TypeVariable(DataType.INTEGER);
        b = new BetweenAST(floatExp, floatExp, floatExp);
        assertFalse(b.inferType(v, ctx));

        v = new TypeVariable(DataType.BOOLEAN);
        b = new BetweenAST(boolExp, boolExp, boolExp);
        assertFalse(b.inferType(v, ctx));

        v = new TypeVariable(DataType.BOOLEAN);
        b = new BetweenAST(boolExp, boolExp, intExp);
        assertFalse(b.inferType(v, ctx));
    }

    @Test
    public void testBetweenCompile() {
        AttributeReferenceAST i1 =
                new AttributeReferenceAST("i1", DataType.INTEGER);
        AttributeReferenceAST i2 =
                new AttributeReferenceAST("i2", DataType.INTEGER);
        AttributeReferenceAST i3 =
                new AttributeReferenceAST("i3", DataType.INTEGER);
        ParserContext ctx = new ParserContext();
        AttributeOrder ord = new AttributeOrder();

        BetweenAST ba = new BetweenAST(i1, i2, i3);
        Between b = (Between) ba.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(b.getType(), equalTo(DataType.BOOLEAN));

        // INTEGER test
        ConstantAST minInt = new ConstantAST(10, DataType.INTEGER);
        ConstantAST midInt = new ConstantAST(25, DataType.INTEGER);
        ConstantAST maxInt = new ConstantAST(30, DataType.INTEGER);

        ba = new BetweenAST(midInt, minInt, maxInt);
        Constant c = (Constant) ba.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.TRUE));

        ba = new BetweenAST(maxInt, minInt, midInt);
        c = (Constant) ba.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.TRUE));

        // FLOAT test
        ConstantAST minFloat = new ConstantAST(1.0f, DataType.FLOAT);
        ConstantAST midFloat = new ConstantAST(2.5f, DataType.FLOAT);
        ConstantAST maxFloat = new ConstantAST(3.5f, DataType.FLOAT);

        ba = new BetweenAST(midFloat, minFloat, maxFloat);
        c = (Constant) ba.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.TRUE));

        ba = new BetweenAST(maxFloat, minFloat, midFloat);
        c = (Constant) ba.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.TRUE));

        // STRING test
        ConstantAST minString = new ConstantAST("aaa", DataType.STRING);
        ConstantAST midString = new ConstantAST("bbb", DataType.STRING);
        ConstantAST maxString = new ConstantAST("ccc", DataType.STRING);

        ba = new BetweenAST(midString, minString, maxString);
        c = (Constant) ba.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.TRUE));

        ba = new BetweenAST(maxString, minString, midString);
        c = (Constant) ba.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.TRUE));

        // TIMESTAMP test
        Instant i = Instant.ofEpochSecond(1438850110);
        ConstantAST minTs = new ConstantAST(i, DataType.TIMESTAMP);
        i = Instant.ofEpochSecond(1438850111);
        ConstantAST midTs = new ConstantAST(i, DataType.TIMESTAMP);
        i = Instant.ofEpochSecond(1438850112);
        ConstantAST maxTs = new ConstantAST(i, DataType.TIMESTAMP);

        ba = new BetweenAST(midTs, minTs, maxTs);
        c = (Constant) ba.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.TRUE));

        ba = new BetweenAST(maxTs, minTs, midTs);
        c = (Constant) ba.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.TRUE));
    }

    @Test
    public void testBetweenForbiddenTypes() {
        ParserContext ctx = new ParserContext();
        AttributeOrder ord = new AttributeOrder();

        // ID test
        ConstantAST minId = new ConstantAST(1, DataType.ID);
        ConstantAST midId = new ConstantAST(2, DataType.ID);
        ConstantAST maxId = new ConstantAST(3, DataType.ID);

        BetweenAST ba = new BetweenAST(midId, minId, maxId);
        Constant c = (Constant) ba.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(1));
        assertThat(c, equalTo(Constant.NULL));

        ctx = new ParserContext();
        ba = new BetweenAST(maxId, minId, midId);
        c = (Constant) ba.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(1));
        assertThat(c, equalTo(Constant.NULL));

        // BOOLEAN test
        ConstantAST minBool = new ConstantAST(1, DataType.BOOLEAN);
        ConstantAST midBool = new ConstantAST(2, DataType.BOOLEAN);
        ConstantAST maxBool = new ConstantAST(3, DataType.BOOLEAN);

        ctx = new ParserContext();
        ba = new BetweenAST(midBool, minBool, maxBool);
        c = (Constant) ba.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(1));
        assertThat(c, equalTo(Constant.NULL));

        ctx = new ParserContext();
        ba = new BetweenAST(maxBool, minBool, midBool);
        c = (Constant) ba.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(1));
        assertThat(c, equalTo(Constant.NULL));
    }

    @Test(expected = IllegalStateException.class)
    public void testBetweenNoType() {
        BetweenAST b = new BetweenAST(intExp, intExp, intExp);
        b.getType();
    }

    @Test(expected = IllegalStateException.class)
    public void testBetweenDoubleTypeSet() {
        BetweenAST b = new BetweenAST(intExp, intExp, intExp);
        ParserContext ctx = new ParserContext();
        TypeVariable v = new TypeVariable(DataType.ANY);
        b.inferType(v, ctx);
        b.inferType(v, ctx);
    }

    @Test
    public void testAggregate() {
        MockExpressionAST op1 = new MockExpressionAST(DataType.INTEGER);
        MockExpressionAST op2 = new MockExpressionAST(DataType.BOOLEAN);
        WindowSizeAST ws = new WindowSizeAST(
                new ConstantAST(12, DataType.INTEGER));
        AggregateAST a = new AggregateAST(AggregateOperation.SUM, op1, ws, op2);
        assertThat(a.getOperation(), equalTo(AggregateOperation.SUM));
        assertThat(a.getOperand(), equalTo(op1));
        assertThat(a.getWindowSize(), equalTo(ws));
        assertThat(a.getFilter(), equalTo(op2));
    }

    @Test
    public void testAggregateInference() {
        WindowSizeAST ws = new WindowSizeAST(
                new ConstantAST(12, DataType.INTEGER));
        ParserContext ctx = new ParserContext();

        AggregateAST a =
                new AggregateAST(AggregateOperation.SUM, intExp, ws, boolExp);
        TypeVariable v = new TypeVariable(DataType.INTEGER);
        assertTrue(a.inferType(v, ctx));
        assertThat(a.getType(), equalTo(DataType.INTEGER));

        v = new TypeVariable(DataType.FLOAT);
        a = new AggregateAST(AggregateOperation.SUM, floatExp, ws, boolExp);
        assertTrue(a.inferType(v, ctx));
        assertThat(a.getType(), equalTo(DataType.FLOAT));

        v = new TypeVariable(DataType.INTEGER);
        a = new AggregateAST(AggregateOperation.SUM, intExp, ws, stringExp);
        assertFalse(a.inferType(v, ctx));

        v = new TypeVariable(DataType.FLOAT);
        a = new AggregateAST(AggregateOperation.SUM, intExp, ws, boolExp);
        assertFalse(a.inferType(v, ctx));

        v = new TypeVariable(DataType.INTEGER);
        a = new AggregateAST(AggregateOperation.COUNT, null, ws, boolExp);
        assertTrue(a.inferType(v, ctx));
        assertThat(a.getType(), equalTo(DataType.INTEGER));

        v = new TypeVariable(DataType.NUMERIC);
        a = new AggregateAST(AggregateOperation.COUNT, null, ws, boolExp);
        assertTrue(a.inferType(v, ctx));
        assertThat(a.getType(), equalTo(DataType.INTEGER));
        assertThat(v.getType(), equalTo(DataType.INTEGER));

        v = new TypeVariable(DataType.FLOAT);
        a = new AggregateAST(AggregateOperation.COUNT, null, ws, boolExp);
        assertFalse(a.inferType(v, ctx));

        v = new TypeVariable(DataType.NUMERIC);
        a = new AggregateAST(AggregateOperation.COUNT, null, ws, floatExp);
        assertFalse(a.inferType(v, ctx));
    }

    @Test
    public void testAggregateCompile() {
        WindowSizeAST ws = new WindowSizeAST(
                new ConstantAST(12, DataType.INTEGER));
        ParserContext ctx = new ParserContext();
        WindowSize cws = ws.compile(ctx);
        assertFalse(ctx.hasErrors());
        AttributeOrder ord = new AttributeOrder();

        AttributeReferenceAST iatt =
                new AttributeReferenceAST("i1", DataType.INTEGER);
        AggregateAST aa = new AggregateAST(AggregateOperation.MIN,
                iatt, ws, ConstantAST.TRUE);
        assertTrue(aa.inferType(new TypeVariable(DataType.ANY), ctx));
        MinAggregate min = (MinAggregate) aa.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(min.getType(), equalTo(DataType.INTEGER));
        assertThat(min.getWindowSize(), equalTo(cws));
        assertThat(min.getFilter(), equalTo(Constant.TRUE));

        iatt = new AttributeReferenceAST("i2", DataType.INTEGER);
        aa = new AggregateAST(AggregateOperation.MAX,
                iatt, ws, ConstantAST.TRUE);
        assertTrue(aa.inferType(new TypeVariable(DataType.ANY), ctx));
        MaxAggregate max = (MaxAggregate) aa.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(max.getType(), equalTo(DataType.INTEGER));
        assertThat(max.getWindowSize(), equalTo(cws));
        assertThat(max.getFilter(), equalTo(Constant.TRUE));

        iatt = new AttributeReferenceAST("i3", DataType.INTEGER);
        aa = new AggregateAST(AggregateOperation.SUM,
                iatt, ws, ConstantAST.TRUE);
        assertTrue(aa.inferType(new TypeVariable(DataType.ANY), ctx));
        SumAggregate sum = (SumAggregate) aa.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(sum.getType(), equalTo(DataType.INTEGER));
        assertThat(sum.getWindowSize(), equalTo(cws));
        assertThat(sum.getFilter(), equalTo(Constant.TRUE));

        iatt = new AttributeReferenceAST("i4", DataType.INTEGER);
        aa = new AggregateAST(AggregateOperation.AVG,
                iatt, ws, ConstantAST.TRUE);
        assertTrue(aa.inferType(new TypeVariable(DataType.ANY), ctx));
        AvgAggregate avg = (AvgAggregate) aa.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(avg.getType(), equalTo(DataType.FLOAT));
        assertThat(avg.getWindowSize(), equalTo(cws));
        assertThat(avg.getFilter(), equalTo(Constant.TRUE));

        iatt = new AttributeReferenceAST("i5", DataType.INTEGER);
        aa = new AggregateAST(AggregateOperation.COUNT,
                iatt, ws, ConstantAST.TRUE);
        assertTrue(aa.inferType(new TypeVariable(DataType.ANY), ctx));
        CountAggregate count = (CountAggregate) aa.toExpression(ctx, ord);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(count.getType(), equalTo(DataType.INTEGER));
        assertThat(count.getWindowSize(), equalTo(cws));
        assertThat(count.getFilter(), equalTo(Constant.TRUE));
    }

    @Test(expected = IllegalStateException.class)
    public void testAggregateNoType() {
        WindowSizeAST ws = new WindowSizeAST(
                new ConstantAST(4, DataType.INTEGER));
        AggregateAST a =
                new AggregateAST(AggregateOperation.SUM, intExp, ws, boolExp);
        a.getType();
    }

    @Test(expected = IllegalStateException.class)
    public void testAggregateDoubleTypeSet() {
        WindowSizeAST ws = new WindowSizeAST(
                new ConstantAST(4, DataType.INTEGER));
        AggregateAST a =
                new AggregateAST(AggregateOperation.SUM, intExp, ws, boolExp);
        ParserContext ctx = new ParserContext();
        TypeVariable v = new TypeVariable(DataType.ANY);
        a.inferType(v, ctx);
        a.inferType(v, ctx);
    }

    @Test(expected = IllegalStateException.class)
    public void testAggregateCountNoType() {
        WindowSizeAST ws = new WindowSizeAST(
                new ConstantAST(4, DataType.INTEGER));
        AggregateAST a =
                new AggregateAST(AggregateOperation.COUNT, null, ws, boolExp);
        a.getType();
    }

    @Test(expected = IllegalStateException.class)
    public void testAggregateContDoubleTypeSet() {
        WindowSizeAST ws = new WindowSizeAST(
                new ConstantAST(4, DataType.INTEGER));
        AggregateAST a =
                new AggregateAST(AggregateOperation.COUNT, null, ws, boolExp);
        ParserContext ctx = new ParserContext();
        TypeVariable v = new TypeVariable(DataType.ANY);
        a.inferType(v, ctx);
        a.inferType(v, ctx);
    }

    @Test
    public void testInference1() {
        ExpressionAST op1 = new ConstantAST("3", DataType.INTEGER);
        ExpressionAST op2 = new AttributeReferenceAST("integer", DataType.ANY);
        ExpressionAST e =
                new ArithmeticAST(ArithmeticOperation.ADDITION, op1, op2);

        ParserContext ctx = new ParserContext();
        TypeVariable v = new TypeVariable(DataType.ANY);
        assertTrue(e.inferType(v, ctx));
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(v.getType(), equalTo(DataType.INTEGER));
        assertThat(e.getType(), equalTo(DataType.INTEGER));

        Map<String, DataType> tm = ctx.getAttributeTypes();
        assertThat(tm.get("integer"), equalTo(DataType.INTEGER));
    }

    @Test
    public void testInference2() {
        ExpressionAST op1 = new AttributeReferenceAST("integer", DataType.ANY);
        ExpressionAST op2 = new ConstantAST("3", DataType.INTEGER);
        ExpressionAST e =
                new ComparisonAST(ComparisonOperation.GT, op1, op2);

        ParserContext ctx = new ParserContext();
        TypeVariable v = new TypeVariable(DataType.ANY);
        assertTrue(e.inferType(v, ctx));
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(v.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));

        Map<String, DataType> tm = ctx.getAttributeTypes();
        assertThat(tm.get("integer"), equalTo(DataType.INTEGER));
    }

}
