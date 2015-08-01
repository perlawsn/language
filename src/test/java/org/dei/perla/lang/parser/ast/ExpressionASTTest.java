package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.TypeVariable;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 * @author Guido Rota 31/07/15.
 */
public class ExpressionASTTest {

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

        Map<String, TypeClass> attTypes = ctx.getAttributeTypes();
        assertThat(attTypes.get("att"), equalTo(TypeClass.INTEGER));
    }

}
