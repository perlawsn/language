package org.dei.perla.lang.executor.buffer;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.lang.CommonAttributes;
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
                    CommonAttributes.INTEGER,
                    CommonAttributes.FLOAT,
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

    private ArrayBuffer createBuffer(int count) {
        ArrayBuffer buf = new ArrayBuffer(atts);
        for (int i = 0; i < count; i++) {
            Instant ts = Instant.ofEpochMilli(i);
            Object[] sample = newSample(ts);
            sample[0] = i;
            buf.add(sample);
        }
        return buf;
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
        int count = 10;
        ArrayBuffer buf = createBuffer(count);
        assertThat(buf.size(), equalTo(count));

        ArrayBufferView view = buf.createView();
        view.release();
        assertThat(buf.size(), equalTo(count));
    }

    @Test
    public void testMultpleViewRelease() throws Exception {
        ArrayBuffer buf = createBuffer(3);

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
        int count = 20;
        ArrayBuffer buf = createBuffer(count);
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
        int count = 20;
        ArrayBuffer buf = createBuffer(count);
        assertThat(buf.size(), equalTo(count));

        BufferView view = buf.createView();
        BufferView subView = view.subView(10);
        assertThat(subView.size(), equalTo(10));
        for (int i = 0; i < 10; i++) {
            Object[] sample = subView.get(i);
            assertThat(sample[0], equalTo(count - i - 1));
        }
        subView.release();
        view.release();
    }

    @Test
    public void testSubViewDuration() throws Exception {
        int count = 20;
        ArrayBuffer buf = createBuffer(count);
        assertThat(buf.size(), equalTo(count));

        BufferView view = buf.createView();
        BufferView subView = view.subView(Duration.ofMillis(10));
        assertThat(subView.size(), equalTo(10));
        for (int i = 0; i < 10; i++) {
            Object[] sample = subView.get(i);
            assertThat(sample[0], equalTo(count - i - 1));
        }
        subView.release();
        view.release();
    }

    @Test
    public void testReleaseOrder() throws Exception {
        int count = 20;
        ArrayBuffer buf = createBuffer(count);
        assertThat(buf.size(), equalTo(count));

        ArrayBufferView view = buf.createView();
        assertThat(view.size(), equalTo(count));

        ArrayBufferView sub1 = view.subView(10);
        assertThat(sub1.size(), equalTo(10));
        ArrayBufferView sub2 = view.subView(5);
        assertThat(sub2.size(), equalTo(5));

        ArrayBufferView sub11 = sub1.subView(5);
        assertThat(sub11.size(), equalTo(5));

        // Releasing
        sub11.release();
        sub2.release();
        sub1.release();
        view.release();
    }

    @Test(expected = IllegalStateException.class)
    public void testWrongReleaseOrder() throws Exception {
        int count = 20;
        ArrayBuffer buf = createBuffer(count);
        assertThat(buf.size(), equalTo(count));

        ArrayBufferView view = buf.createView();
        ArrayBufferView sub1 = view.subView(10);
        ArrayBufferView sub11 = sub1.subView(5);

        sub1.release();
    }

}
