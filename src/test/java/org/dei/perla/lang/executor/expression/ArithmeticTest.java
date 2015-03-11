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
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 02/03/15.
 */
public class ArithmeticTest {

    private static Attribute intAtt =
            Attribute.create("integer", DataType.INTEGER);
    private static Attribute stringAtt =
            Attribute.create("string", DataType.STRING);
    private static Attribute floatAtt =
            Attribute.create("float", DataType.FLOAT);

    private static List<Attribute> atts;
    private static BufferView view;

    private static Expression intField;
    private static Expression floatField;

    @BeforeClass
    public static void setupBuffer() {
        Attribute[] as = new Attribute[] {
                Attribute.TIMESTAMP,
                intAtt,
                stringAtt,
                floatAtt
        };
        atts = Arrays.asList(as);

        intField = new Field(intAtt.getId()).rebuild(atts);
        floatField = new Field(floatAtt.getId()).rebuild(atts);

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
        Arithmetic a = (Arithmetic) Arithmetic.createAddition(e1, intField);

        assertTrue(a.isComplete());
        assertThat(a.getType(), equalTo(DataType.INTEGER));
        assertThat(a.run(view.get(0), view), equalTo(1 + 4));
        assertThat(a.run(view.get(1), view), equalTo(1 + 3));
    }

    @Test
    public void additionFloatTest() {
        Constant e1 = new Constant(1.5f, DataType.FLOAT);
        Arithmetic a = (Arithmetic) Arithmetic.createAddition(e1, floatField);

        assertTrue(a.isComplete());
        assertThat(a.getType(), equalTo(DataType.FLOAT));
        assertThat(a.run(view.get(0), view), equalTo(4.4f + 1.5f));
        assertThat(a.run(view.get(1), view), equalTo(3.3f + 1.5f));
    }

    @Test
    public void subtractionIntegerTest() {
        Constant e1 = new Constant(1, DataType.INTEGER);
        Arithmetic s = (Arithmetic) Arithmetic.createSubtraction(e1, intField);

        assertTrue(s.isComplete());
        assertThat(s.getType(), equalTo(DataType.INTEGER));
        assertThat(s.run(view.get(0), view), equalTo(1 - 4));
        assertThat(s.run(view.get(1), view), equalTo(1 - 3));
    }

    @Test
    public void subtractionFloatTest() {
        Constant e1 = new Constant(1.5f, DataType.FLOAT);
        Arithmetic s = (Arithmetic) Arithmetic.createSubtraction(e1, floatField);

        assertTrue(s.isComplete());
        assertThat(s.getType(), equalTo(DataType.FLOAT));
        assertThat(s.run(view.get(0), view), equalTo(1.5f - 4.4f));
        assertThat(s.run(view.get(1), view), equalTo(1.5f - 3.3f));
    }

    @Test
    public void productIntegerTest() {
        Constant e1 = new Constant(1, DataType.INTEGER);
        Arithmetic p = (Arithmetic) Arithmetic.createProduct(e1, intField);

        assertTrue(p.isComplete());
        assertThat(p.getType(), equalTo(DataType.INTEGER));
        assertThat(p.run(view.get(0), view), equalTo(1 * 4));
        assertThat(p.run(view.get(1), view), equalTo(1 * 3));
    }

    @Test
    public void productFloatTest() {
        Constant e1 = new Constant(1.5f, DataType.FLOAT);
        Arithmetic p = (Arithmetic) Arithmetic.createProduct(e1, floatField);

        assertTrue(p.isComplete());
        assertThat(p.getType(), equalTo(DataType.FLOAT));
        assertThat(p.run(view.get(0), view), equalTo(1.5f * 4.4f));
        assertThat(p.run(view.get(1), view), equalTo(1.5f * 3.3f));
    }

    @Test
    public void divisionIntegerTest() {
        Constant e1 = new Constant(1, DataType.INTEGER);
        Arithmetic d = (Arithmetic) Arithmetic.createDivision(e1, intField);

        assertTrue(d.isComplete());
        assertThat(d.getType(), equalTo(DataType.INTEGER));
        assertThat(d.run(view.get(0), view), equalTo(1 / 4));
        assertThat(d.run(view.get(1), view), equalTo(1 / 3));
    }

    @Test
    public void divisionFloatTest() {
        Constant e1 = new Constant(1.5f, DataType.FLOAT);
        Arithmetic d = (Arithmetic) Arithmetic.createDivision(e1, floatField);

        assertTrue(d.isComplete());
        assertThat(d.getType(), equalTo(DataType.FLOAT));
        assertThat(d.run(view.get(0), view), equalTo(1.5f / 4.4f));
        assertThat(d.run(view.get(1), view), equalTo(1.5f / 3.3f));
    }

    @Test
    public void moduloTest() {
        Constant e1 = new Constant(1, DataType.INTEGER);
        Arithmetic m = (Arithmetic) Arithmetic.createModulo(e1, intField);

        assertTrue(m.isComplete());
        assertThat(m.getType(), equalTo(DataType.INTEGER));
        assertThat(m.run(view.get(0), view), equalTo(1 % 4));
        assertThat(m.run(view.get(1), view), equalTo(1 % 3));
    }

    @Test
    public void inverseTest() {
        Constant e1 = new Constant(1, DataType.INTEGER);

        Arithmetic inv = (Arithmetic) Arithmetic.createInverse(e1);
        assertThat(inv.getType(), equalTo(DataType.INTEGER));
        assertThat(inv.run(view.get(0), view), equalTo(-1));

        inv = (Arithmetic) Arithmetic.createInverse(floatField);
        assertThat(inv.getType(), equalTo(DataType.FLOAT));
        assertThat(inv.run(view.get(0), view),
                equalTo(-(Float) view.get(0)[3]));
        assertThat(inv.run(view.get(1), view),
                equalTo(-(Float) view.get(1)[3]));
    }

}
