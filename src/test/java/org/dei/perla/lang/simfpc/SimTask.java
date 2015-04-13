package org.dei.perla.lang.simfpc;

import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.fpc.TaskHandler;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.record.Record;
import org.dei.perla.core.record.SamplePipeline;
import org.dei.perla.core.record.SamplePipeline.PipelineBuilder;

import java.util.List;

/**
 * @author Guido Rota 13/04/15.
 */
public abstract class SimTask implements Task {

    public static final String ACTION = "action";
    public static final String START = "start";
    public static final String NEW_SAMPLE = "new sample";
    public static final String COMPLETED = "completed";
    public static final String ERROR = "error";

    public static final String SAMPLING_TYPE = "sampling type";
    public static final String GET_SAMPLING = "get oneshot";
    public static final String PERIODIC_SAMPLING = "get periodic";
    public static final String EVENT_SAMPLING = "event";

    public static final String PERIOD = "period";
    public static final String RECORD = "record";

    protected final SamplePipeline pipeline;
    protected final SimFpc fpc;
    protected final TaskHandler handler;

    protected SimTask(List<Attribute> atts, TaskHandler handler, SimFpc fpc) {
        this.fpc = fpc;
        this.handler = handler;

        PipelineBuilder pb = SamplePipeline.newBuilder(SimFpc.ATTRIBUTES);
        pb.reorder(atts);
        pipeline = pb.create();
    }

    @Override
    public List<Attribute> getAttributes() {
        return SimFpc.ATTRIBUTES;
    }

    protected void logComplete(String samplingType) {
        FpcAction a = new FpcAction();
        a.addField(SAMPLING_TYPE, samplingType);
        a.addField(ACTION, COMPLETED);
        fpc.addAction(a);
    }

    protected synchronized void logSample(String samplingType, Record record) {
        FpcAction a = new FpcAction();
        a.addField(SAMPLING_TYPE, samplingType);
        a.addField(ACTION, NEW_SAMPLE);
        a.addField(RECORD, record);
        fpc.addAction(a);
    }


    /**
     * @author Guido Rota 13/04/15
     */
    public static class GetSimTask extends SimTask {

        protected GetSimTask(List<Attribute> atts, TaskHandler handler,
                SimFpc fpc) {
            super(atts, handler, fpc);
            Record r = pipeline.run(fpc.newSample());
            logSample(GET_SAMPLING, r);
            handler.newRecord(this, r);
            handler.complete(this);
        }

        @Override
        public boolean isRunning() {
            return false;
        }

        @Override
        public void stop() { }

    }

    /**
     * @author Guido Rota 13/04/15
     */
    public static class PeriodicSimTask extends SimTask {

        private volatile boolean running = true;
        private final long periodMs;
        private final Thread generator;

        protected PeriodicSimTask(List<Attribute> atts, long periodMs,
                TaskHandler handler, SimFpc fpc) {
            super(atts, handler, fpc);
            FpcAction a = new FpcAction();
            a.addField(SAMPLING_TYPE, PERIODIC_SAMPLING);
            a.addField(ACTION, START);
            a.addField(PERIOD, periodMs);
            fpc.addAction(a);
            this.periodMs = periodMs;
            generator = new Thread(this::generateValues);
            generator.start();
        }

        private void generateValues() {
            try {
                while (true) {
                    Record r = pipeline.run(fpc.newSample());
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
