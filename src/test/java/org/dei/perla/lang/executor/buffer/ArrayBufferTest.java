package org.dei.perla.lang.executor.buffer;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.lang.Common;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Guido Rota 22/10/15.
 */
public class ArrayBufferTest {

    private static final List<Attribute> atts =
            Arrays.asList(new Attribute[] {
                    Common.INT_ATTRIBUTE,
                    Common.FLOAT_ATTRIBUTE,
                    Attribute.TIMESTAMP
            });

    private Object[] newSample() {
        return newSample(atts, Instant.now());
    }

    private Object[] newSample(Instant ts) {
        return newSample(atts, ts);
    }

    private Object[] newSample(List<Attribute> atts, Instant ts) {
        Object[] sample = new Object[atts.size()];
        int tsIdx = atts.indexOf(Attribute.TIMESTAMP);
        if (tsIdx == -1) {
            throw new RuntimeException("Missing timestamp attribute");
        }
        sample[tsIdx] = ts;
        return sample;
    }

    @Test
    public void testCreation() {
        ArrayBuffer buf = new ArrayBuffer(atts);
        assertThat(buf, notNullValue());
        assertThat(buf.size(), equalTo(0));
    }

    @Test
    public void testInsertion() throws Exception {
        ArrayBuffer buf = new ArrayBuffer(atts);
        assertThat(buf.size(), equalTo(0));

        buf.add(newSample());
        assertThat(buf.size(), equalTo(1));

        buf.add(newSample());
        assertThat(buf.size(), equalTo(2));

        Object[] sample;
        for (int i = 0; i < 10; i++) {
            sample = newSample();
            sample[0] = i;
            buf.add(sample);
        }
        assertThat(buf.size(), equalTo(12));

        BufferView view = buf.createView();
        for (int i = 0; i < 10; i++) {
            sample = view.get(i);
            assertThat(sample[0], equalTo(10 - i - 1));
        }
        view.release();
    }

    @Test
    public void testViewRelease() throws Exception {
        ArrayBuffer buf = new ArrayBuffer(atts);
        assertThat(buf.size(), equalTo(0));

        Object[] sample;
        int count = 10;
        for (int i = 0; i < count; i++) {
            sample = newSample();
            sample[0] = i;
            buf.add(sample);
        }
        assertThat(buf.size(), equalTo(count));

        ArrayBufferView view = buf.createView();
        assertThat(view.size(), equalTo(count));
        sample = view.get(5);
        assertThat(sample[0], equalTo(4));
        view.release();
        assertThat(buf.size(), equalTo(6));
    }

    @Test
    public void testViewReleaseNoAccess() throws Exception {
        ArrayBuffer buf = new ArrayBuffer(atts);
        assertThat(buf.size(), equalTo(0));

        int count = 10;
        for (int i = 0; i < count; i++) {
            buf.add(newSample());
        }
        assertThat(buf.size(), equalTo(10));

        ArrayBufferView view = buf.createView();
        view.release();
        assertThat(buf.size(), equalTo(10));
    }

    @Test
    public void testMultpleViewRelease() throws Exception {
        ArrayBuffer buf = new ArrayBuffer(atts);
        buf.add(newSample());
        buf.add(newSample());
        buf.add(newSample());
        assertThat(buf.size(), equalTo(3));

        ArrayBufferView view = buf.createView();
        view.get(1);
        view.release();
        assertThat(buf.size(), equalTo(2));

        view = buf.createView();
        view.get(0);
        view.release();
        assertThat(buf.size(), equalTo(1));
    }

    @Test(expected = UnreleasedViewException.class)
    public void testDuplicateView() throws Exception {
        ArrayBuffer buf = new ArrayBuffer(atts);
        buf.createView();
        buf.createView();
    }

    @Test
    public void testSamplesIn() throws Exception {
        ArrayBuffer buf = new ArrayBuffer(atts);

        int count = 20;
        for (int i = 0; i < count; i++) {
            Instant ts = Instant.ofEpochMilli(i);
            buf.add(newSample(ts));
        }
        assertThat(buf.size(), equalTo(count));

        int samplesIn;
        BufferView view = buf.createView();
        for (int i = 0; i < count + 1; i++) {
            samplesIn = view.samplesIn(Duration.ofMillis(i));
            assertThat(samplesIn, equalTo(i));
        }
        samplesIn = view.samplesIn(Duration.ofDays(1));
        assertThat(samplesIn, equalTo(count));
    }

    @Test
    public void testSubViewCount() throws Exception {
        ArrayBuffer buf = new ArrayBuffer(atts);

        Object[] sample;
        int count = 20;
        for (int i = 0; i < count; i++) {
            sample = newSample();
            sample[0] = i;
            buf.add(sample);
        }
        assertThat(buf.size(), equalTo(count));

        BufferView view = buf.createView();
        BufferView subView = view.subView(10);
        assertThat(subView.size(), equalTo(10));
        for (int i = 0; i < 10; i++) {
            sample = subView.get(i);
            assertThat(sample[0], equalTo(count - i - 1));
        }
        subView.release();
        view.release();
    }

    @Test
    public void testSubViewDuration() throws Exception {
        ArrayBuffer buf = new ArrayBuffer(atts);

        Object[] sample;
        int count = 20;
        for (int i = 0; i < count; i++) {
            Instant ts = Instant.ofEpochMilli(i);
            sample = newSample(ts);
            sample[0] = i;
            buf.add(sample);
        }
        assertThat(buf.size(), equalTo(count));

        BufferView view = buf.createView();
        BufferView subView = view.subView(Duration.ofMillis(10));
        assertThat(subView.size(), equalTo(10));
        for (int i = 0; i < 10; i++) {
            sample = subView.get(i);
            assertThat(sample[0], equalTo(count - i - 1));
        }
        subView.release();
        view.release();
    }

}
