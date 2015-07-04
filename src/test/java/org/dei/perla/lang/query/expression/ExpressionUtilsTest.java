package org.dei.perla.lang.query.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 04/07/15.
 */
public class ExpressionUtilsTest {

    private static final Attribute intAtt =
            Attribute.create("integer", DataType.INTEGER);

    private static final Attribute floatAtt =
            Attribute.create("float", DataType.FLOAT);

    private static final Attribute boolAtt =
            Attribute.create("boolean", DataType.BOOLEAN);

    private static final List<Attribute> atts;
    static {
        List<Attribute> as = new ArrayList<>();
        as.add(intAtt);
        as.add(floatAtt);
        as.add(boolAtt);
        atts = Collections.unmodifiableList(as);
    }

    @Test
    public void testIndexOf() {
        assertThat(ExpressionUtils.indexOf("integer", atts), equalTo(0));
        assertThat(ExpressionUtils.indexOf("float", atts), equalTo(1));
        assertThat(ExpressionUtils.indexOf("boolean", atts), equalTo(2));
        assertThat(ExpressionUtils.indexOf("string", atts), equalTo(-1));
    }

    @Test
    public void testGetById() {
        assertThat(ExpressionUtils.getById("integer", atts), equalTo(intAtt));
        assertThat(ExpressionUtils.getById("float", atts), equalTo(floatAtt));
        assertThat(ExpressionUtils.getById("boolean", atts), equalTo(boolAtt));
        assertThat(ExpressionUtils.getById("string", atts), nullValue());
    }

    @Test
    public void testEvaluateConstantNull() {
        Errors err;
        Expression c;
        Object v;

        err = new Errors();
        v = ExpressionUtils.evaluateConstant(Constant.NULL,
                DataType.FLOAT, err);
        assertTrue(err.isEmpty());
        assertThat(v, nullValue());
    }

    @Test
    public void testEvaluateConstantInteger() {
        Errors err;
        Expression c;
        Object v;

        err = new Errors();
        c = Constant.create(12, DataType.INTEGER);
        v = ExpressionUtils.evaluateConstant(c, DataType.INTEGER, err);
        assertTrue(err.isEmpty());
        assertThat(v, notNullValue());
        assertTrue(v instanceof Integer);
        assertThat(v, equalTo(12));

        v = ExpressionUtils.evaluateConstant(c, DataType.FLOAT, err);
        assertTrue(err.isEmpty());
        assertThat(v, notNullValue());
        assertTrue(v instanceof Float);
        assertThat(v, equalTo(12f));

        v = ExpressionUtils.evaluateConstant(c, DataType.STRING, err);
        assertTrue(err.isEmpty());
        assertThat(v, notNullValue());
        assertTrue(v instanceof String);
        assertThat(v, equalTo("12"));

        err = new Errors();
        v = ExpressionUtils.evaluateConstant(c, DataType.BOOLEAN, err);
        assertFalse(err.isEmpty());
        assertThat(v, nullValue());

        err = new Errors();
        v = ExpressionUtils.evaluateConstant(c, DataType.TIMESTAMP, err);
        assertFalse(err.isEmpty());
        assertThat(v, nullValue());

        err = new Errors();
        v = ExpressionUtils.evaluateConstant(c, DataType.ID, err);
        assertFalse(err.isEmpty());
        assertThat(v, nullValue());
    }

    @Test
    public void testEvaluateConstantFloat() {
        Errors err;
        Expression c;
        Object v;

        err = new Errors();
        c = Constant.create(34.5f, DataType.FLOAT);
        v = ExpressionUtils.evaluateConstant(c, DataType.INTEGER, err);
        assertTrue(err.isEmpty());
        assertThat(v, notNullValue());
        assertTrue(v instanceof Integer);
        assertThat(v, equalTo(34));

        v = ExpressionUtils.evaluateConstant(c, DataType.FLOAT, err);
        assertTrue(err.isEmpty());
        assertThat(v, notNullValue());
        assertTrue(v instanceof Float);
        assertThat(v, equalTo(34.5f));

        v = ExpressionUtils.evaluateConstant(c, DataType.STRING, err);
        assertTrue(err.isEmpty());
        assertThat(v, notNullValue());
        assertTrue(v instanceof String);
        assertThat(v, equalTo("34.5"));

        err = new Errors();
        v = ExpressionUtils.evaluateConstant(c, DataType.BOOLEAN, err);
        assertFalse(err.isEmpty());
        assertThat(v, nullValue());

        err = new Errors();
        v = ExpressionUtils.evaluateConstant(c, DataType.TIMESTAMP, err);
        assertFalse(err.isEmpty());
        assertThat(v, nullValue());

        err = new Errors();
        v = ExpressionUtils.evaluateConstant(c, DataType.ID, err);
        assertFalse(err.isEmpty());
        assertThat(v, nullValue());
    }

