package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.Common;
import org.dei.perla.lang.executor.LatchingQueryHandler;
import org.dei.perla.lang.executor.SimulatorFpc;
import org.dei.perla.lang.parser.ParseException;
import org.dei.perla.lang.parser.ParserAST;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.ast.SelectionStatementAST;
import org.dei.perla.lang.query.statement.SelectionStatement;
import org.junit.Test;

import java.io.StringReader;
import java.time.Instant;
import java.util.*;

import static org.hamcrest.Matchers.equalTo;
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

    private static SelectionStatement getParser(String query)
            throws ParseException {
        ParserContext ctx = new ParserContext();
        ParserAST p = new ParserAST(new StringReader(query));
        SelectionStatementAST sa = p.SelectionStatement(ctx);
        SelectionStatement s = sa.compile(ctx);
        if (ctx.hasErrors()) {
            throw new RuntimeException(ctx.getErrorDescription());
        }
        return s;
    }

    private static final List<Attribute> atts = Arrays.asList(new Attribute[] {
            Attribute.TIMESTAMP,
            temp,
            hum,
            alarm
    });

    @Test
    public void testSampleEvery() throws Exception {
        Map<Attribute, Object> values = new HashMap<>();
        values.put(Common.TEMP_INT, 24);
        values.put(Common.HUM_INT, 12);
        values.put(Attribute.TIMESTAMP, Instant.now());
        SimulatorFpc fpc = new SimulatorFpc(values);

        SelectionStatement query = getParser("every one " +
                        "select temperature:integer, humidity:integer " +
                        "sampling every 30 milliseconds ");

        LatchingQueryHandler<SelectionStatement, Object[]> handler =
                new LatchingQueryHandler<>();
        SelectionExecutor exec = new SelectionExecutor(query, fpc, handler);
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
        throw new RuntimeException("unimplemented");
//        SimulatorFpc fpc = new SimulatorFpc(values);
//        Errors err = new Errors();
//
//        Parser p = new Parser(new StringReader(
//                "every 100 milliseconds " +
//                        "select temperature, humidity " +
//                        "sampling every 30 milliseconds "
//        ));
//
//        SelectionStatement query = p.SelectionStatement(err);
//        assertTrue(err.isEmpty());
//        query = query.bind(atts);
//        assertTrue(query.getWhere().isComplete());
//        assertTrue(query.getExecutionConditions().isComplete());
//        assertTrue(query.getSelect().isComplete());
//
//        LatchingQueryHandler<SelectionStatement, Object[]> handler =
//                new LatchingQueryHandler<>();
//        SelectionExecutor exec = new SelectionExecutor(query, handler, fpc);
//        assertFalse(exec.isRunning());
//        exec.start();
//        fpc.awaitStarted();
//        assertTrue(exec.isRunning());
//        handler.awaitCount(10);
//
//        // Test stop
//        assertTrue(exec.isRunning());
//        exec.stop();
//        fpc.awaitStopped();
//        int count = handler.getDataCount();
//        assertFalse(exec.isRunning());
//        Thread.sleep(300);
//        assertThat(handler.getDataCount(), equalTo(count));
    }

    @Test
    public void testRecordTerminateAfter() throws Exception {
        throw new RuntimeException("unimplemented");
//        SimulatorFpc fpc = new SimulatorFpc(values);
//        Errors err = new Errors();
//
//        Parser p = new Parser(new StringReader(
//                "every one " +
//                        "select temperature, humidity " +
//                        "sampling every 30 milliseconds " +
//                        "terminate after 3 selections"
//        ));
//
//        SelectionStatement query = p.SelectionStatement(err);
//        assertTrue(err.isEmpty());
//        query = query.bind(atts);
//        assertTrue(query.getWhere().isComplete());
//        assertTrue(query.getExecutionConditions().isComplete());
//        assertTrue(query.getSelect().isComplete());
//
//        LatchingQueryHandler<SelectionStatement, Object[]> handler =
//                new LatchingQueryHandler<>();
//        SelectionExecutor exec = new SelectionExecutor(query, handler, fpc);
//        assertFalse(exec.isRunning());
//        exec.start();
//        fpc.awaitStarted();
//        assertTrue(exec.isRunning());
//
//        handler.awaitCount(3);
//        assertFalse(exec.isRunning());
//        assertThat(handler.getDataCount(), equalTo(3));
    }

    @Test
    public void testTimedTerminateAfter() throws Exception {
        throw new RuntimeException("unimplemented");
//        SimulatorFpc fpc = new SimulatorFpc(values);
//        Errors err = new Errors();
//
//        Parser p = new Parser(new StringReader(
//                "every one " +
//                        "select temperature, humidity " +
//                        "sampling every 30 milliseconds " +
//                        "terminate after 300 milliseconds"
//        ));
//
//        SelectionStatement query = p.SelectionStatement(err);
//        assertTrue(err.isEmpty());
//        query = query.bind(atts);
//        assertTrue(query.getWhere().isComplete());
//        assertTrue(query.getExecutionConditions().isComplete());
//        assertTrue(query.getSelect().isComplete());
//
//        LatchingQueryHandler<SelectionStatement, Object[]> handler =
//                new LatchingQueryHandler<>();
//        SelectionExecutor exec = new SelectionExecutor(query, handler, fpc);
//        assertFalse(exec.isRunning());
//        exec.start();
//        fpc.awaitStarted();
//        assertTrue(exec.isRunning());
//
//        Thread.sleep(400);
//        assertFalse(exec.isRunning());
//        assertThat(handler.getDataCount(), greaterThanOrEqualTo(8));
    }

    @Test
    public void testWhereSampling() throws Exception {
        throw new RuntimeException("unimplemented");
//        SimulatorFpc fpc = new SimulatorFpc(values);
//        Errors err = new Errors();
//
//        Parser p = new Parser(new StringReader(
//                "every one " +
//                        "select temperature, humidity " +
//                        "sampling every 30 milliseconds " +
//                        "where temperature > 30 " +
//                        "terminate after 1 selections"
//        ));
//
//        SelectionStatement query = p.SelectionStatement(err);
//        assertTrue(err.isEmpty());
//        query = query.bind(atts);
//        assertTrue(query.getWhere().isComplete());
//        assertTrue(query.getExecutionConditions().isComplete());
//        assertTrue(query.getSelect().isComplete());
//
//        LatchingQueryHandler<SelectionStatement, Object[]> handler =
//                new LatchingQueryHandler<>();
//        SelectionExecutor exec = new SelectionExecutor(query, handler, fpc);
//        assertFalse(exec.isRunning());
//        exec.start();
//        fpc.awaitStarted();
//        assertTrue(exec.isRunning());
//
//        Thread.sleep(100);
//        assertThat(handler.getDataCount(), equalTo(0));
//
//        Map<Attribute, Object> v = new HashMap<>(values);
//        v.put(temp, 35);
//        fpc.setValues(v);
//        handler.awaitCount(1);
//        assertFalse(exec.isRunning());
//        assertThat(handler.getDataCount(), equalTo(1));
    }

    @Test
    public void testExecuteIfTimedRefresh() throws Exception {
        throw new RuntimeException("unimplemented");
//        Map<Attribute, Object> v = new HashMap(values);
//        v.put(temp, 20);
//        SimulatorFpc fpc = new SimulatorFpc(values);
//        Errors err = new Errors();
//
//        Parser p = new Parser(new StringReader(
//                "every one " +
//                        "select temperature, humidity " +
//                        "sampling every 30 milliseconds " +
//                        "execute if temperature > 30 " +
//                        "refresh every 100 milliseconds"
//        ));
//
//        SelectionStatement query = p.SelectionStatement(err);
//        assertTrue(err.isEmpty());
//        query = query.bind(atts);
//        assertTrue(query.getWhere().isComplete());
//        assertTrue(query.getExecutionConditions().isComplete());
//        assertTrue(query.getSelect().isComplete());
//
//        LatchingQueryHandler<SelectionStatement, Object[]> handler =
//                new LatchingQueryHandler<>();
//        SelectionExecutor exec = new SelectionExecutor(query, handler, fpc);
//        assertFalse(exec.isRunning());
//        exec.start();
//        assertTrue(exec.isRunning());
//        Thread.sleep(400);
//
//        // Change fpc values and check if sampling starts
//        assertTrue(exec.isPaused());
//        assertThat(handler.getDataCount(), equalTo(0));
//        v.put(temp, 40);
//        fpc.setValues(v);
//        handler.awaitCount(1);
//        assertTrue(exec.isRunning());
//        assertFalse(exec.isPaused());
//        assertThat(handler.getDataCount(), greaterThan(0));
//
//        // Change fpc values and check if sampling pauses
//        v.put(temp, 20);
//        fpc.setValues(v);
//        Thread.sleep(200);
//        assertTrue(exec.isRunning());
//        fpc.awaitStopped();
//        assertTrue(exec.isPaused());
//        int count = handler.getDataCount();
//        Thread.sleep(200);
//        assertThat(handler.getDataCount(), equalTo(count));
    }

    @Test
    public void testExecuteIfEventRefresh() throws Exception {
        throw new RuntimeException("unimplemented");
//        Map<Attribute, Object> v = new HashMap(values);
//        v.put(temp, 20);
//        SimulatorFpc fpc = new SimulatorFpc(values);
//        Errors err = new Errors();
//
//        Parser p = new Parser(new StringReader(
//                "every one " +
//                        "select temperature, humidity " +
//                        "sampling every 30 milliseconds " +
//                        "execute if temperature > 30 " +
//                        "refresh on event alarm"
//        ));
//
//        SelectionStatement query = p.SelectionStatement(err);
//        assertTrue(err.isEmpty());
//        query = query.bind(atts);
//        assertTrue(query.getWhere().isComplete());
//        assertTrue(query.getExecutionConditions().isComplete());
//        assertTrue(query.getSelect().isComplete());
//
//        LatchingQueryHandler<SelectionStatement, Object[]> handler =
//                new LatchingQueryHandler<>();
//        SelectionExecutor exec = new SelectionExecutor(query, handler, fpc);
//        assertFalse(exec.isRunning());
//        exec.start();
//        assertTrue(exec.isRunning());
//        Thread.sleep(400);
//
//        // Change fpc values and check if sampling starts
//        assertTrue(exec.isPaused());
//        assertThat(handler.getDataCount(), equalTo(0));
//        v.put(temp, 40);
//        fpc.setValues(v);
//        fpc.triggerEvent();
//        handler.awaitCount(1);
//        assertTrue(exec.isRunning());
//        assertFalse(exec.isPaused());
//        assertThat(handler.getDataCount(), greaterThan(0));
//
//
//        // Change fpc values and check if sampling pauses
//        v.put(temp, 20);
//        fpc.setValues(v);
//        fpc.triggerEvent();
//        assertTrue(exec.isRunning());
//        fpc.awaitPeriodicStopped();
//        assertTrue(exec.isPaused());
//        assertThat(fpc.countAsync(), equalTo(1));
//        int count = handler.getDataCount();
//        Thread.sleep(200);
//        assertThat(handler.getDataCount(), equalTo(count));
    }

}
