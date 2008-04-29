package cbit.vcell.mapping.test;

import java.io.PrintStream;

import org.vcell.sbml.vcell.StructureSizeSolver;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.BioModelInfo;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.StructureMapping.StructureMappingParameter;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathDescriptionTest;
import cbit.vcell.math.MathException;
import cbit.vcell.model.DistributedKinetics;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.LumpedKinetics;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.modeldb.VCDatabaseScanner;
import cbit.vcell.modeldb.VCDatabaseVisitor;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.units.VCUnitDefinition;

public class LumpedKineticsTester implements VCDatabaseVisitor {
	int count = 0;
	final int MAX = 1000;

	public boolean filterBioModel(BioModelInfo bioModelInfo) {
		//
		// only biomodels that have non-spatial applications.
		//
		if (bioModelInfo.getBioModelChildSummary()==null){
			return false;
		}
		int[] dims = bioModelInfo.getBioModelChildSummary().getGeometryDimensions();
		for (int i = 0; i < dims.length; i++) {
			if (dims[i] == 0){
				if (count++ < MAX){
					return true;
				}
			}
		}
		return false;
	}

	public void visitBioModel(BioModel bioModel, PrintStream p) {
		StringBuffer buffer = new StringBuffer();
		try {
			p.println("visiting bio-model <"+bioModel.getName()+","+bioModel.getVersion().getVersionKey()+">------------------------------");
			buffer.append("Distributed/Lumped Testing for bio-model <"+bioModel.getName()+","+bioModel.getVersion().getVersionKey()+"> ");
			//
			// get first nonspatial application and use it for testing
			//
			SimulationContext nonspatialSimContext = null;
			SimulationContext[] simContexts = bioModel.getSimulationContexts();
			for (int i = 0; i < simContexts.length; i++) {
				if (simContexts[i].getGeometry().getDimension() == 0){
					nonspatialSimContext = simContexts[i];
					break;
				}
			}
			
			MathDescription distMathDesc = null;
			try {
				distMathDesc = (new MathMapping(nonspatialSimContext)).getMathDescription();
			} catch (Exception e) {
				e.printStackTrace(p);
				p.println("error creating distributed math");
				buffer.append("error creating distributed math: "+e.getMessage()+"\n");
				return;
			}
			
			//
			// translate distributed Kinetics into Lumped Kinetics
			//
			if (nonspatialSimContext.getGeometryContext().isAllSizeSpecifiedNull()){
				StructureSizeSolver structureSizeSolver = new StructureSizeSolver();
				structureSizeSolver.updateAbsoluteStructureSizes(nonspatialSimContext, nonspatialSimContext.getModel().getTopFeature(), 1.0, VCUnitDefinition.UNIT_um3);
			}
			try {
				ReactionStep[] reactionSteps = nonspatialSimContext.getModel().getReactionSteps();
				for (int i = 0; i < reactionSteps.length; i++) {
					Kinetics origKinetics = reactionSteps[i].getKinetics();
					if (origKinetics instanceof DistributedKinetics){
						StructureMappingParameter sizeParameter = nonspatialSimContext.getGeometryContext().getStructureMapping(reactionSteps[i].getStructure()).getSizeParameter();
						if (sizeParameter==null || sizeParameter.getExpression()==null || sizeParameter.getExpression().isZero()){
							throw new RuntimeException("cannot lump, compartment size is not defined for compartment "+reactionSteps[i].getStructure().getName());
						}else{
							double size = sizeParameter.getExpression().evaluateConstant();
							LumpedKinetics newLumpedKinetics = LumpedKinetics.toLumpedKinetics((DistributedKinetics)origKinetics, size);
							reactionSteps[i].setKinetics(newLumpedKinetics);
						}
					}
				}
			}catch (ExpressionException e){
				e.printStackTrace(p);
				return;
			}
			MathDescription lumpedMathDesc = null;
			try {
				lumpedMathDesc = (new MathMapping(nonspatialSimContext)).getMathDescription();
			} catch (Exception e) {
				e.printStackTrace(p);
				p.println("error creating lumped math");
				buffer.append("error creating lumped math: "+e.getMessage()+"\n");
				return;
			}
			StringBuffer reasonBuffer = new StringBuffer();
			boolean equivalency = MathDescriptionTest.testIfSame(distMathDesc, lumpedMathDesc, reasonBuffer);
			p.println("testing: equivalency = "+equivalency+",  reason = "+reasonBuffer.toString());
			if (!equivalency){
				try {
					p.println("DISTRIBUTED MATH:\n"+distMathDesc.getVCML_database());
				} catch (MathException e) {
					e.printStackTrace(p);
					p.println("failed to print VCML for distributed math");
				}
				p.println();
				try {
					p.println("LUMPED MATH:\n"+lumpedMathDesc.getVCML_database());
				} catch (MathException e) {
					e.printStackTrace(p);
					p.println("failed to print VCML for lumped math");
				}
				p.println();
			}
			p.println("end of bio-model <"+bioModel.getName()+">");
			p.println();
			buffer.append("testing: equivalency = "+equivalency+",  reason = "+reasonBuffer.toString()+"\n");
		}finally{
			p.println(buffer);
			p.flush();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LumpedKineticsTester visitor = new LumpedKineticsTester();
		boolean bAbortOnDataAccessException = true;
		try{
			VCDatabaseScanner.scanBioModels(args, visitor, bAbortOnDataAccessException);
		}catch(Exception e){e.printStackTrace(System.err);}
	}

	//
	// required for implementation of interface ... not used.
	//
	
	public boolean filterGeometry(GeometryInfo geometryInfo) {
		return false;
	}

	public void visitGeometry(Geometry geometry, PrintStream arg_p) {
	}


}
