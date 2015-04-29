package org.dei.perla.lang.executor;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.statement.SelectionQuery;
import org.dei.perla.lang.parser.Parser;
import org.junit.Test;

import java.io.StringReader;
import java.time.Instant;
import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.*;

/**
 * @author Guido Rota 24/04/15.
 */
public class SelectExecutorTest {

    private static final Attribute temp =
            Attribute.create("temperature", DataType.INTEGER);
    private static final Attribute hum =
            Attribute.create("humidity", DataType.INTEGER);

    private static final Map<Attribute, Object> values;
    static {
        Map<Attribute, Object> m = new HashMap<>();
        m.put(Attribute.TIMESTAMP, Instant.now());
        m.put(temp, 0);
        m.put(hum, 0);
        values = Collections.unmodifiableMap(m);
    }

    private static final List<Attribute> atts = Arrays.asList(new Attribute[] {
            Attribute.TIMESTAMP,
            temp,
            hum
    });

    @Test
    public void testSampleEvery() throws Exception {
        SimulatorFpc fpc = new SimulatorFpc(values);
        Errors err = new Errors();

        Parser p = new Parser(new StringReader(
                "every 1 samples " +
                        "select temperature, humidity " +
                        "sampling every 30 milliseconds "
        ));

        SelectionQuery query = p.SelectionStatement(err);
        assertTrue(err.isEmpty());
        query = query.bind(atts);
        assertTrue(query.getWhere().isComplete());
        assertTrue(query.getExecutionConditions().isComplete());
        assertTrue(query.getSelect().isComplete());

        LatchingQueryHandler<SelectionQuery, Object[]> handler =
                new LatchingQueryHandler<>(10);
        SelectExecutor exec = new SelectExecutor(query, handler, fpc);
        assertFalse(exec.isRunning());
        exec.start();
        assertTrue(exec.isRunning());
        handler.await();

        // Test stop
        assertTrue(exec.isRunning());
        exec.stop();
        int count = handler.getDataCount();
        assertFalse(exec.isRunning());
        Thread.sleep(300);
        assertThat(handler.getDataCount(), equalTo(count));
    }

    @Test
    public void testTimedEvery() throws Exception {
        SimulatorFpc fpc = new SimulatorFpc(values);
        Errors err = new Errors();

        Parser p = new Parser(new StringReader(
                "every 100 milliseconds " +
                        "select temperature, humidity " +
                        "sampling every 30 milliseconds "
        ));

        SelectionQuery query = p.SelectionStatement(err);
        assertTrue(err.isEmpty());
        query = query.bind(atts);
        assertTrue(query.getWhere().isComplete());
        assertTrue(query.getExecutionConditions().isComplete());
        assertTrue(query.getSelect().isComplete());

        LatchingQueryHandler<SelectionQuery, Object[]> handler =
                new LatchingQueryHandler<>(10);
        SelectExecutor exec = new SelectExecutor(query, handler, fpc);
        assertFalse(exec.isRunning());
        exec.start();
        assertTrue(exec.isRunning());
        handler.await();

        // Test stop
        assertTrue(exec.isRunning());
        exec.stop();
        int count = handler.getDataCount();
        assertFalse(exec.isRunning());
        Thread.sleep(300);
        assertThat(handler.getDataCount(), equalTo(count));
    }

    @Test
    public void testRecordTerminateAfter() throws Exception {
        SimulatorFpc fpc = new SimulatorFpc(values);
        Errors err = new Errors();

        Parser p = new Parser(new StringReader(
                "every one " +
                        "select temperature, humidity " +
                        "sampling every 30 milliseconds " +
                        "terminate after 3 selections"
        ));

        SelectionQuery query = p.SelectionStatement(err);
        assertTrue(err.isEmpty());
        query = query.bind(atts);
        assertTrue(query.getWhere().isComplete());
        assertTrue(query.getExecutionConditions().isComplete());
        assertTrue(query.getSelect().isComplete());

        LatchingQueryHandler<SelectionQuery, Object[]> handler =
                new LatchingQueryHandler<>(3);
        SelectExecutor exec = new SelectExecutor(query, handler, fpc);
        assertFalse(exec.isRunning());
        exec.start();
        assertTrue(exec.isRunning());
        handler.await();

        assertFalse(exec.isRunning());
        assertThat(handler.getDataCount(), equalTo(3));
    }

    @Test
    public void testTimedTerminateAfter() throws Exception {
        SimulatorFpc fpc = new SimulatorFpc(values);
        Errors err = new Errors();

        Parser p = new Parser(new StringReader(
                "every one " +
                        "select temperature, humidity " +
                        "sampling every 30 milliseconds " +
                        "terminate after 300 milliseconds"
        ));

        SelectionQuery query = p.SelectionStatement(err);
        assertTrue(err.isEmpty());
        query = query.bind(atts);
        assertTrue(query.getWhere().isComplete());
        assertTrue(query.getExecutionConditions().isComplete());
        assertTrue(query.getSelect().isComplete());

        LatchingQueryHandler<SelectionQuery, Object[]> handler =
                new LatchingQueryHandler<>(1);
        SelectExecutor exec = new SelectExecutor(query, handler, fpc);
        assertFalse(exec.isRunning());
        exec.start();
        assertTrue(exec.isRunning());
        Thread.sleep(400);

        assertFalse(exec.isRunning());
        assertThat(handler.getDataCount(), greaterThanOrEqualTo(8));
    }

    @Test
    public void testWhereSampling() throws Exception {
        SimulatorFpc fpc = new SimulatorFpc(values);
        Errors err = new Errors();

        Parser p = new Parser(new StringReader(
                "every one " +
                        "select temperature, humidity " +
                        "sampling every 30 milliseconds " +
                        "where temperature > 30 " +
                        "terminate after 1 selections"
        ));

        SelectionQuery query = p.SelectionStatement(err);
        assertTrue(err.isEmpty());
        query = query.bind(atts);
        assertTrue(query.getWhere().isComplete());
        assertTrue(query.getExecutionConditions().isComplete());
        assertTrue(query.getSelect().isComplete());

        LatchingQueryHandler<SelectionQuery, Object[]> handler =
                new LatchingQueryHandler<>(1);
        SelectExecutor exec = new SelectExecutor(query, handler, fpc);
        assertFalse(exec.isRunning());
        exec.start();
        assertTrue(exec.isRunning());

        Thread.sleep(100);
        assertThat(handler.getDataCount(), equalTo(0));

        Map<Attribute, Object> v = new HashMap<>(values);
        v.put(temp, 35);
        fpc.setValues(v);
        handler.await();
        assertFalse(exec.isRunning());
        assertThat(handler.getDataCount(), equalTo(1));
    }

}
