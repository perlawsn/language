package org.dei.perla.lang.query.expression;

import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.executor.buffer.BufferView;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.utility.Utility;

public class MethodExp extends Expression {
	
	private final String methodName;
	private final DataType type;
	
	private MethodExp(String methodName, DataType type) {
		this.methodName = methodName;
		this.type = type;
	}

	@Override
	public DataType getType() {
		return type;
	}

	
	@Override
	public Object run(Object[] sample, BufferView buffer) {
		Object result = null;
		Utility u = Utility.getInstance();
		if(!u.existsFunction(methodName)){
			System.out.println("The function " + methodName + " does not exist ");
			return result;
		}
		else {
			result = u.retrieveValueFunction(methodName, null);
			if (result == null) 
				System.out.println("The function " + methodName + " returned a null result");		
		}
		return result;
	}

	@Override
    protected void buildString(StringBuilder bld) {
        bld.append("(")
                .append(methodName)
                .append(")");
    }

	
	public static MethodExp create(String methodName, DataType type){
		return new MethodExp(methodName, type);
	}

}
