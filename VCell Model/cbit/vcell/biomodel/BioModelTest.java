package cbit.vcell.biomodel;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.geometry.*;
import cbit.vcell.simulation.*;

import java.util.Random;
import cbit.vcell.mapping.*;
/**
 * Insert the type's description here.
 * Creation date: (11/14/00 5:56:27 PM)
 * @author: Jim Schaff
 */
public class BioModelTest {
/**
 * Insert the method's description here.
 * Creation date: (11/14/00 5:56:41 PM)
 * @return cbit.vcell.biomodel.BioModel
 */
public static BioModel getExample() throws Exception {

	BioModel bioModel = new BioModel(null);
	bioModel.setName("bioModel_"+Integer.toHexString(((new Random()).nextInt())));

	//
	// add SimulationContexts
	//	
	SimulationContext sc1 = cbit.vcell.mapping.SimulationContextTest.getExample(2);
	sc1.setName("simContext1_"+Integer.toHexString(((new Random()).nextInt())));
	bioModel.setModel(sc1.getModel());
	bioModel.getModel().setName("physiology_"+Integer.toHexString(((new Random()).nextInt())));
	SimulationContext sc2 = new SimulationContext(bioModel.getModel(),new Geometry("0-D Geometry_"+Integer.toHexString(((new Random()).nextInt())),0));
	sc2.setName("simContext2_"+Integer.toHexString(((new Random()).nextInt())));
	sc1.setMathDescription((new MathMapping(sc1)).getMathDescription());
	sc2.setMathDescription((new MathMapping(sc2)).getMathDescription());
	bioModel.setSimulationContexts(new SimulationContext[] { sc1, sc2 });

	//
	// add simulations (must be after 
	//
	Simulation sim1 = new Simulation(sc1.getMathDescription());
	sim1.setName("sim1_"+Integer.toHexString(((new Random()).nextInt())));
	Simulation sim2 = new Simulation(sc1.getMathDescription());
	sim2.setName("sim2_"+Integer.toHexString(((new Random()).nextInt())));
	bioModel.setSimulations(new Simulation[] { sim1, sim2 });
	return bioModel;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/00 5:56:41 PM)
 * @return cbit.vcell.biomodel.BioModel
 */
public static BioModel getExampleWithImage() throws Exception {

	BioModel bioModel = new BioModel(null);
	bioModel.setName("bioModel_"+Integer.toHexString(((new Random()).nextInt())));

	//
	// add SimulationContexts
	//	
	SimulationContext sc1 = cbit.vcell.mapping.SimulationContextTest.getExample(2);
	sc1.setName("simContext1_"+Integer.toHexString(((new Random()).nextInt())));
	bioModel.setModel(sc1.getModel());
	bioModel.getModel().setName("physiology_"+Integer.toHexString(((new Random()).nextInt())));
	Geometry geo = GeometryTest.getImageExample2D();
	geo.setName("Image_Geometry_"+Integer.toHexString(((new Random()).nextInt())));
	SimulationContext sc2 = new SimulationContext(bioModel.getModel(),geo);
	sc2.getGeometryContext().assignFeature(((cbit.vcell.model.Feature)bioModel.getModel().getStructure("extracellular")),geo.getGeometrySpec().getSubVolume("er"));
	sc2.getGeometryContext().assignFeature(((cbit.vcell.model.Feature)bioModel.getModel().getStructure("cytosol")),geo.getGeometrySpec().getSubVolume("cytosol"));
	sc2.getGeometryContext().assignFeature(((cbit.vcell.model.Feature)bioModel.getModel().getStructure("er")),geo.getGeometrySpec().getSubVolume("cytosol"));
	sc2.setName("simContext2_"+Integer.toHexString(((new Random()).nextInt())));
	sc1.setMathDescription((new MathMapping(sc1)).getMathDescription());
	sc2.setMathDescription((new MathMapping(sc2)).getMathDescription());
	bioModel.setSimulationContexts(new SimulationContext[] { sc1, sc2 });

	//
	// add simulations (must be after 
	//
	Simulation sim1 = new Simulation(sc1.getMathDescription());
	sim1.setName("sim1_"+Integer.toHexString(((new Random()).nextInt())));
	Simulation sim2 = new Simulation(sc1.getMathDescription());
	sim2.setName("sim2_"+Integer.toHexString(((new Random()).nextInt())));
	bioModel.setSimulations(new Simulation[] { sim1, sim2 });
	return bioModel;
}
}
