package org.dei.perla.lang.executor.buffer;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.query.expression.*;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 23/02/15.
 */
public class ArrayBufferTest {

    private static final List<org.dei.perla.core.sample.Attribute> atts;
    static {
        org.dei.perla.core.sample.Attribute[] as = new org.dei.perla.core.sample.Attribute[] {
                org.dei.perla.core.sample.Attribute.TIMESTAMP,
                org.dei.perla.core.sample.Attribute.create("integer", DataType.INTEGER)
        };
        atts = Arrays.asList(as);
    }

    @Test
    public void creationTest() {
        ArrayBuffer b = new ArrayBuffer(atts, 512);

        assertThat(b, notNullValue());
        assertThat(b.length(), equalTo(0));
        assertThat(b.getTimestampIndex(), equalTo(0));
    }

    @Test
    public void insertionTest() {
        Buffer b = new ArrayBuffer(atts, 512);

        assertThat(b.length(), equalTo(0));
        b.add(new Object[]{Instant.now(), 0});
        assertThat(b.length(), equalTo(1));
        b.add(new Object[]{Instant.now(), 1});
        assertThat(b.length(), equalTo(2));
        b.add(new Object[]{Instant.now(), 2});
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

        b.add(new Object[]{Instant.now(), 0});
        b.add(new Object[]{Instant.now(), 1});
        b.add(new Object[]{Instant.now(), 2});

        BufferView v = b.unmodifiableView();
        assertThat(v.length(), equalTo(3));

        assertThat(v.get(2)[1], equalTo(0));
        assertThat(v.get(1)[1], equalTo(1));
        assertThat(v.get(0)[1], equalTo(2));
    }

