package org.dei.perla.lang.query.statement;

import org.dei.perla.core.fpc.DataType;
import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.query.expression.Constant;
import org.junit.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 30/03/15.
 */
public class SamplingTest {

    private static final Attribute lowPowerAtt =
            Attribute.create("low_power", DataType.BOOLEAN);
    private static final Attribute alertAtt =
            Attribute.create("alert", DataType.BOOLEAN);
    private static final Attribute boolAtt =
            Attribute.create("boolean", DataType.BOOLEAN);

    private static final List<Attribute> atts;
    static {
        atts = Arrays.asList(new Attribute[] {
                lowPowerAtt,
                alertAtt,
                boolAtt
        });
    }

    private static final List<Attribute> events;
    static {
        events = Arrays.asList(new Attribute[]{
                Attribute.create("low_power", DataType.ANY),
                Attribute.create("alert", DataType.ANY)
        });
    }

    @Test
    public void testSamplingIfEvery() {
        Errors err = new Errors();
        IfEvery ife = IfEvery.create(Constant.TRUE,
                Constant.create(5, DataType.INTEGER),
                ChronoUnit.SECONDS, err);
        assertTrue(err.isEmpty());
        Refresh refresh = new Refresh(Duration.ofMinutes(10));

        SamplingIfEvery s = new SamplingIfEvery(ife,
                RatePolicy.STRICT, refresh);
        assertThat(s.getIfEvery(), equalTo(ife));
        assertThat(s.getRefresh(), equalTo(refresh));
        assertThat(s.getRatePolicy(),
                equalTo(RatePolicy.STRICT));
    }

    @Test
    public void testSamplingEvent() {
        SamplingEvent s = new SamplingEvent(events);
        List<Attribute> atts = s.getEvents();
        assertThat(atts.size(), equalTo(2));
        assertTrue(atts.contains(Attribute.create("low_power", DataType.ANY)));
        assertTrue(atts.contains(Attribute.create("alert", DataType.ANY)));
    }

}
