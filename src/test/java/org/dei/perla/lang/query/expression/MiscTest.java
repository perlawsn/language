package org.dei.perla.lang.query.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Guido Rota 23/02/15.
 */
public class MiscTest {

    @Test
    public void castFloat() {
        Expression cInt = Constant.create(1, DataType.INTEGER);

        Expression e = new CastFloat(cInt);
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertThat(e.run(null, null), equalTo(1f));
    }

    @Test
    public void castFloatNull() {
        Expression cast = new CastFloat(Constant.NULL);
        assertThat(cast.run(null, null), equalTo(null));
    }

    @Test
    public void castInteger() {
        Expression cFloat = Constant.create(1.2f, DataType.FLOAT);

        Expression e = new CastInteger(cFloat);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(e.run(null, null), equalTo(1));
    }

    @Test
    public void castIntegerNull() {
        Expression cast = new CastInteger(Constant.NULL);
        assertThat(cast.run(null, null), equalTo(null));
    }

    @Test
    public void constantTest() {
        Expression e = Constant.create(1, DataType.INTEGER);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(e.run(null, null), equalTo(1));

        e = Constant.create("test", DataType.STRING);
        assertThat(e.getType(), equalTo(DataType.STRING));
        assertThat(e.run(null, null), equalTo("test"));

        e = Constant.UNKNOWN;
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void constantNullTest() {
        Expression e = Constant.NULL;
        assertThat(e.getType(), nullValue());
        assertThat(((Constant) e).getValue(), nullValue());

        e = Constant.create(null, DataType.INTEGER);
        assertThat(((Constant) e).getValue(), nullValue());
        assertThat(e, equalTo(Constant.NULL));

        e = Constant.create(null, DataType.FLOAT);
        assertThat(((Constant) e).getValue(), nullValue());
        assertThat(e, equalTo(Constant.NULL));

        e = Constant.create(null, DataType.STRING);
        assertThat(((Constant) e).getValue(), nullValue());
        assertThat(e, equalTo(Constant.NULL));

        e = Constant.create(null, DataType.BOOLEAN);
        assertThat(((Constant) e).getValue(), nullValue());
        assertThat(e, equalTo(Constant.NULL));

        e = Constant.create(null, DataType.TIMESTAMP);
        assertThat(((Constant) e).getValue(), nullValue());
        assertThat(e, equalTo(Constant.NULL));

        e = Constant.create(null, DataType.ID);
        assertThat(((Constant) e).getValue(), nullValue());
        assertThat(e, equalTo(Constant.NULL));
    }

    @Test
    public void testConstantLogic() {
        Expression e = Constant.TRUE;
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.TRUE));

        e = Constant.create(LogicValue.TRUE, DataType.BOOLEAN);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.TRUE));
        assertThat(e, equalTo(Constant.TRUE));

        e = Constant.FALSE;
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.FALSE));

        e = Constant.create(LogicValue.FALSE, DataType.BOOLEAN);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.FALSE));
        assertThat(e, equalTo(Constant.FALSE));

        e = Constant.UNKNOWN;
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.UNKNOWN));

        e = Constant.create(LogicValue.UNKNOWN, DataType.BOOLEAN);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(((Constant) e).getValue(), equalTo(LogicValue.UNKNOWN));
        assertThat(e, equalTo(Constant.UNKNOWN));
    }

    @Test
    public void testConstantInteger() {
        Expression e = Constant.INTEGER_0;
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), equalTo(0));

        e = Constant.create(0, DataType.INTEGER);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), equalTo(0));
        assertThat(e, equalTo(Constant.INTEGER_0));

        e = Constant.INTEGER_1;
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), equalTo(1));

        e = Constant.create(1, DataType.INTEGER);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), equalTo(1));
        assertThat(e, equalTo(Constant.INTEGER_1));

        e = Constant.INTEGER_2;
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), equalTo(2));

        e = Constant.create(2, DataType.INTEGER);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(((Constant) e).getValue(), equalTo(2));
        assertThat(e, equalTo(Constant.INTEGER_2));
    }

    @Test
    public void fieldTest() {
        Attribute intAtt = Attribute.create("integer", DataType.INTEGER);
        Attribute floatAtt = Attribute.create("float", DataType.FLOAT);
        Attribute stringAtt = Attribute.create("string", DataType.STRING);
        Object[][] sample = new Object[][]{
                {1, 2.3f, "test"},
                {23, 2.4f, "tset"}
        };

        Expression e = new Field(intAtt.getId(), intAtt.getType(), 0);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertThat(e.run(sample[0], null), equalTo(1));
        assertThat(e.run(sample[1], null), equalTo(23));

        e = new Field(floatAtt.getId(), floatAtt.getType(), 1);
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertThat(e.run(sample[0], null), equalTo(2.3f));
        assertThat(e.run(sample[1], null), equalTo(2.4f));

        e = new Field(stringAtt.getId(), stringAtt.getType(), 2);
        assertThat(e.getType(), equalTo(DataType.STRING));
        assertThat(e.run(sample[0], null), equalTo("test"));
        assertThat(e.run(sample[1], null), equalTo("tset"));
    }

}
