package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.TypeVariable;
import org.dei.perla.lang.query.expression.Expression;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
    public boolean inferType(TypeVariable bound, ParserContext ctx) {
        return bound.restrict(type);
    }

    @Override
    public Expression compile(ParserContext ctx, Map<String, Integer> atts) {
        throw new NotImplementedException();
    }

}
