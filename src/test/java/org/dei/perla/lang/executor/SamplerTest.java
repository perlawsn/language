package org.dei.perla.lang.executor;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.statement.SamplingIfEvery;
import org.dei.perla.lang.parser.Parser;
import org.junit.Test;

import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
        Parser p = new Parser(new StringReader("sampling " +
                "if power > 80 every 100 milliseconds " +
                "if power > 60 every 200 milliseconds " +
                "if power > 20 every 400 milliseconds " +
                "else every 1 seconds"));

        SamplingIfEvery samp = (SamplingIfEvery) p.SamplingClause(err);
        assertTrue(err.isEmpty());

        // Test with power == 100
        SimulatorFpc fpc = new SimulatorFpc(vs);
        samp = samp.bind(fpc.getAttributes());
        Sampler sampler = new Sampler(samp, Collections.emptyList(), fpc,
                new NoopQueryHandler<>());
        fpc.awaitPeriod(100);
        assertThat(fpc.countPeriodic(), equalTo(1));

        // Test with power == 65
        vs.put(power, 65);
        fpc = new SimulatorFpc(vs);
        samp = samp.bind(fpc.getAttributes());
        sampler = new Sampler(samp, Collections.emptyList(), fpc,
                new NoopQueryHandler<>());
        fpc.awaitPeriod(200);
        assertThat(fpc.countPeriodic(), equalTo(1));

        // Test with power == 10
        vs.put(power, 10);
        fpc = new SimulatorFpc(vs);
        samp = samp.bind(fpc.getAttributes());
        sampler = new Sampler(samp, Collections.emptyList(), fpc,
                new NoopQueryHandler<>());
        fpc.awaitPeriod(1000);
        assertThat(fpc.countPeriodic(), equalTo(1));
    }

    @Test
    public void testTimeRefreshIfEvery() throws Exception {
        Map<Attribute, Object> vs = new HashMap<>(values);
        Errors err = new Errors();
        Parser p = new Parser(new StringReader("sampling " +
                "if power > 80 every 100 milliseconds " +
                "if power > 60 every 200 milliseconds " +
                "if power > 20 every 400 milliseconds " +
                "else every 1 seconds " +
                "refresh every 1 seconds"));

        SamplingIfEvery samp = (SamplingIfEvery) p.SamplingClause(err);
        assertTrue(err.isEmpty());

        SimulatorFpc fpc = new SimulatorFpc(vs);
        samp = samp.bind(fpc.getAttributes());
        Sampler sampler = new Sampler(samp, Collections.emptyList(), fpc,
                new NoopQueryHandler<>());
        fpc.awaitPeriod(100);
        assertTrue(fpc.hasPeriod(1000));
        assertTrue(fpc.hasPeriod(100));
        assertThat(fpc.countPeriodic(), equalTo(2));

        // Change power level, check if sampling rate lowers
        vs.put(power, 70);
        fpc.setValues(vs);
        fpc.awaitPeriod(200);
        assertTrue(fpc.hasPeriod(1000));
        assertTrue(fpc.hasPeriod(200));
        assertThat(fpc.countPeriodic(), equalTo(2));

        // Change power level, check if sampling rate lowers
        vs.put(power, 10);
        fpc.setValues(vs);
        fpc.awaitPeriod(200);
        assertTrue(fpc.hasPeriod(1000));
        assertThat(fpc.countPeriodic(), equalTo(2));
    }

    @Test
    public void testEventRefreshIfEvery() throws Exception {
        Map<Attribute, Object> vs = new HashMap<>(values);
        Errors err = new Errors();
        Parser p = new Parser(new StringReader("sampling " +
                "if temperature > 40 every 100 milliseconds " +
                "if temperature > 30 every 500 milliseconds " +
                "else every 1 seconds " +
                "refresh on event fire"));

        SamplingIfEvery samp = (SamplingIfEvery) p.SamplingClause(err);
        assertTrue(err.isEmpty());
        SimulatorFpc fpc = new SimulatorFpc(vs);
        samp = samp.bind(fpc.getAttributes());

        vs.put(temperature, 25);
        fpc.setValues(vs);
        Sampler sampler = new Sampler(samp, Collections.emptyList(), fpc,
                new NoopQueryHandler<>());
        fpc.awaitPeriod(1000);
        assertThat(fpc.countPeriodic(), equalTo(1));
        assertThat(fpc.countAsync(), equalTo(1));

        // Trigger event, check if sampling rate changes
        vs.put(temperature, 35);
        assertThat(fpc.countAsync(), equalTo(1));
        fpc.triggerEvent();
        fpc.awaitPeriod(500);
        assertThat(fpc.countPeriodic(), equalTo(1));

        // Trigger event, check if sampling rate changes
        vs.put(temperature, 50);
        assertThat(fpc.countAsync(), equalTo(1));
        fpc.triggerEvent();
        fpc.awaitPeriod(500);
        assertThat(fpc.countPeriodic(), equalTo(1));
    }

}
