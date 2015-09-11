package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.query.statement.Refresh;
import org.dei.perla.lang.query.statement.RefreshType;
import org.dei.perla.lang.query.statement.WindowSize;
import org.junit.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

/**
 * @author Guido Rota 10/08/15.
 */
public class MiscASTTest {

    @Test
    public void testSampleWindow() {
        ConstantAST c = new ConstantAST(23, DataType.INTEGER);
        WindowSizeAST w = new WindowSizeAST(c);
        assertThat(w.getType(), equalTo(WindowSize.WindowType.SAMPLE));
        assertThat(w.getSamples(), equalTo(c));

        ParserContext ctx = new ParserContext();
        WindowSize ws = w.compile(ctx);
        assertFalse(ctx.hasErrors());
        assertThat(ws.getType(), equalTo(WindowSize.WindowType.SAMPLE));
        assertThat(ws.getSamples(), equalTo(23));

        c = new ConstantAST(-23, DataType.INTEGER);
        w = new WindowSizeAST(c);
        assertThat(w.getSamples(), equalTo(c));

        ctx = new ParserContext();
        w.compile(ctx);
        assertTrue(ctx.hasErrors());
    }
@Test public void testDurationWindow() {
        ConstantAST c = new ConstantAST(65, DataType.INTEGER);
        WindowSizeAST w = new WindowSizeAST(c, ChronoUnit.DAYS);

        assertThat(w.getType(), equalTo(WindowSize.WindowType.TIME));
        assertThat(w.getDurationValue(), equalTo(c));
        assertThat(w.getDurationUnit(), equalTo(ChronoUnit.DAYS));
        ParserContext ctx = new ParserContext();
        WindowSize ws = w.compile(ctx);
        assertFalse(ctx.hasErrors());
        assertThat(ws.getType(), equalTo(WindowSize.WindowType.TIME));
        assertThat(ws.getDuration(), equalTo(Duration.ofDays(65)));

        c = new ConstantAST(-65, DataType.INTEGER);
        w = new WindowSizeAST(c, ChronoUnit.DAYS);
        assertThat(w.getDurationValue(), equalTo(c));
        assertThat(w.getDurationUnit(), equalTo(ChronoUnit.DAYS));

        ctx = new ParserContext();
        w.compile(ctx);
        assertTrue(ctx.hasErrors());
    }

    @Test
    public void testRefreshNever() {
        RefreshAST ra = RefreshAST.NEVER;
        assertThat(ra.getType(), equalTo(RefreshType.NEVER));

        ParserContext ctx = new ParserContext();
        Refresh r = ra.compile(ctx);
        assertThat(r.getType(), equalTo(RefreshType.NEVER));
    }

    @Test
    public void testRefreshDuration() {
        ConstantAST c = new ConstantAST(12, DataType.INTEGER);
        RefreshAST ra = new RefreshAST(c, ChronoUnit.DAYS);
        assertThat(ra.getType(), equalTo(RefreshType.TIME));
        assertThat(ra.getDurationValue(), equalTo(c));
        assertThat(ra.getDurationUnit(), equalTo(ChronoUnit.DAYS));

        ParserContext ctx = new ParserContext();
        Refresh r = ra.compile(ctx);
        assertThat(r.getType(), equalTo(RefreshType.TIME));
        assertThat(r.getDuration(), equalTo(Duration.ofDays(12)));
    }

    @Test
    public void testRefreshEvents() {
        List<String> es = new ArrayList<>();
        es.add("test1");
        es.add("test2");
        RefreshAST ra = new RefreshAST(es);
        assertThat(ra.getType(), equalTo(RefreshType.EVENT));
        assertTrue(ra.getEvents().containsAll(es));
        assertThat(ra.getEvents().size(), equalTo(es.size()));

        ParserContext ctx = new ParserContext();
        Refresh r = ra.compile(ctx);
        assertThat(r.getType(), equalTo(RefreshType.EVENT));
    }

}
