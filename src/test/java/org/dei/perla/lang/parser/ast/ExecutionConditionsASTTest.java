package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.parser.ast.NodeSpecificationsAST.NodeSpecificationsType;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 17/10/15.
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
    public void testNodeSpecificationsAll() {
        NodeSpecificationsAST all = NodeSpecificationsAST.ALL;
        assertThat(all.getType(), equalTo(NodeSpecificationsType.ALL));
        List<Attribute> specAtt = all.getSpecifications();
        assertTrue(specAtt.isEmpty());
    }

    @Test
    public void testNodeSpecifications() {
        NodeSpecificationsAST spec = new NodeSpecificationsAST(atts);
        assertThat(spec.getType(), equalTo(NodeSpecificationsType.SPECS));
        List<Attribute> specAtt = spec.getSpecifications();
        assertThat(specAtt.size(), equalTo(atts.size()));
        assertTrue(specAtt.containsAll(atts));
    }

    @Test
    public void testNodeSpecificationsCompile() {
        throw new RuntimeException("unimplemented");
    }

}
