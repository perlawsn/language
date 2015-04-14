package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.sample.Sample;
import org.dei.perla.lang.executor.ArrayBuffer;
import org.dei.perla.lang.executor.Buffer;
import org.dei.perla.lang.executor.BufferView;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

/**
 * @author Guido Rota 23/02/15.
 */
public class MiscTest {

    private static Attribute intAtt =
            Attribute.create("integer", DataType.INTEGER);
    private static Attribute stringAtt =
            Attribute.create("string", DataType.STRING);
    private static Attribute floatAtt =
            Attribute.create("float", DataType.FLOAT);
    private static Attribute boolAtt =
            Attribute.create("boolean", DataType.BOOLEAN);

    private static final List<Attribute> atts;
    static {
        atts = Arrays.asList(new Attribute[]{
                Attribute.TIMESTAMP,
                intAtt,
                stringAtt,
                floatAtt,
                boolAtt
        });
    }

    @Test
    public void castFloat() {
        Expression cInt = Constant.create(1, DataType.INTEGER);
        Expression cFloat = Constant.create(1.2f, DataType.FLOAT);

        Expression e = CastFloat.create(cInt);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound);
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertThat(e.run(null, null), equalTo(1f));

        e = CastFloat.create(cFloat);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts, bound);
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertThat(e.run(null, null), equalTo(1.2f));
    }

    @Test
    public void castFloatBind() {
        Expression e = CastFloat.create(new Field("float"));
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound);
        assertTrue(e.isComplete());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(floatAtt));
        Object[] sample = new Object[]{4.4f};
        assertThat(e.run(sample, null), equalTo(4.4f));
    }

    @Test
    public void castFloatError() {
        Expression err = new ErrorExpression("test");
        Expression cast = CastFloat.create(err);
        assertTrue(cast.isComplete());
        assertTrue(cast.hasErrors());
    }

    @Test
    public void castFloatNull() {
        Expression cast = CastFloat.create(Constant.NULL);
        assertTrue(cast.isComplete());
        assertFalse(cast.hasErrors());
        assertThat(cast.getType(), nullValue());
        assertThat(cast.run(null, null), equalTo(null));
        assertThat(cast, equalTo(Constant.NULL));

        cast = CastFloat.create(new Field("incomplete"));
        assertFalse(cast.isComplete());
        assertFalse(cast.hasErrors());
        assertThat(cast.getType(), equalTo(DataType.FLOAT));
        assertThat(cast.run(null, null), equalTo(null));
    }

    @Test
    public void castInteger() {
        Expression cInt = Constant.create(1, DataType.INTEGER);
        Expression cFloat = Constant.create(1.2f, DataType.FLOAT);

        Expression e = CastInteger.create(cInt);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound);
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(e.run(null, null), equalTo(1));

        e = CastInteger.create(cFloat);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts, bound);
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(e.run(null, null), equalTo(1));
    }

    @Test
    public void castIntegerBind() {
        Expression cast = CastInteger.create(new Field("integer"));
        assertFalse(cast.isComplete());
        List<Attribute> bound = new ArrayList<>();
        cast = cast.bind(atts, bound);
        assertTrue(cast.isComplete());
        assertFalse(cast.hasErrors());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        Object[] sample = new Object[]{4};
        assertThat(cast.run(sample, null), equalTo(4));
    }

    @Test
    public void castIntegerError() {
        Expression err = new ErrorExpression("test");
        Expression cast = CastInteger.create(err);
        assertTrue(cast.isComplete());
        assertTrue(cast.hasErrors());
    }

    @Test
    public void castIntegerNull() {
        Expression cast = CastInteger.create(Constant.NULL);
        assertTrue(cast.isComplete());
        assertFalse(cast.hasErrors());
        assertThat(cast.getType(), nullValue());
        assertThat(cast.run(null, null), equalTo(null));
        assertThat(cast, equalTo(Constant.NULL));

        cast = CastInteger.create(new Field("incomplete"));
        assertFalse(cast.isComplete());
        assertFalse(cast.hasErrors());
        assertThat(cast.getType(), equalTo(DataType.INTEGER));
        assertThat(cast.run(null, null), equalTo(null));
    }

    @Test
    public void constantTest() {
        Expression e = Constant.create(1, DataType.INTEGER);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound);
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(e.run(null, null), equalTo(1));

        e = Constant.create("test", DataType.STRING);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts, bound);
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.STRING));
        assertThat(e.run(null, null), equalTo("test"));

        e = Constant.UNKNOWN;
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts, bound);
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void constantNullTest() {
        Expression e = Constant.NULL;
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound);
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), nullValue());
        assertThat(((Constant) e).getValue(), nullValue());

        e = Constant.create(null, DataType.INTEGER);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts, bound);
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), nullValue());
        assertThat(((Constant) e).getValue(), nullValue());
        assertThat(e, equalTo(Constant.NULL));

        e = Constant.create(null, DataType.FLOAT);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts, bound);
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), nullValue());
        assertThat(((Constant) e).getValue(), nullValue());
        assertThat(e, equalTo(Constant.NULL));

        e = Constant.create(null, DataType.STRING);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts, bound);
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), nullValue());
        assertThat(((Constant) e).getValue(), nullValue());
        assertThat(e, equalTo(Constant.NULL));

        e = Constant.create(null, DataType.BOOLEAN);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts, bound);
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), nullValue());
        assertThat(((Constant) e).getValue(), nullValue());
        assertThat(e, equalTo(Constant.NULL));

        e = Constant.create(null, DataType.TIMESTAMP);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts, bound);
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), nullValue());
        assertThat(((Constant) e).getValue(), nullValue());
        assertThat(e, equalTo(Constant.NULL));

        e = Constant.create(null, DataType.ID);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts, bound);
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), nullValue());
        assertThat(((Constant) e).getValue(), nullValue());
        assertThat(e, equalTo(Constant.NULL));
    }

    @Test
    public void testConstantLogic() {
        Expression e = Constant.TRUE;
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound);
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.TRUE));

        e = Constant.create(LogicValue.TRUE, DataType.BOOLEAN);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.TRUE));
        assertThat(e, equalTo(Constant.TRUE));

        e = Constant.FALSE;
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts, bound);
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.FALSE));

        e = Constant.create(LogicValue.FALSE, DataType.BOOLEAN);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts, bound);
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.FALSE));
        assertThat(e, equalTo(Constant.FALSE));

        e = Constant.UNKNOWN;
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts, bound);
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.UNKNOWN));

        e = Constant.create(LogicValue.UNKNOWN, DataType.BOOLEAN);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts, bound);
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.UNKNOWN));
        assertThat(e, equalTo(Constant.UNKNOWN));
    }

    @Test
    public void testConstantInteger() {
        Expression e = Constant.INTEGER_0;
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound);
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), equalTo(0));

        e = Constant.create(0, DataType.INTEGER);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts, bound);
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), equalTo(0));
        assertThat(e, equalTo(Constant.INTEGER_0));

        e = Constant.INTEGER_1;
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts, bound);
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), equalTo(1));

        e = Constant.create(1, DataType.INTEGER);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts, bound);
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), equalTo(1));
        assertThat(e, equalTo(Constant.INTEGER_1));

        e = Constant.INTEGER_2;
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts, bound);
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), equalTo(2));

        e = Constant.create(2, DataType.INTEGER);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts, bound);
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), equalTo(2));
        assertThat(e, equalTo(Constant.INTEGER_2));
    }

    @Test
    public void fieldTest() {
        Object[] sample;

        Expression fi = new Field(intAtt.getId());
        assertFalse(fi.isComplete());
        assertFalse(fi.hasErrors());
        List<Attribute> bound = new ArrayList<>();
        fi = fi.bind(atts, bound);
        assertTrue(fi.isComplete());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertThat(fi.getType(), equalTo(DataType.INTEGER));
        sample = new Object[]{4};
        assertThat(fi.run(sample, null), equalTo(4));
        sample = new Object[]{3};
        assertThat(fi.run(sample, null), equalTo(3));

        Expression fs = new Field(stringAtt.getId());
        assertFalse(fs.isComplete());
        assertFalse(fs.hasErrors());
        bound.clear();
        fs = fs.bind(atts, bound);
        assertTrue(fi.isComplete());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(stringAtt));
        assertThat(fs.getType(), equalTo(DataType.STRING));
        sample = new Object[]{"4"};
        assertThat(fs.run(sample, null), equalTo("4"));
        sample = new Object[]{"3"};
        assertThat(fs.run(sample, null), equalTo("3"));

        Expression fb = new Field(boolAtt.getId());
        assertFalse(fb.isComplete());
        assertFalse(fb.hasErrors());
        bound.clear();
        fb = fb.bind(atts, bound);
        assertTrue(fb.isComplete());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(boolAtt));
        assertThat(fb.getType(), equalTo(DataType.BOOLEAN));
        sample = new Object[]{true};
        assertThat(fb.run(sample, null), equalTo(LogicValue.TRUE));
        sample = new Object[]{null};
        assertThat(fb.run(sample, null), nullValue());
        sample = new Object[]{false};
        assertThat(fb.run(sample, null), equalTo(LogicValue.FALSE));

        Expression incomplete = new Field("incomplete");
        assertFalse(incomplete.isComplete());
        assertFalse(incomplete.hasErrors());
        assertThat(incomplete.run(null, null), nullValue());
    }

    @Test
    public void groupTSTest() {
        List<Attribute> ra = Arrays.asList(new Attribute[]{
                Attribute.TIMESTAMP,
                intAtt
        });
        Buffer b = new ArrayBuffer(0, 1);
        b.add(new Sample(ra, new Object[]{Instant.now(), 1}));
        b.add(new Sample(ra, new Object[]{Instant.now(), 2}));
        BufferView view = b.unmodifiableView();

        Expression gts = new GroupTS();
        assertFalse(gts.isComplete());
        assertFalse(gts.hasErrors());
        assertThat(gts.getType(), equalTo(DataType.TIMESTAMP));
        assertThat(gts.run(null, null), nullValue());

        List<Attribute> bound = new ArrayList<>();
        gts = gts.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(Attribute.TIMESTAMP));
        assertTrue(gts.isComplete());
        assertThat(gts.getType(), equalTo(DataType.TIMESTAMP));
        assertThat(gts.run(null, view), equalTo(view.get(0)[0]));
    }

}
