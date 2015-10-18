package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.query.expression.Constant;
import org.dei.perla.lang.query.statement.ExecutionConditions;
import org.dei.perla.lang.query.statement.Refresh;
import org.dei.perla.lang.query.statement.RefreshType;
import org.junit.Test;
import org.dei.perla.lang.parser.ast.NodeSpecificationsAST.NodeSpecificationsType;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 * @author Guido Rota 18/10/15.
 */
public class ExecutionConditionsASTTest {

    private static final Attribute intAtt =
            Attribute.create("int", DataType.INTEGER);
    private static final Attribute floatAtt =
            Attribute.create("float", DataType.FLOAT);
    private static final Attribute stringAtt =
            Attribute.create("string", DataType.STRING);

    private static final List<Attribute> atts =
            Arrays.asList(new Attribute[] {
                    intAtt, floatAtt, stringAtt
            });

    @Test
    public void testCreation() {
        ExecutionConditionsAST cond = new ExecutionConditionsAST(
                ConstantAST.TRUE,
                NodeSpecificationsAST.EMPTY,
                RefreshAST.NEVER
        );

        assertThat(cond.getCondition(), equalTo(ConstantAST.TRUE));
        NodeSpecificationsAST spec = cond.getSpecifications();
        assertThat(spec.getType(), equalTo(NodeSpecificationsType.SPECS));
        assertTrue(spec.getSpecifications().isEmpty());
        assertThat(cond.getRefresh(), equalTo(RefreshAST.NEVER));
    }

    @Test
    public void testCompileSpecs() {
        NodeSpecificationsAST specs = new NodeSpecificationsAST(atts);
        ConstantAST c = new ConstantAST(10, DataType.INTEGER);
        RefreshAST ref = new RefreshAST(c, ChronoUnit.MINUTES);
        ExecutionConditionsAST cond = new ExecutionConditionsAST(
                ConstantAST.TRUE,
                specs,
                ref
        );

        ParserContext ctx = new ParserContext();
        ExecutionConditions condComp = cond.compile(ctx);
        assertThat(condComp, notNullValue());
        assertFalse(ctx.hasErrors());
        assertThat(condComp.getCondition(), equalTo(Constant.TRUE));
        Refresh refComp = condComp.getRefresh();
        assertThat(refComp.getType(), equalTo(RefreshType.TIME));
        assertThat(refComp.getDuration(), equalTo(Duration.ofMinutes(10)));
        List<Attribute> condAtts = condComp.getAttributes();
        assertTrue(condAtts.isEmpty());
        Set<Attribute> specAtts = condComp.getSpecifications();
        assertThat(specAtts.size(), equalTo(atts.size()));
        assertTrue(specAtts.containsAll(atts));
    }

    @Test
    public void testCompileAll() {
        ExpressionAST exp = new AttributeReferenceAST("run", DataType.BOOLEAN);
        ExecutionConditionsAST cond = new ExecutionConditionsAST(
                exp,
                NodeSpecificationsAST.ALL,
                RefreshAST.NEVER
        );

        ParserContext ctx = new ParserContext();
        AttributeReferenceAST ref =
                new AttributeReferenceAST("temp", DataType.INTEGER);
        ctx.addAttributeReference(ref);
        ref = new AttributeReferenceAST("press", DataType.ANY);
        ctx.addAttributeReference(ref);
        ExecutionConditions condComp = cond.compile(ctx);

        assertThat(condComp, notNullValue());
        assertThat(condComp.getRefresh(), equalTo(Refresh.NEVER));

        List<Attribute> condAtts = condComp.getAttributes();
        assertThat(condAtts.size(), equalTo(1));
        Attribute a = Attribute.create("run", DataType.BOOLEAN);
        assertTrue(condAtts.contains(a));

        Set<Attribute> specAtts = condComp.getSpecifications();
        assertThat(specAtts.size(), equalTo(3));
        assertTrue(specAtts.contains(a));
        a = Attribute.create("temp", DataType.INTEGER);
        assertTrue(specAtts.contains(a));
        a = Attribute.create("press", DataType.ANY);
        assertTrue(specAtts.contains(a));
    }

    @Test
    public void testFalseCondition() {
        ExecutionConditionsAST cond = new ExecutionConditionsAST(
                ConstantAST.FALSE,
                NodeSpecificationsAST.EMPTY,
                RefreshAST.NEVER
        );

        ParserContext ctx = new ParserContext();
        ExecutionConditions condComp = cond.compile(ctx);
        assertTrue(ctx.hasErrors());
    }

}
