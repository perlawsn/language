package org.dei.perla.lang.executor.buffer;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.lang.Common;
import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * @author Guido Rota 22/10/15.
 */
public class CircularBufferTest {


    private static final List<Attribute> atts =
            Arrays.asList(new Attribute[]{
                    Common.INT_ATTRIBUTE,
                    Common.FLOAT_ATTRIBUTE,
                    Attribute.TIMESTAMP
            });

    private Object[] newSample() {
        return newSample(atts);
    }

    private Object[] newSample(List<Attribute> atts) {
        Object[] sample = new Object[atts.size()];
        int tsIdx = atts.indexOf(Attribute.TIMESTAMP);
        if (tsIdx == -1) {
            throw new RuntimeException("Missing timestamp attribute");
        }
        sample[tsIdx] = Instant.now();
        return sample;
    }

    @Test
    public void testCreation() {
        CircularBuffer buf = new CircularBuffer(atts);
        assertThat(buf.capacity(), equalTo(CircularBuffer.DEFAULT_CAPACITY));
        assertThat(buf.size(), equalTo(0));

        int newCap = CircularBuffer.DEFAULT_CAPACITY * 12;
        buf = new CircularBuffer(atts, newCap);
        assertThat(buf.capacity(), equalTo(newCap));
    }

    @Test
    public void testAdd() {
        int cap = 64;
        CircularBuffer buf = new CircularBuffer(atts, cap);
        assertThat(buf.capacity(), equalTo(cap));
        assertThat(buf.size(), equalTo(0));

        int count = 10;
        for (int i = 0; i < count; i++) {
            Object[] sample = newSample();
            sample[0] = i;
            buf.add(sample);
        }
        assertThat(buf.capacity(), equalTo(cap));
        assertThat(buf.size(), equalTo(count));

        for (int i = 0; i < count; i++) {
            Object[] sample = buf.get(i);
            assertThat(sample[0], equalTo(count - i - 1));
        }
    }

    @Test
    public void testExpand() {
        int cap = 10;
        Object[] sample;

        CircularBuffer buf = new CircularBuffer(atts, cap);
        assertThat(buf.capacity(), equalTo(cap));
        assertThat(buf.size(), equalTo(0));

        for (int i = 0; i < cap; i++) {
            sample = newSample();
            sample[0] = i;
            buf.add(sample);
        }
        assertThat(buf.capacity(), equalTo(cap));
        assertThat(buf.size(), equalTo(cap));

        sample = newSample();
        sample[0] = cap;
        buf.add(sample);
        assertThat(buf.capacity(), equalTo(2 * cap));
        assertThat(buf.size(), equalTo(cap + 1));
        for (int i = 0; i < cap + 1; i++) {
            sample = buf.get(i);
            assertThat(sample[0], equalTo(cap - i));
        }
    }

    @Test
    public void testDelete() {
        Object[] sample;
        CircularBuffer buf = new CircularBuffer(atts);
        assertThat(buf.size(), equalTo(0));

        int count = 10;
        for (int i = 0; i < count; i++) {
            sample = newSample();
            sample[0] = i;
            buf.add(sample);
        }
        assertThat(buf.size(), equalTo(count));

        throw new RuntimeException("incomplete");
    }

}
