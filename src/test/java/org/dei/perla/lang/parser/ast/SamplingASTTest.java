package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.query.statement.SamplingEvent;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 * @author Guido Rota 14/09/15.
 */
public class SamplingASTTest {

    @Test
    public void testSamplingEvent() {
        List<String> evs = Arrays.asList(new String[] {
                "test1",
                "test2",
                "test3"
        });
        SamplingEventAST ast = new SamplingEventAST(evs);
        assertTrue(ast.getEvents().containsAll(evs));
        assertThat(ast.getEvents().size(), equalTo(evs.size()));

        ParserContext ctx = new ParserContext();
        SamplingEvent samp = ast.compile(ctx);
        assertFalse(ctx.hasErrors());
        List<Attribute> atts = samp.getEvents();
        assertTrue(atts.contains(Attribute.create("test1", DataType.ANY)));
        assertTrue(atts.contains(Attribute.create("test2", DataType.ANY)));
        assertTrue(atts.contains(Attribute.create("test3", DataType.ANY)));
    }

    @Test
    public void testSamplingEventDuplicate() {
        List<String> evs = Arrays.asList(new String[] {
                "test1",
                "test2",
                "test1"
        });
        SamplingEventAST ast = new SamplingEventAST(evs);
        assertTrue(ast.getEvents().containsAll(evs));
        assertThat(ast.getEvents().size(), equalTo(evs.size()));

        ParserContext ctx = new ParserContext();
        SamplingEvent samp = ast.compile(ctx);
        assertTrue(ctx.hasErrors());
    }

}
