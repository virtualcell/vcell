package cbit.vcell.math;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class TestVariableType {

	@Test
	public void list( ) {
		for (int i = 0 ; i < 10; i++) {
			try {
			VariableType vt = VariableType.getVariableTypeFromInteger(i);
			System.out.println(i + " = " + vt.toString());
			} catch (IllegalArgumentException iae) {
				System.out.println(iae.getMessage());
			}
		}
	}
	
//	private String quote(String s) {
//		return '"' + s+ '"';
//	}
//	
//	@Test
//	public void gen( ) {
//		for (VariableType vt : VariableType.allTypes) {
//			char comma = ',';
//			System.out.println(Integer.toString(vt.getType()) + comma + quote(vt.getTypeName()) + comma + 
//					quote(vt.getDefaultUnits()) + comma + quote(vt.getDefaultLabel()));
//			
//		}
//		
//	}
}
