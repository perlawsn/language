package org.dei.perla.lang.executor.expression;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author Guido Rota 12/03/15.
 */
public class LogicValueTest {

    private static final LogicValue t = LogicValue.TRUE;
    private static final LogicValue f = LogicValue.FALSE;
    private static final LogicValue u = LogicValue.UNKNOWN;

    @Test
    public void testNOT() {
        LogicValue res;

        res = LogicValue.not(t);
        assertThat(res, equalTo(f));

        res = LogicValue.not(f);
        assertThat(res, equalTo(t));

        res = LogicValue.not(u);
        assertThat(res, equalTo(u));
    }

    @Test
    public void testAND() {
        LogicValue res;

        res = LogicValue.and(t, t);
        assertThat(res, equalTo(t));

        res = LogicValue.and(t, f);
        assertThat(res, equalTo(f));

        res = LogicValue.and(f, t);
        assertThat(res, equalTo(f));

        res = LogicValue.and(f, f);
        assertThat(res, equalTo(f));

        res = LogicValue.and(f, u);
        assertThat(res, equalTo(f));

        res = LogicValue.and(u, f);
        assertThat(res, equalTo(f));

        res = LogicValue.and(t, u);
        assertThat(res, equalTo(u));

        res = LogicValue.and(u, t);
        assertThat(res, equalTo(u));

        res = LogicValue.and(u, u);
        assertThat(res, equalTo(u));
    }

    @Test
    public void testOR() {
        LogicValue res;

        res = LogicValue.or(t, t);
        assertThat(res, equalTo(t));

        res = LogicValue.or(t, f);
        assertThat(res, equalTo(t));

        res = LogicValue.or(f, t);
        assertThat(res, equalTo(t));

        res = LogicValue.or(f, f);
        assertThat(res, equalTo(f));

        res = LogicValue.or(f, u);
        assertThat(res, equalTo(u));

        res = LogicValue.or(u, f);
        assertThat(res, equalTo(u));

        res = LogicValue.or(t, u);
        assertThat(res, equalTo(t));

        res = LogicValue.or(u, t);
        assertThat(res, equalTo(t));

        res = LogicValue.or(u, u);
        assertThat(res, equalTo(u));
    }

}
