package org.dei.perla.lang.query.statement;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.DataType;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.query.expression.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 27/04/15.
 */
public class SelectionStatementTest {

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

    private static final Expression tempField =
            new AttributeReference("temperature", DataType.INTEGER, 0);
    private static final Expression humField =
            new AttributeReference("humidity", DataType.INTEGER, 0);

    @Test
    public void testSamplingQuery() throws Exception {
        Errors err = new Errors();
        List<Expression> fields = new ArrayList<>();
        fields.add(tempField);
        fields.add(humField);
        fields.add(new SumAggregate(tempField, new WindowSize(5),
                Constant.TRUE));
        assertTrue(err.isEmpty());

        Expression having = new Comparison(ComparisonOperation.GE,
                humField, Constant.INTEGER_0);
        assertTrue(err.isEmpty());

        Select sel = new Select(fields, WindowSize.ONE, GroupBy.NONE, having,
                new Object[0]);
        assertTrue(err.isEmpty());

        List<Attribute> events = Arrays.asList(new Attribute[] {
                Attribute.create("alarm", DataType.ANY)
        });
        Sampling samp = new SamplingEvent(events);

        SelectionStatement query = new SelectionStatement(sel, WindowSize.ONE, samp,
                Constant.TRUE, ExecutionConditions.ALL_NODES, WindowSize.ZERO);

        assertThat(query.getSelect(), equalTo(sel));
        assertThat(query.getSampling(), equalTo(samp));
        assertThat(query.getEvery(), equalTo(WindowSize.ONE));
        assertThat(query.getTerminate(), equalTo(WindowSize.ZERO));
        assertThat(query.getExecutionConditions(),
                equalTo(ExecutionConditions.ALL_NODES));
    }

}
