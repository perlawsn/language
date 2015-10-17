package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.lang.parser.Token;

import java.util.Collections;
import java.util.List;

/**
 * Node specifications Abstract Syntax Tree node
 *
 * @author Guido Rota 30/07/15.
 */
public final class NodeSpecificationsAST extends NodeAST {

    public static NodeSpecificationsAST ALL =
            new NodeSpecificationsAST();

    private final NodeSpecificationsType type;
    private final List<Attribute> specs;

    private NodeSpecificationsAST() {
        super(null);
        type = NodeSpecificationsType.ALL;
        specs = null;
    }

    public NodeSpecificationsAST(List<Attribute> specs) {
        this(null, specs);
    }

    public NodeSpecificationsAST(Token token, List<Attribute> specs) {
        super(token);
        this.type = NodeSpecificationsType.SPECS;
        this.specs = Collections.unmodifiableList(specs);
    }

    public NodeSpecificationsType getType() {
        return type;
    }

    public List<Attribute> getSpecifications() {
        if (type != NodeSpecificationsType.SPECS) {
            throw new RuntimeException(
                    "No specs list in NodeSpecifications ALL");
        }
        return specs;
    }

    public enum NodeSpecificationsType {
        ALL,
        SPECS
    }

}
