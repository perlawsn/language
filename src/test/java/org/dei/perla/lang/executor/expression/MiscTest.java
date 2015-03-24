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
import java.util.Set;

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
    private static Attribute boolAtt =
            Attribute.create("boolean", DataType.BOOLEAN);

    private static List<Attribute> atts;

    private static BufferView view;

    @BeforeClass
    public static void setupBuffer() {
        Attribute[] as = new Attribute[] {
                Attribute.TIMESTAMP,
                integerAtt,
                stringAtt,
                floatAtt,
                boolAtt
        };
        atts = Arrays.asList(as);

        Buffer b = new ArrayBuffer(0, 512);
        b.add(new Record(atts, new Object[]{Instant.now(), 0, "0", 0.0f, true}));
        b.add(new Record(atts, new Object[]{Instant.now(), 1, "1", 1.1f, false}));
        b.add(new Record(atts, new Object[]{Instant.now(), 2, "2", 2.2f, false}));
        b.add(new Record(atts, new Object[]{Instant.now(), 3, "3", 3.3f, null}));
        b.add(new Record(atts, new Object[]{Instant.now(), 4, "4", 4.4f, true}));

        view = b.unmodifiableView();
    }

    @Test
    public void castFloat() {
        Expression cInt = Constant.create(1, DataType.INTEGER);
        Expression cFloat = Constant.create(1.2f, DataType.FLOAT);
        Expression fFloat = new Field(floatAtt.getId()).bind(atts);
        Expression incomplete = new Field("float");
        Expression error = new ErrorExpression("test");

        Expression cast = CastFloat.create(cInt);
        assertTrue(cast.isComplete());
        assertFalse(cast.hasErrors());
        assertTrue(cast.getFields().isEmpty());
        assertThat(cast.getType(), equalTo(DataType.FLOAT));
        assertThat(cast.run(view.get(0), view), equalTo(1f));

        cast = CastFloat.create(cFloat);
        assertTrue(cast.isComplete());
        assertFalse(cast.hasErrors());
        assertTrue(cast.getFields().isEmpty());
        assertThat(cast.getType(), equalTo(DataType.FLOAT));
        assertThat(cast.run(view.get(0), view), equalTo(1.2f));

        cast = CastFloat.create(fFloat);
        assertTrue(cast.isComplete());
        assertFalse(cast.hasErrors());
        Set<String> fields = cast.getFields();
        assertThat(fields.size(), equalTo(1));
        assertTrue(fields.contains("float"));
        assertThat(cast.getType(), equalTo(DataType.FLOAT));
        assertThat(cast.run(view.get(0), view), equalTo(4.4f));
        assertThat(cast.run(view.get(1), view), equalTo(3.3f));

        cast = CastFloat.create(incomplete);
        assertFalse(cast.isComplete());
        assertFalse(cast.hasErrors());
        cast = cast.bind(atts);
        assertTrue(cast.isComplete());
        fields = cast.getFields();
        assertThat(fields.size(), equalTo(1));
        assertTrue(fields.contains("float"));
        assertThat(cast.run(view.get(0), view), equalTo(4.4f));

        cast = CastFloat.create(error);
        assertTrue(cast.isComplete());
        assertTrue(cast.hasErrors());
    }

    @Test
    public void castInteger() {
        Expression cInt = Constant.create(1, DataType.INTEGER);
        Expression cFloat = Constant.create(1.2f, DataType.FLOAT);
        Expression fFloat = new Field(floatAtt.getId()).bind(atts);
        Expression incomplete = new Field("integer");
        Expression error = new ErrorExpression("test");

        Expression cast = CastInteger.create(cInt);
        assertTrue(cast.isComplete());
        assertFalse(cast.hasErrors());
        assertTrue(cast.getFields().isEmpty());
        assertThat(cast.getType(), equalTo(DataType.INTEGER));
        assertThat(cast.run(view.get(0), view), equalTo(1));

        cast = CastInteger.create(cFloat);
        assertTrue(cast.isComplete());
        assertFalse(cast.hasErrors());
        assertTrue(cast.getFields().isEmpty());
        assertThat(cast.getType(), equalTo(DataType.INTEGER));
        assertThat(cast.run(view.get(0), view), equalTo(1));

        cast = CastInteger.create(fFloat);
        assertTrue(cast.isComplete());
        assertFalse(cast.hasErrors());
        Set<String> fields = cast.getFields();
        assertThat(fields.size(), equalTo(1));
        assertTrue(fields.contains("float"));
        assertThat(cast.getType(), equalTo(DataType.INTEGER));
        assertThat(cast.run(view.get(0), view), equalTo(4));
        assertThat(cast.run(view.get(1), view), equalTo(3));

        cast = CastInteger.create(incomplete);
        assertFalse(cast.isComplete());
        assertFalse(cast.hasErrors());
        cast = cast.bind(atts);
        assertTrue(cast.isComplete());
        fields = cast.getFields();
        assertThat(fields.size(), equalTo(1));
        assertTrue(fields.contains("integer"));
        assertThat(cast.run(view.get(0), view), equalTo(4));

        cast = CastInteger.create(error);
        assertTrue(cast.isComplete());
        assertTrue(cast.hasErrors());
    }

    @Test
    public void constantTest() {
        Expression c1 = Constant.create(1, DataType.INTEGER);
        assertTrue(c1.isComplete());
        assertFalse(c1.hasErrors());
        assertTrue(c1.getFields().isEmpty());
        assertThat(c1.getType(), equalTo(DataType.INTEGER));
        assertThat(c1.run(view.get(0), view), equalTo(1));

        Expression c2 = Constant.create("test", DataType.STRING);
        assertTrue(c2.isComplete());
        assertFalse(c2.hasErrors());
        assertTrue(c2.getFields().isEmpty());
        assertThat(c2.getType(), equalTo(DataType.STRING));
        assertThat(c2.run(view.get(0), view), equalTo("test"));

        Constant u = Constant.UNKNOWN;
        assertTrue(u.isComplete());
        assertFalse(u.hasErrors());
        assertTrue(u.getFields().isEmpty());
        assertThat(u.getType(), equalTo(DataType.BOOLEAN));
        assertThat(u.run(null, null), equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void constantNullTest() {
        Expression e = Constant.NULL_INTEGER;
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getFields().isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), nullValue());

        e = Constant.create(null, DataType.INTEGER);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getFields().isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), nullValue());
        assertThat(e, equalTo(Constant.NULL_INTEGER));

        e = Constant.NULL_FLOAT;
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getFields().isEmpty());
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertThat(((Constant) e).getValue(), nullValue());

        e = Constant.create(null, DataType.FLOAT);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getFields().isEmpty());
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertThat(((Constant) e).getValue(), nullValue());
        assertThat(e, equalTo(Constant.NULL_FLOAT));

        e = Constant.NULL_STRING;
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getFields().isEmpty());
        assertThat(e.getType(), equalTo(DataType.STRING));
        assertThat(((Constant) e).getValue(), nullValue());

        e = Constant.create(null, DataType.STRING);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getFields().isEmpty());
        assertThat(e.getType(), equalTo(DataType.STRING));
        assertThat(((Constant) e).getValue(), nullValue());
        assertThat(e, equalTo(Constant.NULL_STRING));

        e = Constant.NULL_BOOLEAN;
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getFields().isEmpty());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), nullValue());

        e = Constant.create(null, DataType.BOOLEAN);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getFields().isEmpty());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), nullValue());
        assertThat(e, equalTo(Constant.NULL_BOOLEAN));

        e = Constant.NULL_TIMESTAMP;
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getFields().isEmpty());
        assertThat(e.getType(), equalTo(DataType.TIMESTAMP));
        assertThat(((Constant) e).getValue(), nullValue());

        e = Constant.create(null, DataType.TIMESTAMP);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getFields().isEmpty());
        assertThat(e.getType(), equalTo(DataType.TIMESTAMP));
        assertThat(((Constant) e).getValue(), nullValue());
        assertThat(e, equalTo(Constant.NULL_TIMESTAMP));

        e = Constant.NULL_ID;
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getFields().isEmpty());
        assertThat(e.getType(), equalTo(DataType.ID));
        assertThat(((Constant) e).getValue(), nullValue());

        e = Constant.create(null, DataType.ID);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getFields().isEmpty());
        assertThat(e.getType(), equalTo(DataType.ID));
        assertThat(((Constant) e).getValue(), nullValue());
        assertThat(e, equalTo(Constant.NULL_ID));
    }

    @Test
    public void testConstantLogic() {
        Expression e = Constant.TRUE;
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getFields().isEmpty());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.TRUE));

        e = Constant.create(LogicValue.TRUE, DataType.BOOLEAN);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getFields().isEmpty());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.TRUE));
        assertThat(e, equalTo(Constant.TRUE));

        e = Constant.FALSE;
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getFields().isEmpty());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.FALSE));

        e = Constant.create(LogicValue.FALSE, DataType.BOOLEAN);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getFields().isEmpty());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.FALSE));
        assertThat(e, equalTo(Constant.FALSE));

        e = Constant.UNKNOWN;
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getFields().isEmpty());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.UNKNOWN));

        e = Constant.create(LogicValue.UNKNOWN, DataType.BOOLEAN);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getFields().isEmpty());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.UNKNOWN));
        assertThat(e, equalTo(Constant.UNKNOWN));
    }

    @Test
    public void testConstantInteger() {
        Expression e = Constant.INTEGER_0;
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getFields().isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), equalTo(0));

        e = Constant.create(0, DataType.INTEGER);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getFields().isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), equalTo(0));
        assertThat(e, equalTo(Constant.INTEGER_0));

        e = Constant.INTEGER_1;
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getFields().isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), equalTo(1));

        e = Constant.create(1, DataType.INTEGER);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getFields().isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), equalTo(1));
        assertThat(e, equalTo(Constant.INTEGER_1));

        e = Constant.INTEGER_2;
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getFields().isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), equalTo(2));

        e = Constant.create(2, DataType.INTEGER);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertTrue(e.getFields().isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), equalTo(2));
        assertThat(e, equalTo(Constant.INTEGER_2));
    }

    @Test
    public void fieldTest() {
        Expression fi = new Field(integerAtt.getId());
        assertFalse(fi.isComplete());
        assertFalse(fi.hasErrors());
        fi = fi.bind(atts);
        assertTrue(fi.isComplete());
        Set<String> fields = fi.getFields();
        assertThat(fields.size(), equalTo(1));
        assertTrue(fields.contains("integer"));
        assertThat(fi.getType(), equalTo(DataType.INTEGER));
        assertThat(fi.run(view.get(0), view), equalTo(4));
        assertThat(fi.run(view.get(1), view), equalTo(3));

        Expression fs = new Field(stringAtt.getId());
        assertFalse(fs.isComplete());
        assertFalse(fs.hasErrors());
        fs = fs.bind(atts);
        assertTrue(fi.isComplete());
        fields = fs.getFields();
        assertThat(fields.size(), equalTo(1));
        assertTrue(fields.contains("string"));
        assertThat(fs.getType(), equalTo(DataType.STRING));
        assertThat(fs.run(view.get(0), view), equalTo("4"));
        assertThat(fs.run(view.get(1), view), equalTo("3"));

        Expression fb = new Field(boolAtt.getId());
        assertFalse(fb.isComplete());
        assertFalse(fb.hasErrors());
        fb = fb.bind(atts);
        assertTrue(fb.isComplete());
        fields = fb.getFields();
        assertThat(fields.size(), equalTo(1));
        assertTrue(fields.contains("boolean"));
        assertThat(fb.getType(), equalTo(DataType.BOOLEAN));
        assertThat(fb.run(view.get(0), view), equalTo(LogicValue.TRUE));
        assertThat(fb.run(view.get(1), view), nullValue());
        assertThat(fb.run(view.get(2), view), equalTo(LogicValue.FALSE));
    }

    @Test
    public void groupTSTest() {
        Expression gts = new GroupTS();
        assertFalse(gts.isComplete());
        assertFalse(gts.hasErrors());
        gts = gts.bind(atts);
        Set<String> fields = gts.getFields();
        assertThat(fields.size(), equalTo(1));
        assertTrue(fields.contains("timestamp"));
        assertTrue(gts.isComplete());
        assertThat(gts.getType(), equalTo(DataType.TIMESTAMP));
        assertThat(gts.run(null, view), equalTo(view.get(0)[0]));
    }

}
