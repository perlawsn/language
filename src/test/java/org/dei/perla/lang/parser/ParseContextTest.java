package org.dei.perla.lang.parser;

import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.parser.ast.AttributeReferenceAST;
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
        AttributeReferenceAST ar0 =
                new AttributeReferenceAST("att1", DataType.ANY);
        AttributeReferenceAST ar1 =
                new AttributeReferenceAST("att1", DataType.NUMERIC);
        AttributeReferenceAST ar2 =
                new AttributeReferenceAST("att1", DataType.INTEGER);
        AttributeReferenceAST ar3 =
                new AttributeReferenceAST("att1", DataType.FLOAT);

        boolean res = ctx.addAttributeReference(ar0);
        assertTrue(res);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(ar0.getType(), equalTo(DataType.ANY));

        res = ctx.addAttributeReference(ar1);
        assertTrue(res);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(ar0.getType(), equalTo(DataType.NUMERIC));
        assertThat(ar1.getType(), equalTo(DataType.NUMERIC));

        res = ctx.addAttributeReference(ar2);
        assertTrue(res);
        assertThat(ctx.getErrorCount(), equalTo(0));
        assertThat(ar0.getType(), equalTo(DataType.INTEGER));
        assertThat(ar1.getType(), equalTo(DataType.INTEGER));
        assertThat(ar2.getType(), equalTo(DataType.INTEGER));

        res = ctx.addAttributeReference(ar3);
        assertFalse(res);
        assertThat(ctx.getErrorCount(), greaterThan(0));
        assertThat(ar0.getType(), equalTo(DataType.INTEGER));
        assertThat(ar1.getType(), equalTo(DataType.INTEGER));
        assertThat(ar2.getType(), equalTo(DataType.INTEGER));
        assertThat(ar3.getType(), equalTo(DataType.FLOAT));
    }

    @Test
    public void testAttributeTypeTracking() {
        ParserContext ctx = new ParserContext();
        AttributeReferenceAST ar0 =
                new AttributeReferenceAST("att1", DataType.ANY);
        AttributeReferenceAST ar1 =
                new AttributeReferenceAST("att1", DataType.INTEGER);
        AttributeReferenceAST ar2 =
                new AttributeReferenceAST("att2", DataType.BOOLEAN);
        AttributeReferenceAST ar3 =
                new AttributeReferenceAST("att3", DataType.FLOAT);

        boolean res = ctx.addAttributeReference(ar0);
        assertTrue(res);
        res = ctx.addAttributeReference(ar1);
        assertTrue(res);
        res = ctx.addAttributeReference(ar2);
        assertTrue(res);
        res = ctx.addAttributeReference(ar3);
        assertTrue(res);

        Map<String, DataType> attTypes = ctx.getAttributeTypes();
        assertThat(attTypes.size(), equalTo(3));
        assertThat(attTypes.get("att1"), equalTo(DataType.INTEGER));
        assertThat(attTypes.get("att2"), equalTo(DataType.BOOLEAN));
        assertThat(attTypes.get("att3"), equalTo(DataType.FLOAT));
    }

}
