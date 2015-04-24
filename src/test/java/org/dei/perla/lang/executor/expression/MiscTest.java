package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.buffer.ArrayBuffer;
import org.dei.perla.lang.executor.buffer.Buffer;
import org.dei.perla.lang.executor.buffer.BufferView;
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
        Errors err = new Errors();
        Expression cInt = Constant.create(1, DataType.INTEGER);
        Expression cFloat = Constant.create(1.2f, DataType.FLOAT);

        Expression e = CastFloat.create(cInt, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertThat(e.run(null, null), equalTo(1f));

        e = CastFloat.create(cFloat, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertThat(e.run(null, null), equalTo(1.2f));
    }

    @Test
    public void castFloatBind() {
        Errors err = new Errors();

        Expression e = CastFloat.create(new Field("float"), err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(floatAtt));
        Object[] sample = new Object[]{4.4f};
        assertThat(e.run(sample, null), equalTo(4.4f));
    }

    @Test
    public void castFloatNull() {
        Errors err = new Errors();
        Expression cast = CastFloat.create(Constant.NULL, err);
        assertTrue(err.isEmpty());
        assertTrue(cast.isComplete());
        assertThat(cast.getType(), nullValue());
        assertThat(cast.run(null, null), equalTo(null));
        assertThat(cast, equalTo(Constant.NULL));

        cast = CastFloat.create(new Field("incomplete"), err);
        assertTrue(err.isEmpty());
        assertFalse(cast.isComplete());
        assertThat(cast.getType(), equalTo(DataType.FLOAT));
        assertThat(cast.run(null, null), equalTo(null));
    }

    @Test
    public void castInteger() {
        Errors err = new Errors();
        Expression cInt = Constant.create(1, DataType.INTEGER);
        Expression cFloat = Constant.create(1.2f, DataType.FLOAT);

        Expression e = CastInteger.create(cInt, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(e.run(null, null), equalTo(1));

        e = CastInteger.create(cFloat, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(e.run(null, null), equalTo(1));
    }

    @Test
    public void castIntegerBind() {
        Errors err = new Errors();
        Expression cast = CastInteger.create(new Field("integer"), err);
        assertFalse(cast.isComplete());
        List<Attribute> bound = new ArrayList<>();
        cast = cast.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(cast.isComplete());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        Object[] sample = new Object[]{4};
        assertThat(cast.run(sample, null), equalTo(4));
    }

    @Test
    public void castIntegerNull() {
        Errors err = new Errors();
        Expression cast = CastInteger.create(Constant.NULL, err);
        assertTrue(err.isEmpty());
        assertTrue(cast.isComplete());
        assertThat(cast.getType(), nullValue());
        assertThat(cast.run(null, null), equalTo(null));
        assertThat(cast, equalTo(Constant.NULL));

        cast = CastInteger.create(new Field("incomplete"), err);
        assertTrue(err.isEmpty());
        assertFalse(cast.isComplete());
        assertThat(cast.getType(), equalTo(DataType.INTEGER));
        assertThat(cast.run(null, null), equalTo(null));
    }

    @Test
    public void constantTest() {
        Errors err = new Errors();
        Expression e = Constant.create(1, DataType.INTEGER);
        assertTrue(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(e.run(null, null), equalTo(1));

        e = Constant.create("test", DataType.STRING);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.STRING));
        assertThat(e.run(null, null), equalTo("test"));

        e = Constant.UNKNOWN;
        assertTrue(e.isComplete());
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void constantNullTest() {
        Errors err = new Errors();
        Expression e = Constant.NULL;
        assertTrue(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), nullValue());
        assertThat(((Constant) e).getValue(), nullValue());

        e = Constant.create(null, DataType.INTEGER);
        assertTrue(e.isComplete());
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), nullValue());
        assertThat(((Constant) e).getValue(), nullValue());
        assertThat(e, equalTo(Constant.NULL));

        e = Constant.create(null, DataType.FLOAT);
        assertTrue(e.isComplete());
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), nullValue());
        assertThat(((Constant) e).getValue(), nullValue());
        assertThat(e, equalTo(Constant.NULL));

        e = Constant.create(null, DataType.STRING);
        assertTrue(e.isComplete());
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), nullValue());
        assertThat(((Constant) e).getValue(), nullValue());
        assertThat(e, equalTo(Constant.NULL));

        e = Constant.create(null, DataType.BOOLEAN);
        assertTrue(e.isComplete());
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), nullValue());
        assertThat(((Constant) e).getValue(), nullValue());
        assertThat(e, equalTo(Constant.NULL));

        e = Constant.create(null, DataType.TIMESTAMP);
        assertTrue(e.isComplete());
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), nullValue());
        assertThat(((Constant) e).getValue(), nullValue());
        assertThat(e, equalTo(Constant.NULL));

        e = Constant.create(null, DataType.ID);
        assertTrue(e.isComplete());
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), nullValue());
        assertThat(((Constant) e).getValue(), nullValue());
        assertThat(e, equalTo(Constant.NULL));
    }

    @Test
    public void testConstantLogic() {
        Errors err = new Errors();
        Expression e = Constant.TRUE;
        assertTrue(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.TRUE));

        e = Constant.create(LogicValue.TRUE, DataType.BOOLEAN);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.TRUE));
        assertThat(e, equalTo(Constant.TRUE));

        e = Constant.FALSE;
        assertTrue(e.isComplete());
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.FALSE));

        e = Constant.create(LogicValue.FALSE, DataType.BOOLEAN);
        assertTrue(e.isComplete());
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.FALSE));
        assertThat(e, equalTo(Constant.FALSE));

        e = Constant.UNKNOWN;
        assertTrue(e.isComplete());
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.UNKNOWN));

        e = Constant.create(LogicValue.UNKNOWN, DataType.BOOLEAN);
        assertTrue(e.isComplete());
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.UNKNOWN));
        assertThat(e, equalTo(Constant.UNKNOWN));
    }

    @Test
    public void testConstantInteger() {
        Errors err = new Errors();
        Expression e = Constant.INTEGER_0;
        assertTrue(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), equalTo(0));

        e = Constant.create(0, DataType.INTEGER);
        assertTrue(e.isComplete());
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), equalTo(0));
        assertThat(e, equalTo(Constant.INTEGER_0));

        e = Constant.INTEGER_1;
        assertTrue(e.isComplete());
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), equalTo(1));

        e = Constant.create(1, DataType.INTEGER);
        assertTrue(e.isComplete());
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), equalTo(1));
        assertThat(e, equalTo(Constant.INTEGER_1));

        e = Constant.INTEGER_2;
        assertTrue(e.isComplete());
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), equalTo(2));

        e = Constant.create(2, DataType.INTEGER);
        assertTrue(e.isComplete());
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(bound.isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), equalTo(2));
        assertThat(e, equalTo(Constant.INTEGER_2));
    }

    @Test
    public void fieldTest() {
        Errors err = new Errors();
        Object[] sample;

        Expression fi = new Field(intAtt.getId());
        assertFalse(fi.isComplete());
        List<Attribute> bound = new ArrayList<>();
        fi = fi.bind(atts, bound, err);
        assertTrue(err.isEmpty());
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
        bound.clear();
        fs = fs.bind(atts, bound, err);
        assertTrue(err.isEmpty());
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
        bound.clear();
        fb = fb.bind(atts, bound, err);
        assertTrue(err.isEmpty());
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
        assertThat(incomplete.run(null, null), nullValue());
    }

    @Test
    public void groupTSTest() {
        Errors err = new Errors();
        List<Attribute> ra = Arrays.asList(new Attribute[]{
                Attribute.TIMESTAMP,
                intAtt
        });
        Buffer b = new ArrayBuffer(atts, 1);
        b.add(new Object[]{Instant.now(), 1});
        b.add(new Object[]{Instant.now(), 2});
        BufferView view = b.unmodifiableView();

        Expression gts = new GroupTS();
        assertFalse(gts.isComplete());
        assertThat(gts.getType(), equalTo(DataType.TIMESTAMP));
        assertThat(gts.run(null, null), nullValue());

        List<Attribute> bound = new ArrayList<>();
        gts = gts.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(Attribute.TIMESTAMP));
        assertTrue(gts.isComplete());
        assertThat(gts.getType(), equalTo(DataType.TIMESTAMP));
        assertThat(gts.run(null, view), equalTo(view.get(0)[0]));
    }

}
