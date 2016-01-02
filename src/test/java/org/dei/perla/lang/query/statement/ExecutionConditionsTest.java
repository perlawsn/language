package org.dei.perla.lang.query.statement;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.lang.CommonAttributes;
import org.dei.perla.lang.query.expression.Constant;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 16/04/15.
 */
public class ExecutionConditionsTest {

    @Test
    public void testSimple() {
        Set<Attribute> specs = new HashSet<>();
        specs.add(CommonAttributes.INTEGER);
        specs.add(CommonAttributes.FLOAT);

        List<Attribute> atts = new ArrayList<>();
        atts.add(CommonAttributes.INTEGER);
        atts.add(CommonAttributes.FLOAT);

        ExecutionConditions ec = new ExecutionConditions(specs, Constant.TRUE,
                atts, Refresh.NEVER);
        assertThat(ec, notNullValue());
        assertThat(ec.getCondition(), equalTo(Constant.TRUE));
        assertThat(ec.getRefresh(), equalTo(Refresh.NEVER));

        Set<Attribute> specAtts = ec.getSpecifications();
        assertThat(specAtts.size(), equalTo(specs.size()));
        assertTrue(specAtts.containsAll(specs));

        List<Attribute> condAtts = ec.getAttributes();
        assertThat(condAtts.size(), equalTo(atts.size()));
        assertTrue(condAtts.containsAll(atts));
    }

}
