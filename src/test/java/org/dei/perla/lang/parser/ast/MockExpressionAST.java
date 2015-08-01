package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.TypeVariable;

/**
 * Test Abstract Syntax Tree expression node
 *
 * @author Guido Rota 01/08/15.
 */
public final class MockExpressionAST extends ExpressionAST {

    private final TypeClass type;

    public MockExpressionAST(TypeClass type) {
        this.type = type;
    }

    @Override
    public boolean inferType(TypeVariable bound, ParserContext ctx) {
        return bound.restrict(type);
    }

}
