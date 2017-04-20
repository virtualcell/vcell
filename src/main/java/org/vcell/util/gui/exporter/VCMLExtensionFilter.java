package org.vcell.util.gui.exporter;

import java.io.File;

import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.xml.XmlHelper;


@SuppressWarnings("serial")
public class VCMLExtensionFilter extends SelectorExtensionFilter {
	
	public VCMLExtensionFilter() {
		super(".vcml","VCML format (.vcml)",SelectorExtensionFilter.Selector.FULL_MODEL,SelectorExtensionFilter.Selector.DEFAULT);
	}

	@Override
	public void writeBioModel(DocumentManager documentManager, BioModel bioModel, File exportFile, SimulationContext simulationContext) throws Exception {
		bioModel.getVCMetaData().cleanupMetadata();
		String resultString = XmlHelper.bioModelToXML(bioModel);
		XmlUtil.writeXMLStringToFile(resultString, exportFile.getAbsolutePath(), true);
	}
}
