package cbit.vcell.modeldb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Vector;

import cbit.util.Issue;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.BioModelInfo;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.mapping.FeatureMapping;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.StochMathMapping;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.mathmodel.MathModelInfo;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.model.ModelException;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.Simulation;
import cbit.vcell.xml.Xmlproducer;

public class StochasticBioModelScanner implements VCDatabaseVisitor {

	public boolean filterBioModel(BioModelInfo bioModelInfo) {
		if (bioModelInfo.getVersion().getOwner().getName().equals("anu")) {
			// System.err.println("BM name : " + bioModelInfo.getVersion().getName() + "\tdate : " + bioModelInfo.getVersion().getDate().toString());
			return true;
		}
		return false;
	}

	public void visitBioModel(BioModel bioModel, PrintStream arg_p) {
		PrintStream p = arg_p;
		
		// If biomodel has stochastic application(s), 
		// 		get math description for each stochastic application, 
		//		regenerate math for stochastic application
		//		compare equivalency of the two.
		
		SimulationContext[] simContexts = bioModel.getSimulationContexts();
		for (int i = 0; i < simContexts.length; i++) {
			if (simContexts[i].isStoch()) {
				p.println("\nVisiting User : '" + bioModel.getVersion().getOwner().getName() + "' ; bio-model <"+bioModel.getName()+">------------");
				p.println("\tVersion : <"+bioModel.getVersion().getDate().toString()+">------------");
				MathDescription old_mathDesc = simContexts[i].getMathDescription();
				MathDescription new_mathDesc = null;
				StochMathMapping stochMathMapping = new StochMathMapping(simContexts[i]); 
				try {
					new_mathDesc = stochMathMapping.getMathDescription();
					// print out the math? 
				} catch (Throwable e) {
					e.printStackTrace();
					p.println("Error Generating Math for application '" + simContexts[i] + "' : " + e.getMessage());
				}
				
				StringBuffer strBuffer = new StringBuffer();
				String equivalency = "unknown";
				if (old_mathDesc != null && new_mathDesc != null) {
					equivalency = MathDescription.testEquivalency(old_mathDesc, new_mathDesc, strBuffer);					
					p.println("\t\tExisting and Regenerated MathDescriptions for simContext '" + simContexts[i].getName() + "' are : " + equivalency.toUpperCase());

					Issue mathMappingIssues[] = stochMathMapping.getIssues(); 
					if (mathMappingIssues != null && mathMappingIssues.length >0) {
						// gather model issues
						Vector<Issue> modelIssueList = new Vector<Issue>();
						bioModel.gatherIssues(modelIssueList);
						StringBuffer buffer = new StringBuffer("Biomodel Issues ("+modelIssueList.size()+") : ");
						for (int jj = 0; jj < modelIssueList.size(); jj++){
							Issue issue = (cbit.util.Issue)modelIssueList.elementAt(jj);
							buffer.append("\n<<"+issue.toString()+">>");
						}
						// gather mathMapping issues
						buffer.append("\n\nMathMapping Issues ("+mathMappingIssues.length+") : ");
						for (int l = 0; l < mathMappingIssues.length; l++){
							buffer.append("\n<<"+mathMappingIssues[l].toString()+">>");
						}
						String issueString = buffer.toString();
						p.println("\nISSUES : " + issueString);
					}
//					if (equivalency.equals(MathDescription.MATH_DIFFERENT)) {
//						try {
//							p.println("\n\nBEGINMATH:\n\n" + old_mathDesc.getVCML_database() + "\n\nENDMATH\n\n");
//							p.println("\n\nBEGINMATH:\n\n" + new_mathDesc.getVCML_database() + "\n\nENDMATH\n\n");
//						} catch (MathException e) {
//							e.printStackTrace();
//						}
//					}
				} else {
					if (old_mathDesc == null) {
						p.println("\t\tExisting MathDescription for simContext '" + simContexts[i].getName() + "' is NULL : " + equivalency.toUpperCase());
					} else if (new_mathDesc == null) {
						p.println("\t\tRegenerated MathDescription for simContext '" + simContexts[i].getName() + "' is NULL : " + equivalency.toUpperCase());
					}
				}
			}
		}
		
		
		
/*		String msg =bioModel.isValidForStochApp();
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
*/
//		p.println("end of bio-model <"+bioModel.getName()+"--------------->");
//		p.println();
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		StochasticBioModelScanner visitor = new StochasticBioModelScanner();
		boolean bAbortOnDataAccessException = false;
		try{
			VCDatabaseScanner.scanBioModels(args, visitor, bAbortOnDataAccessException);
		}catch(Exception e){
			e.printStackTrace(System.err);
		}
	}

	//
	// required for implementation of interface ... not used.
	//
	
	public boolean filterGeometry(GeometryInfo geometryInfo) {
		return false;
	}

	public void visitGeometry(Geometry geometry, PrintStream arg_p) {
	}

	public boolean filterMathModel(MathModelInfo mathModelInfo) {
		return false;
	}

	public void visitMathModel(MathModel mathModel, PrintStream logFilePrintStream) {
	}

	
}
