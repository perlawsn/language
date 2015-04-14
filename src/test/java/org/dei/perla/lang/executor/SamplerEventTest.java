package org.dei.perla.lang.executor;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.statement.Sampling;
import org.dei.perla.lang.executor.statement.SamplingEvent;
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
 * @author Guido Rota 14/04/15.
 */
public class SamplerEventTest {

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
    public void testSamplerEvent() throws Exception {
        Map<Attribute, Object> vs = new HashMap<>(values);
        Errors err = new Errors();
        Parser p = new Parser(new StringReader("sampling on event fire"));
        SamplingEvent samp = (SamplingEvent) p.SamplingClause(err);
        assertTrue(err.isEmpty());

        SimulatorFpc fpc = new SimulatorFpc(vs);
        samp = samp.bind(fpc.getAttributes());
        LatchingQueryHandler<Sampling, Object[]> handler = new
                LatchingQueryHandler<>(3);
        SamplerEvent sampler = new SamplerEvent(samp,
                Collections.emptyList(), fpc, handler);
        sampler.start();
        assertThat(fpc.countAsync(), equalTo(1));
        fpc.triggerEvent();
        assertThat(fpc.countAsync(), equalTo(1));
        fpc.triggerEvent();
        assertThat(fpc.countAsync(), equalTo(1));
        fpc.triggerEvent();
        assertThat(fpc.countAsync(), equalTo(1));

        sampler.stop();
        assertThat(fpc.countAsync(), equalTo(0));
    }

}
