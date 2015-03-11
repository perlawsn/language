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
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 23/02/15.
 */
public class MiscTest {

    private static Attribute integerAtt =
            Attribute.create("integer", DataType.INTEGER);
    private static Attribute stringAtt =
            Attribute.create("string", DataType.STRING);
    private static Attribute floatAtt =
            Attribute.create("float", DataType.FLOAT);

    private static List<Attribute> atts;

    private static BufferView view;

    @BeforeClass
    public static void setupBuffer() {
        Attribute[] as = new Attribute[] {
                Attribute.TIMESTAMP,
                integerAtt,
                stringAtt,
                floatAtt
        };
        atts = Arrays.asList(as);

        Buffer b = new ArrayBuffer(0, 512);
        b.add(new Record(atts, new Object[]{Instant.now(), 0, "0", 0.0f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 1, "1", 1.1f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 2, "2", 2.2f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 3, "3", 3.3f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 4, "4", 4.4f}));

        view = b.unmodifiableView();
    }

    @Test
    public void nullTest() {
        Null n = Null.INSTANCE;

        assertThat(n.getType(), nullValue());
        assertTrue(n.isComplete());
        assertFalse(n.hasErrors());
        assertThat(n.run(view.get(0), view), nullValue());
        assertThat(n.run(view.get(1), view), nullValue());
    }

    @Test
    public void castFloat() {
        Constant cInt = new Constant(1, DataType.INTEGER);
        Constant cFloat = new Constant(1.2f, DataType.FLOAT);
        Expression fFloat = new Field(floatAtt.getId()).rebuild(atts);
        Expression incomplete = new Field("integer");
        Expression error = new ErrorExpression("test");

        Expression cast = CastFloat.create(cInt);
        assertTrue(cast.isComplete());
        assertFalse(cast.hasErrors());
        assertThat(cast.getType(), equalTo(DataType.FLOAT));
        assertThat(cast.run(view.get(0), view), equalTo(1f));

        cast = CastFloat.create(cFloat);
        assertTrue(cast.isComplete());
        assertFalse(cast.hasErrors());
        assertThat(cast.getType(), equalTo(DataType.FLOAT));
        assertThat(cast.run(view.get(0), view), equalTo(1.2f));

        cast = CastFloat.create(fFloat);
        assertTrue(cast.isComplete());
        assertFalse(cast.hasErrors());
        assertThat(cast.getType(), equalTo(DataType.FLOAT));
        assertThat(cast.run(view.get(0), view), equalTo(4.4f));
        assertThat(cast.run(view.get(1), view), equalTo(3.3f));

        cast = CastFloat.create(incomplete);
        assertFalse(cast.isComplete());
        assertFalse(cast.hasErrors());
        cast = cast.rebuild(atts);
        assertTrue(cast.isComplete());
        assertThat(cast.run(view.get(0), view), equalTo(4f));

        cast = CastFloat.create(error);
        assertTrue(cast.isComplete());
        assertTrue(cast.hasErrors());
    }

    @Test
    public void constantTest() {
        Constant c1 = new Constant(1, DataType.INTEGER);
        assertTrue(c1.isComplete());
        assertFalse(c1.hasErrors());
        assertThat(c1.getType(), equalTo(DataType.INTEGER));
        assertThat(c1.run(view.get(0), view), equalTo(1));

        Constant c2 = new Constant("test", DataType.STRING);
        assertTrue(c1.isComplete());
        assertFalse(c1.hasErrors());
        assertThat(c2.getType(), equalTo(DataType.STRING));
        assertThat(c2.run(view.get(0), view), equalTo("test"));
    }

    @Test
    public void fieldTest() {
        Expression fi = new Field(integerAtt.getId());
        assertFalse(fi.isComplete());
        assertFalse(fi.hasErrors());
        fi = fi.rebuild(atts);
        assertTrue(fi.isComplete());
        assertThat(fi.getType(), equalTo(DataType.INTEGER));
        assertThat(fi.run(view.get(0), view), equalTo(4));
        assertThat(fi.run(view.get(1), view), equalTo(3));

        Expression fs = new Field(stringAtt.getId());
        assertFalse(fs.isComplete());
        assertFalse(fs.hasErrors());
        fs = fs.rebuild(atts);
        assertTrue(fi.isComplete());
        assertThat(fs.getType(), equalTo(DataType.STRING));
        assertThat(fs.run(view.get(0), view), equalTo("4"));
        assertThat(fs.run(view.get(1), view), equalTo("3"));
    }

    @Test
    public void groupTSTest() {
        Expression gts = new GroupTS();
        assertFalse(gts.isComplete());
        assertFalse(gts.hasErrors());
        gts = gts.rebuild(atts);
        assertTrue(gts.isComplete());
        assertThat(gts.getType(), equalTo(DataType.TIMESTAMP));
        assertThat(gts.run(null, view), equalTo(view.get(0)[0]));
    }

}
