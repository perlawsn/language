package org.dei.perla.lang.executor;

import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.statement.Sampling;
import org.dei.perla.lang.executor.statement.SamplingIfEvery;
import org.dei.perla.lang.parser.Parser;
import org.dei.perla.lang.simfpc.FpcAction;
import org.dei.perla.lang.simfpc.SimFpc;
import org.dei.perla.lang.simfpc.SimTask;
import org.junit.Test;

import java.io.StringReader;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 13/04/15.
 */
public class SamplerTest {

    @Test
    public void testSimpleIfEvery() throws Exception {
        Errors err = new Errors();
        Parser p = new Parser(new StringReader("sampling " +
                "if power > 80 every 100 milliseconds " +
                "if power > 60 every 200 milliseconds " +
                "if power > 20 every 400 milliseconds " +
                "else every 1 seconds"));

        SamplingIfEvery samp = (SamplingIfEvery) p.SamplingClause(err);
        assertTrue(err.isEmpty());

        SimFpc fpc = new SimFpc();
        samp = samp.bind(fpc.getAttributes());
        assertFalse(samp.hasErrors());
        assertTrue(samp.isComplete());

        // Test with power == 100
        LatchingQueryHandler<Sampling, Object[]> handler = new
                LatchingQueryHandler<>(1);
        Sampler sampler = new Sampler(samp, Collections.emptyList(), fpc,
                handler);

        handler.await();
        List<FpcAction> as = fpc.getActions();

        FpcAction a = as.get(0);
        assertThat(a.getField(SimTask.SAMPLING_TYPE),
                equalTo(SimTask.GET_SAMPLING));
        assertThat(a.getField(SimTask.ACTION),
                equalTo(SimTask.NEW_SAMPLE));

        a = as.get(1);
        assertThat(a.getField(SimTask.SAMPLING_TYPE),
                equalTo(SimTask.PERIODIC_SAMPLING));
        assertThat(a.getField(SimTask.ACTION),
                equalTo(SimTask.START));
        assertThat(a.getField(SimTask.PERIOD),
                equalTo(100l));


        // Test with power == 65
        fpc = new SimFpc();
        fpc.setValues(new Object[]{12, 65});
        handler = new LatchingQueryHandler<>(1);
        sampler = new Sampler(samp, Collections.emptyList(), fpc,
                handler);

        handler.await();
        as = fpc.getActions();

        a = as.get(0);
        assertThat(a.getField(SimTask.SAMPLING_TYPE),
                equalTo(SimTask.GET_SAMPLING));
        assertThat(a.getField(SimTask.ACTION),
                equalTo(SimTask.NEW_SAMPLE));

        a = as.get(1);
        assertThat(a.getField(SimTask.SAMPLING_TYPE),
                equalTo(SimTask.PERIODIC_SAMPLING));
        assertThat(a.getField(SimTask.ACTION),
                equalTo(SimTask.START));
        assertThat(a.getField(SimTask.PERIOD),
                equalTo(200l));


        // Test with power == 10
        fpc = new SimFpc();
        fpc.setValues(new Object[]{12, 10});
        handler = new LatchingQueryHandler<>(1);
        sampler = new Sampler(samp, Collections.emptyList(), fpc,
                handler);

        handler.await();
        as = fpc.getActions();

        a = as.get(0);
        assertThat(a.getField(SimTask.SAMPLING_TYPE),
                equalTo(SimTask.GET_SAMPLING));
        assertThat(a.getField(SimTask.ACTION),
                equalTo(SimTask.NEW_SAMPLE));

        a = as.get(1);
        assertThat(a.getField(SimTask.SAMPLING_TYPE),
                equalTo(SimTask.PERIODIC_SAMPLING));
        assertThat(a.getField(SimTask.ACTION),
                equalTo(SimTask.START));
        assertThat(a.getField(SimTask.PERIOD),
                equalTo(1000l));
    }

}
