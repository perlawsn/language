package org.dei.perla.lang.simfpc;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.fpc.TaskHandler;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.record.Record;

import java.util.Arrays;
import java.util.List;

/**
 * @author Guido Rota 13/04/15.
 */
public abstract class SimTask implements Task {

    public static final String ACTION = "action";
    public static final String NEW_SAMPLE = "new sample";
    public static final String COMPLETED = "completed";
    public static final String ERROR = "error";

    public static final String SAMPLING_TYPE = "sampling type";
    public static final String GET_SAMPLING = "get oneshot";
    public static final String PERIODIC_SAMPLING = "get periodic";
    public static final String EVENT_SAMPLING = "event";

    public static final String RECORD = "record";

    protected static final List<Attribute> ATTRIBUTES;
    static {
        ATTRIBUTES = Arrays.asList(new Attribute[] {
           Attribute.create("integer", DataType.INTEGER)
        });
    }

    private final SimFpc fpc;
    private final TaskHandler handler;

    protected SimTask(TaskHandler handler, SimFpc fpc) {
        this.fpc = fpc;
        this.handler = handler;
    }

    @Override
    public List<Attribute> getAttributes() {
        return ATTRIBUTES;
    }

    private void logComplete(String samplingType) {
        FpcAction a = new FpcAction();
        a.addField(SAMPLING_TYPE, samplingType);
        a.addField(ACTION, COMPLETED);
        fpc.addAction(a);
    }

    private void logSample(String samplingType, Record record) {
        FpcAction a = new FpcAction();
        a.addField(SAMPLING_TYPE, samplingType);
        a.addField(ACTION, NEW_SAMPLE);
        a.addField(RECORD, record);
        fpc.addAction(a);
    }

    private Record newSample() {
        Object[] values = new Object[] {};
        return new Record(ATTRIBUTES, values);
    }

    /**
     * @author Guido Rota 13/04/15
     */
    public class GetSimTask extends SimTask {

        protected GetSimTask(TaskHandler handler, SimFpc fpc) {
            super(handler, fpc);
        }

        @Override
        public boolean isRunning() {
            return false;
        }

        @Override
        public void stop() {

        }

    }

    /**
     * @author Guido Rota 13/04/15
     */
    public class PeriodicSimTask extends SimTask {

        private volatile boolean running = true;
        private final long periodMs;
        private final Thread generator;

        protected PeriodicSimTask(long periodMs, TaskHandler handler,
                SimFpc fpc) {
            super(handler, fpc);
            this.periodMs = periodMs;
            generator = new Thread(this::generateValues);
        }

        private void generateValues() {
            try {
                while (true) {
                    Record r = newSample();
                    logSample(PERIODIC_SAMPLING, r);
                    handler.newRecord(this, r);
                    Thread.sleep(periodMs);
                }
            } catch(InterruptedException e) {
                logComplete(PERIODIC_SAMPLING);
                running = false;
                handler.complete(this);
            }
        }

        @Override
        public boolean isRunning() {
            return running;
        }

        @Override
        public void stop() {
            running = false;
            generator.interrupt();
        }

    }

}
