package org.vcell.util.gui.exporter;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.vcell.util.VCAssert;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.xml.XmlHelper;

@SuppressWarnings("serial")
public class CellMLExtensionFilter extends SelectorExtensionFilter {
	private static final String FNAMES[] = {".xml",".cellml"};
	
	public CellMLExtensionFilter() {
		super(FNAMES,"CELLML format (*.xml .cellml)",SelectorExtensionFilter.Selector.NONSPATIAL);
	}

	@Override
	public void writeBioModel(DocumentManager documentManager, BioModel bioModel, File exportFile, SimulationContext simulationContext) throws Exception {
		VCAssert.assertValid(simulationContext);	
		String applicationName = simulationContext.getName();
		String resultString = XmlHelper.exportCellML(bioModel, applicationName);
		FileUtils.writeStringToFile(exportFile, resultString);
	}
	

}
