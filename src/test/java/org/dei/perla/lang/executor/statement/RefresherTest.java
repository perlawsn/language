package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.lang.executor.LatchingQueryHandler;
import org.dei.perla.lang.executor.SimulatorFpc;
import org.dei.perla.lang.query.statement.Refresh;
import org.junit.Test;

import java.time.Duration;
import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.*;

/**
 * @author Guido Rota 24/04/15.
 */
public class RefresherTest {

    private static final Map<Attribute, Object> values;
    static {
        Map<Attribute, Object> m = new HashMap<>();
        m.put(Attribute.create("fire", DataType.BOOLEAN), true);
        values = Collections.unmodifiableMap(m);
    }

    private static final Set<String> events;
    static {
        Set<String> s = new HashSet<>();
        s.add("fire");
        events = Collections.unmodifiableSet(s);
    }

    @Test
    public void testTimeRefresher() throws Exception {
        Fpc fpc = new SimulatorFpc(values);
        Refresh refresh = new Refresh(Duration.ofMillis(300));

        LatchingQueryHandler<Refresh, Void> handler =
                new LatchingQueryHandler<>();
        Refresher r = new Refresher(refresh, handler, fpc);
        assertFalse(r.isRunning());
        r.start();
        assertTrue(r.isRunning());
        handler.awaitCount(3);

        r.stop();
        assertFalse(r.isRunning());
        int count = handler.getDataCount();
        Thread.sleep(1000);
        assertThat(handler.getDataCount(), equalTo(count));

        r.start();
        assertTrue(r.isRunning());
        Thread.sleep(1000);
        assertThat(handler.getDataCount(), greaterThan(count));
    }
/*
    @Test
    public void testEventRefresher() throws Exception {
        SimulatorFpc fpc = new SimulatorFpc(values);
        Refresh refresh = new Refresh(events);
        refresh = refresh.bind(values.keySet());
        assertTrue(refresh.isComplete());

        LatchingQueryHandler<Refresh, Void> handler =
                new LatchingQueryHandler<>();
        Refresher r = new Refresher(refresh, handler, fpc);
        assertFalse(r.isRunning());
        r.start();
        fpc.awaitStarted();
        assertTrue(r.isRunning());
        fpc.triggerEvent();
        fpc.triggerEvent();
        fpc.triggerEvent();
        handler.awaitCount(3);

        r.stop();
        fpc.awaitStopped();
        assertFalse(r.isRunning());
        fpc.triggerEvent();
        assertThat(handler.getDataCount(), equalTo(3));

        r.start();
        fpc.awaitStarted();
        assertTrue(r.isRunning());
        fpc.triggerEvent();
        handler.awaitCount(4);
        assertThat(handler.getDataCount(), equalTo(4));
    }
*/
}
