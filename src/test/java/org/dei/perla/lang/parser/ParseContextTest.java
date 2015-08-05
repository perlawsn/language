package org.dei.perla.lang.parser;

import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.lang.parser.ast.AttributeAST;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.*;

/**
 * @author Guido Rota 01/08/15.
 */
public class ParseContextTest {

    @Test
    public void testErrors() {
        ParserContext ctx = new ParserContext();
        assertThat(ctx.getErrorCount(), equalTo(0));

        ctx.addError("test");
        assertThat(ctx.getErrorCount(), equalTo(1));
        for (int i = 0; i < 100; i++) {
            ctx.addError("test " + i);
        }
        assertThat(ctx.getErrorCount(), equalTo(101));
    }

    @Test
    public void testAttributeTypeChecking() {
        ParserContext ctx = new ParserContext();
        AttributeAST ar0 =
                new AttributeAST("att1", TypeClass.ANY);
        AttributeAST ar1 =
                new AttributeAST("att1", TypeClass.NUMERIC);
        AttributeAST ar2 =
                new AttributeAST("att1", TypeClass.INTEGER);
        AttributeAST ar3 =
                new AttributeAST("att1", TypeClass.FLOAT);

        boolean res = ctx.addAttributeReference(ar0);
        assertTrue(res);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(ar0.getTypeClass(), equalTo(TypeClass.ANY));

        res = ctx.addAttributeReference(ar1);
        assertTrue(res);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(ar0.getTypeClass(), equalTo(TypeClass.NUMERIC));
        assertThat(ar1.getTypeClass(), equalTo(TypeClass.NUMERIC));

        res = ctx.addAttributeReference(ar2);
        assertTrue(res);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(ar0.getTypeClass(), equalTo(TypeClass.INTEGER));
        assertThat(ar1.getTypeClass(), equalTo(TypeClass.INTEGER));
        assertThat(ar2.getTypeClass(), equalTo(TypeClass.INTEGER));

        res = ctx.addAttributeReference(ar3);
        assertFalse(res);
        assertThat(ctx.getErrorCount(), greaterThan(0));
        assertThat(ar0.getTypeClass(), equalTo(TypeClass.INTEGER));
        assertThat(ar1.getTypeClass(), equalTo(TypeClass.INTEGER));
        assertThat(ar2.getTypeClass(), equalTo(TypeClass.INTEGER));
        assertThat(ar3.getTypeClass(), equalTo(TypeClass.FLOAT));
    }

    @Test
    public void testAttributeTypeTracking() {
        ParserContext ctx = new ParserContext();
        AttributeAST ar0 =
                new AttributeAST("att1", TypeClass.ANY);
        AttributeAST ar1 =
                new AttributeAST("att1", TypeClass.INTEGER);
        AttributeAST ar2 =
                new AttributeAST("att2", TypeClass.BOOLEAN);
        AttributeAST ar3 =
                new AttributeAST("att3", TypeClass.FLOAT);

        boolean res = ctx.addAttributeReference(ar0);
        assertTrue(res);
        res = ctx.addAttributeReference(ar1);
        assertTrue(res);
        res = ctx.addAttributeReference(ar2);
        assertTrue(res);
        res = ctx.addAttributeReference(ar3);
        assertTrue(res);

        Map<String, TypeClass> attTypes = ctx.getAttributeTypes();
        assertThat(attTypes.size(), equalTo(3));
        assertThat(attTypes.get("att1"), equalTo(TypeClass.INTEGER));
        assertThat(attTypes.get("att2"), equalTo(TypeClass.BOOLEAN));
        assertThat(attTypes.get("att3"), equalTo(TypeClass.FLOAT));
    }

}
