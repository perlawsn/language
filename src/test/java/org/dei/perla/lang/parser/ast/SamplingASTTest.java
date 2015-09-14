package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.query.expression.AttributeReference;
import org.dei.perla.lang.query.expression.ComparisonOperation;
import org.dei.perla.lang.query.statement.RatePolicy;
import org.dei.perla.lang.query.statement.SamplingEvent;
import org.dei.perla.lang.query.statement.SamplingIfEvery;
import org.junit.Test;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

    @Test
    public void testEveryAST() {
        ExpressionAST e = new ConstantAST(10, DataType.INTEGER);
        EveryAST ev = new EveryAST(e, ChronoUnit.SECONDS);
        assertThat(ev.getValue(), equalTo(e));
        assertThat(ev.getUnit(), equalTo(ChronoUnit.SECONDS));

        e = new ConstantAST(35, DataType.INTEGER);
        ev = new EveryAST(e, ChronoUnit.HOURS);
        assertThat(ev.getValue(), equalTo(e));
        assertThat(ev.getUnit(), equalTo(ChronoUnit.HOURS));
    }

    @Test
    public void testIfEvery() {
        EveryAST ev = new EveryAST(new ConstantAST(10, DataType.INTEGER),
                ChronoUnit.SECONDS);
        IfEveryAST ife = new IfEveryAST(ConstantAST.TRUE, ev);
        assertThat(ife.getCondition(), equalTo(ConstantAST.TRUE));
        assertThat(ife.getEvery(), equalTo(ev));
    }

    @Test
    public void testSamplingIfEvery() {
        List<IfEveryAST> ifes = new ArrayList<>();
        EveryAST ev = new EveryAST(new ConstantAST(20, DataType.INTEGER),
                ChronoUnit.MINUTES);
        ExpressionAST cond = new ComparisonAST(ComparisonOperation.GT,
                new AttributeReferenceAST("temperature", DataType.ANY),
                new ConstantAST(40, DataType.INTEGER));
        IfEveryAST ife = new IfEveryAST(cond, ev);
        ifes.add(ife);
        ev = new EveryAST(new ConstantAST(10, DataType.INTEGER),
                ChronoUnit.MINUTES);
        ife = new IfEveryAST(ConstantAST.TRUE, ev);
        ifes.add(ife);

        ParserContext ctx = new ParserContext();
        SamplingIfEveryAST sa = new SamplingIfEveryAST(ifes, RatePolicy.STRICT,
                RefreshAST.NEVER);
        SamplingIfEvery s = sa.compile(ctx);
        assertFalse(ctx.hasErrors());
        assertThat(s.getRatePolicy(), equalTo(RatePolicy.STRICT));
        assertThat(s.getRefresh(), equalTo(RefreshAST.NEVER));
    }

}
