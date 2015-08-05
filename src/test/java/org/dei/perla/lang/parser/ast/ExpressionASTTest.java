package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.TypeVariable;
import org.dei.perla.lang.query.expression.*;
import org.dei.perla.lang.query.statement.WindowSize;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

/**
 * @author Guido Rota 31/07/15.
 */
public class ExpressionASTTest {

    private static final MockExpressionAST boolExp =
            new MockExpressionAST(TypeClass.BOOLEAN);
    private static final MockExpressionAST intExp =
            new MockExpressionAST(TypeClass.INTEGER);
    private static final MockExpressionAST floatExp =
            new MockExpressionAST(TypeClass.FLOAT);
    private static final MockExpressionAST stringExp =
            new MockExpressionAST(TypeClass.STRING);
    private static final MockExpressionAST timestampExp =
            new MockExpressionAST(TypeClass.TIMESTAMP);

    private static final AttributeAST idAtt =
            new AttributeAST("id", TypeClass.ID);
    private static final AttributeAST intAtt =
            new AttributeAST("integer", TypeClass.INTEGER);
    private static final AttributeAST floatAtt =
            new AttributeAST("float", TypeClass.FLOAT);
    private static final AttributeAST stringAtt =
            new AttributeAST("string", TypeClass.STRING);
    private static final AttributeAST boolAtt =
            new AttributeAST("bool", TypeClass.BOOLEAN);
    private static final AttributeAST timestampAtt =
            new AttributeAST("timestamp", TypeClass.TIMESTAMP);

    @Test
    public void testConstantAST() {
        ConstantAST c = new ConstantAST(10, TypeClass.INTEGER);
        assertThat(c.getTypeClass(), equalTo(TypeClass.INTEGER));
        assertThat(c.getValue(), equalTo(10));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonConcreteConstantAST() {
        ConstantAST c = new ConstantAST(10, TypeClass.ANY);
    }

    @Test
    public void testConstantInference() {
        ParserContext ctx = new ParserContext();

        ConstantAST c = new ConstantAST(10, TypeClass.INTEGER);
        TypeVariable v = new TypeVariable(TypeClass.INTEGER);
        boolean res = c.inferType(v, ctx);
        assertTrue(res);
        assertThat(c.getTypeClass(), equalTo(TypeClass.INTEGER));
        assertThat(v.getTypeClass(), equalTo(TypeClass.INTEGER));

        v = new TypeVariable(TypeClass.NUMERIC);
        res = c.inferType(v, ctx);
        assertTrue(res);
        assertThat(c.getTypeClass(), equalTo(TypeClass.INTEGER));
        assertThat(v.getTypeClass(), equalTo(TypeClass.INTEGER));

        v = new TypeVariable(TypeClass.ANY);
        res = c.inferType(v, ctx);
        assertTrue(res);
        assertThat(c.getTypeClass(), equalTo(TypeClass.INTEGER));
        assertThat(v.getTypeClass(), equalTo(TypeClass.INTEGER));

        v = new TypeVariable(TypeClass.STRING);
        res = c.inferType(v, ctx);
        assertFalse(res);
        assertThat(v.getTypeClass(), equalTo(TypeClass.STRING));
    }

    @Test
    public void testConstantCompile() {
        ParserContext ctx = new ParserContext();
        Map<String, Integer> atts = new HashMap<>();

        ConstantAST ca = new ConstantAST(10, TypeClass.INTEGER);
        Constant c = (Constant) ca.compile(ctx, atts);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.INTEGER));
        assertThat(c.getValue(), equalTo(10));

