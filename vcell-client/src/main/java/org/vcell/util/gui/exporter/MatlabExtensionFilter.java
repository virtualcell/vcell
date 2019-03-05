package org.vcell.util.gui.exporter;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.vcell.util.VCAssert;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathDescription;
import cbit.vcell.matlab.MatlabOdeFileCoder;
import cbit.vcell.solver.Simulation;

@SuppressWarnings("serial")
public class MatlabExtensionFilter extends SelectorExtensionFilter {

	public MatlabExtensionFilter() {
		super(".m","Matlab v6.0 ode function (*.m)",SelectorExtensionFilter.Selector.NONSPATIAL, SelectorExtensionFilter.Selector.DETERMINISTIC);
	}

	@Override
	public void writeBioModel(DocumentManager documentManager, BioModel model, File exportFile, SimulationContext simulationContext) throws Exception {
		VCAssert.assertValid(simulationContext);
		// matlab from application; get application
		
				// regenerate a fresh MathDescription
				MathMapping mathMapping = simulationContext.createNewMathMapping();
				MathDescription mathDesc = mathMapping.getMathDescription();
				VCAssert.assertValid(mathDesc);
				VCAssert.assertFalse(mathDesc.isSpatial(),"spatial");
				VCAssert.assertFalse(mathDesc.isNonSpatialStoch(),"stochastic");
				Simulation sim = new Simulation(mathDesc);
				MatlabOdeFileCoder coder = new MatlabOdeFileCoder(sim);
				java.io.StringWriter sw = new java.io.StringWriter();
				java.io.PrintWriter pw = new java.io.PrintWriter(sw);
				String functionName = exportFile.getName();
				if (functionName.endsWith(".m")){
					functionName = functionName.substring(0,functionName.length()-2);
				}
				coder.write_V6_MFile(pw,functionName,simulationContext.getOutputFunctionContext());
				pw.flush();
				pw.close();
				String resultString = sw.getBuffer().toString();
				FileUtils.writeStringToFile(exportFile, resultString);
	}
	

}
