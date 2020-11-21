package org.vcell.util.gui.exporter;

import java.io.File;
import java.nio.file.Paths;
import java.util.Objects;

import org.vcell.sedml.SEDMLExporter;
import org.vcell.util.FileUtils;

import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.mapping.SimulationContext;

@SuppressWarnings("serial")
public class OmexExtensionFilter extends SelectorExtensionFilter {
	private static final String FNAMES = ".omex";
	
	public OmexExtensionFilter() {
		super(FNAMES,"OMEX Format (.omex(archive))",SelectorExtensionFilter.Selector.FULL_MODEL);
	}

	@Override
	public void writeBioModel(DocumentManager documentManager, BioModel bioModel, File exportFile, SimulationContext ignored) throws Exception {
		String resultString;
		// export the entire biomodel to a SEDML file (for now, only non-spatial,non-stochastic applns)
		int sedmlLevel = 1;
		int sedmlVersion = 1;
		String sPath = FileUtils.getFullPathNoEndSeparator(exportFile.getAbsolutePath());
		String sFile = FileUtils.getBaseName(exportFile.getAbsolutePath());
		String sExt = FileUtils.getExtension(exportFile.getAbsolutePath());
		
		SEDMLExporter sedmlExporter = null;
		if (bioModel != null) {
			sedmlExporter = new SEDMLExporter(bioModel, sedmlLevel, sedmlVersion);
			resultString = sedmlExporter.getSEDMLFile(sPath);
		} else {
			throw new RuntimeException("unsupported Document Type " + Objects.requireNonNull(bioModel).getClass().getName() + " for SedML export");
		}
		if (sExt.equals("omex")) {
			//sedmlExporter.createManifest(sPath, sFile);
			String sedmlFileName = Paths.get(sPath, sFile + ".sedml").toString();
			XmlUtil.writeXMLStringToFile(resultString, sedmlFileName, true);
			sedmlExporter.addSedmlFileToList(sFile + ".sedml");
			// sedmlExporter.addSedmlFileToList("manifest.xml");
			sedmlExporter.createOmexArchive(sPath, sFile);
			return;
		}
		else {
			XmlUtil.writeXMLStringToFile(resultString, exportFile.getAbsolutePath(), true);
		}
	}
	

}
