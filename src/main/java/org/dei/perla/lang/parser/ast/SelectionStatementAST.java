package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.parser.*;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.statement.Sampling;
import org.dei.perla.lang.query.statement.SelectionStatement;
import org.dei.perla.lang.query.statement.WindowSize;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Selection statement Abstract Syntax Tree node.
 *
 * @author Guido Rota 30/07/15.
 */
public final class SelectionStatementAST extends StatementAST {

    private final WindowSizeAST every;
    private final List<FieldSelectionAST> fields;
    private final GroupByAST groupBy;
    private final ExpressionAST having;
    private final WindowSizeAST upto;
    private final OnEmptySelection oes;
    private final SamplingAST sampling;
    private final ExpressionAST where;
    private final ExecutionConditionsAST execCond;
    private final WindowSizeAST terminate;

    public SelectionStatementAST(WindowSizeAST every,
            List<FieldSelectionAST> fields,
            GroupByAST groupBy,
            ExpressionAST having,
            WindowSizeAST upto,
            OnEmptySelection oes,
            SamplingAST sampling,
            ExpressionAST where,
            ExecutionConditionsAST execCond,
            WindowSizeAST terminate) {
        this(null, every, fields, groupBy, having, upto, oes, sampling,
                where, execCond, terminate);
    }

    public SelectionStatementAST(Token t,
            WindowSizeAST every,
            List<FieldSelectionAST> fields,
            GroupByAST groupBy,
            ExpressionAST having,
            WindowSizeAST upto,
            OnEmptySelection oes,
            SamplingAST sampling,
            ExpressionAST where,
            ExecutionConditionsAST execCond,
            WindowSizeAST terminate) {
        super(t);
        this.every = every;
        this.fields = Collections.unmodifiableList(fields);
        this.groupBy = groupBy;
        this.having = having;
        this.upto = upto;
        this.oes = oes;
        this.sampling = sampling;
        this.where = where;
        this.execCond = execCond;
        this.terminate = terminate;
    }

    public WindowSizeAST getEvery() {
        return every;
    }

    public List<FieldSelectionAST> getFields() {
        return fields;
    }

    public GroupByAST getGroupBy() {
        return groupBy;
    }

    public ExpressionAST getHaving() {
        return having;
    }

    public WindowSizeAST getUpto() {
        return upto;
    }

    public OnEmptySelection getOnEmptySelection() {
        return oes;
    }

    public SamplingAST getSamplingAST() {
        return sampling;
    }

    public ExpressionAST getWhere() {
        return where;
    }

    public ExecutionConditionsAST getExecutionConditions() {
        return execCond;
    }

    public WindowSizeAST getTerminateAfter() {
        return terminate;
    }

    @Override
    public SelectionStatement compile(ParserContext ctx) {
        AttributeOrder selAtts = new AttributeOrder();
        WindowSize everyComp = every.compile(ctx);

        List<FieldSelection> fieldsComp = new ArrayList<>();
        for (FieldSelectionAST fs : fields) {
            Expression f = fs.getField().compile(DataType.ANY, ctx, selAtts);
            Expression d = fs.getDefault().evalConstant(ctx);
            fieldsComp.add(new FieldSelection(f, d));
        }

        //TODO: group by

        Expression havingComp = having.compile(DataType.BOOLEAN, ctx, selAtts);
        WindowSize uptoComp = upto.compile(ctx);
        Sampling samplingComp = sampling.compile(ctx);

        WindowSize terminateComp = terminate.compile(ctx);

        throw new RuntimeException("unimplemented");
    }

}