        ca = new ConstantAST("test", TypeClass.STRING);
        c = (Constant) ca.compile(ctx, atts);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.STRING));
        assertThat(c.getValue(), equalTo("test"));
    }

    @Test(expected = IllegalStateException.class)
    public void testConstantASTSetType() {
        ConstantAST c = new ConstantAST("test", TypeClass.STRING);
        c.setType(new TypeVariable(TypeClass.ANY));
    }

    @Test
    public void testAttribute() {
        AttributeAST a = new AttributeAST("att", TypeClass.ANY);
        assertThat(a.getId(), equalTo("att"));
        assertThat(a.getTypeClass(), equalTo(TypeClass.ANY));
    }

    @Test
    public void testAttributeInference() {
        ParserContext ctx = new ParserContext();

        AttributeAST a = new AttributeAST("att", TypeClass.ANY);
        TypeVariable v = new TypeVariable(TypeClass.NUMERIC);
        boolean res = a.inferType(v, ctx);
        assertTrue(res);
        assertThat(a.getTypeClass(), equalTo(TypeClass.NUMERIC));

        a = new AttributeAST("att", TypeClass.INTEGER);
        res = a.inferType(v, ctx);
        assertTrue(res);
        assertThat(a.getTypeClass(), equalTo(TypeClass.INTEGER));

        assertThat(ctx.getErrorCount(), equalTo(0));
        Map<String, TypeClass> attTypes = ctx.getAttributeTypes();
        assertThat(attTypes.get("att"), equalTo(TypeClass.INTEGER));

        a = new AttributeAST("asd", TypeClass.NUMERIC);
        v = new TypeVariable(TypeClass.ANY);
        res = a.inferType(v, ctx);
        assertTrue(res);
        assertThat(v.getTypeClass(), equalTo(TypeClass.NUMERIC));
        assertThat(a.getTypeClass(), equalTo(TypeClass.NUMERIC));
    }

    @Test
    public void testAttributeCompile() {
        ParserContext ctx = new ParserContext();
        Map<String, Integer> atts = new HashMap<>();

        AttributeAST aa = new AttributeAST("att1", TypeClass.INTEGER);
        Attribute a = (Attribute) aa.compile(ctx, atts);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(a.getId(), equalTo(aa.getId()));
        assertThat(a.getType(), equalTo(aa.getTypeClass().toDataType()));
        assertThat(a.getIndex(), equalTo(0));
        assertThat(atts.get(aa.getId()), equalTo(0));

        aa = new AttributeAST("att2", TypeClass.STRING);
        a = (Attribute) aa.compile(ctx, atts);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(a.getId(), equalTo(aa.getId()));
        assertThat(a.getType(), equalTo(aa.getTypeClass().toDataType()));
        assertThat(a.getIndex(), equalTo(1));
        assertThat(atts.get(aa.getId()), equalTo(1));

        aa = new AttributeAST("att1", TypeClass.INTEGER);
        a = (Attribute) aa.compile(ctx, atts);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(a.getId(), equalTo(aa.getId()));
        assertThat(a.getType(), equalTo(aa.getTypeClass().toDataType()));
        assertThat(a.getIndex(), equalTo(0));

        aa = new AttributeAST("att3", TypeClass.ANY);
        Constant c = (Constant) aa.compile(ctx, atts);
        assertThat(ctx.getErrorCount(), equalTo(1));
        assertThat(c, equalTo(Constant.NULL));
    }

    @Test(expected = IllegalStateException.class)
    public void testAttributeReferenceSetType() {
        AttributeAST a =
                new AttributeAST("att", TypeClass.ANY);
        a.setType(new TypeVariable(TypeClass.NUMERIC));
    }

    @Test
    public void testBooleanBinary() {
        MockExpressionAST left = new MockExpressionAST(TypeClass.BOOLEAN);
        MockExpressionAST right = new MockExpressionAST(TypeClass.BOOLEAN);
        BoolAST b = new BoolAST(BoolOperation.OR, left, right);
        assertThat(b.getLeftOperand(), equalTo(left));
        assertThat(b.getRightOperand(), equalTo(right));
    }

    @Test
    public void testBooleanBinaryInference() {
        ParserContext ctx = new ParserContext();

        BoolAST b = new BoolAST(BoolOperation.AND, boolExp, boolExp);
        TypeVariable v = new TypeVariable(TypeClass.BOOLEAN);
        assertTrue(b.inferType(v, ctx));
        assertThat(b.getTypeClass(), equalTo(TypeClass.BOOLEAN));

        b = new BoolAST(BoolOperation.AND, boolExp, intExp);
        assertFalse(b.inferType(v, ctx));
        assertThat(b.getTypeClass(), equalTo(TypeClass.BOOLEAN));

        b = new BoolAST(BoolOperation.AND, intExp, boolExp);
        assertFalse(b.inferType(v, ctx));
        assertThat(b.getTypeClass(), equalTo(TypeClass.BOOLEAN));

        b = new BoolAST(BoolOperation.AND, intExp, stringExp);
        assertFalse(b.inferType(v, ctx));
        assertThat(b.getTypeClass(), equalTo(TypeClass.BOOLEAN));
    }

    @Test
    public void testBooleanBinaryCompilation() {
        ParserContext ctx = new ParserContext();
        Map<String, Integer> atts = new HashMap<>();

        BoolAST ba = new BoolAST(BoolOperation.AND, boolAtt, boolAtt);
        Bool b = (Bool) ba.compile(ctx, atts);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(b.getOperation(), equalTo(ba.getOperation()));
        assertThat(b.getType(), equalTo(DataType.BOOLEAN));

        ba = new BoolAST(BoolOperation.OR, ConstantAST.TRUE, ConstantAST.FALSE);
        Constant c = (Constant) ba.compile(ctx, atts);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.TRUE));
    }

    @Test(expected = IllegalStateException.class)
    public void testBooleanBinaryNoType() {
        BoolAST b = new BoolAST(BoolOperation.OR, boolExp, boolExp);
        b.getTypeClass();
    }

    @Test(expected = IllegalStateException.class)
    public void testBooleanBinaryDoubleTypeSet() {
        BoolAST b = new BoolAST(BoolOperation.OR, boolExp, boolExp);
        ParserContext ctx = new ParserContext();
        TypeVariable v = new TypeVariable(TypeClass.ANY);
        b.inferType(v, ctx);
        b.inferType(v, ctx);
    }

    @Test
    public void testBooleanNot() {
        MockExpressionAST op = new MockExpressionAST(TypeClass.BOOLEAN);
        NotAST b = new NotAST(op);
        assertThat(b.getOperand(), equalTo(op));
    }

    @Test
    public void testBooleanNotInference() {
        ParserContext ctx = new ParserContext();

        NotAST n = new NotAST(boolExp);
        TypeVariable v = new TypeVariable(TypeClass.BOOLEAN);
        assertTrue(n.inferType(v, ctx));
        assertThat(n.getTypeClass(), equalTo(TypeClass.BOOLEAN));

        n = new NotAST(intExp);
        assertFalse(n.inferType(v, ctx));
        assertThat(n.getTypeClass(), equalTo(TypeClass.BOOLEAN));

        v = new TypeVariable(TypeClass.ANY);
        n = new NotAST(boolExp);
        assertTrue(n.inferType(v, ctx));
        assertThat(n.getTypeClass(), equalTo(TypeClass.BOOLEAN));

        v = new TypeVariable(TypeClass.NUMERIC);
        n = new NotAST(boolExp);
        assertFalse(n.inferType(v, ctx));
    }

    @Test
    public void testBooleanNotCompile() {
        ParserContext ctx = new ParserContext();
        Map<String, Integer> atts = new HashMap<>();

        NotAST na = new NotAST(boolAtt);
        Not n = (Not) na.compile(ctx, atts);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(n.getType(), equalTo(DataType.BOOLEAN));

        na = new NotAST(ConstantAST.TRUE);
        assertThat(ctx.getErrorCount(), equalTo(0));
        Constant c = (Constant) na.compile(ctx, atts);
        assertThat(c.getType(), equalTo(DataType.BOOLEAN));
        assertThat(c.getValue(), equalTo(LogicValue.FALSE));
    }

    @Test(expected = IllegalStateException.class)
    public void testBooleanNotNoType() {
        NotAST b = new NotAST(boolExp);
        b.getTypeClass();
    }

    @Test(expected = IllegalStateException.class)
    public void testBooleanNotDoubleTypeSet() {
        NotAST b = new NotAST(boolExp);
        ParserContext ctx = new ParserContext();
        TypeVariable v = new TypeVariable(TypeClass.ANY);
        b.inferType(v, ctx);
        b.inferType(v, ctx);
    }

    @Test
    public void testBitwiseBinary() {
        MockExpressionAST left = new MockExpressionAST(TypeClass.INTEGER);
        MockExpressionAST right = new MockExpressionAST(TypeClass.INTEGER);
        BitwiseAST b = new BitwiseAST(BitwiseOperation.OR, left, right);
        assertThat(b.getLeftOperand(), equalTo(left));
        assertThat(b.getRightOperand(), equalTo(right));
    }

    @Test
    public void testBitwiseBinaryInference() {
        ParserContext ctx = new ParserContext();

        BitwiseAST b = new BitwiseAST(BitwiseOperation.AND, intExp, intExp);
        TypeVariable v = new TypeVariable(TypeClass.INTEGER);
        assertTrue(b.inferType(v, ctx));
        assertThat(b.getTypeClass(), equalTo(TypeClass.INTEGER));

        b = new BitwiseAST(BitwiseOperation.AND, boolExp, intExp);
        assertFalse(b.inferType(v, ctx));

        b = new BitwiseAST(BitwiseOperation.AND, intExp, boolExp);
        assertFalse(b.inferType(v, ctx));

        b = new BitwiseAST(BitwiseOperation.AND, intExp, stringExp);
        assertFalse(b.inferType(v, ctx));
    }

    @Test(expected = IllegalStateException.class)
    public void testBitwiseBinaryNoType() {
        BitwiseAST b = new BitwiseAST(BitwiseOperation.XOR, intExp, intExp);
        b.getTypeClass();
    }

    @Test(expected = IllegalStateException.class)
    public void testBitwiseBinaryDoubleTypeSet() {
        BitwiseAST b = new BitwiseAST(BitwiseOperation.XOR, intExp, intExp);
        ParserContext ctx = new ParserContext();
        TypeVariable v = new TypeVariable(TypeClass.ANY);
        b.inferType(v, ctx);
        b.inferType(v, ctx);
    }

    @Test
    public void testBitwiseNot() {
        MockExpressionAST op = new MockExpressionAST(TypeClass.INTEGER);
        BitwiseNotAST b = new BitwiseNotAST(op);
        assertThat(b.getOperand(), equalTo(op));
    }

    @Test
    public void testBitwiseNotInference() {
        ParserContext ctx = new ParserContext();

        BitwiseNotAST b = new BitwiseNotAST(intExp);
        TypeVariable v = new TypeVariable(TypeClass.INTEGER);
        assertTrue(b.inferType(v, ctx));
        assertThat(b.getTypeClass(), equalTo(TypeClass.INTEGER));

        b = new BitwiseNotAST(stringExp);
        assertFalse(b.inferType(v, ctx));

        v = new TypeVariable(TypeClass.ANY);
        b = new BitwiseNotAST(intExp);
        assertTrue(b.inferType(v, ctx));
        assertThat(b.getTypeClass(), equalTo(TypeClass.INTEGER));

        v = new TypeVariable(TypeClass.NUMERIC);
        b = new BitwiseNotAST(intExp);
        assertTrue(b.inferType(v, ctx));
        assertThat(b.getTypeClass(), equalTo(TypeClass.INTEGER));
    }

    @Test(expected = IllegalStateException.class)
    public void testBitwiseNotNoType() {
        BitwiseNotAST b = new BitwiseNotAST(intExp);
        b.getTypeClass();
    }

    @Test(expected = IllegalStateException.class)
    public void testBitwiseNotDoubleTypeSet() {
        BitwiseNotAST b = new BitwiseNotAST(intExp);
        ParserContext ctx = new ParserContext();
        TypeVariable v = new TypeVariable(TypeClass.ANY);
        b.inferType(v, ctx);
        b.inferType(v, ctx);
    }

    @Test
    public void testIs() {
        MockExpressionAST op = new MockExpressionAST(TypeClass.BOOLEAN);
        IsAST is = new IsAST(op, LogicValue.UNKNOWN);
        assertThat(is.getOperand(), equalTo(op));
        assertThat(is.getValue(), equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testIsInference() {
        ParserContext ctx = new ParserContext();

        IsAST is = new IsAST(boolExp, LogicValue.TRUE);
        TypeVariable v = new TypeVariable(TypeClass.BOOLEAN);
        assertTrue(is.inferType(v, ctx));
        assertThat(is.getTypeClass(), equalTo(TypeClass.BOOLEAN));

        is = new IsAST(intExp, LogicValue.TRUE);
        assertFalse(is.inferType(v, ctx));

        v = new TypeVariable(TypeClass.NUMERIC);
        is = new IsAST(boolExp, LogicValue.TRUE);
        assertFalse(is.inferType(v, ctx));

        v = new TypeVariable(TypeClass.ANY);
        is = new IsAST(boolExp, LogicValue.TRUE);
        assertTrue(is.inferType(v, ctx));
        assertThat(is.getTypeClass(), equalTo(TypeClass.BOOLEAN));
    }

    @Test(expected = IllegalStateException.class)
    public void testIsNoType() {
        IsAST is = new IsAST(boolExp, LogicValue.FALSE);
        is.getTypeClass();
    }

    @Test(expected = IllegalStateException.class)
    public void testIsDoubleTypeSet() {
        IsAST is = new IsAST(boolExp, LogicValue.FALSE);
        ParserContext ctx = new ParserContext();
        TypeVariable v = new TypeVariable(TypeClass.ANY);
        is.inferType(v, ctx);
        is.inferType(v, ctx);
    }

    @Test
    public void testIsNull() {
        MockExpressionAST op = new MockExpressionAST(TypeClass.TIMESTAMP);
        IsNullAST is = new IsNullAST(op);
        assertThat(is.getOperand(), equalTo(op));
    }

    @Test
    public void testIsNullInference() {
        ParserContext ctx = new ParserContext();

        IsNullAST is = new IsNullAST(intExp);
        TypeVariable v = new TypeVariable(TypeClass.BOOLEAN);
        assertTrue(is.inferType(v, ctx));
        assertThat(is.getTypeClass(), equalTo(TypeClass.BOOLEAN));
        is = new IsNullAST(floatExp);
        assertTrue(is.inferType(v, ctx));
        assertThat(is.getTypeClass(), equalTo(TypeClass.BOOLEAN));
        is = new IsNullAST(stringExp);
        assertTrue(is.inferType(v, ctx));
        assertThat(is.getTypeClass(), equalTo(TypeClass.BOOLEAN));
        is = new IsNullAST(boolExp);
        assertTrue(is.inferType(v, ctx));
        assertThat(is.getTypeClass(), equalTo(TypeClass.BOOLEAN));
        is = new IsNullAST(timestampExp);
        assertTrue(is.inferType(v, ctx));
        assertThat(is.getTypeClass(), equalTo(TypeClass.BOOLEAN));

        v = new TypeVariable(TypeClass.NUMERIC);
        is = new IsNullAST(intExp);
        assertFalse(is.inferType(v, ctx));
    }

    @Test(expected = IllegalStateException.class)
    public void testIsNullNoType() {
        IsNullAST is = new IsNullAST(intExp);
        is.getTypeClass();
    }

    @Test(expected = IllegalStateException.class)
    public void testIsNullDoubleTypeSet() {
        IsNullAST is = new IsNullAST(intExp);
        ParserContext ctx = new ParserContext();
        TypeVariable v = new TypeVariable(TypeClass.ANY);
        is.inferType(v, ctx);
        is.inferType(v, ctx);
    }

    @Test
    public void testLike() {
        MockExpressionAST op = new MockExpressionAST(TypeClass.STRING);
        LikeAST l = new LikeAST(op, "test");
        assertThat(l.getOperand(), equalTo(op));
        assertThat(l.getPattern(), equalTo("test"));
    }

    @Test
    public void testLikeInference() {
        ParserContext ctx = new ParserContext();

        LikeAST l = new LikeAST(stringExp, "test");
        TypeVariable v = new TypeVariable(TypeClass.BOOLEAN);
        assertTrue(l.inferType(v, ctx));
        assertThat(l.getTypeClass(), equalTo(TypeClass.BOOLEAN));

        l = new LikeAST(intExp, "test");
        assertFalse(l.inferType(v, ctx));
    }

    @Test(expected = IllegalStateException.class)
    public void testLikeNoType() {
        LikeAST l = new LikeAST(stringExp, "test");
        l.getTypeClass();
    }

    @Test(expected = IllegalStateException.class)
    public void testLikeDoubleTypeSet() {
        LikeAST l = new LikeAST(stringExp, "test");
        ParserContext ctx = new ParserContext();
        TypeVariable v = new TypeVariable(TypeClass.ANY);
        l.inferType(v, ctx);
        l.inferType(v, ctx);
    }

    @Test
    public void testArithmetic() {
        MockExpressionAST op1 = new MockExpressionAST(TypeClass.INTEGER);
        MockExpressionAST op2 = new MockExpressionAST(TypeClass.INTEGER);
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
        TypeVariable v = new TypeVariable(TypeClass.NUMERIC);
        assertTrue(a.inferType(v, ctx));
        assertThat(v.getTypeClass(), equalTo(TypeClass.INTEGER));
        assertThat(a.getTypeClass(), equalTo(TypeClass.INTEGER));

        v = new TypeVariable(TypeClass.NUMERIC);
        a = new ArithmeticAST(ArithmeticOperation.ADDITION, floatExp, floatExp);
        assertTrue(a.inferType(v, ctx));
        assertThat(v.getTypeClass(), equalTo(TypeClass.FLOAT));
        assertThat(a.getTypeClass(), equalTo(TypeClass.FLOAT));

        v = new TypeVariable(TypeClass.NUMERIC);
        a = new ArithmeticAST(ArithmeticOperation.ADDITION, intExp, floatExp);
        assertFalse(a.inferType(v, ctx));

        v = new TypeVariable(TypeClass.INTEGER);
        a = new ArithmeticAST(ArithmeticOperation.ADDITION, floatExp, floatExp);
        assertFalse(a.inferType(v, ctx));

        v = new TypeVariable(TypeClass.BOOLEAN);
        a = new ArithmeticAST(ArithmeticOperation.ADDITION, floatExp, floatExp);
        assertFalse(a.inferType(v, ctx));
    }

    @Test(expected = IllegalStateException.class)
    public void testArithmeticNoType() {
        ArithmeticAST a =
                new ArithmeticAST(ArithmeticOperation.ADDITION, intExp, intExp);
        a.getTypeClass();
    }

    @Test(expected = IllegalStateException.class)
    public void testArithmeticDoubleTypeSet() {
        ArithmeticAST a =
                new ArithmeticAST(ArithmeticOperation.ADDITION, intExp, intExp);
        ParserContext ctx = new ParserContext();
        TypeVariable v = new TypeVariable(TypeClass.ANY);
        a.inferType(v, ctx);
        a.inferType(v, ctx);
    }

    @Test
    public void testArithmeticInverse() {
        MockExpressionAST op = new MockExpressionAST(TypeClass.INTEGER);
        InverseAST a = new InverseAST(op);
        assertThat(a.getOperand(), equalTo(op));
    }

    @Test
    public void testArithmeticInverseInference() {
        ParserContext ctx = new ParserContext();

        InverseAST i = new InverseAST(intExp);
        TypeVariable v = new TypeVariable(TypeClass.NUMERIC);
        assertTrue(i.inferType(v, ctx));
        assertThat(v.getTypeClass(), equalTo(TypeClass.INTEGER));
        assertThat(i.getTypeClass(), equalTo(TypeClass.INTEGER));

        v = new TypeVariable(TypeClass.NUMERIC);
        i = new InverseAST(floatExp);
        assertTrue(i.inferType(v, ctx));
        assertThat(v.getTypeClass(), equalTo(TypeClass.FLOAT));
        assertThat(i.getTypeClass(), equalTo(TypeClass.FLOAT));

        v = new TypeVariable(TypeClass.INTEGER);
        i = new InverseAST(stringExp);
        assertFalse(i.inferType(v, ctx));

        v = new TypeVariable(TypeClass.ID);
        i = new InverseAST(floatExp);
        assertFalse(i.inferType(v, ctx));
    }

    @Test(expected = IllegalStateException.class)
    public void testArithmeticInverseNoType() {
        InverseAST a = new InverseAST(intExp);
        a.getTypeClass();
    }

    @Test(expected = IllegalStateException.class)
    public void testArithmeticInverseDoubleTypeSet() {
        InverseAST a = new InverseAST(intExp);
        ParserContext ctx = new ParserContext();
        TypeVariable v = new TypeVariable(TypeClass.ANY);
        a.inferType(v, ctx);
        a.inferType(v, ctx);
    }

    @Test
    public void testComparison() {
        MockExpressionAST op1 = new MockExpressionAST(TypeClass.INTEGER);
        MockExpressionAST op2 = new MockExpressionAST(TypeClass.INTEGER);
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
        TypeVariable v = new TypeVariable(TypeClass.BOOLEAN);
        assertTrue(c.inferType(v, ctx));
        assertThat(c.getTypeClass(), equalTo(TypeClass.BOOLEAN));

        v = new TypeVariable(TypeClass.ANY);
        c = new ComparisonAST(ComparisonOperation.LT, stringExp, stringExp);
        assertTrue(c.inferType(v, ctx));
        assertThat(c.getTypeClass(), equalTo(TypeClass.BOOLEAN));
        assertThat(v.getTypeClass(), equalTo(TypeClass.BOOLEAN));

        v = new TypeVariable(TypeClass.BOOLEAN);
        c = new ComparisonAST(ComparisonOperation.LT, intExp, floatExp);
        assertFalse(c.inferType(v, ctx));

        v = new TypeVariable(TypeClass.STRING);
        c = new ComparisonAST(ComparisonOperation.LT, intExp, intExp);
        assertFalse(c.inferType(v, ctx));
    }

    @Test(expected = IllegalStateException.class)
    public void testComparisonNoType() {
        ComparisonAST c =
                new ComparisonAST(ComparisonOperation.EQ, intExp, intExp);
        c.getTypeClass();
    }

    @Test(expected = IllegalStateException.class)
    public void testComparisonDoubleTypeSet() {
        ComparisonAST c =
                new ComparisonAST(ComparisonOperation.EQ, intExp, intExp);
        ParserContext ctx = new ParserContext();
        TypeVariable v = new TypeVariable(TypeClass.ANY);
        c.inferType(v, ctx);
        c.inferType(v, ctx);
    }

    @Test
    public void testBetween() {
        MockExpressionAST op1 = new MockExpressionAST(TypeClass.INTEGER);
        MockExpressionAST op2 = new MockExpressionAST(TypeClass.INTEGER);
        MockExpressionAST op3 = new MockExpressionAST(TypeClass.INTEGER);
        BetweenAST b = new BetweenAST(op1, op2, op3);
        assertThat(b.getOperand(), equalTo(op1));
        assertThat(b.getMin(), equalTo(op2));
        assertThat(b.getMax(), equalTo(op3));
    }

    @Test
    public void testBetweenInference() {
        ParserContext ctx = new ParserContext();

        BetweenAST b = new BetweenAST(intExp, intExp, intExp);
        TypeVariable v = new TypeVariable(TypeClass.BOOLEAN);
        assertTrue(b.inferType(v, ctx));
        assertThat(b.getTypeClass(), equalTo(TypeClass.BOOLEAN));

        v = new TypeVariable(TypeClass.BOOLEAN);
        b = new BetweenAST(floatExp, floatExp, floatExp);
        assertTrue(b.inferType(v, ctx));
        assertThat(b.getTypeClass(), equalTo(TypeClass.BOOLEAN));

        v = new TypeVariable(TypeClass.INTEGER);
        b = new BetweenAST(floatExp, floatExp, floatExp);
        assertFalse(b.inferType(v, ctx));

        v = new TypeVariable(TypeClass.BOOLEAN);
        b = new BetweenAST(boolExp, boolExp, boolExp);
        assertFalse(b.inferType(v, ctx));

        v = new TypeVariable(TypeClass.BOOLEAN);
        b = new BetweenAST(boolExp, boolExp, intExp);
        assertFalse(b.inferType(v, ctx));
    }

    @Test(expected = IllegalStateException.class)
    public void testBetweenNoType() {
        BetweenAST b = new BetweenAST(intExp, intExp, intExp);
        b.getTypeClass();
    }

    @Test(expected = IllegalStateException.class)
    public void testBetweenDoubleTypeSet() {
        BetweenAST b = new BetweenAST(intExp, intExp, intExp);
        ParserContext ctx = new ParserContext();
        TypeVariable v = new TypeVariable(TypeClass.ANY);
        b.inferType(v, ctx);
        b.inferType(v, ctx);
    }

    @Test
    public void testAggregate() {
        MockExpressionAST op1 = new MockExpressionAST(TypeClass.INTEGER);
        MockExpressionAST op2 = new MockExpressionAST(TypeClass.BOOLEAN);
        WindowSize ws = new WindowSize(12);
        AggregateAST a = new AggregateAST(AggregateOperation.SUM, op1, ws, op2);
        assertThat(a.getOperation(), equalTo(AggregateOperation.SUM));
        assertThat(a.getOperand(), equalTo(op1));
        assertThat(a.getWindowSize(), equalTo(ws));
        assertThat(a.getFilter(), equalTo(op2));
    }

    @Test
    public void testAggregateInference() {
        WindowSize ws = new WindowSize(12);
        ParserContext ctx = new ParserContext();

        AggregateAST a =
                new AggregateAST(AggregateOperation.SUM, intExp, ws, boolExp);
        TypeVariable v = new TypeVariable(TypeClass.INTEGER);
        assertTrue(a.inferType(v, ctx));
        assertThat(a.getTypeClass(), equalTo(TypeClass.INTEGER));

        v = new TypeVariable(TypeClass.FLOAT);
        a = new AggregateAST(AggregateOperation.SUM, floatExp, ws, boolExp);
        assertTrue(a.inferType(v, ctx));
        assertThat(a.getTypeClass(), equalTo(TypeClass.FLOAT));

        v = new TypeVariable(TypeClass.INTEGER);
        a = new AggregateAST(AggregateOperation.SUM, intExp, ws, stringExp);
        assertFalse(a.inferType(v, ctx));

        v = new TypeVariable(TypeClass.FLOAT);
        a = new AggregateAST(AggregateOperation.SUM, intExp, ws, boolExp);
        assertFalse(a.inferType(v, ctx));

        v = new TypeVariable(TypeClass.INTEGER);
        a = new AggregateAST(AggregateOperation.COUNT, null, ws, boolExp);
        assertTrue(a.inferType(v, ctx));
        assertThat(a.getTypeClass(), equalTo(TypeClass.INTEGER));

        v = new TypeVariable(TypeClass.NUMERIC);
        a = new AggregateAST(AggregateOperation.COUNT, null, ws, boolExp);
        assertTrue(a.inferType(v, ctx));
        assertThat(a.getTypeClass(), equalTo(TypeClass.INTEGER));
        assertThat(v.getTypeClass(), equalTo(TypeClass.INTEGER));

        v = new TypeVariable(TypeClass.FLOAT);
        a = new AggregateAST(AggregateOperation.COUNT, null, ws, boolExp);
        assertFalse(a.inferType(v, ctx));

        v = new TypeVariable(TypeClass.NUMERIC);
        a = new AggregateAST(AggregateOperation.COUNT, null, ws, floatExp);
        assertFalse(a.inferType(v, ctx));
    }

    @Test(expected = IllegalStateException.class)
    public void testAggregateNoType() {
        WindowSize ws = new WindowSize(4);
        AggregateAST a =
                new AggregateAST(AggregateOperation.SUM, intExp, ws, boolExp);
        a.getTypeClass();
    }

    @Test(expected = IllegalStateException.class)
    public void testAggregateDoubleTypeSet() {
        WindowSize ws = new WindowSize(4);
        AggregateAST a =
                new AggregateAST(AggregateOperation.SUM, intExp, ws, boolExp);
        ParserContext ctx = new ParserContext();
        TypeVariable v = new TypeVariable(TypeClass.ANY);
        a.inferType(v, ctx);
        a.inferType(v, ctx);
    }

    @Test(expected = IllegalStateException.class)
    public void testAggregateCountNoType() {
        WindowSize ws = new WindowSize(4);
        AggregateAST a =
                new AggregateAST(AggregateOperation.COUNT, null, ws, boolExp);
        a.getTypeClass();
    }

    @Test(expected = IllegalStateException.class)
    public void testAggregateContDoubleTypeSet() {
        WindowSize ws = new WindowSize(4);
        AggregateAST a =
                new AggregateAST(AggregateOperation.COUNT, null, ws, boolExp);
        ParserContext ctx = new ParserContext();
        TypeVariable v = new TypeVariable(TypeClass.ANY);
        a.inferType(v, ctx);
        a.inferType(v, ctx);
    }

    @Test
    public void testInference() {
        ExpressionAST op1 = new ConstantAST("3", TypeClass.INTEGER);
        ExpressionAST op2 = new AttributeAST("integer", TypeClass.ANY);
        ExpressionAST e =
                new ArithmeticAST(ArithmeticOperation.ADDITION, op1, op2);

        ParserContext ctx = new ParserContext();
        TypeVariable v = new TypeVariable(TypeClass.ANY);
        assertTrue(e.inferType(v, ctx));
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(v.getTypeClass(), equalTo(TypeClass.INTEGER));
        assertThat(e.getTypeClass(), equalTo(TypeClass.INTEGER));

        Map<String, TypeClass> tm = ctx.getAttributeTypes();
        assertThat(tm.get("integer"), equalTo(TypeClass.INTEGER));
    }

}
