package cbit.vcell.modeldb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.BioModelInfo;
import cbit.vcell.mapping.FeatureMapping;
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.parser.Expression;
import cbit.vcell.solver.Simulation;
import cbit.vcell.xml.Xmlproducer;

public class StochasticBioModelScanner implements VCDatabaseVisitor {

	public boolean filterBioModel(BioModelInfo bioModelInfo) {
		return true;
	}

	public void visitBioModel(BioModel bioModel, PrintStream arg_p) {
		PrintStream p=arg_p;
		p.println("visiting bio-model <"+bioModel.getName()+">------------------------------");
		String msg =bioModel.isValidForStochApp();
		if(msg.equals("ok"))
		{
			p.println("Total species:"+bioModel.getModel().getSpeciesContexts().length);
			for(int i=0;i<bioModel.getModel().getSpeciesContexts().length;i++)
				p.print(bioModel.getModel().getSpeciesContexts()[i].getName()+"\t");
			p.println();
			p.println("Total reactions:"+bioModel.getModel().getReactionSteps().length);
			for(int i=0; i<bioModel.getModel().getReactionSteps().length;i++)
				p.println(bioModel.getModel().getReactionSteps()[i].getName()+"\t"+bioModel.getModel().getReactionSteps()[i].getKinetics().getKineticsDescription().getName());
			p.println();
			String stochName = bioModel.getName()+"StochApp";
			p.println("Add a new stochastic simulation:"+stochName);
			try
			{
				SimulationContext sc = bioModel.addNewSimulationContext(stochName, true);
				p.println("Add a new simulation and meanwhile create math description");
				StructureMapping[] sm = sc.getGeometryContext().getStructureMappings();
				for(int i=0;i<sm.length;i++)
				{
					if(sm[i] instanceof FeatureMapping) ((FeatureMapping)sm[i]).getSizeParameter().setExpression(new Expression(1));
					else if(sm[i] instanceof MembraneMapping) ((MembraneMapping)sm[i]).getSizeParameter().setExpression(new Expression(1));
						
				}
				Simulation sim = sc.addNewSimulation();
				p.println("Simulation Context-"+sc.getName()+" math description:");
				p.println(sim.getMathDescription().getVCML());
				p.println("end of simualtion context-"+sc.getName());
				p.println();
			}catch (Exception ex){
				System.err.println(ex.getMessage());
				p.println("Error:"+ex.getMessage());
				p.println("Failed to add new stochastic simulation"+stochName);
			}
		}
		else
		{
			p.println("can not creat stochastic application for the model.\n Error:"+msg);
		}
//		SimulationContext[] simContexts = bioModel.getSimulationContexts();
//		for (int i = 0; i < simContexts.length; i++) {
//			if (simContexts[i].getGeometry().getDimension()==0 && simContexts[i].isStoch()==false){
				//
				// create a new SimulationContext that is a copy of this one and generate a stochastic math.
				// you will have to automatically specify (solve-for) the unknown structure sizes.
				// report any errors in math generation
				//
				//start writing log file for this application
//				p.println("visiting application :"+simContexts[i].getName());	
//				
//				//end of log for this application
//				System.out.println("processing simContext["+simContexts[i].getName()+"] in BioModel["+bioModel.getVersion()+"]");
//			}
//		}
		p.println("end of bio-model <"+bioModel.getName()+">");
		p.println();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		StochasticBioModelScanner visitor = new StochasticBioModelScanner();
		boolean bAbortOnDataAccessException = false;
		try{
			VCDatabaseScanner.scanBioModels(args, visitor, bAbortOnDataAccessException);
		}catch(Exception e){e.printStackTrace(System.err);}
	}

}
