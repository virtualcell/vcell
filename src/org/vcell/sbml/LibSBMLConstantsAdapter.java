package org.vcell.sbml;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.sbml.libsbml.libsbmlConstants;

public class LibSBMLConstantsAdapter {
	
	private static Map<Integer,List<String> > constantsMap = null; 
	
	
	private LibSBMLConstantsAdapter() {
	}
	
	private static void create( ) {
		constantsMap = new HashMap<Integer, List<String> >();
		for (Field field: libsbmlConstants.class.getDeclaredFields()) {
			int m = field.getModifiers();
			if (Modifier.isStatic(m) && Modifier.isFinal(m)) {
					try {
						Object o = field.get(null);
						if (o instanceof Integer) {
							Integer i = (Integer) o;
							if (!constantsMap.containsKey(i)) {
								constantsMap.put(i, new ArrayList<String>( ));
							}
							constantsMap.get(i).add(field.getName());
							System.out.println("case " + field.getName( ) + ':');
						}
					} catch (IllegalArgumentException | IllegalAccessException e) {
					}
				}
		}
	}
	
	/**
	 * get matching list
	 * @param i
	 * @return matching String or empty list
	 */
	private static List<String> retrieve(int i) {
		if (constantsMap == null) {
			create( );
		}
		Integer iobj = Integer.valueOf(i);
		if (constantsMap.containsKey(iobj)) {
			return constantsMap.get(iobj);
		}
		return Collections.emptyList();
	}
	
	/**
	 * convert libsbml code into symbol
	 * @param i
	 * @return symbol or i as String if not found
	 */
	public static String lookup(int i) {
		List<String> list = retrieve(i);
		if (!list.isEmpty()) {
			return StringUtils.join(",", list); 
		}
		return Integer.toString(i);
	}
	/**
	 * convert libsbml code into symbol 
	 * @param i
	 * @param filter substring to match, if multiple occurrences present 
	 * @return arbitrary matching constant String or i if no match
	 */
	public static String lookup(int i, String filter) {
		List<String> list = retrieve(i);
		for (String s : list) {
			if (s.contains(filter)) {
				return s;
			}
		}
		return Integer.toString(i);
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