    @Test
    public void testEvaluateConstantString() {
        Errors err;
        Expression c;
        Object v;

        err = new Errors();
        c = Constant.create("test", DataType.STRING);
        v = ExpressionUtils.evaluateConstant(c, DataType.STRING, err);
        assertTrue(err.isEmpty());
        assertThat(v, notNullValue());
        assertTrue(v instanceof String);
        assertThat(v, equalTo("test"));

        err = new Errors();
        v = ExpressionUtils.evaluateConstant(c, DataType.INTEGER, err);
        assertFalse(err.isEmpty());
        assertThat(v, nullValue());

        err = new Errors();
        v = ExpressionUtils.evaluateConstant(c, DataType.FLOAT, err);
        assertFalse(err.isEmpty());
        assertThat(v, nullValue());

        err = new Errors();
        v = ExpressionUtils.evaluateConstant(c, DataType.BOOLEAN, err);
        assertFalse(err.isEmpty());
        assertThat(v, nullValue());

        err = new Errors();
        v = ExpressionUtils.evaluateConstant(c, DataType.TIMESTAMP, err);
        assertFalse(err.isEmpty());
        assertThat(v, nullValue());

        err = new Errors();
        v = ExpressionUtils.evaluateConstant(c, DataType.ID, err);
        assertFalse(err.isEmpty());
        assertThat(v, nullValue());
    }

    @Test
    public void testEvaluateConstantBoolean() {
        Errors err;
        Expression c;
        Object v;

        err = new Errors();
        c = Constant.create(LogicValue.TRUE, DataType.BOOLEAN);
        v = ExpressionUtils.evaluateConstant(c, DataType.BOOLEAN, err);
        assertTrue(err.isEmpty());
        assertThat(v, notNullValue());
        assertTrue(v instanceof LogicValue);
        assertThat(v, equalTo(LogicValue.TRUE));

        v = ExpressionUtils.evaluateConstant(c, DataType.STRING, err);
        assertTrue(err.isEmpty());
        assertThat(v, notNullValue());
        assertTrue(v instanceof String);
        assertThat(v, equalTo("true"));

        err = new Errors();
        v = ExpressionUtils.evaluateConstant(c, DataType.INTEGER, err);
        assertFalse(err.isEmpty());
        assertThat(v, nullValue());

        err = new Errors();
        v = ExpressionUtils.evaluateConstant(c, DataType.FLOAT, err);
        assertFalse(err.isEmpty());
        assertThat(v, nullValue());

        err = new Errors();
        v = ExpressionUtils.evaluateConstant(c, DataType.TIMESTAMP, err);
        assertFalse(err.isEmpty());
        assertThat(v, nullValue());

        err = new Errors();
        v = ExpressionUtils.evaluateConstant(c, DataType.ID, err);
        assertFalse(err.isEmpty());
        assertThat(v, nullValue());
    }

    @Test
    public void testEvaluateConstantTimestamp() {
        Errors err;
        Expression c;
        Object v;
        Instant now = Instant.now();

        err = new Errors();
        c = Constant.create(now, DataType.TIMESTAMP);
        v = ExpressionUtils.evaluateConstant(c, DataType.TIMESTAMP, err);
        assertTrue(err.isEmpty());
        assertThat(v, notNullValue());
        assertTrue(v instanceof Instant);
        assertThat(v, equalTo(now));

        v = ExpressionUtils.evaluateConstant(c, DataType.STRING, err);
        assertTrue(err.isEmpty());
        assertThat(v, notNullValue());
        assertTrue(v instanceof String);
        assertThat(v, equalTo(now.toString()));

        err = new Errors();
        v = ExpressionUtils.evaluateConstant(c, DataType.INTEGER, err);
        assertFalse(err.isEmpty());
        assertThat(v, nullValue());

        err = new Errors();
        v = ExpressionUtils.evaluateConstant(c, DataType.FLOAT, err);
        assertFalse(err.isEmpty());
        assertThat(v, nullValue());

        err = new Errors();
        v = ExpressionUtils.evaluateConstant(c, DataType.BOOLEAN, err);
        assertFalse(err.isEmpty());
        assertThat(v, nullValue());

        err = new Errors();
        v = ExpressionUtils.evaluateConstant(c, DataType.ID, err);
        assertFalse(err.isEmpty());
        assertThat(v, nullValue());
    }

    @Test
    public void testEvaluateConstantId() {
        Errors err;
        Expression c;
        Object v;

        err = new Errors();
        c = Constant.create(4245, DataType.ID);
        v = ExpressionUtils.evaluateConstant(c, DataType.ID, err);
        assertTrue(err.isEmpty());
        assertThat(v, notNullValue());
        assertTrue(v instanceof Integer);
        assertThat(v, equalTo(4245));

        v = ExpressionUtils.evaluateConstant(c, DataType.STRING, err);
        assertTrue(err.isEmpty());
        assertThat(v, notNullValue());
        assertTrue(v instanceof String);
        assertThat(v, equalTo("4245"));

        err = new Errors();
        v = ExpressionUtils.evaluateConstant(c, DataType.INTEGER, err);
        assertFalse(err.isEmpty());
        assertThat(v, nullValue());

        err = new Errors();
        v = ExpressionUtils.evaluateConstant(c, DataType.FLOAT, err);
        assertFalse(err.isEmpty());
        assertThat(v, nullValue());

        err = new Errors();
        v = ExpressionUtils.evaluateConstant(c, DataType.BOOLEAN, err);
        assertFalse(err.isEmpty());
        assertThat(v, nullValue());

        err = new Errors();
        v = ExpressionUtils.evaluateConstant(c, DataType.TIMESTAMP, err);
        assertFalse(err.isEmpty());
        assertThat(v, nullValue());
    }

}
