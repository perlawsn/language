package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;
import org.dei.perla.lang.query.expression.*;
import org.dei.perla.lang.query.statement.WindowSize;

import java.util.Map;

/**
 * Aggregate Abstract Syntax Tree node
 *
 * @author Guido Rota 30/07/15.
 */
public final class AggregateAST extends ExpressionAST {

    private final AggregateOperation op;
    private final ExpressionAST operand;
    private final WindowSizeAST window;
    private final ExpressionAST filter;

    public AggregateAST(AggregateOperation op, ExpressionAST operand,
            WindowSizeAST window, ExpressionAST filter) {
        super();
        this.op = op;
        this.operand = operand;
        this.window = window;
        this.filter = filter;
    }

    public AggregateAST(Token token, AggregateOperation op,
            ExpressionAST operand, WindowSizeAST window, ExpressionAST filter) {
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

    public WindowSizeAST getWindowSize() {
        return window;
    }

    public ExpressionAST getFilter() {
        return filter;
    }

    @Override
    public boolean inferType(TypeVariable bound, ParserContext ctx) {
        if (op == AggregateOperation.COUNT) {
            return inferCountType(bound, ctx);
        } else if (op == AggregateOperation.AVG) {
            return inferAvgType(bound, ctx);
        } else {
            return inferGeneralType(bound, ctx);
        }
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

    private boolean inferAvgType(TypeVariable bound, ParserContext ctx) {
        boolean res = bound.restrict(TypeClass.FLOAT);
        if (!res) {
            String msg = typeErrorString(op.name(), getPosition(),
                    bound.getTypeClass(), TypeClass.FLOAT);
            ctx.addError(msg);
            return false;
        }
        setType(bound);
        TypeVariable ob = new TypeVariable(TypeClass.NUMERIC);
        TypeVariable fb = new TypeVariable(TypeClass.BOOLEAN);
        res = operand.inferType(ob, ctx);
        return res && filter.inferType(fb, ctx);
    }

    protected boolean inferGeneralType(TypeVariable bound, ParserContext ctx) {
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

    @Override
    protected Expression toExpression(ParserContext ctx, Map<Attribute, Integer> atts) {
        Expression opExp = null;
        if (operand != null) {
            opExp = operand.toExpression(ctx, atts);
        }
        Expression filExp = filter.toExpression(ctx, atts);

        WindowSize ws = window.compile(ctx);

        switch (op) {
            case AVG:
                return new AvgAggregate(opExp, ws, filExp);
            case MIN:
                return new MinAggregate(opExp, ws, filExp);
            case MAX:
                return new MaxAggregate(opExp, ws, filExp);
            case SUM:
                return new SumAggregate(opExp, ws, filExp);
            case COUNT:
                return new CountAggregate(ws, filExp);
            default:
                throw new RuntimeException("unknown aggregate " + op);
        }
    }

}
