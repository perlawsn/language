package org.dei.perla.lang.executor;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.statement.Sampling;
import org.dei.perla.lang.executor.statement.SamplingIfEvery;
import org.dei.perla.lang.parser.Parser;
import org.junit.Test;

import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 13/04/15.
 */
public class SamplerTest {

    private static final Attribute power =
            Attribute.create("power", DataType.INTEGER);
    private static final Attribute temperature =
            Attribute.create("temperature", DataType.INTEGER);

    private static final Map<Attribute, Object> values;
    static {
        Map<Attribute, Object> vs = new HashMap<>();
        vs.put(power, 100);
        vs.put(temperature, 12);
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
        LatchingQueryHandler<Sampling, Object[]> handler = new
                LatchingQueryHandler<>(1);
        Sampler sampler = new Sampler(samp, Collections.emptyList(), fpc,
                handler);
        fpc.awaitPeriod(100);

        // Test with power == 65
        vs.put(power, 65);
        fpc = new SimulatorFpc(vs);
        samp = samp.bind(fpc.getAttributes());
        handler = new LatchingQueryHandler<>(1);
        sampler = new Sampler(samp, Collections.emptyList(), fpc,
                handler);
        fpc.awaitPeriod(200);

        // Test with power == 10
        vs.put(power, 10);
        fpc = new SimulatorFpc(vs);
        samp = samp.bind(fpc.getAttributes());
        handler = new LatchingQueryHandler<>(1);
        sampler = new Sampler(samp, Collections.emptyList(), fpc,
                handler);
        fpc.awaitPeriod(1000);
    }

    @Test
    public void testRefreshIfEvery() throws Exception {
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
    }

}
