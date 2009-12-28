package br.ufrj.cos.lens.odyssey.tools.charon.util;

public class CharonUtil {

	/**
	 * <i>Constante.</i>
	 */
	public static final String ACTIVITY = "1";

	/**
	 * <i>Constante.</i>
	 */
	public static final String PROCESS = "2";
	
	/**
	 * <i>Constante.</i>
	 */
	public static final String SYNCHRONISM = "3";

	/**
	 * <i>Constante.</i>
	 */
	public static final String DECISION = "4";
	
	/**
	 * <i>Constante.</i>
	 */
	public static final String INITIAL = "5";
	
	/**
	 * <i>Constante.</i>
	 */
	public static final String FINAL = "6";
	
	/**
	 * <i>Constante.</i>
	 */
	public static final String PRODUCT = "7";
	
	/**
	 * <i>Constante.</i>
	 */
	public static final String OPTION = "8";
	
	/**
	 * <i>Constante.</i>
	 */
	public static final String EXECUTING_STATUS = "1";
	
	/**
	 * <i>Constante.</i>
	 */
	public static final String EXECUTED_STATUS = "2";
	
	public static String createElement(String elementType, String elementId){
		
		final String a = elementType; 
		if(elementType.equals(ACTIVITY))
			return "activity('"+elementType+"')";
		else
		if(elementType.equals(PROCESS))
			return "process('"+elementType+"')";
		else
		if(elementType.equals(DECISION))
			return "decision('"+elementType+"')";
		else
		if(elementType.equals(SYNCHRONISM))
			return "synchronism('"+elementType+"')";
		else
		if(elementType.equals(INITIAL))
			return "initial('"+elementType+"')";
		else
		if(elementType.equals(FINAL))
			return "final('"+elementType+"')";
		else
		if(elementType.equals(PRODUCT))
			return "product('"+elementType+"')";
		else return null;
	}
	
	public static String createContextList(String context){
		StringBuffer contextList = new StringBuffer("[");
		
		for (String element : context.split(",")) {
			contextList.append("process('"+element+"'),");
		}
		
		if(contextList.length()>1)
			return contextList.substring(0, contextList.length()-1)+"]";
		else
			return contextList.toString()+"]";
	}
	
}
