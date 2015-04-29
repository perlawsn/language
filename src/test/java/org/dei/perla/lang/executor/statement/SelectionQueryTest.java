package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.expression.*;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 27/04/15.
 */
public class SelectionQueryTest {

    private static final Attribute tempAtt =
            Attribute.create("temperature", DataType.INTEGER);
    private static final Attribute humAtt =
            Attribute.create("humidity", DataType.INTEGER);
    private static final Attribute alarmAtt =
            Attribute.create("alarm", DataType.BOOLEAN);

    private static final List<Attribute> atts = Arrays.asList(new Attribute[]{
            tempAtt,
            humAtt,
            alarmAtt
    });

    private static final Expression tempField = new Field("temperature");
    private static final Expression humField = new Field("humidity");

    @Test
    public void testSamplingQuery() throws Exception {
        Errors err = new Errors();
        List<Expression> fields = new ArrayList<>();
        fields.add(tempField);
        fields.add(humField);
        fields.add(Aggregate.createSum(tempField, new WindowSize(5),
                Constant.TRUE, err));
        assertTrue(err.isEmpty());

        Expression having = Comparison.createGE(humField,
                Constant.INTEGER_0, err);
        assertTrue(err.isEmpty());

        Select sel = new Select(fields, WindowSize.ONE, GroupBy.NONE, having,
                new Object[0]);
        assertTrue(err.isEmpty());

        Set<String> events = new HashSet<>();
        events.add("alarm");
        Sampling samp = new SamplingEvent(events);

        SelectionQuery query = new SelectionQuery(sel, WindowSize.ONE, samp,
                Constant.TRUE, ExecutionConditions.ALL_NODES, WindowSize.ZERO);

        assertThat(query.getSelect(), equalTo(sel));
        assertThat(query.getSampling(), equalTo(samp));
        assertThat(query.getEvery(), equalTo(WindowSize.ONE));
        assertThat(query.getTerminate(), equalTo(WindowSize.ZERO));
        assertThat(query.getExecutionConditions(),
                equalTo(ExecutionConditions.ALL_NODES));

        query = query.bind(atts);
        assertTrue(query.getExecutionConditions().isComplete());
        assertTrue(query.getSampling().isComplete());
        assertTrue(query.getExecutionConditions().isComplete());
        assertTrue(query.getWhere().isComplete());
    }

}
