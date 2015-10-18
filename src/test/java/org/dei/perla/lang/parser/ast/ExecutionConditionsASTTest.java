package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.ast.NodeSpecificationsAST.NodeSpecificationsType;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

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
    public void testAll() {
        NodeSpecificationsAST all = NodeSpecificationsAST.ALL;
        assertThat(all.getType(), equalTo(NodeSpecificationsType.ALL));
        List<Attribute> specAtt = all.getSpecifications();
        assertTrue(specAtt.isEmpty());
    }

    @Test
    public void testSpecs() {
        NodeSpecificationsAST spec = new NodeSpecificationsAST(atts);
        assertThat(spec.getType(), equalTo(NodeSpecificationsType.SPECS));
        List<Attribute> specAtts = spec.getSpecifications();
        assertThat(specAtts.size(), equalTo(atts.size()));
        assertTrue(specAtts.containsAll(atts));
    }

    @Test
    public void testCompileSpecs() {
        NodeSpecificationsAST spec = new NodeSpecificationsAST(atts);
        assertThat(spec.getType(), equalTo(NodeSpecificationsType.SPECS));
        List<Attribute> specAtts = spec.getSpecifications();
        assertThat(specAtts.size(), equalTo(specAtts.size()));
        assertTrue(specAtts.containsAll(atts));

        ParserContext ctx = new ParserContext();
        Set<Attribute> compAtts = spec.compile(ctx);
        assertFalse(ctx.hasErrors());
        assertThat(compAtts.size(), equalTo(atts.size()));
        assertTrue(compAtts.containsAll(atts));
    }

    @Test
    public void testCompileSpecsDuplicate() {
        List<Attribute> dupAtts = Arrays.asList(new Attribute[] {
                intAtt, intAtt, floatAtt, stringAtt
        });
        NodeSpecificationsAST spec = new NodeSpecificationsAST(dupAtts);
        assertThat(spec.getType(), equalTo(NodeSpecificationsType.SPECS));
        List<Attribute> specAtts = spec.getSpecifications();
        assertThat(specAtts.size(), equalTo(4));

        ParserContext ctx = new ParserContext();
        Set<Attribute> compAtts = spec.compile(ctx);
        assertTrue(ctx.hasErrors());
    }

    @Test
    public void testCompileSpecsEmpty() {
        NodeSpecificationsAST spec =
                new NodeSpecificationsAST(Collections.emptyList());
        assertThat(spec.getType(), equalTo(NodeSpecificationsType.SPECS));
        List<Attribute> specAtts = spec.getSpecifications();
        assertTrue(specAtts.isEmpty());

        ParserContext ctx = new ParserContext();
        Set<Attribute> compAtts = spec.compile(ctx);
        assertFalse(ctx.hasErrors());
        assertTrue(compAtts.isEmpty());

        spec = NodeSpecificationsAST.EMPTY;
        assertThat(spec.getType(), equalTo(NodeSpecificationsType.SPECS));
        specAtts = spec.getSpecifications();
        assertTrue(specAtts.isEmpty());

        ctx = new ParserContext();
        compAtts = spec.compile(ctx);
        assertFalse(ctx.hasErrors());
        assertTrue(compAtts.isEmpty());
    }

    @Test
    public void testCompileAll() {
        NodeSpecificationsAST spec = NodeSpecificationsAST.ALL;

        ParserContext ctx = new ParserContext();
        AttributeReferenceAST ref =
                new AttributeReferenceAST("int", DataType.INTEGER);
        ctx.addAttributeReference(ref);
        ref = new AttributeReferenceAST("float", DataType.FLOAT);
        ctx.addAttributeReference(ref);
        ref = new AttributeReferenceAST("string", DataType.STRING);
        ctx.addAttributeReference(ref);

        Set<Attribute> compAtts = spec.compile(ctx);
        assertFalse(ctx.hasErrors());
        assertThat(compAtts.size(), equalTo(3));
        assertTrue(compAtts.contains(intAtt));
        assertTrue(compAtts.contains(floatAtt));
        assertTrue(compAtts.contains(stringAtt));
    }

}
