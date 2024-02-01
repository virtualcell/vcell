package cbit.vcell.biomodel;

import java.util.Random;

/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryTest;
import cbit.vcell.geometry.GeometryThumbnailImageFactoryAWT;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Model;
import cbit.vcell.model.ModelTest;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import cbit.vcell.solver.simulation.Simulation;
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
	bioModel.setSimulationContexts(new SimulationContext[] { sc1, sc2 });
	bioModel.updateAll(false);

	//
	// add simulations (must be after 
	//
	Simulation sim1 = new Simulation(sc1.getMathDescription(), sc1);
	sim1.setName("sim1_"+Integer.toHexString(((new Random()).nextInt())));
	Simulation sim2 = new Simulation(sc1.getMathDescription(), sc1);
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
	Model model = bioModel.getModel();
	model.setName("physiology_"+Integer.toHexString(((new Random()).nextInt())));
	Geometry geo = GeometryTest.getImageExample2D();
	geo.setName("Image_Geometry_"+Integer.toHexString(((new Random()).nextInt())));
	geo.precomputeAll(new GeometryThumbnailImageFactoryAWT(),true,false);
	
	SimulationContext sc2 = new SimulationContext(model,geo);
	GeometryContext geoContext = sc2.getGeometryContext();
	
	Structure structure_ec = model.getStructure("extracellular");
	SubVolume subVolume_cytosol = geo.getGeometrySpec().getSubVolume("cytosol");
	geoContext.assignStructure(structure_ec,subVolume_cytosol);
	geoContext.getStructureMapping(structure_ec).getUnitSizeParameter().setExpression(new Expression(1.0));
	
	Structure structure_cyt = model.getStructure("cytosol");
	SubVolume subVolume_er = geo.getGeometrySpec().getSubVolume("er");
	geoContext.assignStructure(structure_cyt,subVolume_er);
	geoContext.getStructureMapping(structure_cyt).getUnitSizeParameter().setExpression(new Expression(0.5));
	
	Structure structure_er = model.getStructure("er");
	geoContext.assignStructure(structure_er,subVolume_er);
	geoContext.getStructureMapping(structure_er).getUnitSizeParameter().setExpression(new Expression(0.5));
	
	Structure structure_pm = model.getStructure("plasmaMembrane");
	SurfaceClass surfaceClass_erMem = geoContext.getGeometry().getGeometrySurfaceDescription().getSurfaceClass(subVolume_cytosol, subVolume_er);
	geoContext.assignStructure(structure_pm, surfaceClass_erMem);
	geoContext.getStructureMapping(structure_pm).getUnitSizeParameter().setExpression(new Expression(1.0));
	
	Structure structure_erMem = model.getStructure("erMembrane");
	geoContext.assignStructure(structure_erMem, subVolume_er);
	geoContext.getStructureMapping(structure_erMem).getUnitSizeParameter().setExpression(new Expression(2.4));
	

	bioModel.setSimulationContexts(new SimulationContext[] { /*sc1,*/ sc2 });
	sc2.setName("simContext2_"+Integer.toHexString(((new Random()).nextInt())));
//	sc1.setMathDescription(sc1.createNewMathMapping().getMathDescription());
	bioModel.updateAll(false);

	//
	// add simulations (must be after 
	//
	Simulation sim1 = new Simulation(sc2.getMathDescription(), sc2);
	sim1.setName("sim1_"+Integer.toHexString(((new Random()).nextInt())));
	Simulation sim2 = new Simulation(sc2.getMathDescription(), sc2);
	sim2.setName("sim2_"+Integer.toHexString(((new Random()).nextInt())));
	bioModel.setSimulations(new Simulation[] { sim1, sim2 });
	return bioModel;
}

	public static BioModel getSimpleExampleWithImage(boolean bIncludeER) throws Exception {

		BioModel bioModel = new BioModel(null);
		bioModel.setModel(ModelTest.getExample_Wagner_simple(bIncludeER));
		Model model = bioModel.getModel();
		Structure structure_ec = model.getStructure("extracellular");
		Structure structure_cyt = model.getStructure("cytosol");
		Structure structure_pm = model.getStructure("plasmaMembrane");

		// create a spatial geometry (image-based)
		Geometry geo = GeometryTest.getImageExample2D();
		SubVolume subVolume_cytosol = geo.getGeometrySpec().getSubVolume("cytosol");
		SubVolume subVolume_ec = geo.getGeometrySpec().getSubVolume("ec");
		geo.precomputeAll(new GeometryThumbnailImageFactoryAWT(),true,false);
		SurfaceClass surfaceClass_pm = geo.getGeometrySurfaceDescription().getSurfaceClass(subVolume_cytosol, subVolume_ec);

		//
		// add a spatial application
		//
		SimulationContext simContext = new SimulationContext(model, geo, null,null, SimulationContext.Application.NETWORK_DETERMINISTIC);
		simContext.setName("image_application");
		bioModel.setSimulationContexts(new SimulationContext[] { simContext });
		GeometryContext geoContext = simContext.getGeometryContext();

		//
		// map model structures to geometry
		//
		geoContext.assignStructure(structure_ec,subVolume_cytosol);
		geoContext.getStructureMapping(structure_ec).getUnitSizeParameter().setExpression(new Expression(1.0));

		geoContext.assignStructure(structure_cyt,subVolume_cytosol);
		geoContext.getStructureMapping(structure_cyt).getUnitSizeParameter().setExpression(new Expression(0.5));


		geoContext.assignStructure(structure_pm, surfaceClass_pm);
		geoContext.getStructureMapping(structure_pm).getUnitSizeParameter().setExpression(new Expression(1.0));

		if (bIncludeER) {
			Structure structure_er = model.getStructure("er");
			Structure structure_erMem = model.getStructure("erMembrane");

			geoContext.assignStructure(structure_er,subVolume_cytosol);
			geoContext.getStructureMapping(structure_er).getUnitSizeParameter().setExpression(new Expression(0.5));

			geoContext.assignStructure(structure_erMem, subVolume_cytosol);
			geoContext.getStructureMapping(structure_erMem).getUnitSizeParameter().setExpression(new Expression(2.4));
		}

		//
		// update everything - including math generation
		//  must do this before adding a simulation
		//
		bioModel.updateAll(false);

		//
		// add a simulation
		//
		Simulation sim1 = new Simulation(simContext.getMathDescription(), simContext);
		sim1.setName("sim1");
		bioModel.setSimulations(new Simulation[] { sim1 });
		return bioModel;
	}

}
