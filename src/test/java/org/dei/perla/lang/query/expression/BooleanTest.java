package org.dei.perla.lang.query.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.query.BindingException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
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

    private static Attribute intAtt =
            Attribute.create("integer", DataType.INTEGER);
    private static Attribute boolAtt =
            Attribute.create("boolean", DataType.BOOLEAN);

    private static final List<Attribute> atts;
    static {
        atts = Arrays.asList(new Attribute[] {
                intAtt,
                boolAtt
        });
    }

    @Test
    public void testNOT() {
        Errors err = new Errors();
        Expression e = Bool.createNOT(trueExpr, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertTrue(res instanceof LogicValue);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = Bool.createNOT(falseExpr, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertTrue(res instanceof LogicValue);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = Bool.createNOT(unknownExpr, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertTrue(res instanceof LogicValue);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testNOTUnknown() {
        Errors err = new Errors();
        Expression e = Bool.createNOT(Constant.NULL, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testNOTIncomplete() {
        Errors err = new Errors();

        Expression e = Bool.createNOT(new Field("boolean"), err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testNOTBind() throws BindingException {
        Errors err = new Errors();
        Expression f = new Field("boolean");

        Expression e = Bool.createNOT(f, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(boolAtt));
        assertTrue(e.isComplete());
    }

    @Test
    public void testAND() {
        Errors err = new Errors();

        Expression e = Bool.createAND(trueExpr, trueExpr, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertTrue(res instanceof LogicValue);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = Bool.createAND(falseExpr, trueExpr, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertTrue(res instanceof LogicValue);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = Bool.createAND(trueExpr, falseExpr, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertTrue(res instanceof LogicValue);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = Bool.createAND(falseExpr, falseExpr, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertTrue(res instanceof LogicValue);
        assertThat(res, equalTo(LogicValue.FALSE));
    }

    @Test
    public void testANDUnknown() {
        Errors err = new Errors();
        Expression nul = Constant.NULL;

        Expression e = Bool.createAND(trueExpr, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Bool.createAND(falseExpr, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = Bool.createAND(nul, trueExpr, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Bool.createAND(nul, falseExpr, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = Bool.createAND(nul, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testANDIncomplete() {
        Errors err = new Errors();
        Expression c = Constant.create(LogicValue.TRUE, DataType.BOOLEAN);

        Expression e = Bool.createAND(c, new Field("boolean"), err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Bool.createAND(new Field("boolean"), c, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testANDBind() throws BindingException {
        Errors err = new Errors();
        Field f = new Field("boolean");

        Expression e = Bool.createAND(trueExpr, f, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(boolAtt));
        assertTrue(e.isComplete());

        e = Bool.createAND(f, trueExpr, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(boolAtt));
        assertTrue(e.isComplete());

        e = Bool.createAND(f, f, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(boolAtt));
        assertTrue(e.isComplete());
    }

    @Test
    public void testOR() {
        Errors err = new Errors();

        Expression e = Bool.createOR(trueExpr, trueExpr, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertTrue(res instanceof LogicValue);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = Bool.createOR(falseExpr, trueExpr, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertTrue(res instanceof LogicValue);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = Bool.createOR(trueExpr, falseExpr, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertTrue(res instanceof LogicValue);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = Bool.createOR(falseExpr, falseExpr, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertTrue(res instanceof LogicValue);
        assertThat(res, equalTo(LogicValue.FALSE));
    }

    @Test
    public void testORUnknown() {
        Errors err = new Errors();
        Expression nul = Constant.NULL;

        Expression e = Bool.createOR(trueExpr, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = Bool.createOR(falseExpr, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Bool.createOR(nul, trueExpr, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = Bool.createOR(nul, falseExpr, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Bool.createOR(nul, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testORIncomplete() {
        Errors err = new Errors();
        Expression c = Constant.create(LogicValue.FALSE, DataType.BOOLEAN);

        Expression e = Bool.createOR(c, new Field("boolean"), err);
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Bool.createOR(new Field("boolean"), c, err);
        assertFalse(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        c = Constant.create(LogicValue.TRUE, DataType.BOOLEAN);

        e = Bool.createOR(c, new Field("boolean"), err);
        assertFalse(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = Bool.createOR(new Field("boolean"), c, err);
        assertFalse(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));
    }

    @Test
    public void testORBind() throws BindingException {
        Errors err = new Errors();
        Field f = new Field("boolean");

        Expression e = Bool.createOR(trueExpr, f, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(boolAtt));
        assertTrue(e.isComplete());

        e = Bool.createOR(f, trueExpr, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(boolAtt));
        assertTrue(e.isComplete());

        e = Bool.createOR(f, f, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(boolAtt));
        assertTrue(e.isComplete());
    }

    @Test
    public void testXOR() {
        Errors err = new Errors();

        Expression e = Bool.createXOR(trueExpr, trueExpr, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertTrue(res instanceof LogicValue);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = Bool.createXOR(falseExpr, trueExpr, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertTrue(res instanceof LogicValue);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = Bool.createXOR(trueExpr, falseExpr, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertTrue(res instanceof LogicValue);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = Bool.createXOR(falseExpr, falseExpr, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertTrue(res instanceof LogicValue);
        assertThat(res, equalTo(LogicValue.FALSE));
    }

    @Test
    public void testXORUnknown() {
        Errors err = new Errors();
        Expression nul = Constant.NULL;

        Expression e = Bool.createXOR(trueExpr, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Bool.createXOR(falseExpr, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Bool.createXOR(nul, trueExpr, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Bool.createXOR(nul, falseExpr, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Bool.createXOR(nul, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testXORIncomplete() {
        Errors err = new Errors();
        Expression c = Constant.create(LogicValue.TRUE, DataType.BOOLEAN);

        Expression e = Bool.createXOR(c, new Field("boolean"), err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Bool.createXOR(new Field("boolean"), c, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testXORBind() throws BindingException {
        Errors err = new Errors();
        Field f = new Field("boolean");

        Expression e = Bool.createXOR(trueExpr, f, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(boolAtt));
        assertTrue(e.isComplete());

        e = Bool.createXOR(f, trueExpr, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(boolAtt));
        assertTrue(e.isComplete());

        e = Bool.createXOR(f, f, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(boolAtt));
        assertTrue(e.isComplete());
    }

}
