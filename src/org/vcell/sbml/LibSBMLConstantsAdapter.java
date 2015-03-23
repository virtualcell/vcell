package org.vcell.sbml;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

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
	 * display error message to stream if i != {@link libsbmlConstants#LIBSBML_OPERATION_SUCCESS}
	 * @param dest
	 * @param i
	 * @param throwException throw exception if i != success
	 * @throws RuntimeException if requested
	 */
	public static void displayIfError(OutputStream dest, int i, boolean throwException) {
		if (i == libsbmlConstants.LIBSBML_OPERATION_SUCCESS) {
			return;
		}
		PrintWriter pw = new PrintWriter(dest);
		String msg = "SBML Error " + lookup(i);
		pw.println(msg);
		if (throwException) {
			throw new RuntimeException(msg);
		}
		
	}
	
	

}
