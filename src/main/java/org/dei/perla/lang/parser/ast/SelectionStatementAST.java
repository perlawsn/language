package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.parser.*;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.statement.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

        List<Expression> fieldsComp = new ArrayList<>();
        Object[] def = new Object[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            FieldSelectionAST fs = fields.get(i);
            FieldSelection fsComp = fs.compile(
                    DataType.ANY, ctx, selAtts);
            fieldsComp.add(fsComp.getField());
            def[i] = fsComp.getDefault();
        }

        GroupBy groupByComp = compileGroupBy(ctx);
        Expression havingComp = having.compile(DataType.BOOLEAN, ctx, selAtts);
        Expression whereComp = where.compile(DataType.BOOLEAN, ctx, selAtts);
        WindowSize uptoComp = upto.compile(ctx);
        Sampling samplingComp = sampling.compile(ctx);
        WindowSize terminateComp = null;
        if (terminate != null) {
            terminateComp = terminate.compile(ctx);
        }

        ExecutionConditions condComp = execCond.compile(ctx);

        Select sel = new Select(fieldsComp, everyComp, uptoComp, havingComp,
                def);

        List<Attribute> compAttList = selAtts.toList(ctx);
        if (!compAttList.contains(Attribute.TIMESTAMP)) {
            compAttList = new ArrayList<>(compAttList);
            compAttList.add(Attribute.TIMESTAMP);
        }
        return new SelectionStatement(sel, compAttList, groupByComp,
                samplingComp, whereComp, condComp, terminateComp);
    }

    private GroupBy compileGroupBy(ParserContext ctx) {
        if (groupBy == null) {
            return null;
        }

        Set<String> selFields = fields.parallelStream()
                .map(FieldSelectionAST::getField)
                .filter(e -> e instanceof AttributeReferenceAST)
                .map(e -> ((AttributeReferenceAST) e).getId())
                .collect(Collectors.toSet());

        List<String> missing = groupBy.getFields().parallelStream()
                .filter(selFields::contains)
                .collect(Collectors.toList());

        if (missing.size() != 0) {
            for (String m : missing) {
                ctx.addError("GROUP BY attribute '" + m + "' is not declared " +
                        "as a SELECT field");
            }
            return null;
        }

        return new GroupBy(groupBy.getFields());
    }

}
