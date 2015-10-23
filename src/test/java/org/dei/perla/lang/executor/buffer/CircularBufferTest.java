package org.dei.perla.lang.executor.buffer;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.lang.Common;
import org.junit.Test;

import java.time.Duration;
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
        return newSample(atts, Instant.now());
    }

    private Object[] newSample(Instant i) {
        return newSample(atts, i);
    }

    private Object[] newSample(List<Attribute> atts, Instant i) {
        Object[] sample = new Object[atts.size()];
        int tsIdx = atts.indexOf(Attribute.TIMESTAMP);
        if (tsIdx == -1) {
            throw new RuntimeException("Missing timestamp attribute");
        }
        sample[tsIdx] = i;
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
    public void testOutOfOrderAdd() {
        CircularBuffer buf = new CircularBuffer(atts);

        Object[] sample = new Object[3];
        sample[0] = 2;
        sample[2] = Instant.ofEpochMilli(10);
        buf.add(sample);

        sample = new Object[3];
        sample[0] = 0;
        sample[2] = Instant.ofEpochMilli(30);
        buf.add(sample);

        sample = new Object[3];
        sample[0] = 1;
        sample[2] = Instant.ofEpochMilli(20);
        buf.add(sample);

        sample = new Object[3];
        sample[0] = 3;
        sample[2] = Instant.ofEpochMilli(5);
        buf.add(sample);

        assertThat(buf.size(), equalTo(4));

        for (int i = 0; i < 4; i++) {
            sample = buf.get(i);
            assertThat(sample[0], equalTo(i));
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

    @Test(expected = IndexOutOfBoundsException.class)
    public void testIndexOutOfBound() {
        CircularBuffer buf = new CircularBuffer(atts);

        buf.add(newSample());
        buf.add(newSample());
        buf.get(2);
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

        sample = buf.get(9);
        assertThat(sample[0], equalTo(0));

        buf.deleteLast(8);
        assertThat(buf.size(), equalTo(2));
        sample = buf.get(0);
        assertThat(sample[0], equalTo(9));
        sample = buf.get(1);
        assertThat(sample[0], equalTo(8));

        Exception outOfBound = null;
        try {
            buf.get(2);
        } catch(IndexOutOfBoundsException e) {
            outOfBound = e;
        }
        assertThat(outOfBound, notNullValue());
    }

    @Test
    public void testSamplesIn() {
        CircularBuffer buf = new CircularBuffer(atts);

        int count = 5;
        for (int i = 0; i < count; i++) {
            buf.add(newSample(Instant.ofEpochMilli(i)));
        }
        assertThat(buf.size(), equalTo(count));

        for (int i = 0; i < count; i++) {
            Duration d = Duration.ofMillis(i);
            assertThat(buf.samplesIn(d), equalTo(i));
        }

        Duration d = Duration.ofMillis(count * 2);
        assertThat(buf.samplesIn(d), equalTo(count));
    }

}
