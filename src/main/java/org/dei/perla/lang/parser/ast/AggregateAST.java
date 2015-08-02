package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;
import org.dei.perla.lang.query.expression.AggregateOperation;
import org.dei.perla.lang.query.statement.WindowSize;

/**
 * Aggregate Abstract Syntax Tree node
 *
 * @author Guido Rota 30/07/15.
 */
public final class AggregateAST extends ExpressionAST {

    private final AggregateOperation op;
    private final ExpressionAST operand;
    private final WindowSize window;
    private final ExpressionAST filter;

    public AggregateAST(AggregateOperation op, ExpressionAST operand,
            WindowSize window, ExpressionAST filter) {
        super();
        this.op = op;
        this.operand = operand;
        this.window = window;
        this.filter = filter;
    }

    public AggregateAST(Token token, AggregateOperation op,
            ExpressionAST operand, WindowSize window, ExpressionAST filter) {
        super(token);
        this.op = op;
        this.operand = operand;
        this.window = window;
        this.filter = filter;
    }

    public AggregateOperation getOperation() {
        return op;
    }

    public ExpressionAST getOperand() {
        return operand;
    }

    public WindowSize getWindowSize() {
        return window;
    }

    public ExpressionAST getFilter() {
        return filter;
    }

    @Override
    public boolean inferType(TypeVariable bound, ParserContext ctx) {
        if (op == AggregateOperation.COUNT) {
            return inferCountType(bound, ctx);
        }

        boolean res = bound.restrict(TypeClass.NUMERIC);
        if (!res) {
            String msg = typeErrorString(op.name(), getPosition(),
                    bound.getTypeClass(), TypeClass.NUMERIC);
            ctx.addError(msg);
            return false;
        }
        setType(bound);
        res = operand.inferType(bound, ctx);
        TypeVariable filterBound = new TypeVariable(TypeClass.BOOLEAN);
        return res && filter.inferType(filterBound, ctx);
    }

    private boolean inferCountType(TypeVariable bound, ParserContext ctx) {
        boolean res = bound.restrict(TypeClass.INTEGER);
        if (!res) {
            String msg = typeErrorString(op.name(), getPosition(),
                    bound.getTypeClass(), TypeClass.INTEGER);
            ctx.addError(msg);
            return false;
        }
        setType(bound);
        TypeVariable filterBound = new TypeVariable(TypeClass.BOOLEAN);
        return filter.inferType(filterBound, ctx);
    }

}
