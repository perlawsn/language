package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.parser.AttributeOrder;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.query.expression.Constant;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.statement.SetParameter;
import org.dei.perla.lang.query.statement.SetStatement;
import org.dei.perla.lang.query.statement.Statement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Guido Rota 30/07/15.
 */
public final class SetStatementAST extends StatementAST {

    private final List<SetParameterAST> params;
    private final List<Integer> ids;

    public SetStatementAST(List<SetParameterAST> params, List<Integer> ids) {
        this(null, params, ids);
    }

    public SetStatementAST(Token token, List<SetParameterAST> params,
            List<Integer> ids) {
        super(token);
        this.params = Collections.unmodifiableList(params);
        this.ids = Collections.unmodifiableList(ids);
    }

    public List<SetParameterAST> getParameters() {
        return params;
    }

    public List<Integer> getIds() {
        return ids;
    }

    @Override
    public Statement compile(ParserContext ctx) {
    	List<SetParameter> paramsSet = new ArrayList<SetParameter>(params.size());
    	List<Integer> idsSet = new ArrayList<Integer>(ids);
    	for(SetParameterAST p: params){
    		Expression value = p.getValue().compile(DataType.ANY, ctx, new AttributeOrder());
    		if(!(value instanceof Constant))
    			ctx.addError("Set parameter expression must be constant");
    		else {
    			SetParameter pp = new SetParameter(p.getAttributeId(), value);
    			paramsSet.add(pp);
    		}
    	}
        return new SetStatement(paramsSet, idsSet);
    }

}
