package org.dei.perla.lang.parser;

import org.dei.perla.lang.parser.expression.NullNode;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.StringReader;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 04/03/15.
 */
public class JavaccParserTest {

    @Test
    public void testSign() throws Exception {
        JavaccParser p = new JavaccParser(new StringReader("+"));
        Sign s = p.Sign();
        assertThat(s, equalTo(Sign.PLUS));

        p = new JavaccParser(new StringReader("-"));
        s = p.Sign();
        assertThat(s, equalTo(Sign.MINUS));
    }

    @Test
    public void testLogicValue() throws Exception {
        JavaccParser p = new JavaccParser(new StringReader("true"));
        LogicValue l = p.LogicValue();
        assertThat(l, equalTo(LogicValue.TRUE));

        p = new JavaccParser(new StringReader("false"));
        l = p.LogicValue();
        assertThat(l, equalTo(LogicValue.FALSE));

        p = new JavaccParser(new StringReader("unknown"));
        l = p.LogicValue();
        assertThat(l, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testConstantBoolean() throws Exception {
        JavaccParser p = new JavaccParser(new StringReader("true"));
        boolean b = p.ConstantBoolean();
        assertTrue(b);

        p = new JavaccParser(new StringReader("false"));
        b = p.ConstantBoolean();
        assertFalse(b);
    }

    @Test
    public void testConstantFloat() throws Exception {
        JavaccParser p = new JavaccParser(new StringReader("12.0"));
        float f = p.ConstantFloat();
        assertThat(f, equalTo(12f));

        p = new JavaccParser(new StringReader("43.9586"));
        f = p.ConstantFloat();
        assertThat(f, equalTo(43.9586f));
    }

    @Test
    public void testConstantInteger() throws Exception {
        JavaccParser p = new JavaccParser(new StringReader("12"));
        int i = p.ConstantInteger();
        assertThat(i, equalTo(12));

        p = new JavaccParser(new StringReader("45"));
        i = p.ConstantInteger();
        assertThat(i, equalTo(45));

        p = new JavaccParser(new StringReader("0x12"));
        i = p.ConstantInteger();
        assertThat(i, equalTo(18));
    }

    @Test
    public void testConstantString() throws Exception {
        JavaccParser p = new JavaccParser(new StringReader("\"test\""));
        String s = p.ConstantString();
        assertThat(s, equalTo("test"));

        p = new JavaccParser(new StringReader("'test'"));
        s = p.ConstantString();
        assertThat(s, equalTo("test"));
    }

}
