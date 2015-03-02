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

}
