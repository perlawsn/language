package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.registry.DataTemplate;
import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.expression.Constant;
import org.dei.perla.lang.executor.expression.Expression;
import org.dei.perla.lang.executor.expression.Field;
import org.junit.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;
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
        assertTrue(ec.isComplete());
        assertThat(ec.getCondition(), equalTo(Constant.TRUE));
        assertThat(ec.getRefresh(), equalTo(Refresh.NEVER));
        List<DataTemplate> s = ec.getSpecs();
        assertThat(s.size(), equalTo(specs.size()));
        assertTrue(s.contains(specs.get(0)));
        assertTrue(s.contains(specs.get(1)));
        assertTrue(ec.getAttributes().isEmpty());
    }

    @Test
    public void testBinding() {
        Errors err = new Errors();
        Expression f = new Field("temperature");
        Refresh r = new Refresh(Duration.ofHours(1));
        List<Attribute> atts = Arrays.asList(new Attribute[] {
                Attribute.create("temperature", DataType.FLOAT),
                Attribute.create("pressure", DataType.INTEGER)
        });

        ExecutionConditions ec = ExecutionConditions.create(f, specs, r, err);
        assertTrue(err.isEmpty());
        assertFalse(ec.isComplete());
        assertTrue(ec.getAttributes().isEmpty());

        ec = ec.bind(atts, err);
        assertTrue(err.isEmpty());
        assertTrue(ec.isComplete());
        assertThat(ec.getRefresh(), equalTo(r));
        List<DataTemplate> s = ec.getSpecs();
        assertThat(s.size(), equalTo(specs.size()));
        assertTrue(s.contains(specs.get(0)));
        assertTrue(s.contains(specs.get(1)));
        List<Attribute> as = ec.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(atts.get(0)));
    }

}
