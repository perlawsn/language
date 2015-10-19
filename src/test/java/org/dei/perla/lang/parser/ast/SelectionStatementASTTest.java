package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.parser.AttributeOrder;
import org.dei.perla.lang.parser.FieldSelection;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.query.expression.AttributeReference;
import org.dei.perla.lang.query.expression.Expression;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * @author Guido Rota 19/10/15.
 */
public class SelectionStatementASTTest {

    @Test
    public void testGroupByAST() {
        List<String> ids = Arrays.asList(new String[] {
                "temperature",
                "pressure"
        });
        GroupByAST group = new GroupByAST(ids);
        List<String> fields = group.getFields();
        assertThat(fields.size(), equalTo(ids.size()));
        assertTrue(fields.containsAll(ids));
    }

    @Test
    public void testFieldSelectionAST() {
        // Attribute reference with default value
        ParserContext ctx = new ParserContext();
        AttributeOrder ord = new AttributeOrder();
        AttributeReferenceAST ref =
                new AttributeReferenceAST("temp", DataType.ANY);
        ConstantAST def = new ConstantAST(12, DataType.INTEGER);
        FieldSelectionAST fs = new FieldSelectionAST(ref, def);
        assertThat(fs.getField(), equalTo(ref));
        assertThat(fs.getDefault(), equalTo(def));

        FieldSelection fsComp = fs.compile(DataType.ANY, ctx, ord);
        assertFalse(ctx.hasErrors());
        assertThat(fsComp, notNullValue());
        AttributeReference refComp =
                (AttributeReference) fsComp.getField();
        assertThat(refComp.getId(), equalTo("temp"));
        assertThat(refComp.getType(), equalTo(DataType.INTEGER));
        assertThat(refComp.getIndex(), equalTo(0));
        assertThat(ord.getIndex("temp"), equalTo(0));
        assertThat(fsComp.getDefault(), equalTo(12));

        // Attribute reference without default value
        ctx = new ParserContext();
        ord = new AttributeOrder();
        ref = new AttributeReferenceAST("press", DataType.FLOAT);
        fs = new FieldSelectionAST(ref, ConstantAST.NULL);

        fsComp = fs.compile(DataType.NUMERIC, ctx, ord);
        assertFalse(ctx.hasErrors());
        assertThat(fsComp, notNullValue());
        refComp = (AttributeReference) fsComp.getField();
        assertThat(refComp.getId(), equalTo("press"));
        assertThat(refComp.getType(), equalTo(DataType.FLOAT));
        assertThat(refComp.getIndex(), equalTo(0));
        assertThat(ord.getIndex("press"), equalTo(0));
        assertThat(fsComp.getDefault(), equalTo(null));

        // Type mismatch
        ctx = new ParserContext();
        ord = new AttributeOrder();
        ref = new AttributeReferenceAST("hum", DataType.FLOAT);
        fs = new FieldSelectionAST(ref, ConstantAST.NULL);

        fs.compile(DataType.BOOLEAN, ctx, ord);
        assertTrue(ctx.hasErrors());

        // Type mismatch with default value
        ctx = new ParserContext();
        ord = new AttributeOrder();
        ref = new AttributeReferenceAST("alarm", DataType.BOOLEAN);
        def = new ConstantAST(12, DataType.INTEGER);
        fs = new FieldSelectionAST(ref, def);

        fs.compile(DataType.NUMERIC, ctx, ord);
        assertTrue(ctx.hasErrors());
    }

}
