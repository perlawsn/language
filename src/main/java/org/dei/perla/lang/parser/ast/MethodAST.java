package org.dei.perla.lang.parser.ast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.dei.perla.core.fpc.DataType;
import org.dei.perla.core.fpc.DataType.ConcreteType;
import org.dei.perla.lang.parser.AttributeOrder;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;
import org.dei.perla.lang.parser.ast.ExpressionAST;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.expression.MethodExp;
import org.dei.perla.lang.utility.Utility;


/*
 * A function requested by the user
 * The class contains the complete path of the method (package.className.method)  
 */
public class MethodAST extends ExpressionAST{

	private String methodName;
	private DataType type;
	private boolean inferenced = false;
	
	private MethodAST(Token token, String methodName, DataType type) {
		super(token);
		this.methodName = methodName;
		this.type = type;
	}
	
	private MethodAST(String className, String methodName) {
		super(null);
		this.methodName = methodName;
	}
	
//	public static MethodAST create(Token token, String methodName, ParserContext ctx){
//		int index = methodName.lastIndexOf( '.' );
//		int len = methodName.length();
//		String className = methodName.substring(0,index);
//		String function = methodName.substring(index+1, len);
//		Class classe = Class.class;
//		try {
//			classe = Class.forName(className);
//		} catch (ClassNotFoundException e) {
//			ctx.addError(e.getMessage());
//			className = "";
//		}
//		Method getFunctionName = null;
//		DataType type = null;
//		try {
//			getFunctionName = classe.getDeclaredMethod(function, null);
//			Object result = getFunctionName.invoke(classe.newInstance());
//			String classResult = result.getClass().getCanonicalName();
//			index = classResult.lastIndexOf( '.' );
//			len = classResult.length();
//			String typeString = classResult.substring(index+1, len);
//			type = ConcreteType.parse(typeString);
//			if(type == null) 
//				ctx.addError("The function returns an unknown type. The available types are: "
//						+ "String, Integer, Boolean, Id, Float, TimeStamp");
//		} catch (NoSuchMethodException | SecurityException | IllegalAccessException |
//				IllegalArgumentException | InvocationTargetException | InstantiationException e) {
//			ctx.addError(e.getMessage());
//		}
//		return new MethodAST(token, className, function, type);
//	}
	
	public static MethodAST create(Token token, String function, ParserContext ctx){
		DataType type = null;
		if(function.contains("()")){
			int len = function.length()-2;
			function = function.substring(0, len);
		}
		Utility u = Utility.getInstance();
		if(!u.existsFunction(function)){
			ctx.addError("The function " + function + " does not exist");
		}
		else {
			Object result = u.retrieveValueFunction(function, null);
			if (result == null) 
				ctx.addError("The function " + function + " returned a null result");
			else {
				String classResult = result.getClass().getCanonicalName();
				int index = classResult.lastIndexOf( '.' );
				int len = classResult.length();
				String typeString = classResult.substring(index+1, len);
				type = ConcreteType.parse(typeString);
				if(type == null) 
					ctx.addError("The function returns an unknown type. The available types are: "
							+ "String, Integer, Boolean, Id, Float, TimeStamp");
			}		
		}
		return new MethodAST(token, function, type);
	}
	
	
	public static MethodAST create(String methodName, ParserContext ctx){
		return MethodAST.create(null, methodName, ctx);
	}

    @Override
    protected void setType(TypeVariable type) {
        throw new IllegalStateException("Cannot set type to MethodAST node");
    }
	
    public DataType getType() {
        return type;
    }
    
	@Override
	protected boolean inferType(TypeVariable bound, ParserContext ctx) {
        if (inferenced) {
            throw new IllegalStateException("Type inference already performed");
        }
        inferenced = true;
        boolean res = true;
        if (!bound.restrict(type)) {
            String msg = "Incompatible type for return value of function '" + methodName ;
            ctx.addError(msg);
            res = false;
        }
        type = bound.getType();
        return res;
	}

	@Override
	protected Expression toExpression(ParserContext ctx, AttributeOrder ord) {
		return MethodExp.create(methodName, type); 
	}


	
}
