package org.dei.perla.lang.parser.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.expression.CountAggregate;
import org.dei.perla.lang.executor.expression.Expression;
import org.dei.perla.lang.executor.statement.WindowSize;
import org.dei.perla.lang.parser.AggregationOperator;
import org.junit.Test;

import java.time.Duration;
import java.util.Collections;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

/**
 * @author Guido Rota 09/03/15.
 */
public class AggregateTest {

    private static Node trueNode = new ConstantNode(true, DataType.BOOLEAN);
    private static Node falseNode = new ConstantNode(false, DataType.BOOLEAN);

    private static WindowSize samples = new WindowSize(10);
    private static WindowSize duration = new WindowSize(Duration.ofSeconds(12));

    @Test
    public void testCount() {
        Node n = AggregateNode.create(AggregationOperator.COUNT, null,
                samples, trueNode);
        assertThat(n, notNullValue());
        assertThat(n.getType(), equalTo(DataType.INTEGER));
        assertTrue(n instanceof AggregateNode);
        AggregateNode a = (AggregateNode) n;
        assertThat(a.getAggregationOperator(),
                equalTo(AggregationOperator.COUNT));
        assertThat(a.getExpression(), nullValue());
        assertThat(a.getWindowSize(), equalTo(samples));
        assertThat(a.getFilter(), equalTo(trueNode));
        Expression e = n.build(Collections.emptyList());
        assertTrue(e instanceof CountAggregate);
    }

}
