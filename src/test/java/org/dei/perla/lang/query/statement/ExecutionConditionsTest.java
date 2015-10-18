package org.dei.perla.lang.query.statement;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.DataType;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.query.expression.Constant;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 16/04/15.
 */
public class ExecutionConditionsTest {

    private static final List<Attribute> specs =
            Arrays.asList(new Attribute[] {
            Attribute.create("temperature", DataType.ANY),
            Attribute.create("pressure", DataType.INTEGER)
    });

    @Test
    public void testSimple() {
        throw new RuntimeException("unimplemented");
//        Errors err = new Errors();
//
//        ExecutionConditions ec = ExecutionConditions.create(Constant.TRUE,
//                specs, Refresh.NEVER, err);
//        assertThat(ec, notNullValue());
//        assertTrue(err.isEmpty());
//        assertThat(ec.getCondition(), equalTo(Constant.TRUE));
//        assertThat(ec.getRefresh(), equalTo(Refresh.NEVER));
//        List<Attribute> s = ec.getSpecifications();
//        assertThat(s.size(), equalTo(specs.size()));
//        assertTrue(s.contains(specs.get(0)));
//        assertTrue(s.contains(specs.get(1)));
//        assertTrue(ec.getAttributes().isEmpty());
    }

}
