package org.dei.perla.lang.executor;

import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.record.Record;

import java.time.Duration;
import java.util.List;

/**
 * @author Guido Rota 22/02/15.
 */
public class ArrayBuffer implements Buffer {

    private final List<Attribute> atts;

    private Object[][] data;
    private int len;

    public ArrayBuffer(List<Attribute> atts, int len) {
        this.atts = atts;
        this.len = len;
        data = new Object[len][];
    }

    @Override
    public List<Attribute> attributes() {
        return atts;
    }

    @Override
    public void add(Record r) {

    }

    @Override
    public Buffer range(int samples) {
        return null;
    }

    @Override
    public Buffer range(Duration d) {
        return null;
    }

}
