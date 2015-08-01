package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.TypeVariable;
import org.dei.perla.lang.query.expression.BoolOperation;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 * @author Guido Rota 31/07/15.
 */
public class TestExpressionAST {

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

    @Test
    public void testConstantAST() {
        ParserContext ctx = new ParserContext();
        ConstantAST c = new ConstantAST(TypeClass.INTEGER, 10);
        assertThat(c.getType(), equalTo(TypeClass.INTEGER));
        assertThat(c.getValue(), equalTo(10));

        TypeVariable v = new TypeVariable(TypeClass.INTEGER);
        boolean res = c.inferType(v, ctx);
        assertTrue(res);
        assertThat(v.getTypeClass(), equalTo(TypeClass.INTEGER));

        v = new TypeVariable(TypeClass.NUMERIC);
        res = c.inferType(v, ctx);
        assertTrue(res);
        assertThat(v.getTypeClass(), equalTo(TypeClass.INTEGER));

        v = new TypeVariable(TypeClass.ANY);
        res = c.inferType(v, ctx);
        assertTrue(res);
        assertThat(v.getTypeClass(), equalTo(TypeClass.INTEGER));

        v = new TypeVariable(TypeClass.STRING);
        res = c.inferType(v, ctx);
        assertFalse(res);
        assertThat(v.getTypeClass(), equalTo(TypeClass.STRING));
    }

    @Test
    public void testAttributeReference() {
        ParserContext ctx = new ParserContext();
        AttributeReferenceAST a =
                new AttributeReferenceAST("att", TypeClass.ANY);
        assertThat(a.getIdentifier(), equalTo("att"));
        assertThat(a.getType().getTypeClass(), equalTo(TypeClass.ANY));

        TypeVariable v = new TypeVariable(TypeClass.NUMERIC);
        boolean res = a.inferType(v, ctx);
        assertTrue(res);
        assertThat(a.getType().getTypeClass(), equalTo(TypeClass.NUMERIC));

        a = new AttributeReferenceAST("att", TypeClass.INTEGER);
        res = a.inferType(v, ctx);
        assertTrue(res);
        assertThat(a.getType().getTypeClass(), equalTo(TypeClass.INTEGER));

        assertThat(ctx.getErrorCount(), equalTo(0));
        Map<String, TypeClass> attTypes = ctx.getAttributeTypes();
        assertThat(attTypes.get("att"), equalTo(TypeClass.INTEGER));
    }

    @Test
    public void testBooleanBinary() {
        ParserContext ctx = new ParserContext();
        MockExpressionAST left = new MockExpressionAST(TypeClass.BOOLEAN);
        MockExpressionAST right = new MockExpressionAST(TypeClass.BOOLEAN);
        BoolAST b = new BoolAST(BoolOperation.OR, left, right);
        assertThat(b.getLeftOperand(), equalTo(left));
        assertThat(b.getRightOperand(), equalTo(right));

        TypeVariable v = new TypeVariable(TypeClass.BOOLEAN);
        b = new BoolAST(BoolOperation.AND, boolExp, boolExp);
        assertTrue(b.inferType(v, ctx));

        b = new BoolAST(BoolOperation.AND, boolExp, intExp);
        assertFalse(b.inferType(v, ctx));

        b = new BoolAST(BoolOperation.AND, intExp, boolExp);
        assertFalse(b.inferType(v, ctx));

        b = new BoolAST(BoolOperation.AND, intExp, stringExp);
        assertFalse(b.inferType(v, ctx));
    }

}
