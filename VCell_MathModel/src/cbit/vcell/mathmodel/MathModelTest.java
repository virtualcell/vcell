package cbit.vcell.mathmodel;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.math.MathDescription;
import cbit.vcell.simulation.*;

import java.util.Random;
/**
 * Insert the type's description here.
 * Creation date: (11/14/00 5:56:27 PM)
 * @author: Jim Schaff
 */
public class MathModelTest {
/**
 * Insert the method's description here.
 * Creation date: (11/14/00 5:56:41 PM)
 * @return cbit.vcell.biomodel.BioModel
 */
public static MathModel getExample() throws Exception {

	MathModel mathModel = new MathModel(null);
	mathModel.setName("mathModel_"+Integer.toHexString(((new Random()).nextInt())));

	//
	// add MathDescription
	//
	MathDescription mathDesc = cbit.vcell.math.MathDescriptionTest.getImageExample();
	mathModel.setMathDescription(mathDesc);

	//
	// add simulations (must be after 
	//
	Simulation sim1 = new Simulation(mathModel.getMathDescription());
	sim1.setName("sim1_"+Integer.toHexString(((new Random()).nextInt())));
	Simulation sim2 = new Simulation(mathModel.getMathDescription());
	sim2.setName("sim2_"+Integer.toHexString(((new Random()).nextInt())));
	mathModel.setSimulations(new Simulation[] { sim1, sim2 });
	return mathModel;
}
}
