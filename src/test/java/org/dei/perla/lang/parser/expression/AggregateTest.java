package org.dei.perla.lang.parser.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.expression.*;
import org.dei.perla.lang.executor.statement.WindowSize;
import org.dei.perla.lang.parser.AggregationOperator;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

/**
 * @author Guido Rota 09/03/15.
 */
public class AggregateTest {

    private static Node intNode = new ConstantNode(5, DataType.INTEGER);
    private static Node floatNode = new ConstantNode(3.2f, DataType.FLOAT);
    private static Node tsNode = new ConstantNode(Instant.now(),
            DataType.TIMESTAMP);

    private static Node trueNode = new ConstantNode(true, DataType.BOOLEAN);
    private static Node falseNode = new ConstantNode(false, DataType.BOOLEAN);

    private static WindowSize samples = new WindowSize(10);
    private static WindowSize duration = new WindowSize(Duration.ofSeconds(12));

    @Test
    public void testCount() {
        Node n = AggregateNode.create(AggregationOperator.COUNT, null,
                samples, null);
        assertThat(n, notNullValue());
        assertThat(n.getType(), equalTo(DataType.INTEGER));
        assertTrue(n instanceof AggregateNode);

        AggregateNode a = (AggregateNode) n;
        assertThat(a.getAggregation(),
                equalTo(AggregationOperator.COUNT));
        assertThat(a.getOperand(), nullValue());
        assertThat(a.getWindowSize(), equalTo(samples));

        Expression e = n.build(Collections.emptyList());
        assertTrue(e instanceof CountAggregate);
        CountAggregate ca = (CountAggregate) e;
        assertThat(ca.getType(), equalTo(DataType.INTEGER));
        assertThat(ca.getWindowSize(), equalTo(samples));
        assertThat(ca.getFilter(), nullValue());
    }

    @Test
    public void testCountFilter() {
        Node n = AggregateNode.create(AggregationOperator.COUNT, null,
                duration, falseNode);
        assertThat(((AggregateNode) n).getFilter(), equalTo(falseNode));
        CountAggregate ca = (CountAggregate) n.build(Collections.emptyList());
        assertThat(ca.getFilter(), notNullValue());
    }

    @Test
    public void testIntAvg() {
        Node n = AggregateNode.create(AggregationOperator.AVG, intNode,
                duration, null);
        assertThat(n, notNullValue());
        assertThat(n.getType(), equalTo(DataType.FLOAT));
        assertTrue(n instanceof AggregateNode);

        AggregateNode a = (AggregateNode) n;
        assertThat(a.getAggregation(), equalTo(AggregationOperator.AVG));
        assertThat(a.getOperand(), equalTo(intNode));
        assertThat(a.getWindowSize(), equalTo(duration));

        Expression e = n.build(Collections.emptyList());
        assertTrue(e instanceof AvgAggregate);
        AvgAggregate aa = (AvgAggregate) e;
        assertThat(aa.getOperand().getType(), equalTo(DataType.INTEGER));
        assertThat(aa.getType(), equalTo(DataType.FLOAT));
        assertThat(aa.getWindowSize(), equalTo(duration));
        assertThat(aa.getFilter(), nullValue());
    }

    @Test
    public void testFloatAvg() {
        Node n = AggregateNode.create(AggregationOperator.AVG, floatNode,
                duration, null);
        assertThat(n, notNullValue());
        assertThat(n.getType(), equalTo(DataType.FLOAT));
        assertTrue(n instanceof AggregateNode);

        AggregateNode a = (AggregateNode) n;
        assertThat(a.getAggregation(), equalTo(AggregationOperator.AVG));
        assertThat(a.getOperand(), equalTo(floatNode));
        assertThat(a.getWindowSize(), equalTo(duration));

        Expression e = n.build(Collections.emptyList());
        assertTrue(e instanceof AvgAggregate);
        AvgAggregate aa = (AvgAggregate) e;
        assertThat(aa.getOperand().getType(), equalTo(DataType.FLOAT));
        assertThat(aa.getType(), equalTo(DataType.FLOAT));
        assertThat(aa.getWindowSize(), equalTo(duration));
        assertThat(aa.getFilter(), nullValue());
    }

    @Test
    public void testAvgFilter() {
        Node n = AggregateNode.create(AggregationOperator.AVG, intNode,
                duration, falseNode);
        assertThat(((AggregateNode) n).getFilter(), equalTo(falseNode));
        AvgAggregate ca = (AvgAggregate) n.build(Collections.emptyList());
        assertThat(ca.getFilter(), notNullValue());
    }

