package org.dei.perla.lang.parser;

import org.dei.perla.core.fpc.DataType;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 * @author Guido Rota 31/07/15.
 */
public class TypeVariableTest {

    @Test
    public void testCreation() {
        TypeVariable v = new TypeVariable(DataType.INTEGER);
        assertThat(v.getType(), equalTo(DataType.INTEGER));

        v = new TypeVariable(DataType.NUMERIC);
        assertThat(v.getType(), equalTo(DataType.NUMERIC));
    }

    @Test
    public void testRestrict() {
        TypeVariable v = new TypeVariable(DataType.INTEGER);
        assertTrue(v.restrict(DataType.INTEGER));
        assertFalse(v.restrict(DataType.FLOAT));
        assertFalse(v.restrict(DataType.STRING));
        assertFalse(v.restrict(DataType.BOOLEAN));
        assertFalse(v.restrict(DataType.TIMESTAMP));
        assertFalse(v.restrict(DataType.ID));

        assertTrue(v.restrict(DataType.ANY));
        assertThat(v.getType(), equalTo(DataType.INTEGER));
        assertTrue(v.restrict(DataType.NUMERIC));
        assertThat(v.getType(), equalTo(DataType.INTEGER));

        v = new TypeVariable(DataType.ANY);
        assertTrue(v.restrict(DataType.INTEGER));
        assertThat(v.getType(), equalTo(DataType.INTEGER));
        v = new TypeVariable(DataType.ANY);
        assertTrue(v.restrict(DataType.FLOAT));
        assertThat(v.getType(), equalTo(DataType.FLOAT));
        v = new TypeVariable(DataType.ANY);
        assertTrue(v.restrict(DataType.BOOLEAN));
        assertThat(v.getType(), equalTo(DataType.BOOLEAN));
        v = new TypeVariable(DataType.ANY);
        assertTrue(v.restrict(DataType.STRING));
        assertThat(v.getType(), equalTo(DataType.STRING));
        v = new TypeVariable(DataType.ANY);
        assertTrue(v.restrict(DataType.TIMESTAMP));
        assertThat(v.getType(), equalTo(DataType.TIMESTAMP));
        v = new TypeVariable(DataType.ANY);
        assertTrue(v.restrict(DataType.ID));
        assertThat(v.getType(), equalTo(DataType.ID));

        v = new TypeVariable(DataType.NUMERIC);
        assertTrue(v.restrict(DataType.INTEGER));
        assertThat(v.getType(), equalTo(DataType.INTEGER));
        v = new TypeVariable(DataType.NUMERIC);
        assertTrue(v.restrict(DataType.FLOAT));
        assertThat(v.getType(), equalTo(DataType.FLOAT));
        v = new TypeVariable(DataType.NUMERIC);
        assertFalse(v.restrict(DataType.BOOLEAN));
        assertThat(v.getType(), equalTo(DataType.NUMERIC));
        v = new TypeVariable(DataType.NUMERIC);
        assertFalse(v.restrict(DataType.STRING));
        assertThat(v.getType(), equalTo(DataType.NUMERIC));
        v = new TypeVariable(DataType.NUMERIC);
        assertFalse(v.restrict(DataType.TIMESTAMP));
        assertThat(v.getType(), equalTo(DataType.NUMERIC));
        v = new TypeVariable(DataType.NUMERIC);
        assertFalse(v.restrict(DataType.ID));
        assertThat(v.getType(), equalTo(DataType.NUMERIC));
    }

}
