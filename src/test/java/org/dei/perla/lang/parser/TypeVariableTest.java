package org.dei.perla.lang.parser;

import org.dei.perla.core.registry.TypeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 * @author Guido Rota 31/07/15.
 */
public class TypeVariableTest {

    @Test
    public void testCreation() {
        TypeVariable v = new TypeVariable(TypeClass.INTEGER);
        assertThat(v.getTypeClass(), equalTo(TypeClass.INTEGER));

        v = new TypeVariable(TypeClass.NUMERIC);
        assertThat(v.getTypeClass(), equalTo(TypeClass.NUMERIC));
    }

    @Test
    public void testRestrict() {
        TypeVariable v = new TypeVariable(TypeClass.INTEGER);
        assertTrue(v.restrict(TypeClass.INTEGER));
        assertFalse(v.restrict(TypeClass.FLOAT));
        assertFalse(v.restrict(TypeClass.STRING));
        assertFalse(v.restrict(TypeClass.BOOLEAN));
        assertFalse(v.restrict(TypeClass.TIMESTAMP));
        assertFalse(v.restrict(TypeClass.ID));

        assertTrue(v.restrict(TypeClass.ANY));
        assertThat(v.getTypeClass(), equalTo(TypeClass.INTEGER));
        assertTrue(v.restrict(TypeClass.NUMERIC));
        assertThat(v.getTypeClass(), equalTo(TypeClass.INTEGER));

        v = new TypeVariable(TypeClass.ANY);
        assertTrue(v.restrict(TypeClass.INTEGER));
        assertThat(v.getTypeClass(), equalTo(TypeClass.INTEGER));
        v = new TypeVariable(TypeClass.ANY);
        assertTrue(v.restrict(TypeClass.FLOAT));
        assertThat(v.getTypeClass(), equalTo(TypeClass.FLOAT));
        v = new TypeVariable(TypeClass.ANY);
        assertTrue(v.restrict(TypeClass.BOOLEAN));
        assertThat(v.getTypeClass(), equalTo(TypeClass.BOOLEAN));
        v = new TypeVariable(TypeClass.ANY);
        assertTrue(v.restrict(TypeClass.STRING));
        assertThat(v.getTypeClass(), equalTo(TypeClass.STRING));
        v = new TypeVariable(TypeClass.ANY);
        assertTrue(v.restrict(TypeClass.TIMESTAMP));
        assertThat(v.getTypeClass(), equalTo(TypeClass.TIMESTAMP));
        v = new TypeVariable(TypeClass.ANY);
        assertTrue(v.restrict(TypeClass.ID));
        assertThat(v.getTypeClass(), equalTo(TypeClass.ID));

        v = new TypeVariable(TypeClass.NUMERIC);
        assertTrue(v.restrict(TypeClass.INTEGER));
        assertThat(v.getTypeClass(), equalTo(TypeClass.INTEGER));
        v = new TypeVariable(TypeClass.NUMERIC);
        assertTrue(v.restrict(TypeClass.FLOAT));
        assertThat(v.getTypeClass(), equalTo(TypeClass.FLOAT));
        v = new TypeVariable(TypeClass.NUMERIC);
        assertFalse(v.restrict(TypeClass.BOOLEAN));
        assertThat(v.getTypeClass(), equalTo(TypeClass.NUMERIC));
        v = new TypeVariable(TypeClass.NUMERIC);
        assertFalse(v.restrict(TypeClass.STRING));
        assertThat(v.getTypeClass(), equalTo(TypeClass.NUMERIC));
        v = new TypeVariable(TypeClass.NUMERIC);
        assertFalse(v.restrict(TypeClass.TIMESTAMP));
        assertThat(v.getTypeClass(), equalTo(TypeClass.NUMERIC));
        v = new TypeVariable(TypeClass.NUMERIC);
        assertFalse(v.restrict(TypeClass.ID));
        assertThat(v.getTypeClass(), equalTo(TypeClass.NUMERIC));
    }

}
