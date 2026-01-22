package org.vcell.util.gui.exporter;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.mapping.SimulationContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.sedml.PublicationMetadata;
import org.vcell.sedml.SedMLExporter;
import org.vcell.util.FileUtils;
import org.vcell.util.document.BioModelInfo;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@SuppressWarnings("serial")
public class OmexExtensionFilter extends SedmlExtensionFilter {
	private static Logger logger = LogManager.getLogger(OmexExtensionFilter.class);
	private static final String FNAMES = ".omex";

	public OmexExtensionFilter() {
		super(FNAMES, "COMBINE archive (.omex)", SelectorExtensionFilter.Selector.FULL_MODEL);
	}

	@Override
	public void writeBioModel(DocumentManager _documentManager, BioModel bioModel, File exportFile, SimulationContext _ignored) throws Exception {
		if (bioModel != null) {
			String sExt = FileUtils.getExtension(exportFile.getAbsolutePath());
			boolean bCreateOmexArchive = false;
			if (sExt.equals("omex")) {
				bCreateOmexArchive = true;
			}
			boolean bRoundTripSBMLValidation = true;
			boolean bHasPython = false;
			Optional<PublicationMetadata> publicationInfo = Optional.empty();
			if (bioModel.getVersion()!=null && bioModel.getVersion().getVersionKey()!=null){
				BioModelInfo bioModelInfo = _documentManager.getBioModelInfo(bioModel.getVersion().getVersionKey());
				if (bioModelInfo != null && bioModelInfo.getPublicationInfos()!=null && bioModelInfo.getPublicationInfos().length>0){
					try {
						publicationInfo = Optional.of(PublicationMetadata.fromPublicationInfoAndWeb(bioModelInfo.getPublicationInfos()[0]));
					}catch (Exception e){
						logger.warn("failed to extract publication info from web", e);
						publicationInfo = Optional.of(PublicationMetadata.fromPublicationInfo(bioModelInfo.getPublicationInfos()[0]));
					}
				}
			}
			Map<String, String> unsupportedApplications = SedMLExporter.getUnsupportedApplicationMap(bioModel, modelFormat);
			Predicate<SimulationContext> simContextFilter = (SimulationContext sc) -> !unsupportedApplications.containsKey(sc.getName());
			SedMLExporter.writeBioModel(bioModel, publicationInfo, exportFile, modelFormat, simContextFilter, bHasPython, bRoundTripSBMLValidation, bCreateOmexArchive);
		}
	}
}
