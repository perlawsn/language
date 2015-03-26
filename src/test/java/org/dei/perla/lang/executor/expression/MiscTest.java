package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.record.Record;
import org.dei.perla.lang.executor.ArrayBuffer;
import org.dei.perla.lang.executor.Buffer;
import org.dei.perla.lang.executor.BufferView;
import org.junit.Test;

import java.time.Instant;
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
        Object[] record;

        Expression cInt = Constant.create(1, DataType.INTEGER);
        Expression cFloat = Constant.create(1.2f, DataType.FLOAT);

        Expression cast = CastFloat.create(cInt);
        assertTrue(cast.isComplete());
        assertFalse(cast.hasErrors());
        assertTrue(cast.getAttributes().isEmpty());
        assertThat(cast.getType(), equalTo(DataType.FLOAT));
        assertThat(cast.run(null, null), equalTo(1f));

        cast = CastFloat.create(cFloat);
        assertTrue(cast.isComplete());
        assertFalse(cast.hasErrors());
        assertTrue(cast.getAttributes().isEmpty());
        assertThat(cast.getType(), equalTo(DataType.FLOAT));
        assertThat(cast.run(null, null), equalTo(1.2f));
    }

    @Test
    public void castFloatBind() {
        Expression cast = CastFloat.create(new Field("float"));
        assertFalse(cast.isComplete());
        assertFalse(cast.hasErrors());
        cast = cast.bind(atts);
        assertTrue(cast.isComplete());
        List<Attribute> as = cast.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(floatAtt));
        Object[] record = new Object[]{4.4f};
        assertThat(cast.run(record, null), equalTo(4.4f));
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
        Expression fFloat = new Field(floatAtt.getId()).bind(atts);
        Expression incomplete = new Field("integer");
        Expression error = new ErrorExpression("test");
        Object[] record;

        Expression cast = CastInteger.create(cInt);
        assertTrue(cast.isComplete());
        assertFalse(cast.hasErrors());
        assertTrue(cast.getAttributes().isEmpty());
        assertThat(cast.getType(), equalTo(DataType.INTEGER));
        assertThat(cast.run(null, null), equalTo(1));

        cast = CastInteger.create(cFloat);
        assertTrue(cast.isComplete());
        assertFalse(cast.hasErrors());
        assertTrue(cast.getAttributes().isEmpty());
        assertThat(cast.getType(), equalTo(DataType.INTEGER));
        assertThat(cast.run(null, null), equalTo(1));

        cast = CastInteger.create(fFloat);
        assertTrue(cast.isComplete());
        assertFalse(cast.hasErrors());
        List<Attribute> as = cast.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(floatAtt));
        assertThat(cast.getType(), equalTo(DataType.INTEGER));
        record = new Object[]{4.4f};
        assertThat(cast.run(record, null), equalTo(4));
        record = new Object[]{3.3f};
        assertThat(cast.run(record, null), equalTo(3));

        cast = CastInteger.create(incomplete);
        assertFalse(cast.isComplete());
        assertFalse(cast.hasErrors());
        cast = cast.bind(atts);
        assertTrue(cast.isComplete());
        as = cast.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(intAtt));
        record = new Object[]{4};
        assertThat(cast.run(record, null), equalTo(4));

        cast = CastInteger.create(error);
        assertTrue(cast.isComplete());
        assertTrue(cast.hasErrors());
    }

    @Test
    public void constantTest() {
        Expression c1 = Constant.create(1, DataType.INTEGER);
        assertTrue(c1.isComplete());
        assertFalse(c1.hasErrors());
        assertTrue(c1.getAttributes().isEmpty());
        assertThat(c1.getType(), equalTo(DataType.INTEGER));
        assertThat(c1.run(null, null), equalTo(1));

        Expression c2 = Constant.create("test", DataType.STRING);
        assertTrue(c2.isComplete());
        assertFalse(c2.hasErrors());
        assertTrue(c2.getAttributes().isEmpty());
        assertThat(c2.getType(), equalTo(DataType.STRING));
        assertThat(c2.run(null, null), equalTo("test"));

        Constant u = Constant.UNKNOWN;
        assertTrue(u.isComplete());
        assertFalse(u.hasErrors());
        assertTrue(u.getAttributes().isEmpty());
        assertThat(u.getType(), equalTo(DataType.BOOLEAN));
        assertThat(u.run(null, null), equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void constantNullTest() {
        Expression e = Constant.NULL;
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getAttributes().isEmpty());
        assertThat(e.getType(), nullValue());
        assertThat(((Constant) e).getValue(), nullValue());

        e = Constant.create(null, DataType.INTEGER);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getAttributes().isEmpty());
        assertThat(e.getType(), nullValue());
        assertThat(((Constant) e).getValue(), nullValue());
        assertThat(e, equalTo(Constant.NULL));

        e = Constant.create(null, DataType.FLOAT);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getAttributes().isEmpty());
        assertThat(e.getType(), nullValue());
        assertThat(((Constant) e).getValue(), nullValue());
        assertThat(e, equalTo(Constant.NULL));

        e = Constant.create(null, DataType.STRING);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getAttributes().isEmpty());
        assertThat(e.getType(), nullValue());
        assertThat(((Constant) e).getValue(), nullValue());
        assertThat(e, equalTo(Constant.NULL));

        e = Constant.create(null, DataType.BOOLEAN);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getAttributes().isEmpty());
        assertThat(e.getType(), nullValue());
        assertThat(((Constant) e).getValue(), nullValue());
        assertThat(e, equalTo(Constant.NULL));

        e = Constant.create(null, DataType.TIMESTAMP);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getAttributes().isEmpty());
        assertThat(e.getType(), nullValue());
        assertThat(((Constant) e).getValue(), nullValue());
        assertThat(e, equalTo(Constant.NULL));

        e = Constant.create(null, DataType.ID);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getAttributes().isEmpty());
        assertThat(e.getType(), nullValue());
        assertThat(((Constant) e).getValue(), nullValue());
        assertThat(e, equalTo(Constant.NULL));
    }

    @Test
    public void testConstantLogic() {
        Expression e = Constant.TRUE;
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getAttributes().isEmpty());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.TRUE));

        e = Constant.create(LogicValue.TRUE, DataType.BOOLEAN);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getAttributes().isEmpty());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.TRUE));
        assertThat(e, equalTo(Constant.TRUE));

        e = Constant.FALSE;
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getAttributes().isEmpty());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.FALSE));

        e = Constant.create(LogicValue.FALSE, DataType.BOOLEAN);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getAttributes().isEmpty());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.FALSE));
        assertThat(e, equalTo(Constant.FALSE));

        e = Constant.UNKNOWN;
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getAttributes().isEmpty());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.UNKNOWN));

        e = Constant.create(LogicValue.UNKNOWN, DataType.BOOLEAN);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getAttributes().isEmpty());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.UNKNOWN));
        assertThat(e, equalTo(Constant.UNKNOWN));
    }

    @Test
    public void testConstantInteger() {
        Expression e = Constant.INTEGER_0;
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getAttributes().isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), equalTo(0));

        e = Constant.create(0, DataType.INTEGER);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getAttributes().isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), equalTo(0));
        assertThat(e, equalTo(Constant.INTEGER_0));

        e = Constant.INTEGER_1;
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getAttributes().isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), equalTo(1));

        e = Constant.create(1, DataType.INTEGER);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getAttributes().isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), equalTo(1));
        assertThat(e, equalTo(Constant.INTEGER_1));

        e = Constant.INTEGER_2;
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getAttributes().isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), equalTo(2));

        e = Constant.create(2, DataType.INTEGER);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getAttributes().isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), equalTo(2));
        assertThat(e, equalTo(Constant.INTEGER_2));
    }

    @Test
    public void fieldTest() {
        Object[] record;

        Expression fi = new Field(intAtt.getId());
        assertFalse(fi.isComplete());
        assertFalse(fi.hasErrors());
        fi = fi.bind(atts);
        assertTrue(fi.isComplete());
        List<Attribute> as = fi.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(intAtt));
        assertThat(fi.getType(), equalTo(DataType.INTEGER));
        record = new Object[]{4};
        assertThat(fi.run(record, null), equalTo(4));
        record = new Object[]{3};
        assertThat(fi.run(record, null), equalTo(3));

        Expression fs = new Field(stringAtt.getId());
        assertFalse(fs.isComplete());
        assertFalse(fs.hasErrors());
        fs = fs.bind(atts);
        assertTrue(fi.isComplete());
        as = fs.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(stringAtt));
        assertThat(fs.getType(), equalTo(DataType.STRING));
        record = new Object[]{"4"};
        assertThat(fs.run(record, null), equalTo("4"));
        record = new Object[]{"3"};
        assertThat(fs.run(record, null), equalTo("3"));

        Expression fb = new Field(boolAtt.getId());
        assertFalse(fb.isComplete());
        assertFalse(fb.hasErrors());
        fb = fb.bind(atts);
        assertTrue(fb.isComplete());
        as = fb.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(boolAtt));
        assertThat(fb.getType(), equalTo(DataType.BOOLEAN));
        record = new Object[]{true};
        assertThat(fb.run(record, null), equalTo(LogicValue.TRUE));
        record = new Object[]{null};
        assertThat(fb.run(record, null), equalTo(Constant.NULL));
        record = new Object[]{false};
        assertThat(fb.run(record, null), equalTo(LogicValue.FALSE));
    }

    @Test
    public void groupTSTest() {
        Expression gts = new GroupTS();
        assertFalse(gts.isComplete());
        assertFalse(gts.hasErrors());
        gts = gts.bind(atts);

        List<Attribute> as = Arrays.asList(new Attribute[]{
                Attribute.TIMESTAMP,
                intAtt
        });

        Buffer b = new ArrayBuffer(0, 1);
        b.add(new Record(as, new Object[]{Instant.now(), 1}));
        b.add(new Record(as, new Object[]{Instant.now(), 2}));
        BufferView view = b.unmodifiableView();

        List<Attribute> atts = gts.getAttributes();
        assertThat(atts.size(), equalTo(1));
        assertTrue(atts.contains(Attribute.TIMESTAMP));
        assertTrue(gts.isComplete());
        assertThat(gts.getType(), equalTo(DataType.TIMESTAMP));
        assertThat(gts.run(null, view), equalTo(view.get(0)[0]));
    }

}
