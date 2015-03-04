package org.dei.perla.lang.executor.expression;

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
import static org.junit.Assert.assertThat;

/**
 * @author Guido Rota 02/03/15.
 */
public class ArithmeticTest {

    private static Attribute integerAtt =
            Attribute.create("integer", DataType.INTEGER);
    private static Attribute stringAtt =
            Attribute.create("string", DataType.STRING);
    private static Attribute floatAtt =
            Attribute.create("float", DataType.FLOAT);

    private static BufferView view;

    @BeforeClass
    public static void setupBuffer() {
        Attribute[] as = new Attribute[] {
                Attribute.TIMESTAMP,
                integerAtt,
                stringAtt,
                floatAtt
        };
        List<Attribute> atts = Arrays.asList(as);

        Buffer b = new ArrayBuffer(0, 512);
        b.add(new Record(atts, new Object[]{Instant.now(), 0, "0", 0.0f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 1, "1", 1.1f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 2, "2", 2.2f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 3, "3", 3.3f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 4, "4", 4.4f}));

        view = b.unmodifiableView();
    }

    @Test
    public void additionIntegerTest() {
        Constant e1 = new Constant(1, DataType.INTEGER);
        Field e2 = new Field(1, DataType.INTEGER);
        Addition a = new Addition(e1, e2);

        assertThat(a.getType(), equalTo(DataType.INTEGER));
        assertThat(a.run(view.get(0), view), equalTo(1 + 4));
        assertThat(a.run(view.get(1), view), equalTo(1 + 3));
    }

    @Test
    public void additionFloatTest() {
        Constant e1 = new Constant(1.5f, DataType.FLOAT);
        Field e2 = new Field(3, DataType.FLOAT);
        Addition a = new Addition(e1, e2);

        assertThat(a.getType(), equalTo(DataType.FLOAT));
        assertThat(a.run(view.get(0), view), equalTo(4.4f + 1.5f));
        assertThat(a.run(view.get(1), view), equalTo(3.3f + 1.5f));
    }

    @Test
    public void subtractionIntegerTest() {
        Constant e1 = new Constant(1, DataType.INTEGER);
        Field e2 = new Field(1, DataType.INTEGER);
        Subtraction s = new Subtraction(e1, e2);

        assertThat(s.getType(), equalTo(DataType.INTEGER));
        assertThat(s.run(view.get(0), view), equalTo(1 - 4));
        assertThat(s.run(view.get(1), view), equalTo(1 - 3));
    }

    @Test
    public void subtractionFloatTest() {
        Constant e1 = new Constant(1.5f, DataType.FLOAT);
        Field e2 = new Field(3, DataType.FLOAT);
        Subtraction s = new Subtraction(e1, e2);

        assertThat(s.getType(), equalTo(DataType.FLOAT));
        assertThat(s.run(view.get(0), view), equalTo(1.5f - 4.4f));
        assertThat(s.run(view.get(1), view), equalTo(1.5f - 3.3f));
    }

    @Test
    public void productIntegerTest() {
        Constant e1 = new Constant(1, DataType.INTEGER);
        Field e2 = new Field(1, DataType.INTEGER);
        Product p = new Product(e1, e2);

        assertThat(p.getType(), equalTo(DataType.INTEGER));
        assertThat(p.run(view.get(0), view), equalTo(1 * 4));
        assertThat(p.run(view.get(1), view), equalTo(1 * 3));
    }

    @Test
    public void productFloatTest() {
        Constant e1 = new Constant(1.5f, DataType.FLOAT);
        Field e2 = new Field(3, DataType.FLOAT);
        Product p = new Product(e1, e2);

        assertThat(p.getType(), equalTo(DataType.FLOAT));
        assertThat(p.run(view.get(0), view), equalTo(1.5f * 4.4f));
        assertThat(p.run(view.get(1), view), equalTo(1.5f * 3.3f));
    }

    @Test
    public void divisionIntegerTest() {
        Constant e1 = new Constant(1, DataType.INTEGER);
        Field e2 = new Field(1, DataType.INTEGER);
        Division d = new Division(e1, e2);

        assertThat(d.getType(), equalTo(DataType.INTEGER));
        assertThat(d.run(view.get(0), view), equalTo(1 / 4));
        assertThat(d.run(view.get(1), view), equalTo(1 / 3));
    }

    @Test
    public void divisionFloatTest() {
        Constant e1 = new Constant(1.5f, DataType.FLOAT);
        Field e2 = new Field(3, DataType.FLOAT);
        Division d = new Division(e1, e2);

        assertThat(d.getType(), equalTo(DataType.FLOAT));
        assertThat(d.run(view.get(0), view), equalTo(1.5f / 4.4f));
        assertThat(d.run(view.get(1), view), equalTo(1.5f / 3.3f));
    }

    @Test
    public void moduloTest() {
        Constant e1 = new Constant(1, DataType.INTEGER);
        Field e2 = new Field(1, DataType.INTEGER);
        Modulo m = new Modulo(e1, e2);

        assertThat(m.getType(), equalTo(DataType.INTEGER));
        assertThat(m.run(view.get(0), view), equalTo(1 % 4));
        assertThat(m.run(view.get(1), view), equalTo(1 % 3));
    }

    @Test
    public void inverseTest() {
        Constant e1 = new Constant(1, DataType.INTEGER);
        Field e2 = new Field(3, DataType.FLOAT);

        Inverse inv = new Inverse(e1);
        assertThat(inv.getType(), equalTo(DataType.INTEGER));
        assertThat(inv.run(view.get(0), view), equalTo(-1));

        inv = new Inverse(e2);
        assertThat(inv.getType(), equalTo(DataType.FLOAT));
        assertThat(inv.run(view.get(0), view),
                equalTo(-(Float) view.get(0)[3]));
        assertThat(inv.run(view.get(1), view),
                equalTo(-(Float) view.get(1)[3]));
    }

}
