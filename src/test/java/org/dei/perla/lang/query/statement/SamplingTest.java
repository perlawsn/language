package org.dei.perla.lang.query.statement;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.query.expression.Constant;
import org.junit.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

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

    private static final Attribute intAtt =
            Attribute.create("integer", DataType.INTEGER);
    private static final Attribute boolAtt =
            Attribute.create("boolean", DataType.BOOLEAN);

    private static final List<Attribute> atts;
    static {
        atts = Arrays.asList(new Attribute[] {
                intAtt,
                boolAtt
        });
    }

    private static final List<Attribute> events;
    static {
        events = Arrays.asList(new Attribute[] {
                lowPowerAtt,
                alertAtt
        });
    }

    @Test
    public void testSamplingIfEvery() {
        IfEvery ife = new IfEvery(Constant.TRUE,
                Constant.create(5, DataType.INTEGER),
                ChronoUnit.SECONDS, null);
        Refresh refresh = new Refresh(Duration.ofMinutes(10));

        SamplingIfEvery s = new SamplingIfEvery(ife,
                RatePolicy.STRICT, refresh, atts);
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
        assertTrue(atts.contains(lowPowerAtt));
        assertTrue(atts.contains(alertAtt));
    }

}
