package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.lang.CommonAttributes;
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
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.*;

/**
 * @author Guido Rota 24/04/15.
 */
public class SelectionExecutorTest {

    private static SelectionStatement getStatement(String query)
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
            CommonAttributes.TEMP_INT,
            CommonAttributes.HUM_INT,
            CommonAttributes.ALARM_BOOL
    });

    private static Map<Attribute, Object> createDefaultValues() {
        Map<Attribute, Object> values = new HashMap<>();
        values.put(CommonAttributes.TEMP_INT, 24);
        values.put(CommonAttributes.HUM_INT, 12);
        values.put(Attribute.TIMESTAMP, Instant.now());

        return values;
    }

    @Test
    public void testSampleEvery() throws Exception {

    	Map<Attribute, Object> values = createDefaultValues();
        SimulatorFpc fpc = new SimulatorFpc(values);

        SelectionStatement query = getStatement("every one " +
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
        System.out.println("Valore: "+count);
        assertFalse(exec.isRunning());
        Thread.sleep(300);
        assertThat(handler.getDataCount(), equalTo(count));
    }

    @Test
    public void testTimedEvery() throws Exception {
        Map<Attribute, Object> values = createDefaultValues();
        SimulatorFpc fpc = new SimulatorFpc(values);

        SelectionStatement query = getStatement("every 100 milliseconds " +
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
    public void testRecordTerminateAfter() throws Exception {
        Map<Attribute, Object> values = createDefaultValues();
        SimulatorFpc fpc = new SimulatorFpc(values);

        SelectionStatement query = getStatement("every one " +
                        "select temperature:integer, humidity:integer " +
                        "sampling every 30 milliseconds " +
                        "terminate after 3 selections");

        LatchingQueryHandler<SelectionStatement, Object[]> handler =
                new LatchingQueryHandler<>();
        SelectionExecutor exec = new SelectionExecutor(query, fpc, handler);
        assertFalse(exec.isRunning());
        fpc.pausePeriodicSampling();
        exec.start();
        fpc.awaitStarted();
        assertTrue(exec.isRunning());
        fpc.resumePeriodicSampling();

        handler.awaitCount(3);
        assertFalse(exec.isRunning());
        assertThat(handler.getDataCount(), equalTo(3));
    }

    @Test
    public void testWhereSampling() throws Exception {
        Map<Attribute, Object> values = createDefaultValues();
        SimulatorFpc fpc = new SimulatorFpc(values);

        SelectionStatement query = getStatement("every one " +
                        "select temperature:integer, humidity:integer " +
                        "sampling every 30 milliseconds " +
                        "where temperature > 30 " +
                        "terminate after 1 selections"
        );

        LatchingQueryHandler<SelectionStatement, Object[]> handler =
                new LatchingQueryHandler<>();
        SelectionExecutor exec = new SelectionExecutor(query, fpc, handler);
        assertFalse(exec.isRunning());
        exec.start();
        fpc.awaitStarted();
        assertTrue(exec.isRunning());

        Thread.sleep(100);
        assertThat(handler.getDataCount(), equalTo(0));

        Map<Attribute, Object> v = new HashMap<>(values);
        v.put(CommonAttributes.TEMP_INT, 35);
        fpc.setValues(v);
        handler.awaitCount(1);
        assertFalse(exec.isRunning());
        assertThat(handler.getDataCount(), equalTo(1));
    }

}
