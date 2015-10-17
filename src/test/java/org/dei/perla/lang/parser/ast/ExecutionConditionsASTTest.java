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

    private static final List<Attribute> attList =
            Arrays.asList(new Attribute[] { intAtt, floatAtt, stringAtt });

    @Test(expected = RuntimeException.class)
    public void testNodeSpecificationsAll() {
        NodeSpecificationsAST all = NodeSpecificationsAST.ALL;
        assertThat(all.getType(), equalTo(NodeSpecificationsType.ALL));
        all.getSpecifications();
    }

    @Test
    public void testNodeSpecifications() {
        NodeSpecificationsAST spec = new NodeSpecificationsAST(attList);
        assertThat(spec.getType(), equalTo(NodeSpecificationsType.SPECS));
        List<Attribute> specAtt = spec.getSpecifications();
        assertThat(specAtt.size(), equalTo(attList.size()));
        assertTrue(specAtt.containsAll(attList));
    }

}
