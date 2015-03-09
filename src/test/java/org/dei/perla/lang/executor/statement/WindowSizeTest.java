package org.dei.perla.lang.executor.statement;

import org.junit.Test;

import java.time.Duration;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author Guido Rota 09/03/15.
 */
public class WindowSizeTest {

    @Test
    public void testCreation() {
        WindowSize wss = new WindowSize(1);
        assertThat(wss.getSamples(), equalTo(1));
        assertThat(wss.getDuration(), nullValue());

        wss = new WindowSize(543);
        assertThat(wss.getSamples(), equalTo(543));
        assertThat(wss.getDuration(), nullValue());

        WindowSize wsd = new WindowSize(Duration.ofSeconds(12));
        assertThat(wsd.getDuration(), equalTo(Duration.ofSeconds(12)));
        assertThat(wsd.getSamples(), equalTo(-1));

        wsd = new WindowSize(Duration.ofDays(23));
        assertThat(wsd.getDuration(), equalTo(Duration.ofDays(23)));
        assertThat(wsd.getSamples(), equalTo(-1));
    }

    @Test
    public void testEquals() {
        WindowSize w1 = new WindowSize(12);
        WindowSize w2 = new WindowSize(12);
        WindowSize w3 = new WindowSize(86);
        WindowSize w4 = new WindowSize(Duration.ofSeconds(60));
        WindowSize w5 = new WindowSize(Duration.ofMinutes(1));
        WindowSize w6 = new WindowSize(Duration.ofDays(34));

        assertThat(w1, equalTo(w1));
        assertThat(w1, equalTo(w2));
        assertThat(w1, not(equalTo(w3)));
        assertThat(w1, not(equalTo(w4)));

        assertThat(w4, equalTo(w4));
        assertThat(w4, equalTo(w5));
        assertThat(w4, not(equalTo(w6)));
        assertThat(w4, not(equalTo(w3)));
    }

}
