package org.vcell.sbml;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.sbml.libsbml.libsbmlConstants;

public class LibSBMLConstantsAdapter {
	
	private static Map<Integer,String> constantsMap = null; 
	
	
	private LibSBMLConstantsAdapter() {
	}
	
	private static void create( ) {
		constantsMap = new HashMap<Integer, String>();
		for (Field field: libsbmlConstants.class.getDeclaredFields()) {
			int m = field.getModifiers();
			if (Modifier.isStatic(m) && Modifier.isFinal(m)) {
					try {
						Object o = field.get(null);
						if (o instanceof Integer) {
							Integer i = (Integer) o;
							constantsMap.put(i, field.getName());
						}
					} catch (IllegalArgumentException | IllegalAccessException e) {
					}
				}
		}
	}
	
	/**
	 * convert libsbml code into symbol
	 * @param i
	 * @return symbol or i as String if not found
	 */
	public static String lookup(int i) {
		if (constantsMap == null) {
			create( );
		}
		Integer iobj = Integer.valueOf(i);
		if (constantsMap.containsKey(iobj)) {
			return constantsMap.get(iobj);
		}
		return iobj.toString();
		
	}
	
	/**
	 * if i != {@link libsbmlConstants#LIBSBML_OPERATION_SUCCESS}, log warning message 
	 * @param lg logger to log to 
	 * @param i libsbml return code
	 */
	public static void checkReturnCode(Logger lg, int i) {
		if (i == libsbmlConstants.LIBSBML_OPERATION_SUCCESS) {
			return;
		}
		String msg = "SBML Error " + lookup(i);
		lg.warn(msg);
	}
	
	/**
	 * @param code
	 * @return code = = {@link libsbmlConstants#LIBSBML_OPERATION_SUCCESS}  
	 */
	public static boolean validReturn(int code) {
		return code == libsbmlConstants.LIBSBML_OPERATION_SUCCESS;
	}

	/**
	 * if i != {@link libsbmlConstants#LIBSBML_OPERATION_SUCCESS}  throw exception
	 * @param dest, may be null if throwException is true
	 * @param i libsbml return code
	 * @throws SbmlException 
	 */
	public static void throwIfError(int i) throws SbmlException {
		if (validReturn(i)) {
			return;
		}
		String msg = "SBML Error " + lookup(i);
		throw new SbmlException(msg);
	}
	
	

}
