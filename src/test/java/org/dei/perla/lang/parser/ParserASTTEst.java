package org.dei.perla.lang.parser;

import org.dei.perla.lang.query.expression.AggregateOperation;
import org.dei.perla.lang.query.expression.ComparisonOperation;
import org.dei.perla.lang.query.expression.LogicValue;
import org.dei.perla.lang.query.statement.WindowSize;
import org.dei.perla.lang.query.statement.WindowSize.WindowType;
import org.junit.Test;

import java.io.StringReader;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * @author Guido Rota 30/07/15.
 */
public class ParserASTTEst {

    private static ParserAST getParser(String s) {
        return new ParserAST(new StringReader(s));
    }

    @Test
    public void testSign() throws Exception {
        ParserAST p = getParser("+");
        Sign s = p.Sign();
        assertThat(s, equalTo(Sign.PLUS));

        p = getParser("-");
        s = p.Sign();
        assertThat(s, equalTo(Sign.MINUS));
    }

    @Test
    public void testConstantBoolean() throws Exception {
        ParserAST p = getParser("TRUE");
        LogicValue v = p.ConstantBoolean();
        assertThat(v, equalTo(LogicValue.TRUE));

        p = getParser("true");
        v = p.ConstantBoolean();
        assertThat(v, equalTo(LogicValue.TRUE));

        p = getParser("false");
        v = p.ConstantBoolean();
        assertThat(v, equalTo(LogicValue.FALSE));

        p = getParser("FALSE");
        v = p.ConstantBoolean();
        assertThat(v, equalTo(LogicValue.FALSE));
    }

    @Test(expected = ParseException.class)
    public void testUnknown() throws Exception {
        ParserAST p = getParser("UNKNOWN");
        p.ConstantBoolean();
    }

    @Test
    public void testConstantFloat() throws Exception {
        ParserAST p = getParser("3.14");
        float f = p.ConstantFloat();
        assertThat(f, equalTo(3.14f));
    }

    @Test
    public void testConstantInteger() throws Exception {
        ParserAST p = getParser("3");
        int i = p.ConstantInteger();
        assertThat(i, equalTo(3));
    }

    @Test
    public void testConstant() throws Exception {
        throw new RuntimeException("waiting for new type system");
    }

    @Test
    public void testComparisonOperation() throws Exception {
        ParserAST p = getParser("=");
        ComparisonOperation c = p.ComparisonOperation();
        assertThat(c, equalTo(ComparisonOperation.EQ));

        p = getParser("!=");
        c = p.ComparisonOperation();
        assertThat(c, equalTo(ComparisonOperation.NE));

        p = getParser("<>");
        c = p.ComparisonOperation();
        assertThat(c, equalTo(ComparisonOperation.NE));

        p = getParser(">");
        c = p.ComparisonOperation();
        assertThat(c, equalTo(ComparisonOperation.GT));

        p = getParser(">=");
        c = p.ComparisonOperation();
        assertThat(c, equalTo(ComparisonOperation.GE));

        p = getParser("<");
        c = p.ComparisonOperation();
        assertThat(c, equalTo(ComparisonOperation.LT));

        p = getParser("<=");
        c = p.ComparisonOperation();
        assertThat(c, equalTo(ComparisonOperation.LE));
    }

    @Test
    public void testDataType() throws Exception {
        throw new RuntimeException("waiting for new type system");
    }

    @Test
    public void testTypeClass() throws Exception {
        throw new RuntimeException("waiting for new type system");
    }

    @Test
    public void testTimeUnit() throws Exception {
        ParserAST p = getParser("milliseconds");
        TemporalUnit t = p.TimeUnit();
        assertThat(t, equalTo(ChronoUnit.MILLIS));

        p = getParser("ms");
        t = p.TimeUnit();
        assertThat(t, equalTo(ChronoUnit.MILLIS));

        p = getParser("seconds");
        t = p.TimeUnit();
        assertThat(t, equalTo(ChronoUnit.SECONDS));

        p = getParser("s");
        t = p.TimeUnit();
        assertThat(t, equalTo(ChronoUnit.SECONDS));

        p = getParser("minutes");
        t = p.TimeUnit();
        assertThat(t, equalTo(ChronoUnit.MINUTES));

        p = getParser("m");
        t = p.TimeUnit();
        assertThat(t, equalTo(ChronoUnit.MINUTES));

        p = getParser("hours");
        t = p.TimeUnit();
        assertThat(t, equalTo(ChronoUnit.HOURS));

        p = getParser("h");
        t = p.TimeUnit();
        assertThat(t, equalTo(ChronoUnit.HOURS));

        p = getParser("days");
        t = p.TimeUnit();
        assertThat(t, equalTo(ChronoUnit.DAYS));

        p = getParser("d");
        t = p.TimeUnit();
        assertThat(t, equalTo(ChronoUnit.DAYS));
    }

    @Test
    public void testAggregateOperation() throws Exception {
        ParserAST p = getParser("AVG");
        AggregateOperation a = p.AggregateOperation();
        assertThat(a, equalTo(AggregateOperation.AVG));

        p = getParser("MIN");
        a = p.AggregateOperation();
        assertThat(a, equalTo(AggregateOperation.MIN));

        p = getParser("MAX");
        a = p.AggregateOperation();
        assertThat(a, equalTo(AggregateOperation.MAX));

        p = getParser("SUM");
        a = p.AggregateOperation();
        assertThat(a, equalTo(AggregateOperation.SUM));
    }

    @Test
    public void testDuration() throws Exception {
        ParserAST p = getParser("3 seconds");
        Duration d = p.Duration();
        assertThat(d, equalTo(Duration.ofSeconds(3)));

        p = getParser("3 s");
        d = p.Duration();
        assertThat(d, equalTo(Duration.ofSeconds(3)));

        p = getParser("45 days");
        d = p.Duration();
        assertThat(d, equalTo(Duration.ofDays(45)));
    }

    @Test
    public void testSamplesNumber() throws Exception {
        ParserAST p = getParser("3 samples");
        int s = p.SamplesNumber();
        assertThat(s, equalTo(3));

        p = getParser("one");
        s = p.SamplesNumber();
        assertThat(s, equalTo(1));
    }

    @Test
    public void testSelectionsNumber() throws Exception {
        ParserAST p = getParser("4 selections");
        int s = p.SelectionsNumber();
        assertThat(s, equalTo(4));
    }

    @Test
    public void testWindowSize() throws Exception {
        ParserAST p = getParser("3 seconds");
        WindowSize w = p.WindowSize();
        assertThat(w.getType(), equalTo(WindowType.TIME));
        assertThat(w.getDuration(), equalTo(Duration.ofSeconds(3)));

        p = getParser("23 samples");
        w = p.WindowSize();
        assertThat(w.getType(), equalTo(WindowType.SAMPLE));
        assertThat(w.getSamples(), equalTo(23));
    }

    @Test
    public void testIdentifier() throws Exception {
        String t1 = "test1";
        String t2 = "test2";

        ParserAST p = getParser(t1);
        String s = p.Identifier();
        assertThat(s, equalTo(t1));

        p = getParser(t2);
        s = p.Identifier();
        assertThat(s, equalTo(t2));
    }

}
