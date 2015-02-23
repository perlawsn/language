package org.dei.perla.lang.executor;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.record.Record;
import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

/**
 * @author Guido Rota 23/02/15.
 */
public class ArrayBufferTest {

    private static final List<Attribute> atts;
    static {
        Attribute[] as = new Attribute[] {
                Attribute.TIMESTAMP_ATTRIBUTE,
                Attribute.create("integer", DataType.INTEGER)
        };
        atts = Arrays.asList(as);
    }

    @Test
    public void creationTest() {
        Buffer b = new ArrayBuffer(atts, 512);

        assertThat(b, notNullValue());
        assertTrue(b.attributes().containsAll(atts));
        assertTrue(atts.containsAll(b.attributes()));
        assertThat(b.length(), equalTo(0));
    }

    @Test
    public void insertionTest() {
        Buffer b = new ArrayBuffer(atts, 512);

        assertThat(b.length(), equalTo(0));
        b.add(new Record(atts, new Object[]{Instant.now(), 0}));
        assertThat(b.length(), equalTo(1));
        b.add(new Record(atts, new Object[]{Instant.now(), 1}));
        assertThat(b.length(), equalTo(2));
        b.add(new Record(atts, new Object[]{Instant.now(), 2}));
        assertThat(b.length(), equalTo(3));

        BufferView v = b.unmodifiableView();
        assertThat(v.length(), equalTo(3));

        Object[] o0 = v.get(2);
        assertThat(o0, notNullValue());
        assertThat(o0.length, equalTo(2));
        assertThat(o0[1], equalTo(0));

        Object[] o1 = v.get(1);
        assertThat(o1, notNullValue());
        assertThat(o1.length, equalTo(2));
        assertThat(o1[1], equalTo(1));

        Object[] o2 = v.get(0);
        assertThat(o2, notNullValue());
        assertThat(o2.length, equalTo(2));
        assertThat(o2[1], equalTo(2));
    }

    @Test
    public void bufferGrowth() {
        Buffer b = new ArrayBuffer(atts, 2);

        b.add(new Record(atts, new Object[]{Instant.now(), 0}));
        b.add(new Record(atts, new Object[]{Instant.now(), 1}));
        b.add(new Record(atts, new Object[]{Instant.now(), 2}));

        BufferView v = b.unmodifiableView();
        assertThat(v.length(), equalTo(3));

        assertThat(v.get(2)[1], equalTo(0));
        assertThat(v.get(1)[1], equalTo(1));
        assertThat(v.get(0)[1], equalTo(2));
    }

    @Test
    public void outOfOrderInsertionTest() throws InterruptedException {
        Buffer b = new ArrayBuffer(atts, 512);

        Record r0 = new Record(atts, new Object[]{
                Instant.parse("2015-02-23T15:07:46.000Z"), 0});
        Record r1 = new Record(atts, new Object[]{
                Instant.parse("2015-02-23T15:07:47.000Z"), 1});
        Record r2 = new Record(atts, new Object[]{
                Instant.parse("2015-02-23T15:07:48.000Z"), 2});

        b.add(r1);
        b.add(r2);
        b.add(r0);
        assertThat(b.length(), equalTo(3));

        BufferView v = b.unmodifiableView();
        assertThat(v.length(), equalTo(3));

        Object[] o0 = v.get(2);
        assertThat(o0[1], equalTo(0));

        Object[] o1 = v.get(1);
        assertThat(o1[1], equalTo(1));

        Object[] o2 = v.get(0);
        assertThat(o2[1], equalTo(2));
    }

    public void multipleViews() {
        Buffer b = new ArrayBuffer(atts, 521);

        b.add(new Record(atts, new Object[]{
                Instant.parse("2015-02-23T15:07:46.000Z"), 1}));
        b.add(new Record(atts, new Object[]{
                Instant.parse("2015-02-23T15:07:47.000Z"), 2}));
        b.add(new Record(atts, new Object[]{
                Instant.parse("2015-02-23T15:07:48.000Z"), 3}));
        assertThat(b.length(), equalTo(3));

        BufferView v0 = b.unmodifiableView();
        assertThat(v0, notNullValue());
        assertThat(v0.length(), equalTo(3));

        b.add(new Record(atts, new Object[]{
                Instant.parse("2015-02-23T15:07:44.000Z"), 0}));
        assertThat(b.length(), equalTo(4));
        assertThat(v0.length(), equalTo(3));

        assertThat(v0.get(2)[1], equalTo(1));
        assertThat(v0.get(1)[1], equalTo(2));
        assertThat(v0.get(0)[1], equalTo(3));

        v0.release();
        BufferView v1 = b.unmodifiableView();
        assertThat(v1, notNullValue());
        assertThat(v1.length(), equalTo(4));

        assertThat(v1.get(3)[1], equalTo(0));
        assertThat(v1.get(2)[1], equalTo(1));
        assertThat(v1.get(1)[1], equalTo(2));
        assertThat(v1.get(0)[1], equalTo(3));
    }

    @Test(expected = IllegalStateException.class)
    public void multipleViewsWithoutRelease() {
        Buffer b = new ArrayBuffer(atts, 521);

        b.add(new Record(atts, new Object[]{Instant.now(), 0}));
        b.add(new Record(atts, new Object[]{Instant.now(), 0}));
        b.add(new Record(atts, new Object[]{Instant.now(), 0}));

        BufferView v0 = b.unmodifiableView();
        assertThat(v0, notNullValue());

        BufferView v1 = b.unmodifiableView();
    }

}