    @Test
    public void testIntSum() {
        Node n = AggregateNode.create(AggregationOperator.SUM, intNode,
                duration, null);
        assertThat(n, notNullValue());
        assertThat(n.getType(), equalTo(DataType.INTEGER));
        assertTrue(n instanceof AggregateNode);

        AggregateNode a = (AggregateNode) n;
        assertThat(a.getAggregation(), equalTo(AggregationOperator.SUM));
        assertThat(a.getOperand(), equalTo(intNode));
        assertThat(a.getWindowSize(), equalTo(duration));

        Expression e = n.build(Collections.emptyList());
        assertTrue(e instanceof SumAggregate);
        SumAggregate sa = (SumAggregate) e;
        assertThat(sa.getOperand().getType(), equalTo(DataType.INTEGER));
        assertThat(sa.getType(), equalTo(DataType.INTEGER));
        assertThat(sa.getWindowSize(), equalTo(duration));
        assertThat(sa.getFilter(), nullValue());
    }

    @Test
    public void testFloatSum() {
        Node n = AggregateNode.create(AggregationOperator.SUM, floatNode,
                duration, null);
        assertThat(n, notNullValue());
        assertThat(n.getType(), equalTo(DataType.FLOAT));
        assertTrue(n instanceof AggregateNode);

        AggregateNode a = (AggregateNode) n;
        assertThat(a.getAggregation(), equalTo(AggregationOperator.SUM));
        assertThat(a.getOperand(), equalTo(floatNode));
        assertThat(a.getWindowSize(), equalTo(duration));

        Expression e = n.build(Collections.emptyList());
        assertTrue(e instanceof SumAggregate);
        SumAggregate sa = (SumAggregate) e;
        assertThat(sa.getOperand().getType(), equalTo(DataType.FLOAT));
        assertThat(sa.getType(), equalTo(DataType.FLOAT));
        assertThat(sa.getWindowSize(), equalTo(duration));
        assertThat(sa.getFilter(), nullValue());
    }

    @Test
    public void testSumFilter() {
        Node n = AggregateNode.create(AggregationOperator.SUM, intNode,
                duration, falseNode);
        assertThat(((AggregateNode) n).getFilter(), equalTo(falseNode));
        SumAggregate sa = (SumAggregate) n.build(Collections.emptyList());
        assertThat(sa.getFilter(), notNullValue());
    }

    @Test
    public void testIntMin() {
        Node n = AggregateNode.create(AggregationOperator.MIN, intNode,
                duration, null);
        assertThat(n, notNullValue());
        assertThat(n.getType(), equalTo(DataType.INTEGER));
        assertTrue(n instanceof AggregateNode);

        AggregateNode a = (AggregateNode) n;
        assertThat(a.getAggregation(), equalTo(AggregationOperator.MIN));
        assertThat(a.getOperand(), equalTo(intNode));
        assertThat(a.getWindowSize(), equalTo(duration));

        Expression e = n.build(Collections.emptyList());
        assertTrue(e instanceof MinAggregate);
        MinAggregate ma = (MinAggregate) e;
        assertThat(ma.getOperand().getType(), equalTo(DataType.INTEGER));
        assertThat(ma.getType(), equalTo(DataType.INTEGER));
        assertThat(ma.getWindowSize(), equalTo(duration));
        assertThat(ma.getFilter(), nullValue());
    }

    @Test
    public void testFloatMin() {
        Node n = AggregateNode.create(AggregationOperator.MIN, floatNode,
                duration, null);
        assertThat(n, notNullValue());
        assertThat(n.getType(), equalTo(DataType.FLOAT));
        assertTrue(n instanceof AggregateNode);

        AggregateNode a = (AggregateNode) n;
        assertThat(a.getAggregation(), equalTo(AggregationOperator.MIN));
        assertThat(a.getOperand(), equalTo(floatNode));
        assertThat(a.getWindowSize(), equalTo(duration));

        Expression e = n.build(Collections.emptyList());
        assertTrue(e instanceof MinAggregate);
        MinAggregate ma = (MinAggregate) e;
        assertThat(ma.getOperand().getType(), equalTo(DataType.FLOAT));
        assertThat(ma.getType(), equalTo(DataType.FLOAT));
        assertThat(ma.getWindowSize(), equalTo(duration));
        assertThat(ma.getFilter(), nullValue());
    }

