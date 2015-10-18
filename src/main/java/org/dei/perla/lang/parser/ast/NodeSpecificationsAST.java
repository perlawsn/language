package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Node specifications Abstract Syntax Tree node
 *
 * @author Guido Rota 30/07/15.
 */
public final class NodeSpecificationsAST extends NodeAST {

    public static NodeSpecificationsAST ALL =
            new NodeSpecificationsAST();

    public static NodeSpecificationsAST EMPTY =
            new NodeSpecificationsAST(Collections.emptyList());

    private final NodeSpecificationsType type;
    private final List<Attribute> specs;

    private NodeSpecificationsAST() {
        super(null);
        type = NodeSpecificationsType.ALL;
        specs = Collections.emptyList();
    }

    public NodeSpecificationsAST(List<Attribute> specs) {
        this(null, specs);
    }

    public NodeSpecificationsAST(Token token, List<Attribute> specs) {
        super(token);
        this.type = NodeSpecificationsType.SPECS;
        this.specs = specs;
    }

    public NodeSpecificationsType getType() {
        return type;
    }

    public List<Attribute> getSpecifications() {
        return specs;
    }

    public Set<Attribute> compile(ParserContext ctx) {
        switch (type) {
            case ALL:
                return compileAll(ctx);
            case SPECS:
                return compileSpecs(ctx);
            default:
                throw new RuntimeException(
                        "Unknown node specification type " + type);
        }
    }

    private Set<Attribute> compileAll(ParserContext ctx) {
        return ctx.getAttributes();
    }

    private Set<Attribute> compileSpecs(ParserContext ctx) {
        Set<Attribute> atts = new HashSet<>();
        Set<String> ids = new HashSet<>();
        for (Attribute a : specs) {
            String id = a.getId();
            if (ids.contains(id)) {
                ctx.addError("Duplicate attribute '" + id + "' in " +
                        "execution conditions clause");
            }
            ids.add(id);
            atts.add(a);
        }
        return atts;
    }

    public enum NodeSpecificationsType {
        ALL,
        SPECS
    }

}
