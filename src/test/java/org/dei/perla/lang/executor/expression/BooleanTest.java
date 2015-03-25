package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

/**
 * @author Guido Rota 02/03/15.
 */
public class BooleanTest {

    private static final Expression trueExpr =
            Constant.TRUE;
    private static final Expression falseExpr =
            Constant.FALSE;
    private static final Expression unknownExpr =
            Constant.UNKNOWN;

    private static List<Attribute> atts;

    private static Attribute intAtt =
            Attribute.create("integer", DataType.INTEGER);
    private static Attribute boolAtt =
            Attribute.create("boolean", DataType.BOOLEAN);

    @BeforeClass
    public static void setup() {
        atts = new ArrayList<>();
        atts.add(intAtt);
        atts.add(boolAtt);
    }

    @Test
    public void testNOT() {
        Expression e = Bool.createNOT(trueExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertTrue(res instanceof LogicValue);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = Bool.createNOT(falseExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertTrue(res instanceof LogicValue);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = Bool.createNOT(unknownExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertTrue(res instanceof LogicValue);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testNOTUnknown() {
        Expression e = Bool.createNOT(Constant.NULL_BOOLEAN);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testNOTError() {
        Expression err = new ErrorExpression("test");

        Expression e = Bool.createNOT(err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

    @Test
    public void testNOTBind() {
        Expression f = new Field("boolean");

        Expression e = Bool.createNOT(f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        List<Attribute> atts = e.getAttributes();
        assertThat(atts.size(), equalTo(1));
        assertTrue(atts.contains(boolAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

    @Test
    public void testAND() {
        Expression e = Bool.createAND(trueExpr, trueExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertTrue(res instanceof LogicValue);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = Bool.createAND(falseExpr, trueExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertTrue(res instanceof LogicValue);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = Bool.createAND(trueExpr, falseExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertTrue(res instanceof LogicValue);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = Bool.createAND(falseExpr, falseExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertTrue(res instanceof LogicValue);
        assertThat(res, equalTo(LogicValue.FALSE));
    }

    @Test
    public void testANDUnknown() {
        Expression nul = Constant.NULL_BOOLEAN;

        Expression e = Bool.createAND(trueExpr, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Bool.createAND(falseExpr, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = Bool.createAND(nul, trueExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Bool.createAND(nul, falseExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = Bool.createAND(nul, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testANDError() {
        Expression err = new ErrorExpression("test");
        Expression c = Constant.create(85, DataType.INTEGER);

        Expression e = Bool.createAND(err, c);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Bool.createAND(c, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Bool.createAND(err, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

    @Test
    public void testANDBind() {
        Field f = new Field("boolean");

        Expression e = Bool.createAND(trueExpr, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        List<Attribute> atts = e.getAttributes();
        assertThat(atts.size(), equalTo(1));
        assertTrue(atts.contains(boolAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Bool.createAND(f, trueExpr);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        atts = e.getAttributes();
        assertThat(atts.size(), equalTo(1));
        assertTrue(atts.contains(boolAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Bool.createAND(f, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        atts = e.getAttributes();
        assertThat(atts.size(), equalTo(1));
        assertTrue(atts.contains(boolAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

    @Test
    public void testOR() {
        Expression e = Bool.createOR(trueExpr, trueExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertTrue(res instanceof LogicValue);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = Bool.createOR(falseExpr, trueExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertTrue(res instanceof LogicValue);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = Bool.createOR(trueExpr, falseExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertTrue(res instanceof LogicValue);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = Bool.createOR(falseExpr, falseExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertTrue(res instanceof LogicValue);
        assertThat(res, equalTo(LogicValue.FALSE));
    }

    @Test
    public void testORUnknown() {
        Expression nul = Constant.NULL_BOOLEAN;

        Expression e = Bool.createOR(trueExpr, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = Bool.createOR(falseExpr, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Bool.createOR(nul, trueExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = Bool.createOR(nul, falseExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Bool.createOR(nul, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testORError() {
        Expression err = new ErrorExpression("test");
        Expression c = Constant.create(85, DataType.INTEGER);

        Expression e = Bool.createOR(err, c);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Bool.createOR(c, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Bool.createOR(err, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

    @Test
    public void testORBind() {
        Field f = new Field("boolean");

        Expression e = Bool.createOR(trueExpr, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        List<Attribute> atts = e.getAttributes();
        assertThat(atts.size(), equalTo(1));
        assertTrue(atts.contains(boolAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Bool.createOR(f, trueExpr);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        atts = e.getAttributes();
        assertThat(atts.size(), equalTo(1));
        assertTrue(atts.contains(boolAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Bool.createOR(f, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        atts = e.getAttributes();
        assertThat(atts.size(), equalTo(1));
        assertTrue(atts.contains(boolAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

    @Test
    public void testXOR() {
        Expression e = Bool.createXOR(trueExpr, trueExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertTrue(res instanceof LogicValue);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = Bool.createXOR(falseExpr, trueExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertTrue(res instanceof LogicValue);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = Bool.createXOR(trueExpr, falseExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertTrue(res instanceof LogicValue);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = Bool.createXOR(falseExpr, falseExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertTrue(res instanceof LogicValue);
        assertThat(res, equalTo(LogicValue.FALSE));
    }

    @Test
    public void testXORUnknown() {
        Expression nul = Constant.NULL_BOOLEAN;

        Expression e = Bool.createXOR(trueExpr, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Bool.createXOR(falseExpr, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Bool.createXOR(nul, trueExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Bool.createXOR(nul, falseExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Bool.createXOR(nul, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testXORError() {
        Expression err = new ErrorExpression("test");
        Expression c = Constant.create(85, DataType.INTEGER);

        Expression e = Bool.createXOR(err, c);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Bool.createXOR(c, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Bool.createXOR(err, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

    @Test
    public void testXORBind() {
        Field f = new Field("boolean");

        Expression e = Bool.createXOR(trueExpr, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        List<Attribute> atts = e.getAttributes();
        assertThat(atts.size(), equalTo(1));
        assertTrue(atts.contains(boolAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Bool.createXOR(f, trueExpr);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        atts = e.getAttributes();
        assertThat(atts.size(), equalTo(1));
        assertTrue(atts.contains(boolAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Bool.createXOR(f, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        atts = e.getAttributes();
        assertThat(atts.size(), equalTo(1));
        assertTrue(atts.contains(boolAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

}
