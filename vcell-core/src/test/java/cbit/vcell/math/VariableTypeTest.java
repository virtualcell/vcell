package cbit.vcell.math;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Disabled
@Tag("Fast")
public class VariableTypeTest {

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
