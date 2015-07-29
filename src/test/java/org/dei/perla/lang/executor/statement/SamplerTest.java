package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.LatchingQueryHandler;
import org.dei.perla.lang.executor.NoopQueryHandler;
import org.dei.perla.lang.executor.SimulatorFpc;
import org.dei.perla.lang.parser.Parser;
import org.dei.perla.lang.query.statement.Sampling;
import org.dei.perla.lang.query.statement.SamplingEvent;
import org.dei.perla.lang.query.statement.SamplingIfEvery;
import org.junit.Test;

import java.io.StringReader;
import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

/**
 * @author Guido Rota 13/04/15.
 */
public class SamplerTest {

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
    public void testSimpleIfEvery() throws Exception {
        Map<Attribute, Object> vs = new HashMap<>(values);
        Errors err = new Errors();
        Set<String> ids = new TreeSet<>();
        Parser p = new Parser(new StringReader("sampling " +
                "if power > 80 every 100 milliseconds " +
                "if power > 60 every 200 milliseconds " +
                "if power > 20 every 400 milliseconds " +
                "else every 1 seconds"));

        SamplingIfEvery samp = (SamplingIfEvery) p.SamplingClause(err, ids);
        assertTrue(err.isEmpty());

        // Test with power == 100
        SimulatorFpc fpc = new SimulatorFpc(vs);
        samp = samp.bind(fpc.getAttributes(), err);
        assertTrue(err.isEmpty());
        SamplerIfEvery sampler = new SamplerIfEvery(samp, Collections.emptyList(), fpc,
                new NoopQueryHandler());
        sampler.start();
        fpc.awaitPeriod(100);
        assertTrue(sampler.isRunning());
        assertThat(fpc.countPeriodic(), equalTo(1));

        // Test with power == 65
        vs.put(power, 65);
        fpc = new SimulatorFpc(vs);
        samp = samp.bind(fpc.getAttributes(), err);
        assertTrue(err.isEmpty());
        sampler = new SamplerIfEvery(samp, Collections.emptyList(), fpc,
                new NoopQueryHandler());
        sampler.start();
        fpc.awaitPeriod(200);
        assertTrue(sampler.isRunning());
        assertThat(fpc.countPeriodic(), equalTo(1));

        // Test with power == 10
        vs.put(power, 10);
        fpc = new SimulatorFpc(vs);
        samp = samp.bind(fpc.getAttributes(), err);
        assertTrue(err.isEmpty());
        sampler = new SamplerIfEvery(samp, Collections.emptyList(), fpc,
                new NoopQueryHandler());
        sampler.start();
        fpc.awaitPeriod(1000);
        assertTrue(sampler.isRunning());
        assertThat(fpc.countPeriodic(), equalTo(1));

        // Stop sampler
        sampler.stop();
        fpc.awaitStopped();
        assertFalse(sampler.isRunning());
        assertThat(fpc.countPeriodic(), equalTo(0));
        assertThat(fpc.countAsync(), equalTo(0));
    }

