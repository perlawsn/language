package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.OnEmptySelection;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.query.statement.Statement;

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
    private final HavingAST having;
    private final WindowSizeAST upto;
    private final OnEmptySelection oes;
    private final SamplingAST sampling;
    private final ExpressionAST where;
    private final ExecutionConditionsAST execCond;
    private final WindowSizeAST terminate;

    public SelectionStatementAST(WindowSizeAST every,
            List<FieldSelectionAST> fields,
            GroupByAST groupBy,
            HavingAST having,
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
            HavingAST having,
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

    public HavingAST getHaving() {
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
    public Statement compile(ParserContext ctx) {
        throw new RuntimeException("unimplemented");
    }

}
