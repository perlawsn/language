package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.expression.Constant;
import org.dei.perla.lang.executor.expression.Field;
import org.junit.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

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

    private static final Set<String> names;
    static {
        Set<String> s = new TreeSet<>();
        s.add("low_power");
        s.add("alert");
        names = Collections.unmodifiableSet(s);
    }

    @Test
    public void testSamplingIfEvery() {
        IfEvery ife = IfEvery.create(Constant.TRUE,
                Constant.create(5, DataType.INTEGER),
                ChronoUnit.SECONDS).getClause();
        Refresh refresh = new Refresh(Duration.ofMinutes(10));

        SamplingIfEvery s = new SamplingIfEvery(ife,
                RatePolicy.DO_NOT_SAMPLE, refresh);
        assertFalse(s.hasErrors());
        assertTrue(s.isComplete());
        assertThat(s.getIfEvery(), equalTo(ife));
        assertThat(s.getRefresh(), equalTo(refresh));
        assertThat(s.getRatePolicy(),
                equalTo(RatePolicy.DO_NOT_SAMPLE));
    }

    @Test
    public void testSamplingIfEveryBind() {
        IfEvery ife = IfEvery.create(new Field("boolean"),
                Constant.create(5, DataType.INTEGER),
                ChronoUnit.SECONDS).getClause();
        Refresh refresh = new Refresh(names);

        SamplingIfEvery s = new SamplingIfEvery(ife,
                RatePolicy.SLOW_DOWN, refresh);
        assertFalse(s.hasErrors());
        assertFalse(s.isComplete());
        assertThat(s.getRatePolicy(),
                equalTo(RatePolicy.SLOW_DOWN));

        s = s.bind(atts);
        assertFalse(s.hasErrors());
        assertTrue(s.isComplete());
        assertThat(s.getRatePolicy(),
                equalTo(RatePolicy.SLOW_DOWN));
    }

    @Test
    public void testSamplingEvent() {
        SamplingEvent s = new SamplingEvent(names);
        assertFalse(s.hasErrors());
        assertFalse(s.isComplete());
        s = s.bind(atts);
        assertFalse(s.hasErrors());
        assertTrue(s.isComplete());
        List<Attribute> bound = s.getEvents();
        assertThat(bound.size(), equalTo(2));
        assertTrue(bound.contains(lowPowerAtt));
        assertTrue(bound.contains(alertAtt));
    }

}
