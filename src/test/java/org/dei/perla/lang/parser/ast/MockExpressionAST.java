package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.TypeVariable;
import org.dei.perla.lang.query.expression.Expression;

import java.util.Map;

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
    protected boolean inferType(TypeVariable bound, ParserContext ctx) {
        return bound.restrict(type);
    }

    @Override
    protected Expression toExpression(ParserContext ctx,
            Map<Attribute, Integer> atts) {
        throw new UnsupportedOperationException();
    }

}
