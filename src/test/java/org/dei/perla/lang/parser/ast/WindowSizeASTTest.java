package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.query.statement.WindowSize;
import org.junit.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 * @author Guido Rota 10/08/15.
 */
public class WindowSizeASTTest {

    @Test
    public void testSampleWindow() {
        ConstantAST c = new ConstantAST(23, TypeClass.INTEGER);
        WindowSizeAST w = new WindowSizeAST(c);
        assertThat(w.getType(), equalTo(WindowSize.WindowType.SAMPLE));
        assertThat(w.getSamples(), equalTo(c));

        ParserContext ctx = new ParserContext();
        WindowSize ws = w.compile(ctx);
        assertFalse(ctx.hasErrors());
        assertThat(ws.getType(), equalTo(WindowSize.WindowType.SAMPLE));
        assertThat(ws.getSamples(), equalTo(23));

        c = new ConstantAST(-23, TypeClass.INTEGER);
        w = new WindowSizeAST(c);
        assertThat(w.getSamples(), equalTo(c));

        ctx = new ParserContext();
        w.compile(ctx);
        assertTrue(ctx.hasErrors());
    }

    @Test
    public void testDurationWindow() {
        ConstantAST c = new ConstantAST(65, TypeClass.INTEGER);
        WindowSizeAST w = new WindowSizeAST(c, ChronoUnit.DAYS);

        assertThat(w.getType(), equalTo(WindowSize.WindowType.TIME));
        assertThat(w.getDurationValue(), equalTo(c));
        assertThat(w.getDurationUnit(), equalTo(ChronoUnit.DAYS));
        ParserContext ctx = new ParserContext();
        WindowSize ws = w.compile(ctx);
        assertFalse(ctx.hasErrors());
        assertThat(ws.getType(), equalTo(WindowSize.WindowType.TIME));
        assertThat(ws.getDuration(), equalTo(Duration.ofDays(65)));

        c = new ConstantAST(-65, TypeClass.INTEGER);
        w = new WindowSizeAST(c, ChronoUnit.DAYS);
        assertThat(w.getDurationValue(), equalTo(c));
        assertThat(w.getDurationUnit(), equalTo(ChronoUnit.DAYS));

        ctx = new ParserContext();
        w.compile(ctx);
        assertTrue(ctx.hasErrors());
    }

}
