package org.dei.perla.lang.query.expression;

import org.dei.perla.core.fpc.DataType;
import org.dei.perla.core.fpc.Attribute;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

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
    public void testGetById() {
        assertThat(ExpressionUtils.getById("integer", atts), equalTo(intAtt));
        assertThat(ExpressionUtils.getById("float", atts), equalTo(floatAtt));
        assertThat(ExpressionUtils.getById("boolean", atts), equalTo(boolAtt));
        assertThat(ExpressionUtils.getById("string", atts), nullValue());
    }

}