    @Test
    public void testTimestampMin() {
        Node n = AggregateNode.create(AggregationOperator.MIN, tsNode,
                duration, null);
        assertThat(n, notNullValue());
        assertThat(n.getType(), equalTo(DataType.TIMESTAMP));
        assertTrue(n instanceof AggregateNode);

        AggregateNode a = (AggregateNode) n;
        assertThat(a.getAggregation(), equalTo(AggregationOperator.MIN));
        assertThat(a.getOperand(), equalTo(tsNode));
        assertThat(a.getWindowSize(), equalTo(duration));

        Expression e = n.build(Collections.emptyList());
        assertTrue(e instanceof MinAggregate);
        MinAggregate ma = (MinAggregate) e;
        assertThat(ma.getOperand().getType(), equalTo(DataType.TIMESTAMP));
        assertThat(ma.getType(), equalTo(DataType.TIMESTAMP));
        assertThat(ma.getWindowSize(), equalTo(duration));
        assertThat(ma.getFilter(), nullValue());
    }

    @Test
    public void testMinFilter() {
        Node n = AggregateNode.create(AggregationOperator.MIN, intNode,
                duration, falseNode);
        assertThat(((AggregateNode) n).getFilter(), equalTo(falseNode));
        MinAggregate sa = (MinAggregate) n.build(Collections.emptyList());
        assertThat(sa.getFilter(), notNullValue());
    }

    @Test
    public void testIntMax() {
        Node n = AggregateNode.create(AggregationOperator.MAX, intNode,
                duration, null);
        assertThat(n, notNullValue());
        assertThat(n.getType(), equalTo(DataType.INTEGER));
        assertTrue(n instanceof AggregateNode);

        AggregateNode a = (AggregateNode) n;
        assertThat(a.getAggregation(), equalTo(AggregationOperator.MAX));
        assertThat(a.getOperand(), equalTo(intNode));
        assertThat(a.getWindowSize(), equalTo(duration));

        Expression e = n.build(Collections.emptyList());
        assertTrue(e instanceof MaxAggregate);
        MaxAggregate ma = (MaxAggregate) e;
        assertThat(ma.getOperand().getType(), equalTo(DataType.INTEGER));
        assertThat(ma.getType(), equalTo(DataType.INTEGER));
        assertThat(ma.getWindowSize(), equalTo(duration));
        assertThat(ma.getFilter(), nullValue());
    }

    @Test
    public void testFloatMax() {
        Node n = AggregateNode.create(AggregationOperator.MAX, floatNode,
                duration, null);
        assertThat(n, notNullValue());
        assertThat(n.getType(), equalTo(DataType.FLOAT));
        assertTrue(n instanceof AggregateNode);

        AggregateNode a = (AggregateNode) n;
        assertThat(a.getAggregation(), equalTo(AggregationOperator.MAX));
        assertThat(a.getOperand(), equalTo(floatNode));
        assertThat(a.getWindowSize(), equalTo(duration));

        Expression e = n.build(Collections.emptyList());
        assertTrue(e instanceof MaxAggregate);
        MaxAggregate ma = (MaxAggregate) e;
        assertThat(ma.getOperand().getType(), equalTo(DataType.FLOAT));
        assertThat(ma.getType(), equalTo(DataType.FLOAT));
        assertThat(ma.getWindowSize(), equalTo(duration));
        assertThat(ma.getFilter(), nullValue());
    }

    @Test
    public void testTimestampMax() {
        Node n = AggregateNode.create(AggregationOperator.MAX, tsNode,
                duration, null);
        assertThat(n, notNullValue());
        assertThat(n.getType(), equalTo(DataType.TIMESTAMP));
        assertTrue(n instanceof AggregateNode);

        AggregateNode a = (AggregateNode) n;
        assertThat(a.getAggregation(), equalTo(AggregationOperator.MAX));
        assertThat(a.getOperand(), equalTo(tsNode));
        assertThat(a.getWindowSize(), equalTo(duration));

        Expression e = n.build(Collections.emptyList());
        assertTrue(e instanceof MaxAggregate);
        MaxAggregate ma = (MaxAggregate) e;
        assertThat(ma.getOperand().getType(), equalTo(DataType.TIMESTAMP));
        assertThat(ma.getType(), equalTo(DataType.TIMESTAMP));
        assertThat(ma.getWindowSize(), equalTo(duration));
        assertThat(ma.getFilter(), nullValue());
    }

    @Test
    public void testMaxFilter() {
        Node n = AggregateNode.create(AggregationOperator.MAX, intNode,
                duration, falseNode);
        assertThat(((AggregateNode) n).getFilter(), equalTo(falseNode));
        MaxAggregate sa = (MaxAggregate) n.build(Collections.emptyList());
        assertThat(sa.getFilter(), notNullValue());
    }

}
