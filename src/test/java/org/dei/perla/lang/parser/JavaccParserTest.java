package org.dei.perla.lang.parser;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.StringReader;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Guido Rota 04/03/15.
 */
public class JavaccParserTest {

    @Test
    public void testIntegerConstant() throws Exception {
        JavaccParser p = new JavaccParser(new StringReader("12"));
        int value = p.ConstantInteger();
        assertThat(value, equalTo(12));

        p = new JavaccParser(new StringReader("45"));
        value = p.ConstantInteger();
        assertThat(value, equalTo(45));

        p = new JavaccParser(new StringReader("0x12"));
        value = p.ConstantInteger();
        assertThat(value, equalTo(18));
    }

    @Test
    public void testFloatConstant() throws Exception {
        JavaccParser p = new JavaccParser(new StringReader("12.0"));
        float value = p.ConstantFloat();
        assertThat(value, equalTo(12f));

        p = new JavaccParser(new StringReader("43.9586"));
        value = p.ConstantFloat();
        assertThat(value, equalTo(43.9586f));
    }

}
