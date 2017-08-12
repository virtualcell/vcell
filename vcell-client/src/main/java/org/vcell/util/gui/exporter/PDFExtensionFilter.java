package org.vcell.util.gui.exporter;

import java.io.File;
import java.io.FileOutputStream;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.mapping.SimulationContext;


@SuppressWarnings("serial")
public class PDFExtensionFilter extends SelectorExtensionFilter {
	
	public PDFExtensionFilter() {
		super(".pdf",	"Report (*.pdf)",SelectorExtensionFilter.Selector.FULL_MODEL);
		
	}

	@Override
	public void writeBioModel(DocumentManager documentManager, BioModel bioModel, File exportFile, SimulationContext simulationContext) throws Exception {
		try( FileOutputStream fos = new FileOutputStream(exportFile)) {
			documentManager.generatePDF(bioModel, fos);		
		}
	}
}
