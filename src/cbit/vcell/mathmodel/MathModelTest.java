/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mathmodel;

import cbit.vcell.math.MathDescription;
import cbit.vcell.geometry.*;
import cbit.vcell.solver.*;
import java.util.Random;
import cbit.vcell.mapping.*;
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
