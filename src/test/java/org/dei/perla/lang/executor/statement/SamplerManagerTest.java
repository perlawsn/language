package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.DataType;
import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.lang.executor.LatchingQueryHandler;
import org.dei.perla.lang.executor.SimulatorFpc;
import org.dei.perla.lang.query.expression.*;
import org.dei.perla.lang.query.statement.*;
import org.junit.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * @author Guido Rota 19/10/15.
 */
public class SamplerManagerTest {

    private static final Attribute power =
            Attribute.create("power", DataType.INTEGER);
    private static final Attribute temperature =
            Attribute.create("temperature", DataType.INTEGER);
    private static final Attribute fire =
            Attribute.create("fire", DataType.BOOLEAN);

    private static final Map<Attribute, Object> values;
    static {
        Map<Attribute, Object> vs = new HashMap<>();
        vs.put(power, 100);
        vs.put(temperature, 12);
        vs.put(fire, false);
        values = Collections.unmodifiableMap(vs);
    }

    @Test
    public void testPlain() throws InterruptedException {
        IfEvery ife = new IfEvery(
                Constant.TRUE,
                Constant.create(10, DataType.INTEGER),
                ChronoUnit.MILLIS,
                null
        );
        SamplingIfEvery sampling = new SamplingIfEvery(
                ife,
                RatePolicy.STRICT,
                Refresh.NEVER,
                Collections.emptyList()
        );
        SelectionStatement query = new SelectionStatement(
                null,
                null,
                sampling,
                Constant.TRUE,
                ExecutionConditions.ALL_NODES,
                WindowSize.ZERO
        );

        SimulatorFpc fpc = new SimulatorFpc(values);
        LatchingQueryHandler<Sampling, Object[]> handler =
                new LatchingQueryHandler<>();
        SamplerManager mgr = new SamplerManager(query, fpc, handler);
        assertFalse(mgr.isRunning());

        mgr.start();
        assertTrue(mgr.isRunning());
        handler.awaitCount(10);
        mgr.stop();
        fpc.awaitPeriodicStopped();
        assertFalse(mgr.isRunning());
    }

    @Test
    public void testStartRestart() throws InterruptedException {
        IfEvery ife = new IfEvery(
                Constant.TRUE,
                Constant.create(10, DataType.INTEGER),
                ChronoUnit.MILLIS,
                null
        );
        SamplingIfEvery sampling = new SamplingIfEvery(
                ife,
                RatePolicy.STRICT,
                Refresh.NEVER,
                Collections.emptyList()
        );
        Expression exp = new Comparison(
                ComparisonOperation.GT,
                new AttributeReference("power", DataType.INTEGER, 0),
                Constant.create(80, DataType.INTEGER)
        );
        ExecutionConditions cond = new ExecutionConditions(
                Collections.emptySet(),
                exp,
                Arrays.asList(new Attribute[] {power}),
                new Refresh(Duration.ofMillis(1000))
        );
        SelectionStatement query = new SelectionStatement(
                null,
                null,
                sampling,
                Constant.TRUE,
                cond,
                WindowSize.ZERO
        );

        SimulatorFpc fpc = new SimulatorFpc(values);
        LatchingQueryHandler<Sampling, Object[]> handler =
                new LatchingQueryHandler<>();
        SamplerManager mgr = new SamplerManager(query, fpc, handler);
        assertFalse(mgr.isRunning());

        mgr.start();
        assertTrue(mgr.isRunning());
        handler.awaitCount(10);

        fpc.setValue(power, 70);
        fpc.awaitPeriodicStopped();
        assertTrue(mgr.isRunning());

        handler.reset();
        fpc.setValue(power, 90);
        handler.awaitCount(10);
        assertTrue(mgr.isRunning());
    }

}
