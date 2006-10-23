package org.vcell.ncbc.physics.engine;
import org.vcell.ncbc.physics.component.PhysicalModel;

import cbit.vcell.modelapp.SimulationContext;
import cbit.vcell.modelapp.SimulationContextTest;
/**
 * Insert the type's description here.
 * Creation date: (1/12/2004 1:35:08 AM)
 * @author: Jim Schaff
 */
public class MappingUtilitiesTest {
/**
 * Insert the method's description here.
 * Creation date: (1/9/2006 8:11:18 PM)
 * @return ncbc_old.physics.component.PhysicalModel
 */
public static PhysicalModel getExample() {
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (1/15/2004 3:25:48 PM)
 * @param args java.lang.String[]
 */

public static void main(String[] args) {
	try {
		SimulationContext simContext = SimulationContextTest.getExampleElectrical(3);
		org.vcell.ncbc.physics.component.PhysicalModel physicalModel = MappingUtilities.createFromSimulationContext(simContext);
	} catch (Throwable e) {
		e.printStackTrace(System.out);
	}
}
}