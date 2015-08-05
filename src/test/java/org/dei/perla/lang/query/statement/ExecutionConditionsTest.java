package org.dei.perla.lang.query.statement;

import org.dei.perla.core.registry.DataTemplate;
import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.query.expression.Constant;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 16/04/15.
 */
public class ExecutionConditionsTest {

    private static final List<DataTemplate> specs =
            Arrays.asList(new DataTemplate[] {
            DataTemplate.create("temperature", TypeClass.ANY),
            DataTemplate.create("pressure", TypeClass.INTEGER)
    });

    @Test
    public void testSimple() {
        Errors err = new Errors();

        ExecutionConditions ec = ExecutionConditions.create(Constant.TRUE,
                specs, Refresh.NEVER, err);
        assertTrue(err.isEmpty());
        assertThat(ec.getCondition(), equalTo(Constant.TRUE));
        assertThat(ec.getRefresh(), equalTo(Refresh.NEVER));
        List<DataTemplate> s = ec.getSpecs();
        assertThat(s.size(), equalTo(specs.size()));
        assertTrue(s.contains(specs.get(0)));
        assertTrue(s.contains(specs.get(1)));
        assertTrue(ec.getAttributes().isEmpty());
    }

}