    @Test
    public void testTimeRefreshIfEvery() throws Exception {
        Map<Attribute, Object> vs = new HashMap<>(values);
        Errors err = new Errors();
        Set<String> ids = new TreeSet<>();
        Parser p = new Parser(new StringReader("sampling " +
                "if power > 80 every 100 milliseconds " +
                "if power > 60 every 200 milliseconds " +
                "if power > 20 every 400 milliseconds " +
                "else every 1 seconds " +
                "refresh every 1 seconds"));

        SamplingIfEvery samp = (SamplingIfEvery) p.SamplingClause(err, ids);
        assertTrue(err.isEmpty());

        SimulatorFpc fpc = new SimulatorFpc(vs);
        samp = samp.bind(fpc.getAttributes(), err);
        assertTrue(err.isEmpty());
        SamplerIfEvery sampler = new SamplerIfEvery(samp, Collections.emptyList(), fpc,
                new NoopQueryHandler());
        sampler.start();
        fpc.awaitPeriod(100);
        assertTrue(sampler.isRunning());
        assertTrue(fpc.hasPeriod(100));
        assertThat(fpc.countPeriodic(), equalTo(1));

        // Change power level, check if sampling rate lowers
        vs.put(power, 70);
        fpc.setValues(vs);
        fpc.awaitPeriod(200);
        assertTrue(fpc.hasPeriod(200));
        assertThat(fpc.countPeriodic(), equalTo(1));

        // Change power level, check if sampling rate lowers
        vs.put(power, 10);
        fpc.setValues(vs);
        fpc.awaitPeriod(200);
        assertTrue(fpc.hasPeriod(200));
        assertThat(fpc.countPeriodic(), equalTo(1));

        // Stop sampler
        sampler.stop();
        fpc.awaitStopped();
        assertFalse(sampler.isRunning());
        assertThat(fpc.countPeriodic(), equalTo(0));
        assertThat(fpc.countAsync(), equalTo(0));

        // Restart sampler
        sampler.start();
        fpc.awaitStarted();
        assertTrue(sampler.isRunning());
        assertTrue(sampler.isRunning());

        // Change power level, check if sampling rate lowers
        vs.put(power, 70);
        fpc.setValues(vs);
        fpc.awaitPeriod(200);
        assertTrue(fpc.hasPeriod(200));
        assertThat(fpc.countPeriodic(), equalTo(1));

        // Change power level, check if sampling rate lowers
        vs.put(power, 10);
        fpc.setValues(vs);
        fpc.awaitPeriod(200);
        assertTrue(fpc.hasPeriod(200));
        assertThat(fpc.countPeriodic(), equalTo(1));
    }

    @Test
    public void testEventRefreshIfEvery() throws Exception {
        Map<Attribute, Object> vs = new HashMap<>(values);
        Errors err = new Errors();
        Set<String> ids = new TreeSet<>();
        Parser p = new Parser(new StringReader("sampling " +
                "if temperature > 40 every 100 milliseconds " +
                "if temperature > 30 every 500 milliseconds " +
                "else every 1 seconds " +
                "refresh on event fire"));

        SamplingIfEvery samp = (SamplingIfEvery) p.SamplingClause(err, ids);
        assertTrue(err.isEmpty());
        SimulatorFpc fpc = new SimulatorFpc(vs);
        samp = samp.bind(fpc.getAttributes(), err);
        assertTrue(err.isEmpty());

        vs.put(temperature, 25);
        fpc.setValues(vs);
        SamplerIfEvery sampler = new SamplerIfEvery(samp, Collections.emptyList(), fpc,
                new NoopQueryHandler());
        sampler.start();
        fpc.awaitPeriod(1000);
        assertTrue(sampler.isRunning());
        assertThat(fpc.countPeriodic(), equalTo(1));
        assertThat(fpc.countAsync(), equalTo(1));

        // Trigger event, check if sampling rate changes
        vs.put(temperature, 35);
        fpc.setValues(vs);
        fpc.triggerEvent();
        fpc.awaitPeriod(500);
        assertThat(fpc.countPeriodic(), equalTo(1));

        // Trigger event, check if sampling rate changes
        vs.put(temperature, 50);
        fpc.setValues(vs);
        fpc.triggerEvent();
        fpc.awaitPeriod(100);
        assertThat(fpc.countPeriodic(), equalTo(1));

        // Stop sampler
        sampler.stop();
        fpc.awaitStopped();
        assertFalse(sampler.isRunning());
        assertThat(fpc.countPeriodic(), equalTo(0));
        assertThat(fpc.countAsync(), equalTo(0));

        // Restart sampler
        sampler.start();
        fpc.awaitStarted();
        assertTrue(sampler.isRunning());

        // Trigger event, check if sampling rate changes
        vs.put(temperature, 35);
        fpc.setValues(vs);
        fpc.triggerEvent();
        fpc.awaitPeriod(500);
        assertThat(fpc.countAsync(), equalTo(1));
        assertThat(fpc.countPeriodic(), equalTo(1));

        // Trigger event, check if sampling rate changes
        vs.put(temperature, 50);
        fpc.setValues(vs);
        fpc.triggerEvent();
        fpc.awaitPeriod(100);
        assertThat(fpc.countAsync(), equalTo(1));
        assertThat(fpc.countPeriodic(), equalTo(1));
    }

