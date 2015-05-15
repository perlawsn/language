package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.LatchingQueryHandler;
import org.dei.perla.lang.executor.SimulatorFpc;
import org.dei.perla.lang.query.parser.Parser;
import org.dei.perla.lang.query.statement.SelectionQuery;
import org.junit.Test;

import java.io.StringReader;
import java.time.Instant;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * @author Guido Rota 24/04/15.
 */
public class SelectionExecutorTest {

    private static final Attribute temp =
            Attribute.create("temperature", DataType.INTEGER);
    private static final Attribute hum =
            Attribute.create("humidity", DataType.INTEGER);
    private static final Attribute alarm =
            Attribute.create("alarm", DataType.BOOLEAN);

    private static final Map<Attribute, Object> values;
    static {
        Map<Attribute, Object> m = new HashMap<>();
        m.put(Attribute.TIMESTAMP, Instant.now());
        m.put(temp, 0);
        m.put(hum, 0);
        m.put(alarm, false);
        values = Collections.unmodifiableMap(m);
    }

    private static final List<Attribute> atts = Arrays.asList(new Attribute[] {
            Attribute.TIMESTAMP,
            temp,
            hum,
            alarm
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
                new LatchingQueryHandler<>();
        SelectionExecutor exec = new SelectionExecutor(query, handler, fpc);
        assertFalse(exec.isRunning());
        exec.start();
        fpc.awaitStarted();
        assertTrue(exec.isRunning());
        handler.awaitCount(10);

        // Test stop
        assertTrue(exec.isRunning());
        exec.stop();
        fpc.awaitStopped();
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
                new LatchingQueryHandler<>();
        SelectionExecutor exec = new SelectionExecutor(query, handler, fpc);
        assertFalse(exec.isRunning());
        exec.start();
        fpc.awaitStarted();
        assertTrue(exec.isRunning());
        handler.awaitCount(10);

        // Test stop
        assertTrue(exec.isRunning());
        exec.stop();
        fpc.awaitStopped();
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
                new LatchingQueryHandler<>();
        SelectionExecutor exec = new SelectionExecutor(query, handler, fpc);
        assertFalse(exec.isRunning());
        exec.start();
        fpc.awaitStarted();
        assertTrue(exec.isRunning());
        handler.awaitCount(3);

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
                new LatchingQueryHandler<>();
        SelectionExecutor exec = new SelectionExecutor(query, handler, fpc);
        assertFalse(exec.isRunning());
        exec.start();
        fpc.awaitStarted();
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
                new LatchingQueryHandler<>();
        SelectionExecutor exec = new SelectionExecutor(query, handler, fpc);
        assertFalse(exec.isRunning());
        exec.start();
        fpc.awaitStarted();
        assertTrue(exec.isRunning());

        Thread.sleep(100);
        assertThat(handler.getDataCount(), equalTo(0));

        Map<Attribute, Object> v = new HashMap<>(values);
        v.put(temp, 35);
        fpc.setValues(v);
        handler.awaitCount(1);
        assertFalse(exec.isRunning());
        assertThat(handler.getDataCount(), equalTo(1));
    }

    @Test
    public void testExecuteIfTimedRefresh() throws Exception {
        Map<Attribute, Object> v = new HashMap(values);
        v.put(temp, 20);
        SimulatorFpc fpc = new SimulatorFpc(values);
        Errors err = new Errors();

        Parser p = new Parser(new StringReader(
                "every one " +
                        "select temperature, humidity " +
                        "sampling every 30 milliseconds " +
                        "execute if temperature > 30 " +
                        "refresh every 100 milliseconds"
        ));

        SelectionQuery query = p.SelectionStatement(err);
        assertTrue(err.isEmpty());
        query = query.bind(atts);
        assertTrue(query.getWhere().isComplete());
        assertTrue(query.getExecutionConditions().isComplete());
        assertTrue(query.getSelect().isComplete());

        LatchingQueryHandler<SelectionQuery, Object[]> handler =
                new LatchingQueryHandler<>();
        SelectionExecutor exec = new SelectionExecutor(query, handler, fpc);
        assertFalse(exec.isRunning());
        exec.start();
        assertTrue(exec.isRunning());
        Thread.sleep(400);

        // Change fpc values and check if sampling starts
        assertTrue(exec.isPaused());
        assertThat(handler.getDataCount(), equalTo(0));
        v.put(temp, 40);
        fpc.setValues(v);
        handler.awaitCount(1);
        assertTrue(exec.isRunning());
        assertFalse(exec.isPaused());
        assertThat(handler.getDataCount(), greaterThan(0));

        // Change fpc values and check if sampling pauses
        v.put(temp, 20);
        fpc.setValues(v);
        Thread.sleep(200);
        assertTrue(exec.isRunning());
        fpc.awaitStopped();
        assertTrue(exec.isPaused());
        int count = handler.getDataCount();
        Thread.sleep(200);
        assertThat(handler.getDataCount(), equalTo(count));
    }

    @Test
    public void testExecuteIfEventRefresh() throws Exception {
        Map<Attribute, Object> v = new HashMap(values);
        v.put(temp, 20);
        SimulatorFpc fpc = new SimulatorFpc(values);
        Errors err = new Errors();

        Parser p = new Parser(new StringReader(
                "every one " +
                        "select temperature, humidity " +
                        "sampling every 30 milliseconds " +
                        "execute if temperature > 30 " +
                        "refresh on event alarm"
        ));

        SelectionQuery query = p.SelectionStatement(err);
        assertTrue(err.isEmpty());
        query = query.bind(atts);
        assertTrue(query.getWhere().isComplete());
        assertTrue(query.getExecutionConditions().isComplete());
        assertTrue(query.getSelect().isComplete());

        LatchingQueryHandler<SelectionQuery, Object[]> handler =
                new LatchingQueryHandler<>();
        SelectionExecutor exec = new SelectionExecutor(query, handler, fpc);
        assertFalse(exec.isRunning());
        exec.start();
        assertTrue(exec.isRunning());
        Thread.sleep(400);

        // Change fpc values and check if sampling starts
        assertTrue(exec.isPaused());
        assertThat(handler.getDataCount(), equalTo(0));
        v.put(temp, 40);
        fpc.setValues(v);
        fpc.triggerEvent();
        handler.awaitCount(1);
        assertTrue(exec.isRunning());
        assertFalse(exec.isPaused());
        assertThat(handler.getDataCount(), greaterThan(0));


        // Change fpc values and check if sampling pauses
        v.put(temp, 20);
        fpc.setValues(v);
        fpc.triggerEvent();
        assertTrue(exec.isRunning());
        fpc.awaitPeriodicStopped();
        assertTrue(exec.isPaused());
        assertThat(fpc.countAsync(), equalTo(1));
        int count = handler.getDataCount();
        Thread.sleep(200);
        assertThat(handler.getDataCount(), equalTo(count));
    }

}