    @Test
    public void outOfOrderInsertionTest() throws InterruptedException {
        Buffer b = new ArrayBuffer(atts, 512);

        Object[] s0 = new Object[]{
                Instant.parse("2015-02-23T15:07:46.000Z"), 0};
        Object[] s1 = new Object[]{
                Instant.parse("2015-02-23T15:07:47.000Z"), 1};
        Object[] s2 = new Object[]{
                Instant.parse("2015-02-23T15:07:48.000Z"), 2};

        b.add(s1);
        b.add(s2);
        b.add(s0);
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

        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:46.000Z"), 1});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:47.000Z"), 2});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:48.000Z"), 3});
        assertThat(b.length(), equalTo(3));

        BufferView v0 = b.unmodifiableView();
        assertThat(v0, notNullValue());
        assertThat(v0.length(), equalTo(3));

        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:44.000Z"), 0});
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

        b.add(new Object[]{Instant.now(), 0});
        b.add(new Object[]{Instant.now(), 0});
        b.add(new Object[]{Instant.now(), 0});

        BufferView v0 = b.unmodifiableView();
        assertThat(v0, notNullValue());

        b.unmodifiableView();
    }

    @Test
    public void grandchildView() {
        Buffer b = new ArrayBuffer(atts, 512);

        b.add(new Object[]{Instant.now(), 0});
        b.add(new Object[]{Instant.now(), 1});
        b.add(new Object[]{Instant.now(), 2});
        b.add(new Object[]{Instant.now(), 3});
        b.add(new Object[]{Instant.now(), 4});

        BufferView v0 = b.unmodifiableView();
        assertThat(v0.length(), equalTo(5));
        assertThat(v0.get(0)[1], equalTo(4));
        assertThat(v0.get(1)[1], equalTo(3));
        assertThat(v0.get(2)[1], equalTo(2));
        assertThat(v0.get(3)[1], equalTo(1));
        assertThat(v0.get(4)[1], equalTo(0));

        BufferView v1 = v0.subView(4);
        assertThat(v1.length(), equalTo(4));
        assertThat(v1.get(0)[1], equalTo(4));
        assertThat(v1.get(1)[1], equalTo(3));
        assertThat(v1.get(2)[1], equalTo(2));
        assertThat(v1.get(3)[1], equalTo(1));

        BufferView v2 = v1.subView(3);
        assertThat(v2.length(), equalTo(3));
        assertThat(v2.get(0)[1], equalTo(4));
        assertThat(v2.get(1)[1], equalTo(3));
        assertThat(v2.get(2)[1], equalTo(2));

        v2.release();
        v1.release();
        v0.release();
    }

    @Test(expected = IllegalStateException.class)
    public void wrongReleaseOrder() {
        Buffer b = new ArrayBuffer(atts, 512);

        b.add(new Object[]{Instant.now(), 0});
        b.add(new Object[]{Instant.now(), 0});
        b.add(new Object[]{Instant.now(), 0});
        b.add(new Object[]{Instant.now(), 0});
        b.add(new Object[]{Instant.now(), 0});

        BufferView v0 = b.unmodifiableView();
        BufferView v1 = v0.subView(4);
        v1.subView(3);

        v1.release();
    }

    @Test
    public void samplesIn() {
        Buffer b = new ArrayBuffer(atts, 512);

        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:10.000Z"), 0});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:12.000Z"), 1});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:13.000Z"), 2});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:20.000Z"), 3});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:26.000Z"), 4});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:30.000Z"), 5});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:32.000Z"), 6});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:39.000Z"), 7});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:45.000Z"), 8});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:50.000Z"), 9});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:59.000Z"), 10});

        BufferView v = b.unmodifiableView();

        int i = v.samplesIn(Duration.ofSeconds(0));
        assertThat(i, equalTo(1));

        i = v.samplesIn(Duration.ofSeconds(9));
        assertThat(i, equalTo(2));
        i = v.samplesIn(Duration.ofSeconds(10));
        assertThat(i, equalTo(2));

        i = v.samplesIn(Duration.ofSeconds(14));
        assertThat(i, equalTo(3));
        i = v.samplesIn(Duration.ofSeconds(15));
        assertThat(i, equalTo(3));

        i = v.samplesIn(Duration.ofSeconds(20));
        assertThat(i, equalTo(4));
        i = v.samplesIn(Duration.ofSeconds(21));
        assertThat(i, equalTo(4));

        i = v.samplesIn(Duration.ofSeconds(27));
        assertThat(i, equalTo(5));
        i = v.samplesIn(Duration.ofSeconds(28));
        assertThat(i, equalTo(5));

        i = v.samplesIn(Duration.ofSeconds(29));
        assertThat(i, equalTo(6));
        i = v.samplesIn(Duration.ofSeconds(30));
        assertThat(i, equalTo(6));

        i = v.samplesIn(Duration.ofSeconds(33));
        assertThat(i, equalTo(7));
        i = v.samplesIn(Duration.ofSeconds(34));
        assertThat(i, equalTo(7));

        i = v.samplesIn(Duration.ofSeconds(39));
        assertThat(i, equalTo(8));
        i = v.samplesIn(Duration.ofSeconds(40));
        assertThat(i, equalTo(8));

        i = v.samplesIn(Duration.ofSeconds(46));
        assertThat(i, equalTo(9));

        i = v.samplesIn(Duration.ofSeconds(47));
        assertThat(i, equalTo(10));

        i = v.samplesIn(Duration.ofSeconds(49));
        assertThat(i, equalTo(11));
        i = v.samplesIn(Duration.ofSeconds(50));
        assertThat(i, equalTo(11));

        i = v.samplesIn(Duration.ofDays(1));
        assertThat(i, equalTo(11));
    }

    @Test
    public void durationView() {
        Buffer b = new ArrayBuffer(atts, 512);

        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:10.000Z"), 0});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:12.000Z"), 1});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:13.000Z"), 2});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:20.000Z"), 3});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:26.000Z"), 4});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:30.000Z"), 5});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:32.000Z"), 6});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:39.000Z"), 7});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:45.000Z"), 8});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:50.000Z"), 9});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:59.000Z"), 10});

        BufferView v = b.unmodifiableView();

        BufferView d0 = v.subView(Duration.ofSeconds(5));
        assertThat(d0, notNullValue());
        assertThat(d0.length(), equalTo(1));
        d0.release();

        d0 = v.subView(Duration.ofSeconds(40));
        assertThat(d0, notNullValue());
        assertThat(d0.length(), equalTo(8));
        d0.release();

        d0 = v.subView(Duration.ofSeconds(0));
        assertThat(d0, notNullValue());
        assertThat(d0.length(), equalTo(1));
        d0.release();

        d0 = v.subView(Duration.ofHours(10));
        assertThat(d0, notNullValue());
        assertThat(d0.length(), equalTo(v.length()));
        d0.release();
    }

    @Test
    public void groupByTimestamp1() {
        Buffer b = new ArrayBuffer(atts, 512);

        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:10.000Z"), 0});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:12.000Z"), 1});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:13.000Z"), 2});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:20.000Z"), 3});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:26.000Z"), 4});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:30.000Z"), 5});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:39.500Z"), 6});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:40.000Z"), 7});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:45.000Z"), 8});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:50.000Z"), 9});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:59.000Z"), 10});

        BufferView v = b.unmodifiableView();

        List<BufferView> gs = v.groupBy(Duration.ofSeconds(10), 3);
        assertThat(gs.size(), equalTo(3));

        BufferView g0 = gs.get(0);
        assertThat(g0.length(), equalTo(11));
        assertThat(g0.get(0)[0], equalTo(v.get(0)[0]));

        BufferView g1 = gs.get(1);
        assertThat(g1.length(), equalTo(9));
        assertThat(g1.get(0)[0], equalTo(v.get(2)[0]));

        BufferView g2 = gs.get(2);
        assertThat(g2.length(), equalTo(6));
        assertThat(g2.get(0)[0], equalTo(v.get(5)[0]));
    }

    @Test
    public void groupByTimestamp2() {
        Buffer b = new ArrayBuffer(atts, 512);

        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:10.000Z"), 0});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:12.000Z"), 1});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:13.000Z"), 2});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:20.000Z"), 3});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:26.000Z"), 4});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:30.000Z"), 5});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:39.000Z"), 6});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:40.000Z"), 7});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:45.000Z"), 8});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:50.000Z"), 9});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:59.000Z"), 10});

        BufferView v = b.unmodifiableView();

        List<BufferView> gs = v.groupBy(Duration.ofSeconds(1), 3);
        assertThat(gs.size(), equalTo(3));

        BufferView g0 = gs.get(0);
        assertThat(g0.length(), equalTo(11));
        assertThat(g0.get(0)[0], equalTo(v.get(0)[0]));

        BufferView g1 = gs.get(1);
        assertThat(g1.length(), equalTo(10));
        assertThat(g1.get(0)[0], equalTo(v.get(1)[0]));

        BufferView g2 = gs.get(2);
        assertThat(g2.length(), equalTo(9));
        assertThat(g2.get(0)[0], equalTo(v.get(2)[0]));
    }

    @Test
    public void groupByTimestamp3() {
        Buffer b = new ArrayBuffer(atts, 512);

        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:10.000Z"), 0});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:12.000Z"), 1});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:13.000Z"), 2});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:20.000Z"), 3});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:26.000Z"), 4});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:30.000Z"), 5});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:39.000Z"), 6});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:40.000Z"), 7});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:45.000Z"), 8});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:50.000Z"), 9});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:59.000Z"), 10});

        BufferView v = b.unmodifiableView();

        List<BufferView> gs = v.groupBy(Duration.ofSeconds(30), 3);
        assertThat(gs.size(), equalTo(2));

        BufferView g0 = gs.get(0);
        assertThat(g0.length(), equalTo(11));
        assertThat(g0.get(0)[0], equalTo(v.get(0)[0]));

        BufferView g1 = gs.get(1);
        assertThat(g1.length(), equalTo(5));
        assertThat(g1.get(0)[0], equalTo(v.get(6)[0]));
    }

    @Test
    public void forEach() {
        Buffer b = new ArrayBuffer(atts, 512);

        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:10.000Z"), 0});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:12.000Z"), 1});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:13.000Z"), 2});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:20.000Z"), 3});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:26.000Z"), 4});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:30.000Z"), 5});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:32.000Z"), 6});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:39.000Z"), 7});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:45.000Z"), 8});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:50.000Z"), 9});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:59.000Z"), 10});

        BufferView v = b.unmodifiableView();

        WrapInt count = new WrapInt();
        v.forEach((r, view) -> count.value++);
        assertThat(count.value, equalTo(v.length()));

        WrapInt sum = new WrapInt();
        v.forEach((r, view) -> sum.value += (Integer) r[1]);
        assertThat(sum.value, equalTo(55));

        count.value = 0;
        BufferView sub = v.subView(11);
        sub.forEach((r, view) -> count.value++);
        assertThat(count.value, equalTo(sub.length()));
        sub.release();

        count.value = 0;
        sub = v.subView(11);
        Errors err = new Errors();
        Expression where = new Comparison(ComparisonOperation.GT,
                new Attribute("integer", DataType.INTEGER, 1),
                Constant.create(5, DataType.INTEGER));
        assertTrue(err.isEmpty());
        assertTrue(err.isEmpty());
        sub.forEach((r, view) -> count.value++, where);
        assertThat(count.value, equalTo(5));
        sub.release();
    }

    private static final class WrapInt {
        public int value = 0;
    }

}
