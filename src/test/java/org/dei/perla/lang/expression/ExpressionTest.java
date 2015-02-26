package org.dei.perla.lang.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.record.Record;
import org.dei.perla.lang.executor.ArrayBuffer;
import org.dei.perla.lang.executor.Buffer;
import org.dei.perla.lang.executor.BufferView;
import org.junit.BeforeClass;
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
public class ExpressionTest {

    private static Attribute integer =
            Attribute.create("integer", DataType.INTEGER);
    private static Attribute string =
            Attribute.create("string", DataType.STRING);

    private static BufferView view;

    @BeforeClass
    public static void setupBuffer() {
        Attribute[] as = new Attribute[] {
                Attribute.TIMESTAMP,
                integer
        };
        List<Attribute> atts = Arrays.asList(as);

        Buffer b = new ArrayBuffer(atts, 512);
        b.add(new Record(atts, new Object[]{Instant.now(), 0, "0"}));
        b.add(new Record(atts, new Object[]{Instant.now(), 1, "1"}));
        b.add(new Record(atts, new Object[]{Instant.now(), 2, "2"}));
        b.add(new Record(atts, new Object[]{Instant.now(), 3, "3"}));
        b.add(new Record(atts, new Object[]{Instant.now(), 4, "4"}));

        view = b.unmodifiableView();
    }

    @Test
    public void constantTest() {
        Constant c = new Constant(DataType.INTEGER, 1);

        assertThat(c.getType(), equalTo(DataType.INTEGER));
        assertThat(c.getValue(), equalTo(1));
    }

    @Test
    public void fieldTest() {
        Field fi = new Field(integer, 1);
        assertThat(fi.getType(), equalTo(DataType.INTEGER));
        assertThat(fi.getAttribute(), equalTo(integer));
        assertThat(fi.getIndex(), equalTo(1));
        assertThat(fi.compute(view.get(0), view), equalTo(4));
        assertThat(fi.compute(view.get(1), view), equalTo(3));

        Field fs = new Field(string, 2);
        assertThat(fs.getType(), equalTo(DataType.STRING));
        assertThat(fs.getAttribute(), equalTo(string));
        assertThat(fs.getIndex(), equalTo(2));
        assertThat(fs.compute(view.get(0), view), equalTo("4"));
        assertThat(fs.compute(view.get(1), view), equalTo("3"));
    }

}
