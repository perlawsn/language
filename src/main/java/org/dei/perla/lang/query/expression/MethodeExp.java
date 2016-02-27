package org.dei.perla.lang.query.statement;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.executor.buffer.BufferView;
import org.dei.perla.lang.query.expression.Expression;

public class MethodExp extends Expression {
	
	private final String methodName;
	private final String className; 
	private final DataType type;
	
	private MethodExp(String methodName, String className, DataType type) {
		this.methodName = methodName;
		this.className = className;
		this.type = type;
	}

	@Override
	public DataType getType() {
		return type;
	}

	
	@Override
	public Object run(Object[] sample, BufferView buffer) {
		Class classe = Class.class;
		try {
			classe = Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
		Method getFunctionName;
		try {
			getFunctionName = classe.getDeclaredMethod(methodName, null);
		} catch (NoSuchMethodException | SecurityException e) {
			return null;
		}
		Object result;
		try {
			result = getFunctionName.invoke(classe.newInstance());
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | InstantiationException e) {
			result = null;
		}
		return result;
	}

	@Override
    protected void buildString(StringBuilder bld) {
        bld.append("(")
                .append(className)
                .append(".")
                .append(methodName)
                .append(")");
    }

	
	public static MethodExp create(String className, String methodName, DataType type){
		return new MethodExp(className, methodName, type);
	}

}
