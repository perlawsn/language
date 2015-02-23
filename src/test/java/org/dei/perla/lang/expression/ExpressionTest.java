package org.dei.perla.lang.expression;

import org.dei.perla.core.descriptor.DataType;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

/**
 * @author Guido Rota 23/02/15.
 */
public class ExpressionTest {

    @Test
    public void constantTest() {
        Constant c = new Constant(DataType.INTEGER, 1);

        assertThat(c.getType(), equalTo(DataType.INTEGER));
        assertThat(c.getValue(), equalTo(1));
    }

    @Test
    public void sumTest() {


    }

}
