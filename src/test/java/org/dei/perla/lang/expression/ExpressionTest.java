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
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Guido Rota 23/02/15.
 */
public class ExpressionTest {

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
                floatAtt
        };
        List<Attribute> atts = Arrays.asList(as);

        Buffer b = new ArrayBuffer(atts, 512);
        b.add(new Record(atts, new Object[]{Instant.now(), 0, "0", 0.0f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 1, "1", 1.1f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 2, "2", 2.2f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 3, "3", 3.3f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 4, "4", 4.4f}));

        view = b.unmodifiableView();
    }

    @Test
    public void nullTest() {
        Null n = new Null();

        assertThat(n.getType(), nullValue());
        assertThat(n.compute(view.get(0), view), nullValue());
        assertThat(n.compute(view.get(1), view), nullValue());
    }

    @Test
    public void CastFloat() {
        Constant cInt = new Constant(1, DataType.INTEGER);
        Constant cFloat = new Constant(1.2f, DataType.FLOAT);
        Field fFloat = new Field(3, DataType.FLOAT);

        CastFloat cast = new CastFloat(cInt);
        assertThat(cast.getType(), equalTo(DataType.FLOAT));
        assertThat(cast.compute(view.get(0), view), equalTo(1f));

        cast = new CastFloat(cFloat);
        assertThat(cast.getType(), equalTo(DataType.FLOAT));
        assertThat(cast.compute(view.get(0), view), equalTo(1.2f));

        cast = new CastFloat(fFloat);
        assertThat(cast.getType(), equalTo(DataType.FLOAT));
        assertThat(cast.compute(view.get(0), view), equalTo(4.4f));
        assertThat(cast.compute(view.get(1), view), equalTo(3.3f));
    }

    @Test
    public void constantTest() {
        Constant c1 = new Constant(1, DataType.INTEGER);
        assertThat(c1.getType(), equalTo(DataType.INTEGER));
        assertThat(c1.compute(view.get(0), view), equalTo(1));

        Constant c2 = new Constant("test", DataType.STRING);
        assertThat(c2.getType(), equalTo(DataType.STRING));
        assertThat(c2.compute(view.get(0), view), equalTo("test"));
    }

    @Test
    public void fieldTest() {
        Field fi = new Field(1, integerAtt.getType());
        assertThat(fi.getType(), equalTo(DataType.INTEGER));
        assertThat(fi.compute(view.get(0), view), equalTo(4));
        assertThat(fi.compute(view.get(1), view), equalTo(3));

        Field fs = new Field(2, stringAtt.getType());
        assertThat(fs.getType(), equalTo(DataType.STRING));
        assertThat(fs.compute(view.get(0), view), equalTo("4"));
        assertThat(fs.compute(view.get(1), view), equalTo("3"));
    }

    @Test
    public void additionIntegerTest() {
        Constant e1 = new Constant(1, DataType.INTEGER);
        Field e2 = new Field(1, DataType.INTEGER);
        Addition a = new Addition(e1, e2, DataType.INTEGER);

        assertThat(a.getType(), equalTo(DataType.INTEGER));
        assertThat(a.compute(view.get(0), view), equalTo(1 + 4));
        assertThat(a.compute(view.get(1), view), equalTo(1 + 3));
    }

    @Test
    public void additionFloatTest() {
        Constant e1 = new Constant(1.5f, DataType.INTEGER);
        Field e2 = new Field(3, DataType.FLOAT);
        Addition a = new Addition(e1, e2, DataType.FLOAT);

        assertThat(a.getType(), equalTo(DataType.FLOAT));
        assertThat(a.compute(view.get(0), view), equalTo(4.4f + 1.5f));
        assertThat(a.compute(view.get(1), view), equalTo(3.3f + 1.5f));
    }

    @Test
    public void subtractionIntegerTest() {
        Constant e1 = new Constant(1, DataType.INTEGER);
        Field e2 = new Field(1, DataType.INTEGER);
        Subtraction s = new Subtraction(e1, e2, DataType.INTEGER);

        assertThat(s.getType(), equalTo(DataType.INTEGER));
        assertThat(s.compute(view.get(0), view), equalTo(1 - 4));
        assertThat(s.compute(view.get(1), view), equalTo(1 - 3));
    }

    @Test
    public void subtractionFloatTest() {
        Constant e1 = new Constant(1.5f, DataType.INTEGER);
        Field e2 = new Field(3, DataType.FLOAT);
        Subtraction s = new Subtraction(e1, e2, DataType.FLOAT);

        assertThat(s.getType(), equalTo(DataType.FLOAT));
        assertThat(s.compute(view.get(0), view), equalTo(1.5f - 4.4f));
        assertThat(s.compute(view.get(1), view), equalTo(1.5f - 3.3f));
    }

    @Test
    public void productIntegerTest() {
        Constant e1 = new Constant(1, DataType.INTEGER);
        Field e2 = new Field(1, DataType.INTEGER);
        Product p = new Product(e1, e2, DataType.INTEGER);

        assertThat(p.getType(), equalTo(DataType.INTEGER));
        assertThat(p.compute(view.get(0), view), equalTo(1 * 4));
        assertThat(p.compute(view.get(1), view), equalTo(1 * 3));
    }

    @Test
    public void productFloatTest() {
        Constant e1 = new Constant(1.5f, DataType.INTEGER);
        Field e2 = new Field(3, DataType.FLOAT);
        Product p = new Product(e1, e2, DataType.FLOAT);

        assertThat(p.getType(), equalTo(DataType.FLOAT));
        assertThat(p.compute(view.get(0), view), equalTo(1.5f * 4.4f));
        assertThat(p.compute(view.get(1), view), equalTo(1.5f * 3.3f));
    }

    @Test
    public void divisionIntegerTest() {
        Constant e1 = new Constant(1, DataType.INTEGER);
        Field e2 = new Field(1, DataType.INTEGER);
        Division d = new Division(e1, e2, DataType.INTEGER);

        assertThat(d.getType(), equalTo(DataType.INTEGER));
        assertThat(d.compute(view.get(0), view), equalTo(1 / 4));
        assertThat(d.compute(view.get(1), view), equalTo(1 / 3));
    }

    @Test
    public void divisionFloatTest() {
        Constant e1 = new Constant(1.5f, DataType.INTEGER);
        Field e2 = new Field(3, DataType.FLOAT);
        Division d = new Division(e1, e2, DataType.FLOAT);

        assertThat(d.getType(), equalTo(DataType.FLOAT));
        assertThat(d.compute(view.get(0), view), equalTo(1.5f / 4.4f));
        assertThat(d.compute(view.get(1), view), equalTo(1.5f / 3.3f));
    }

    @Test
    public void moduloTest() {
        Constant e1 = new Constant(1, DataType.INTEGER);
        Field e2 = new Field(1, DataType.INTEGER);
        Modulo m = new Modulo(e1, e2, DataType.INTEGER);

        assertThat(m.getType(), equalTo(DataType.INTEGER));
        assertThat(m.compute(view.get(0), view), equalTo(1 % 4));
        assertThat(m.compute(view.get(1), view), equalTo(1 % 3));
    }

    @Test
    public void inverseTest() {
        Constant e1 = new Constant(1, DataType.INTEGER);
        Field e2 = new Field(3, DataType.FLOAT);

        Inverse inv = new Inverse(e1, DataType.INTEGER);
        assertThat(inv.getType(), equalTo(DataType.INTEGER));
        assertThat(inv.compute(view.get(0), view), equalTo(-1));

        inv = new Inverse(e2, DataType.FLOAT);
        assertThat(inv.getType(), equalTo(DataType.FLOAT));
        assertThat(inv.compute(view.get(0), view),
                equalTo(-(Float) view.get(0)[3]));
        assertThat(inv.compute(view.get(1), view),
                equalTo(-(Float) view.get(1)[3]));
    }

}
