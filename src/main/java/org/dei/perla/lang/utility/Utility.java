package org.dei.perla.lang.utility;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Utility {

	private static Set<String> functions;
	private static Utility utility;
	
	private Utility(){
		functions = new HashSet<String>();
		addFunction("getRoleId");
		addFunction("getMonth");
		addFunction("getIdCompagnia");
		addFunction("getUserId");
	}
	
	public static Utility getInstance(){
		if(utility == null)
			utility = new Utility();
		return utility;
	}
	
	private void addFunction(String functionName){
		functions.add(functionName);
	}
	
	public boolean existsFunction(String functionName){
		return functions.contains(functionName);
	}
	
	public Object retrieveValueFunction(String nameFunction, Object[] param){
		Object result = null;
		try{
		 Class<?> c = Class.forName("org.dei.perla.lang.utility.Utility");
		    Object t = c.newInstance();

		    Method[] allMethods = c.getDeclaredMethods();
		   
		    for (Method m : allMethods) {
				String mname = m.getName();
		//		System.out.println(mname + " count " + m.getParameterCount());
		//		System.out.println("tipo parametri " + param[0].getClass());
		//		System.out.println("tipo parametri " + param[1].getClass());
				if(mname.equals(nameFunction)){	
					if(param == null){
						result = m.invoke(t);
						break;
					}
					else if(param.length == m.getParameterCount()){
				
						Type[] types = m.getGenericParameterTypes();
//						for(Type type: types){
//							System.out.println(type.getTypeName() + " " + type.getClass());
						result = m.invoke(t, param);
						break;
					}
				}
	 		} 
		}catch (ClassNotFoundException x) {
	 		    x.printStackTrace();
	 		} catch (InstantiationException x) {
	 		    x.printStackTrace();
	 		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException x) {
	 		    x.printStackTrace();
	 		} 
		return result;
	}
	
	
	public String getRoleId(){
		String[] roles = new String[]{"SkyInstructor", "SnowGroomer", "BaseStation", "RescueSquad"};
		Random rand = new Random();
		int index = rand.nextInt(4);
		return roles[index];
	}
	
	public String getIdCompagnia(){
		return "idCompagnia";
	}
	
	public String getIdApparecchio(){
		return "idApparecchio";
	}
	
	public String getUserId(){
		return "ab";
	}
	
	public int getMonth(){
		return Calendar.getInstance().get(Calendar.MONTH);
	}

}

