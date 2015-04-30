package org.dei.perla.lang.query.statement;

import org.dei.perla.lang.query.statement.WindowSize.WindowType;
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
        WindowSize ws;
        ws = new WindowSize(1);
        assertThat(ws.getType(), equalTo(WindowType.SAMPLE));
        assertThat(ws.getSamples(), equalTo(1));

        ws = new WindowSize(543);
        assertThat(ws.getType(), equalTo(WindowType.SAMPLE));
        assertThat(ws.getSamples(), equalTo(543));

        ws = new WindowSize(Duration.ofSeconds(12));
        assertThat(ws.getType(), equalTo(WindowType.TIME));
        assertThat(ws.getDuration(), equalTo(Duration.ofSeconds(12)));

        ws = new WindowSize(Duration.ofDays(23));
        assertThat(ws.getType(), equalTo(WindowType.TIME));
        assertThat(ws.getDuration(), equalTo(Duration.ofDays(23)));
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
