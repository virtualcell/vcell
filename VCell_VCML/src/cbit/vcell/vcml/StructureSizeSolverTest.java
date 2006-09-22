package cbit.vcell.vcml;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
import cbit.vcell.parser.Expression;
/**
 * Insert the type's description here.
 * Creation date: (5/16/2006 6:02:15 PM)
 * @author: Jim Schaff
 */
public class StructureSizeSolverTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		//
		// get structures with known sizes,
		// calculate structuremappings (surface to volume ratios and volume fractions)
		// and invoke structureSizeSolver
		// make sure process is invertable ... need both directions in StructureSizeSolver.
		//
		cbit.vcell.model.Model model = new cbit.vcell.model.Model("model");
		model.addFeature("ec",null,null);
		Feature ec = (Feature)model.getStructure("ec");
		model.addFeature("cyt",ec,"pm");
		Feature cyt = (Feature)model.getStructure("cyt");
		Membrane pm = (Membrane)model.getStructure("pm");
		model.addFeature("nucleus",cyt,"nucMem");
		Feature nucleus = (Feature)model.getStructure("nucleus");
		Membrane nucMem = (Membrane)model.getStructure("nucMem");
		model.addFeature("er",cyt,"erMem");
		Feature er = (Feature)model.getStructure("er");
		Membrane erMem = (Membrane)model.getStructure("erMem");
		model.addFeature("mito",cyt,"mitoMem");
		Feature mito = (Feature)model.getStructure("mito");
		Membrane mitoMem = (Membrane)model.getStructure("mitoMem");

		cbit.vcell.modelapp.SimulationContext simContext = new cbit.vcell.modelapp.SimulationContext(model,new cbit.vcell.geometry.Geometry("geo",0));
		simContext.getGeometryContext().getStructureMapping(ec).getSizeParameter().setExpression(new Expression(1.0));
		simContext.getGeometryContext().getStructureMapping(cyt).getSizeParameter().setExpression(new Expression(2.0));
		simContext.getGeometryContext().getStructureMapping(pm).getSizeParameter().setExpression(new Expression(3.0));
		simContext.getGeometryContext().getStructureMapping(nucleus).getSizeParameter().setExpression(new Expression(4.0));
		simContext.getGeometryContext().getStructureMapping(nucMem).getSizeParameter().setExpression(new Expression(5.0));
		simContext.getGeometryContext().getStructureMapping(er).getSizeParameter().setExpression(new Expression(6.0));
		simContext.getGeometryContext().getStructureMapping(erMem).getSizeParameter().setExpression(new Expression(7.0));
		simContext.getGeometryContext().getStructureMapping(mito).getSizeParameter().setExpression(new Expression(8.0));
		simContext.getGeometryContext().getStructureMapping(mitoMem).getSizeParameter().setExpression(new Expression(9.0));

		StructureSizeSolver structSizeSolver = new StructureSizeSolver();
		structSizeSolver.updateRelativeStructureSizes(simContext);

		cbit.vcell.modelapp.StructureMapping[] structureMappings = simContext.getGeometryContext().getStructureMappings();
		for (int i = 0; i < structureMappings.length; i++){
			System.out.println("\""+structureMappings[i].getStructure().getName()+"\" size = "+ 
										structureMappings[i].getSizeParameter().getExpression().infix() + 
										" ["+structureMappings[i].getSizeParameter().getUnitDefinition().getSymbol()+"]");
		}
		System.out.println();
		for (int i = 0; i < structureMappings.length; i++){
			if (structureMappings[i] instanceof cbit.vcell.modelapp.MembraneMapping){
				cbit.vcell.modelapp.MembraneMapping mm = (cbit.vcell.modelapp.MembraneMapping)structureMappings[i];
				System.out.println("\""+mm.getMembrane().getInsideFeature().getName() + 
										"\" volFract="+mm.getVolumeFractionParameter().getExpression().evaluateConstant() + 
										", svRatio="+mm.getSurfaceToVolumeParameter().getExpression().evaluateConstant());
			}
		}

		//
		// clear sizes and ask StructureSizeSolver to solve for them.
		//
		for (int i = 0; i < structureMappings.length; i++){
			structureMappings[i].getSizeParameter().setExpression(null);
		}


		structSizeSolver.updateAbsoluteStructureSizes(simContext,ec,1.0,cbit.vcell.units.VCUnitDefinition.UNIT_um3);
		System.out.println("\n\nsolution:\n");
		for (int i = 0; i < structureMappings.length; i++){
			System.out.println("\""+structureMappings[i].getStructure().getName()+"\" size = "+ 
										structureMappings[i].getSizeParameter().getExpression() + 
										" ["+structureMappings[i].getSizeParameter().getUnitDefinition().getSymbol()+"]");
		}
		
	} catch (Throwable e) {
		e.printStackTrace(System.out);
	}
}
}