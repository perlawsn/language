package org.dei.perla.lang.parser;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.parser.ast.AttributeReferenceAST;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author Guido Rota 25/09/15.
 */
public class AttributeOrderTest {

    @Test
    public void testAttributeOrder() {
        ParserContext ctx = new ParserContext();
        AttributeOrder order = new AttributeOrder();

        int idx = order.getIndex("test1");
        assertThat(idx, equalTo(0));
        AttributeReferenceAST ref =
                new AttributeReferenceAST("test1", DataType.INTEGER);
        ctx.addAttributeReference(ref);

        idx = order.getIndex("test2");
        assertThat(idx, equalTo(1));
        ref = new AttributeReferenceAST("test2", DataType.STRING);
        ctx.addAttributeReference(ref);

        idx = order.getIndex("test1");
        assertThat(idx, equalTo(0));
        idx = order.getIndex("test2");
        assertThat(idx, equalTo(1));

        List<Attribute> atts = order.toList(ctx);
        assertThat(atts.get(0),
                equalTo(Attribute.create("test1", DataType.INTEGER)));
        assertThat(atts.get(1),
                equalTo(Attribute.create("test2", DataType.STRING)));
    }

}
