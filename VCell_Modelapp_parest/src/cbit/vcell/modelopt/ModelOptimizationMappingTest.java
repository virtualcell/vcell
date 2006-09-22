package cbit.vcell.modelopt;
import cbit.vcell.math.MathException;
import cbit.vcell.mapping.MappingException;
/**
 * Insert the type's description here.
 * Creation date: (8/22/2005 9:51:02 AM)
 * @author: Jim Schaff
 */
public class ModelOptimizationMappingTest {
/**
 * Insert the method's description here.
 * Creation date: (8/22/2005 9:51:09 AM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {
		test();
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}		
}


/**
 * Insert the method's description here.
 * Creation date: (8/22/2005 9:52:30 AM)
 */
public static void test() throws MappingException, MathException {
	ModelOptimizationSpec modelOptSpec = ModelOptimizationSpecTest.getExample();
	ModelOptimizationMapping modelOptMapping = new ModelOptimizationMapping(modelOptSpec);
	cbit.vcell.opt.OptimizationSpec optSpec = modelOptMapping.getOptimizationSpec();
	System.out.println(optSpec.getVCML());
}
}