    @Test
    public void testSamplerIfEveryError() throws Exception {
        Map<Attribute, Object> vs = new HashMap<>(values);
        Errors err = new Errors();
        Set<String> ids = new TreeSet<>();
        Parser p = new Parser(new StringReader("sampling " +
                "if temperature > 40 every 100 milliseconds " +
                "if temperature > 30 every 500 milliseconds " +
                "else every 1 seconds " +
                "refresh on event fire"));

        SamplingIfEvery samp = (SamplingIfEvery) p.SamplingClause(err, ids);
        assertTrue(err.isEmpty());
        SimulatorFpc fpc = new SimulatorFpc(vs);
        samp = samp.bind(fpc.getAttributes(), err);
        assertTrue(err.isEmpty());

        vs.put(temperature, 25);
        fpc.setValues(vs);
        SamplerIfEvery sampler = new SamplerIfEvery(samp, Collections.emptyList(), fpc,
                new NoopQueryHandler());
        sampler.start();
        fpc.awaitPeriod(1000);
        assertTrue(sampler.isRunning());
        assertThat(fpc.countPeriodic(), equalTo(1));
        assertThat(fpc.countAsync(), equalTo(1));

        fpc.triggerError();
        fpc.awaitStopped();
        assertFalse(sampler.isRunning());
        assertThat(fpc.countPeriodic(), equalTo(0));
        assertThat(fpc.countAsync(), equalTo(0));
    }

    @Test
    public void testSamplerEvent() throws Exception {
        Map<Attribute, Object> vs = new HashMap<>(values);
        Errors err = new Errors();
        Set<String> ids = new TreeSet<>();
        Parser p = new Parser(new StringReader("sampling on event fire"));
        SamplingEvent samp = (SamplingEvent) p.SamplingClause(err, ids);
        assertTrue(err.isEmpty());

        SimulatorFpc fpc = new SimulatorFpc(vs);
        samp = samp.bind(fpc.getAttributes(), err);
        assertTrue(err.isEmpty());
        LatchingQueryHandler<Sampling, Object[]> handler = new
                LatchingQueryHandler<>();
        SamplerEvent sampler = new SamplerEvent(samp,
                Collections.emptyList(), fpc, handler);
        sampler.start();
        fpc.awaitStarted();
        assertTrue(sampler.isRunning());
        assertThat(fpc.countAsync(), equalTo(1));
        fpc.triggerEvent();
        assertThat(fpc.countAsync(), equalTo(1));
        fpc.triggerEvent();
        assertThat(fpc.countAsync(), equalTo(1));
        fpc.triggerEvent();
        assertThat(fpc.countAsync(), equalTo(1));

        sampler.stop();
        fpc.awaitStopped();
        assertFalse(sampler.isRunning());
        assertThat(fpc.countAsync(), equalTo(0));
    }

    @Test
    public void testSamplerEventError() throws Exception {
        Map<Attribute, Object> vs = new HashMap<>(values);
        Errors err = new Errors();
        Set<String> ids = new TreeSet<>();
        Parser p = new Parser(new StringReader("sampling on event fire"));
        SamplingEvent samp = (SamplingEvent) p.SamplingClause(err, ids);
        assertTrue(err.isEmpty());

        SimulatorFpc fpc = new SimulatorFpc(vs);
        samp = samp.bind(fpc.getAttributes(), err);
        assertTrue(err.isEmpty());
        LatchingQueryHandler<Sampling, Object[]> handler = new
                LatchingQueryHandler<>();
        SamplerEvent sampler = new SamplerEvent(samp,
                Collections.emptyList(), fpc, handler);
        sampler.start();
        fpc.awaitStarted();
        assertTrue(sampler.isRunning());

        fpc.triggerError();
        fpc.awaitStopped();
        assertFalse(sampler.isRunning());
        assertThat(fpc.countAsync(), equalTo(0));
        assertThat(fpc.countPeriodic(), equalTo(0));
    }

}
