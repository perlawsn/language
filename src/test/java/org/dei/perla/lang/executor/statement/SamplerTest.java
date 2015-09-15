package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.executor.LatchingQueryHandler;
import org.dei.perla.lang.executor.NoopQueryHandler;
import org.dei.perla.lang.executor.SimulatorFpc;
import org.dei.perla.lang.query.expression.*;
import org.dei.perla.lang.query.statement.*;
import org.junit.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
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

        Expression cond = Constant.TRUE;
        Expression value = Constant.create(1, DataType.INTEGER);
        IfEvery ife = new IfEvery(cond, value, ChronoUnit.SECONDS, null);

        cond = new Comparison(
                ComparisonOperation.GT,
                new AttributeReference("power", DataType.INTEGER, 0),
                Constant.create(20, DataType.INTEGER));
        value = Constant.create(400, DataType.INTEGER);
        ife = new IfEvery(cond, value, ChronoUnit.MILLIS, ife);

        cond = new Comparison(
                ComparisonOperation.GT,
                new AttributeReference("power", DataType.INTEGER, 0),
                Constant.create(60, DataType.INTEGER));
        value = Constant.create(200, DataType.INTEGER);
        ife = new IfEvery(cond, value, ChronoUnit.MILLIS, ife);

        cond = new Comparison(
                ComparisonOperation.GT,
                new AttributeReference("power", DataType.INTEGER, 0),
                Constant.create(80, DataType.INTEGER));
        value = Constant.create(100, DataType.INTEGER);
        ife = new IfEvery(cond, value, ChronoUnit.MILLIS, ife);

        List<Attribute> atts = Arrays.asList(new Attribute[] {
                Attribute.create("power", DataType.INTEGER)
        });
        SamplingIfEvery samp = new SamplingIfEvery(ife, RatePolicy.STRICT,
                Refresh.NEVER, atts);

        // Test with power == 100
        SimulatorFpc fpc = new SimulatorFpc(vs);
        SamplerIfEvery sampler = new SamplerIfEvery(samp, fpc,
                new NoopQueryHandler());
        sampler.start();
        fpc.awaitPeriod(100);
        assertTrue(sampler.isRunning());
        assertThat(fpc.countPeriodic(), equalTo(1));

        // Test with power == 65
        vs.put(power, 65);
        fpc = new SimulatorFpc(vs);
        sampler = new SamplerIfEvery(samp, fpc, new NoopQueryHandler());
        sampler.start();
        fpc.awaitPeriod(200);
        assertTrue(sampler.isRunning());
        assertThat(fpc.countPeriodic(), equalTo(1));

        // Test with power == 10
        vs.put(power, 10);
        fpc = new SimulatorFpc(vs);
        sampler = new SamplerIfEvery(samp, fpc, new NoopQueryHandler());
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
        vs.put(power, 90);
        Expression cond = Constant.TRUE;
        Expression value = Constant.create(1, DataType.INTEGER);
        IfEvery ife = new IfEvery(cond, value, ChronoUnit.SECONDS, null);

        cond = new Comparison(
                ComparisonOperation.GT,
                new AttributeReference("power", DataType.INTEGER, 0),
                Constant.create(20, DataType.INTEGER));
        value = Constant.create(400, DataType.INTEGER);
        ife = new IfEvery(cond, value, ChronoUnit.MILLIS, ife);

        cond = new Comparison(
                ComparisonOperation.GT,
                new AttributeReference("power", DataType.INTEGER, 0),
                Constant.create(60, DataType.INTEGER));
        value = Constant.create(200, DataType.INTEGER);
        ife = new IfEvery(cond, value, ChronoUnit.MILLIS, ife);

        cond = new Comparison(
                ComparisonOperation.GT,
                new AttributeReference("power", DataType.INTEGER, 0),
                Constant.create(80, DataType.INTEGER));
        value = Constant.create(100, DataType.INTEGER);
        ife = new IfEvery(cond, value, ChronoUnit.MILLIS, ife);

        Refresh ref = new Refresh(Duration.ofSeconds(1));
        List<Attribute> atts = Arrays.asList(new Attribute[] {
                Attribute.create("power", DataType.INTEGER)
        });
        SamplingIfEvery samp = new SamplingIfEvery(ife, RatePolicy.STRICT,
                ref, atts);

        SimulatorFpc fpc = new SimulatorFpc(vs);
        SamplerIfEvery sampler = new SamplerIfEvery(samp, fpc,
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
        vs.put(power, 25);
        fpc.setValues(vs);
        fpc.awaitPeriod(400);
        assertTrue(fpc.hasPeriod(400));
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
        vs.put(temperature, 25);
        SimulatorFpc fpc = new SimulatorFpc(vs);

        Expression cond = Constant.TRUE;
        Expression value = Constant.create(1, DataType.INTEGER);
        IfEvery ife = new IfEvery(cond, value, ChronoUnit.SECONDS, null);

        cond = new Comparison(
                ComparisonOperation.GT,
                new AttributeReference("temperature", DataType.INTEGER, 0),
                Constant.create(30, DataType.INTEGER));
        value = Constant.create(500, DataType.INTEGER);
        ife = new IfEvery(cond, value, ChronoUnit.MILLIS, ife);

        cond = new Comparison(
                ComparisonOperation.GT,
                new AttributeReference("temperature", DataType.INTEGER, 0),
                Constant.create(40, DataType.INTEGER));
        value = Constant.create(100, DataType.INTEGER);
        ife = new IfEvery(cond, value, ChronoUnit.MILLIS, ife);

        List<Attribute> evs = Arrays.asList(new Attribute[] {
                Attribute.create("fire", DataType.ANY)
        });
        Refresh ref = new Refresh(evs);
        List<Attribute> atts = Arrays.asList(new Attribute[] {
                Attribute.create("temperature", DataType.INTEGER)
        });
        SamplingIfEvery samp = new SamplingIfEvery(ife, RatePolicy.STRICT,
                ref, atts);

        SamplerIfEvery sampler = new SamplerIfEvery(samp, fpc,
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
        vs.put(temperature, 25);

        Expression cond = Constant.TRUE;
        Expression value = Constant.create(1, DataType.INTEGER);
        IfEvery ife = new IfEvery(cond, value, ChronoUnit.SECONDS, null);

        cond = new Comparison(
                ComparisonOperation.GT,
                new AttributeReference("temperature", DataType.INTEGER, 0),
                Constant.create(30, DataType.INTEGER));
        value = Constant.create(500, DataType.INTEGER);
        ife = new IfEvery(cond, value, ChronoUnit.MILLIS, ife);

        cond = new Comparison(
                ComparisonOperation.GT,
                new AttributeReference("temperature", DataType.INTEGER, 0),
                Constant.create(40, DataType.INTEGER));
        value = Constant.create(100, DataType.INTEGER);
        ife = new IfEvery(cond, value, ChronoUnit.MILLIS, ife);

        List<Attribute> evs = Arrays.asList(new Attribute[] {
                Attribute.create("fire", DataType.ANY)
        });
        Refresh ref = new Refresh(evs);
        List<Attribute> atts = Arrays.asList(new Attribute[] {
                Attribute.create("temperature", DataType.INTEGER)
        });
        SamplingIfEvery samp = new SamplingIfEvery(ife, RatePolicy.STRICT,
                ref, atts);

        SimulatorFpc fpc = new SimulatorFpc(vs);
        SamplerIfEvery sampler = new SamplerIfEvery(samp, fpc,
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
        List<Attribute> evs = Arrays.asList(new Attribute[] {
                Attribute.create("fire", DataType.ANY)
        });
        SamplingEvent samp = new SamplingEvent(evs);

        SimulatorFpc fpc = new SimulatorFpc(vs);
        LatchingQueryHandler<Sampling, Object[]> handler = new
                LatchingQueryHandler<>();
        SamplerEvent sampler = new SamplerEvent(samp, fpc, handler);
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
        List<Attribute> evs = Arrays.asList(new Attribute[] {
                Attribute.create("fire", DataType.ANY)
        });
        SamplingEvent samp = new SamplingEvent(evs);

        SimulatorFpc fpc = new SimulatorFpc(vs);
        LatchingQueryHandler<Sampling, Object[]> handler = new
                LatchingQueryHandler<>();
        SamplerEvent sampler = new SamplerEvent(samp, fpc, handler);
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
